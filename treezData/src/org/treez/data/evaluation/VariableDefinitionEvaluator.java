package org.treez.data.evaluation;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.treez.core.Activator;
import org.treez.core.octave.OctaveProcess;
import org.treez.core.octave.OctaveProcessHandler;
import org.treez.core.quantity.Quantity;

/**
 * Evaluates variable definitions
 */
public class VariableDefinitionEvaluator {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(VariableDefinitionEvaluator.class);

	//#region ATTRIBUTES

	private static final String OCTAVE_PATH = "C:\\Octave\\Octave-3.8.1\\bin\\octave.exe --persist --interactive --quiet";

	private static final String RELATIVE_OCTAVE_UNITS_PATH = "\\lib\\units_Rob_deCarvalho";

	/**
	 * Octave is used for the evaluations
	 */
	private static OctaveProcess octave = null;

	/**
	 * The current octave error text
	 */
	private String octaveError;

	/**
	 * Maps from unit to type
	 */
	private Map<String, String> typeMap;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 */
	public VariableDefinitionEvaluator() {
		if (VariableDefinitionEvaluator.octave == null) {
			VariableDefinitionEvaluator.octave = createOctaveProcess();
		}
		this.octaveError = "";
		defineTypeMap();
	}

	//#end region

	//#region METHODS

	/**
	 * Defines the map that translates units into types Also see the Octave file units.m for the definition of the units
	 */
	private void defineTypeMap() {
		typeMap = new HashMap<String, String>();

		typeMap.put("[1]", "Number");

		typeMap.put("[m]", "Length");

		typeMap.put("[s]", "Time");

		typeMap.put("[kg]", "Mass");

	}

	/**
	 * Creates the octave process
	 *
	 * @return
	 */
	private OctaveProcess createOctaveProcess() {

		//create output and error handler
		OctaveProcessHandler octaveHandler = new OctaveProcessHandler() {

			@Override
			public void handleOutput(String outputString) {
				//raw octave result is not used here
				/*
				 * sysLog.debug("*** Octave raw output:***********************\n"
				 * + outputString +
				 * "\n********************************************");
				 */
			}

			@Override
			public void handleError(String errorString) {
				//throw error that will be cached by evaluateWithOctave
				//to be able to show errors
				octaveError = errorString;

				/*
				 * sysLog.debug("*** Octave raw error:***********************\n"
				 * + errorString +
				 * "\n********************************************");
				 */
			}
		};

		//create octave process
		OctaveProcess newOctaveProcess = new OctaveProcess(OCTAVE_PATH, octaveHandler);

		//support calculation with units of measure
		String workingDirectory = Activator.getAbsolutePathStatic();
		String unitsPath = workingDirectory + RELATIVE_OCTAVE_UNITS_PATH;
		newOctaveProcess.execute("cd " + unitsPath + ";");
		newOctaveProcess.execute("u = units;");

		//set number format
		newOctaveProcess.execute("format short g");

		return newOctaveProcess;
	}

	/**
	 * Evaluates a single variable definition
	 *
	 * @param variableName
	 * @param definitionString
	 * @return
	 */
	public VariableDefinitionResult evaluate(String variableName, String definitionString) {

		//sysLog.debug("evaluating definition " + defintionString);
		if (definitionString.isEmpty()) {

			//create new empty result
			return new VariableDefinitionResult("", "", "", "");

		} else if (variableName.isEmpty()) {

			//create new NaN result
			return new VariableDefinitionResult("NaN", "NaN", "NaN", "Name is empty.");

		} else {

			//evaluate definition

			//prepare definition for octave
			String definition = preprocessForOctave(definitionString);

			//try to evaluate the definition with octave
			return evaluateWithOctave(variableName, definition);
		}
	}

	/**
	 * Preprocess the definition entry for octave
	 *
	 * @param oldEntry
	 * @return
	 */
	private static String preprocessForOctave(String oldEntry) {
		String entry = oldEntry;
		return entry;
	}

	/**
	 * Try to evaluate the definition with octave
	 *
	 * @param variableName
	 * @param definition
	 * @return
	 */
	private VariableDefinitionResult evaluateWithOctave(String variableName, String definition) {

		VariableDefinitionResult result;
		Quantity resultQuantity = null;
		String errorString = "";
		String expression = variableName + " = " + definition;

		sysLog.debug("evaluating expression '" + expression + "'");

		resultQuantity = octave.evalQuantity(expression); //errors are written to octaveError, see octaveHandler
		if (!octaveError.isEmpty()) {
			errorString = octaveError;
			octaveError = "";
		}

		//extract variable definition result from quantity
		boolean evaluated = errorString.equals("");

		if (evaluated) {
			result = quantityToVariableDefinitonResult(resultQuantity);
		} else {
			result = new VariableDefinitionResult("NaN", "NaN", "NaN", errorString);
		}
		return result;
	}

	/**
	 * Extracts value, unit and type from result
	 *
	 * @param result
	 * @return
	 */
	private VariableDefinitionResult quantityToVariableDefinitonResult(Quantity result) {

		String error = checkResult(result);
		if (error.isEmpty()) {
			String value = postprocessValue(result.getValue());
			String unit = postprocessUnit(result.getUnit());
			String type = getTypeFromUnit(unit);
			return new VariableDefinitionResult(value, unit, type, "");
		} else {
			return new VariableDefinitionResult("NaN", "NaN", "unknown", error);
		}
	}

	/**
	 * Checks the result
	 *
	 * @param result
	 * @return
	 */
	private static String checkResult(Quantity result) {

		String value = result.getValue();
		//check for matrices with dimension > 2
		Boolean containsMultiDimensionalMatrix = value.contains("ans(");
		if (containsMultiDimensionalMatrix) {
			return "Result contains matrix with dimension > 2. This is not implemented here.";
		}

		return "";
	}

	/**
	 * Post processes the filePath string from octave
	 *
	 * @param value
	 * @return
	 */
	private static String postprocessValue(String value) {
		String newValue = value;

		String octaveLineSeparator = "\n";
		String lineSeparator = ";"; //an extra space is added automatically
		String octaveValueSeparator = " ";
		String valueSeparator = ","; //an extra space is added automatically

		//write all lines into a single line, separated by lineSeparator
		newValue = newValue.replace(octaveLineSeparator, lineSeparator);

		Boolean isMatrix = newValue.contains(octaveValueSeparator);
		if (isMatrix) {

			//replace filePath separator '   ' => ', '
			newValue = newValue.replace(octaveValueSeparator, valueSeparator);
			//remove double filePath separators ', , ' => ', '
			newValue = newValue.replaceAll(valueSeparator + "+", valueSeparator);

			//create matrix string
			String[] lines = newValue.split(lineSeparator);
			newValue = createFormattedMatrixString(lines, lineSeparator, valueSeparator);

			//add extra spaces after separators
			newValue = newValue.replaceAll(valueSeparator, valueSeparator + " ");
			newValue = newValue.replaceAll(lineSeparator, lineSeparator + " ");
		} else {
			newValue = formatValue(newValue);
		}

		return newValue;
	}

	/**
	 * Creates a display string for a vector for a given array of values
	 *
	 * @param lines
	 * @param lineSeparator
	 * @param valueSeparator
	 * @return
	 */
	private static String createFormattedMatrixString(String[] lines, String lineSeparator, String valueSeparator) {
		String matrixString = "[";
		for (String line : lines) {
			String[] values = line.split(valueSeparator);
			for (String value : values) {
				matrixString = matrixString + formatValue(value) + valueSeparator;
			}
			matrixString = matrixString.substring(0, matrixString.length() - valueSeparator.length());
			matrixString = matrixString + lineSeparator;
		}
		matrixString = matrixString.substring(0, matrixString.length() - lineSeparator.length());
		matrixString = matrixString + "]";

		return matrixString;
	}

	/**
	 * Formats a single number
	 *
	 * @param valueString
	 * @return
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private static String formatValue(String valueString) {

		try {
			Double value = Double.parseDouble(valueString);
			String result = String.format(Locale.US, "%g", value);
			if (result.indexOf(".") > 0) {
				result = result.replaceAll("0*$", "");
				result = result.replaceAll("\\.$", "");
				result = result.replaceAll("0*e", "e");
			}
			return result;
		} catch (Exception e) {
			sysLog.warn("Could not parse '" + valueString + "' as Double.");
			return "NaN";
		}

	}

	/**
	 * Post processes the unit string from octave
	 *
	 * @param unit
	 * @return
	 */
	private static String postprocessUnit(String unit) {
		String newUnit = unit;
		//sysLog.debug("Unit: '" + unit + "'");

		newUnit = newUnit.replace("^1]", "]"); // [m^1] => [m]

		newUnit = newUnit.replace("][", " "); // [m][s] => [m s]

		if (unit.isEmpty()) {
			newUnit = "[1]";
		}

		return newUnit;
	}

	/**
	 * Tries to examine the type of the quantity from its unit string
	 *
	 * @param unit
	 * @return
	 */
	private String getTypeFromUnit(String unit) {

		if (typeMap.containsKey(unit)) {
			return typeMap.get(unit);
		} else {
			return "unknown";
		}
	}

	//#end region

}
