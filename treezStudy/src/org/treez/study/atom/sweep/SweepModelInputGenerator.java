package org.treez.study.atom.sweep;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.data.database.mysql.MySqlDatabase;
import org.treez.data.database.sqlite.SqLiteDatabase;
import org.treez.model.input.HashMapModelInput;
import org.treez.model.input.ModelInput;
import org.treez.study.atom.ModelInputGenerator;
import org.treez.study.atom.Study;
import org.treez.study.atom.range.AbstractVariableRange;

/**
 * Creates the model input for a Sweep parameter variation
 */
public class SweepModelInputGenerator implements ModelInputGenerator {

	private static final Logger LOG = Logger.getLogger(SweepModelInputGenerator.class);

	//#region ATTRIBUTES

	private Sweep sweep;

	//#end region

	//#region CONSTRUCTORS

	SweepModelInputGenerator(Sweep sweep) {
		this.sweep = sweep;
	}

	//#end region

	//#region METHODS

	@Override
	public List<ModelInput> createModelInputs() {
		List<AbstractVariableRange<?>> enabledVariableRanges = getEnabledVariableRanges();
		return createModelInputs(enabledVariableRanges);
	}

	@Override
	public int getNumberOfSimulations() {
		List<AbstractVariableRange<?>> enabledVariableRanges = getEnabledVariableRanges();
		return getNumberOfSimulations(enabledVariableRanges);
	}

	@Override
	public void exportStudyInfoToTextFile(String filePath) {

		int numberOfSimulations = getNumberOfSimulations();

		String studyInfo = "---------- SweepInfo ----------\r\n\r\n" + //
				"Total number of simulations:\r\n" + numberOfSimulations + "\r\n\r\n" + //
				"Variable model paths and values:\r\n\r\n";

		List<AbstractVariableRange<?>> variableRanges = getEnabledVariableRanges();
		for (AbstractVariableRange<?> range : variableRanges) {
			String variablePath = range.getSourceVariableModelPath();
			studyInfo += variablePath + "\r\n";
			List<?> rangeValues = range.getRange();
			for (Object value : rangeValues) {
				studyInfo += value.toString() + "\r\n";
			}
			studyInfo += "\r\n";
		}

		File file = new File(filePath);

		try {
			FileUtils.writeStringToFile(file, studyInfo);
		} catch (IOException exception) {
			String message = "The specified exportStudyInfoPath '" + filePath
					+ "' is not valid. Export of study info is skipped.";
			LOG.error(message);
		}
	}

	@Override
	public void fillStudyInfo(SqLiteDatabase database, String tableName, String studyId) {
		List<AbstractVariableRange<?>> variableRanges = getEnabledVariableRanges();
		for (AbstractVariableRange<?> range : variableRanges) {
			String variablePath = range.getSourceVariableModelPath();
			List<?> rangeValues = range.getRange();
			for (Object value : rangeValues) {
				String query = "INSERT INTO '" + tableName + "' VALUES(null, '" + studyId + "', '" + variablePath
						+ "','" + value + "')";
				database.execute(query);
			}
		}
	}

	@Override
	public void fillStudyInfo(MySqlDatabase database, String schemaName, String tableName, String studyId) {
		List<AbstractVariableRange<?>> variableRanges = getEnabledVariableRanges();
		for (AbstractVariableRange<?> range : variableRanges) {
			String variablePath = range.getSourceVariableModelPath();
			List<?> rangeValues = range.getRange();
			for (Object value : rangeValues) {
				String query = "INSERT INTO `" + schemaName + "`.`" + tableName + "` VALUES(null, '" + studyId + "', '"
						+ variablePath + "','" + value + "')";
				database.execute(query);
			}
		}
	}

	/**
	 * Gets the enabled variable ranges from the children of the given parent atom
	 */
	public List<AbstractVariableRange<?>> getEnabledVariableRanges() {
		List<AbstractVariableRange<?>> variableRanges = new ArrayList<>();
		for (AbstractAtom<?> child : sweep.getChildAtoms()) {
			boolean isVariableRange = child instanceof AbstractVariableRange;
			if (isVariableRange) {
				AbstractVariableRange<?> variableRange = (AbstractVariableRange<?>) child;
				boolean isEnabled = variableRange.enabled.get();
				if (isEnabled) {
					variableRanges.add(variableRange);
				}
			}
		}
		return variableRanges;
	}

	/**
	 * Determines the total number of simulations to run
	 */
	private static int getNumberOfSimulations(List<AbstractVariableRange<?>> variableRanges) {

		//initialize number of simulations
		int numberOfSimulations = 1;
		//used to check if initial value of 1 makes sense
		boolean atLeastOneSimulation = false;

		//loop through variable ranges and calculate number of values by multiplication
		for (AbstractVariableRange<?> variableRange : variableRanges) {
			List<? extends Object> rangeValues = variableRange.getRange();
			int numberOfValues = rangeValues.size();
			if (numberOfValues > 0) {
				atLeastOneSimulation = true;
				numberOfSimulations *= numberOfValues;
			}
		}

		//return number of simulations
		if (atLeastOneSimulation) {
			return numberOfSimulations;
		} else {
			return 0;
		}
	}

	/**
	 * Creates the model inputs for the given variable ranges
	 */
	private List<ModelInput> createModelInputs(List<AbstractVariableRange<?>> variableRanges) {
		List<ModelInput> modelInputs = new ArrayList<>();
		if (!variableRanges.isEmpty()) {
			AbstractVariableRange<?> firstRange = variableRanges.get(0);
			List<AbstractVariableRange<?>> remainingRanges = variableRanges.subList(1, variableRanges.size());
			String variableModelPath = firstRange.getSourceVariableModelPath();
			Study study = (Study) firstRange.getParentAtom();
			String studyId = study.getId();
			String studyDescription = study.getDescription();

			List<? extends Object> rangeObjects = firstRange.getRange();
			for (Object currentRangeObject : rangeObjects) {
				//create model input that initially contains the current quantity
				ModelInput initialInput = createInitialModelInput(variableModelPath, studyId, studyDescription,
						currentRangeObject);

				//copy and extended the initial model input using the remaining variable ranges
				List<ModelInput> modelInputsWithCurrentQuantity = extendModelInputs(initialInput, remainingRanges);
				modelInputs.addAll(modelInputsWithCurrentQuantity);
			}
		}
		return modelInputs;

	}

	/**
	 * Creates the model inputs for the given Quantity and the given list of remaining variable ranges
	 *
	 * @param initialInput
	 * @param variableRanges
	 * @return
	 */
	private List<ModelInput> extendModelInputs(ModelInput initialInput, List<AbstractVariableRange<?>> variableRanges) {
		List<ModelInput> modelInputs = new ArrayList<>();

		boolean isLastEntry = variableRanges.isEmpty();
		if (isLastEntry) {
			//the model input is already finished and can be returned as a single model input
			modelInputs.add(initialInput);
			return modelInputs;
		} else {
			//the initial model input needs to be copied and extended using the remaining variable ranges
			AbstractVariableRange<?> firstRange = variableRanges.get(0);
			String variableModelPath = firstRange.getSourceVariableModelPath();
			List<? extends java.lang.Object> rangeObjects = firstRange.getRange();
			List<AbstractVariableRange<?>> remainingRanges = variableRanges.subList(1, variableRanges.size());

			int counter = 1;
			for (java.lang.Object currentRangeObject : rangeObjects) {
				//copy initial model input
				ModelInput modelInput = initialInput.copy();

				//increase the id for new model inputs
				if (counter > 1) {
					modelInput.increaseJobId();
				}

				//add current quantity
				modelInput.add(variableModelPath, currentRangeObject);
				//copy and extend with remaining variable ranges
				List<ModelInput> modelInputsWithCurrentQuantities = extendModelInputs(modelInput, remainingRanges);
				modelInputs.addAll(modelInputsWithCurrentQuantities);
				counter++;
			}
			return modelInputs;
		}

	}

	/**
	 * Creates an initial model input that contains a single Quantity.
	 */
	private ModelInput createInitialModelInput(
			String variableModelPath,
			String studyId,
			String studyDescription,
			Object rangeObject) {
		String sweepModelPath = sweep.createTreeNodeAdaption().getTreePath();
		ModelInput initialInput = new HashMapModelInput(sweepModelPath, studyId, studyDescription);
		initialInput.add(variableModelPath, rangeObject);
		return initialInput;
	}

	//#end region

}
