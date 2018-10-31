package org.treez.study.atom.picking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.variablefield.VariableField;
import org.treez.core.atom.variablelist.AbstractVariableListField;
import org.treez.data.database.mysql.MySqlDatabase;
import org.treez.data.database.sqlite.SqLiteDatabase;
import org.treez.model.input.HashMapModelInput;
import org.treez.model.input.ModelInput;
import org.treez.study.atom.ModelInputGenerator;
import org.treez.study.atom.sweep.SweepModelInputGenerator;

/**
 * Creates the model input for a Picking parameter variation
 */
public class PickingModelInputGenerator implements ModelInputGenerator {

	private static final Logger LOG = LogManager.getLogger(SweepModelInputGenerator.class);

	//#region ATTRIBUTES

	private Picking picking;

	//#end region

	//#region CONSTRUCTORS

	PickingModelInputGenerator(Picking picking) {
		this.picking = picking;
	}

	//#end region

	//#region METHODS

	@Override
	public List<ModelInput> createModelInputs() {

		List<ModelInput> modelInputs = new ArrayList<>();

		List<Sample> samples = getEnabledSamples();

		if (!samples.isEmpty()) {

			String studyName = picking.getId();
			String studyDescription = picking.getDescription();
			String sourceModelPath = picking.getSourceModelPath();
			boolean isTimeDependent = picking.isTimeDependent.get();

			if (isTimeDependent) {
				String timeVariablePath = picking.timeVariableModelPath.get();
				List<Number> timeRange = picking.getTimeRange();
				for (int timeIndex = 0; timeIndex < timeRange.size(); timeIndex++) {
					Number timeValue = timeRange.get(timeIndex);
					for (Sample sample : samples) {
						ModelInput modelInput = createModelInputFromSampleForTimeStep(sourceModelPath, studyName,
								studyDescription, sample, timeVariablePath, timeIndex, timeValue);
						modelInputs.add(modelInput);
					}
				}

			} else {

				for (Sample sample : samples) {
					ModelInput modelInput = createModelInputFromSample(sourceModelPath, studyName, studyDescription,
							sample);
					modelInputs.add(modelInput);
				}
			}
		}

		return modelInputs;
	}

	@Override
	public int getNumberOfSimulations() {
		return getEnabledSamples().size();
	}

	@Override
	public void exportStudyInfoToTextFile(String filePath) {

		List<Sample> samples = getEnabledSamples();
		int numberOfSimulations = getNumberOfSimulations();

		String sourceModelPath = picking.getSourceModelPath();

		String studyInfo = "---------- PickingInfo ----------\r\n\r\n" + //
				"Total number of simulations:\r\n" + numberOfSimulations + "\r\n\r\n" + //
				"Source model path:\r\n" + sourceModelPath + "\r\n\r\n" + //
				"Variable names and values:\r\n\r\n";

		for (Sample sample : samples) {
			studyInfo += "== Sample '" + sample.getName() + "' ===\r\n";

			Map<String, VariableField<?, ?>> variableData = sample.getVariableData();
			for (String variableName : variableData.keySet()) {
				VariableField<?, ?> variableField = variableData.get(variableName);
				String valueString = variableField.getValueString();
				studyInfo += variableName + ": " + valueString + "\r\n";
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
	public void fillStudyInfo(SqLiteDatabase database, String tableName, String studyName) {

		List<Sample> samples = getEnabledSamples();
		Map<String, Set<String>> uniqueVariableValues = collectUniqueVariableValues(samples);
		for (Entry<String, Set<String>> entry : uniqueVariableValues.entrySet()) {

			String variableName = entry.getKey();
			Set<String> variableValues = entry.getValue();

			for (String variableValue : variableValues) {
				String query = "INSERT INTO '" + tableName + "' VALUES(null, '" + studyName + "', '" + variableName
						+ "','" + variableValue + "')";
				database.execute(query);
			}
		}
	}

	@Override
	public void fillStudyInfo(MySqlDatabase database, String schemaName, String tableName, String studyName) {
		List<Sample> samples = getEnabledSamples();
		Map<String, Set<String>> uniqueVariableValues = collectUniqueVariableValues(samples);
		for (Entry<String, Set<String>> entry : uniqueVariableValues.entrySet()) {

			String variableName = entry.getKey();
			Set<String> variableValues = entry.getValue();

			for (String variableValue : variableValues) {
				String query = "INSERT INTO `" + schemaName + "`.`" + tableName + "` VALUES(null, '" + studyName + "', '"
						+ variableName + "','" + variableValue + "')";
				database.execute(query);
			}
		}

	}

	private static Map<String, Set<String>> collectUniqueVariableValues(List<Sample> samples) {
		Map<String, Set<String>> uniqueVariableValues = new HashMap<>();
		for (Sample sample : samples) {

			Map<String, VariableField<?, ?>> variableData = sample.getVariableData();
			for (String variableName : variableData.keySet()) {
				VariableField<?, ?> variableField = variableData.get(variableName);
				String variableValue = variableField.getValueString();
				if (!uniqueVariableValues.containsKey(variableName)) {
					uniqueVariableValues.put(variableName, new HashSet<>());
				}
				Set<String> variableValues = uniqueVariableValues.get(variableName);
				variableValues.add(variableValue);
			}
		}
		return uniqueVariableValues;
	}

	private ModelInput createModelInputFromSampleForTimeStep(
			String sourceModelPath,
			String studyName,
			String studyDescription,
			Sample sample,
			String timeVariablePath,
			int timeIndex,
			Number timeValue) {

		String pickingModelPath = picking.createTreeNodeAdaption().getTreePath();

		ModelInput modelInput = new HashMapModelInput(pickingModelPath, studyName, studyDescription);

		//set time value
		modelInput.add(timeVariablePath, timeValue);

		//set sample values
		Map<String, AbstractVariableListField<?, ?>> variableData = sample.getVariableSeriesData();
		for (String variableName : variableData.keySet()) {
			AbstractVariableListField<?, ?> variableListField = variableData.get(variableName);
			String variablePath = sourceModelPath + "." + variableName;

			Object listObject = variableListField.get();
			List<?> list = (List<?>) listObject;
			try {
				Object value = list.get(timeIndex);
				modelInput.add(variablePath, value);
			} catch (IndexOutOfBoundsException exception) {
				String message = "Could not retieve sample value for sample " + sample.getName() + "\n" + //
						" and variable '" + variableName + "' at time index '" + timeIndex
						+ "'. The length of the list is " + list.size() + ".";
				throw new IllegalArgumentException(message, exception);
			}

		}

		return modelInput;

	}

	private
			ModelInput
			createModelInputFromSample(String sourceModelPath, String studyName, String studyDescription, Sample sample) {

		String pickingModelPath = picking.createTreeNodeAdaption().getTreePath();
		ModelInput modelInput = new HashMapModelInput(pickingModelPath, studyName, studyDescription);
		Map<String, VariableField<?, ?>> variableData = sample.getVariableData();
		for (String variableName : variableData.keySet()) {
			VariableField<?, ?> variableField = variableData.get(variableName);
			String variablePath = sourceModelPath + "." + variableName;

			Object value = variableField.get();
			modelInput.add(variablePath, value);
		}
		return modelInput;
	}

	/**
	 * Gets the samples from the picking
	 */
	public List<Sample> getEnabledSamples() {
		List<Sample> samples = new ArrayList<>();
		for (AbstractAtom<?> child : picking.getChildAtoms()) {
			boolean isSample = child instanceof Sample;
			if (isSample) {
				Sample sample = (Sample) child;
				boolean isEnabled = sample.enabled.get();
				if (isEnabled) {
					samples.add(sample);
				}
			}
		}
		return samples;
	}

	/**
	 * Returns the number of time steps
	 */
	public int getNumberOfTimeSteps() {
		List<Number> timeRange = picking.getTimeRange();
		int numberOfTimeSteps = timeRange.size();
		return numberOfTimeSteps;

	}

	//#end region

}
