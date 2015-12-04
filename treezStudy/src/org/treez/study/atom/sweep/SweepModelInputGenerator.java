package org.treez.study.atom.sweep;

import java.util.ArrayList;
import java.util.List;

import org.treez.core.atom.base.AbstractAtom;
import org.treez.model.input.HashMapModelInput;
import org.treez.model.input.ModelInput;
import org.treez.study.atom.range.AbstractVariableRange;

/**
 * Creates the model input for a sweep
 */
public class SweepModelInputGenerator {

	//#region ATTRIBUTES

	private String sweepModelPath;

	//#end region

	//#region CONSTRUCTORS

	SweepModelInputGenerator(String sweepModelPath) {
		this.sweepModelPath = sweepModelPath;
	}

	//#end region

	//#region METHODS

	/**
	 * Creates the model inputs for the given variable ranges
	 *
	 * @param variableRanges
	 * @return
	 */
	public List<ModelInput> createModelInputs(List<AbstractVariableRange<?>> variableRanges) {
		List<ModelInput> modelInputs = new ArrayList<>();
		if (!variableRanges.isEmpty()) {
			AbstractVariableRange<?> firstRange = variableRanges.get(0);
			List<AbstractVariableRange<?>> remainingRanges = variableRanges.subList(1, variableRanges.size());
			String variableModelPath = firstRange.getSourceVariableModelPath();

			List<? extends Object> rangeObjects = firstRange.getRange();
			for (Object currentRangeObject : rangeObjects) {
				//create model input that initially contains the current quantity
				ModelInput initialInput = createInitialModelInput(variableModelPath, currentRangeObject);

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
					modelInput.increaseId();
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
	 *
	 * @param variableModelPath
	 * @param currentQuantity
	 * @return
	 */
	private ModelInput createInitialModelInput(String variableModelPath, Object rangeObject) {
		ModelInput initialInput = new HashMapModelInput(sweepModelPath);
		initialInput.add(variableModelPath, rangeObject);
		return initialInput;
	}

	/**
	 * Gets the variable ranges from the children of the given parent atom
	 *
	 * @param parentAtom
	 * @return
	 */
	public List<AbstractVariableRange<?>> getActiveVariableRanges(AbstractAtom parentAtom) {
		List<AbstractVariableRange<?>> variableRanges = new ArrayList<>();
		for (AbstractAtom child : parentAtom.getChildAtoms()) {
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
	 *
	 * @param variableRanges
	 * @return
	 */
	public int getNumberOfSimulations(List<AbstractVariableRange<?>> variableRanges) {

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

	//#end region

}
