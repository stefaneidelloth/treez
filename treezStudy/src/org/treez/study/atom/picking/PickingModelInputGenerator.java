package org.treez.study.atom.picking;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.variablefield.VariableField;
import org.treez.core.atom.variablelist.AbstractVariableListField;
import org.treez.model.input.HashMapModelInput;
import org.treez.model.input.ModelInput;

/**
 * Creates the model input for a Picking parameter variation
 */
public class PickingModelInputGenerator {

	//#region ATTRIBUTES

	private Picking picking;

	private String pickingModelPath;

	//#end region

	//#region CONSTRUCTORS

	PickingModelInputGenerator(Picking picking) {
		this.picking = picking;
		this.pickingModelPath = picking.createTreeNodeAdaption().getTreePath();
	}

	//#end region

	//#region METHODS

	/**
	 * Creates the model inputs for the given samples
	 *
	 * @param samples
	 * @return
	 */
	public List<ModelInput> createModelInputs(List<Sample> samples) {

		List<ModelInput> modelInputs = new ArrayList<>();

		if (!samples.isEmpty()) {

			Sample firstSample = samples.get(0);
			String sourceModelPath = getSourceModelPath(firstSample);

			boolean isTimeDependent = picking.isTimeDependent.get();

			if (isTimeDependent) {
				String timeVariablePath = picking.timeVariableModelPath.get();
				List<Number> timeRange = picking.getTimeRange();
				for (int timeIndex = 0; timeIndex < timeRange.size(); timeIndex++) {
					Number timeValue = timeRange.get(timeIndex);
					for (Sample sample : samples) {
						ModelInput modelInput = createModelInputFromSampleForTimeStep(sourceModelPath, sample,
								timeVariablePath, timeIndex, timeValue);
						modelInputs.add(modelInput);
					}
				}

			} else {

				for (Sample sample : samples) {
					ModelInput modelInput = createModelInputFromSample(sourceModelPath, sample);
					modelInputs.add(modelInput);
				}
			}
		}

		return modelInputs;

	}

	private ModelInput createModelInputFromSampleForTimeStep(
			String sourceModelPath,
			Sample sample,
			String timeVariablePath,
			int timeIndex,
			Number timeValue) {

		ModelInput modelInput = new HashMapModelInput(pickingModelPath);

		//set time value
		modelInput.add(timeVariablePath, timeValue);

		//set sample values
		Map<String, AbstractVariableListField<?>> variableData = sample.getVariableSeriesData();
		for (String variableName : variableData.keySet()) {
			AbstractVariableListField<?> variableListField = variableData.get(variableName);
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

	private static String getSourceModelPath(Sample sample) {
		Picking picking = (Picking) sample.getParentAtom();
		String sourceModelPath = picking.sourceModelPath.get();
		return sourceModelPath;
	}

	private ModelInput createModelInputFromSample(String sourceModelPath, Sample sample) {
		ModelInput modelInput = new HashMapModelInput(pickingModelPath);
		Map<String, VariableField<?>> variableData = sample.getVariableData();
		for (String variableName : variableData.keySet()) {
			VariableField<?> variableField = variableData.get(variableName);
			String variablePath = sourceModelPath + "." + variableName;

			Object value = variableField.get();
			modelInput.add(variablePath, value);
		}
		return modelInput;
	}

	/**
	 * Gets the samples from the picking
	 *
	 * @return
	 */
	public List<Sample> getEnabledSamples() {
		List<Sample> samples = new ArrayList<>();
		for (AbstractAtom child : picking.getChildAtoms()) {
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
	 *
	 * @return
	 */
	public int getNumberOfTimeSteps() {
		List<Number> timeRange = picking.getTimeRange();
		int numberOfTimeSteps = timeRange.size();
		return numberOfTimeSteps;

	}

	//#end region

}
