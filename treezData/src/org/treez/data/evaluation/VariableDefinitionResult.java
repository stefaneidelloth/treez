package org.treez.data.evaluation;

/**
 * Represents the results of the evaluation of a variable definition
 * 
 */
public class VariableDefinitionResult {

	//#region ATTRIBUTES

	private String value;
	private String unit;
	private String type;
	private String error;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 * 
	 * @param value
	 * @param unit
	 * @param type
	 * @param error
	 */
	public VariableDefinitionResult(String value, String unit, String type, String error) {
		this.value = value;
		this.unit = unit;
		this.type = type;
		this.error = error;
	}

	//#end region

	//#region ACCESSORS

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the filePath to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @param unit
	 *            the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @param error
	 *            the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}

	//#end region

}
