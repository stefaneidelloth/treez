package org.treez.study.atom.sensitivity;

import java.util.List;

import org.treez.core.atom.variablefield.DoubleVariableField;
import org.treez.core.atom.variablefield.VariableField;
import org.treez.core.atom.variablelist.VariableListWithInfo;

public class SensitivityValueFactory {

	//#region METHODS

	public static void updateVariableInfos(VariableListWithInfo variableList, Sensitivity sensitivity) {

		SensitivityType type = sensitivity.sensitivityType.get();
		switch (type) {
		case RELATIVE_DISTANCE:
			updateByRelativeDistance(variableList, sensitivity);
			break;
		case RELATIVE_POSITION:
			updateByRelativePosition(variableList, sensitivity);
			break;
		case ABSOLUTE_DISTANCE:
			updateByAbsoluteDistance(variableList, sensitivity);
			break;
		default:
			throw new IllegalStateException("Not yet implemented type " + type);
		}

	}

	private static void updateByRelativeDistance(VariableListWithInfo variableList, Sensitivity sensitivity) {
		RelationType type = sensitivity.relationType.get();
		switch (type) {
		case PERCENTAGE:
			updateByRelativeDistanceWithPercentage(variableList, sensitivity);
			break;
		case FACTOR:
			updateByRelativeDistanceWithFactor(variableList, sensitivity);
			break;
		case EXPONENT:
			updateByRelativeDistanceWithExponent(variableList, sensitivity);
			break;
		default:
			throw new IllegalStateException("Not yet implemented type " + type);
		}

	}

	private static
			void
			updateByRelativeDistanceWithPercentage(VariableListWithInfo variableList, Sensitivity sensitivity) {

	}

	private static void updateByRelativeDistanceWithFactor(VariableListWithInfo variableList, Sensitivity sensitivity) {

	}

	private static
			void
			updateByRelativeDistanceWithExponent(VariableListWithInfo variableList, Sensitivity sensitivity) {

	}

	private static void updateByRelativePosition(VariableListWithInfo variableList, Sensitivity sensitivity) {
		RelationType type = sensitivity.relationType.get();
		switch (type) {
		case PERCENTAGE:
			updateByRelativePositionWithPercentage(variableList, sensitivity);
			break;
		case FACTOR:
			updateByRelativePositionWithFactor(variableList, sensitivity);
			break;
		case EXPONENT:
			updateByRelativePositionWithExponent(variableList, sensitivity);
			break;
		default:
			throw new IllegalStateException("Not yet implemented type " + type);
		}

	}

	private static
			void
			updateByRelativePositionWithPercentage(VariableListWithInfo variableList, Sensitivity sensitivity) {

		for (VariableField<?, ?> variableField : variableList.get()) {
			DoubleVariableField doubleVariableField = (DoubleVariableField) variableField;
			String variableName = variableField.getName();
			Double workingPointValue = doubleVariableField.get();

			List<Double> percentages = getSortedIndividualValues(sensitivity);
			String variableInfo = getVariableInfoByRelativePositionWithPercentage(workingPointValue, percentages);

			variableList.setVariableInfo(variableName, variableInfo);
		}

	}

	private static
			String
			getVariableInfoByRelativePositionWithPercentage(Double workingPointValue, List<Double> percentages) {
		if (percentages.isEmpty()) {
			return "{" + workingPointValue + "}";
		}

		final Double percent = 1.0 / 100;

		String variableInfo = "{";
		boolean workingPointValueIncluded = false;
		for (Double percentage : percentages) {
			if (percentage == 0) {
				variableInfo += "0, ";
			} else if (percentage == 1) {
				variableInfo += workingPointValue + ", ";
			} else if (percentage > 1) {
				if (!workingPointValueIncluded) {
					variableInfo += workingPointValue + ", ";
				}
				variableInfo += (workingPointValue * (percentage * percent)) + ", ";

			} else {
				variableInfo += (workingPointValue * (percentage * percent)) + ", ";
			}
		}

		variableInfo = variableInfo.substring(variableInfo.length() - 2) + "}";

		return variableInfo;
	}

	private static void updateByRelativePositionWithFactor(VariableListWithInfo variableList, Sensitivity sensitivity) {

		for (VariableField<?, ?> variableField : variableList.get()) {
			DoubleVariableField doubleVariableField = (DoubleVariableField) variableField;
			String variableName = variableField.getName();
			Double workingPointValue = doubleVariableField.get();

			List<Double> factors = getSortedIndividualValues(sensitivity);
			String variableInfo = getVariableInfoByRelativePositionWithFactor(workingPointValue, factors);

			variableList.setVariableInfo(variableName, variableInfo);
		}

	}

	private static String getVariableInfoByRelativePositionWithFactor(Double workingPointValue, List<Double> factors) {

		if (factors.isEmpty()) {
			return "{" + workingPointValue + "}";
		}

		String variableInfo = "{";
		boolean workingPointValueIncluded = false;
		for (Double factor : factors) {
			if (factor == 0) {
				variableInfo += "0, ";
			} else if (factor == 1) {
				variableInfo += workingPointValue + ", ";
			} else if (factor > 1) {
				if (!workingPointValueIncluded) {
					variableInfo += workingPointValue + ", ";
				}
				variableInfo += (workingPointValue * factor) + ", ";

			} else {
				variableInfo += (workingPointValue * factor) + ", ";
			}
		}

		variableInfo = variableInfo.substring(variableInfo.length() - 2) + "}";

		return variableInfo;
	}

	private static
			void
			updateByRelativePositionWithExponent(VariableListWithInfo variableList, Sensitivity sensitivity) {

		for (VariableField<?, ?> variableField : variableList.get()) {
			DoubleVariableField doubleVariableField = (DoubleVariableField) variableField;
			String variableName = variableField.getName();
			Double workingPointValue = doubleVariableField.get();

			List<Double> exponents = getSortedIndividualValues(sensitivity);
			String variableInfo = getVariableInfoByRelativePositionWithExponent(workingPointValue, exponents);

			variableList.setVariableInfo(variableName, variableInfo);
		}

	}

	private static
			String
			getVariableInfoByRelativePositionWithExponent(Double workingPointValue, List<Double> expontents) {

		if (expontents.isEmpty()) {
			return "{" + workingPointValue + "}";
		}

		final Double base = 10.0;

		String variableInfo = "{";
		boolean workingPointValueIncluded = false;
		for (Double exponent : expontents) {
			if (exponent == 0) {
				variableInfo += workingPointValue + ", ";
			} else if (exponent > 1) {
				if (!workingPointValueIncluded) {
					variableInfo += workingPointValue + ", ";
				}
				variableInfo += (workingPointValue * Math.pow(base, exponent)) + ", ";

			} else {
				variableInfo += (workingPointValue * Math.pow(base, exponent)) + ", ";
			}
		}

		variableInfo = variableInfo.substring(variableInfo.length() - 2) + "}";

		return variableInfo;
	}

	private static void updateByAbsoluteDistance(VariableListWithInfo variableList, Sensitivity sensitivity) {

		for (VariableField<?, ?> variableField : variableList.get()) {
			DoubleVariableField doubleVariableField = (DoubleVariableField) variableField;
			String variableName = variableField.getName();
			Double workingPointValue = doubleVariableField.get();

			List<Double> distances = getSortedIndividualValues(sensitivity);
			String variableInfo = getVariableInfoByAbsoluteDistance(workingPointValue, distances);

			variableList.setVariableInfo(variableName, variableInfo);
		}
	}

	private static String getVariableInfoByAbsoluteDistance(Double workingPointValue, List<Double> distances) {
		if (distances.isEmpty()) {
			return "{" + workingPointValue + "}";
		}

		String variableInfo = "{";
		boolean workingPointValueIncluded = false;
		for (Double distance : distances) {
			if (distance == 0) {
				variableInfo += workingPointValue + ",";
			} else if (distance > 0) {
				if (!workingPointValueIncluded) {
					variableInfo += workingPointValue + ",";
				}
				variableInfo += (workingPointValue + distance) + ",";

			} else {
				variableInfo += (workingPointValue + distance) + ",";
			}
		}

		variableInfo = variableInfo.substring(variableInfo.length() - 1) + "}";

		return variableInfo;
	}

	private static List<Double> getSortedIndividualValues(Sensitivity sensitivity) {
		List<Double> individualValues = sensitivity.values.get();
		individualValues.sort(null);
		return individualValues;
	}

	//#end region

}
