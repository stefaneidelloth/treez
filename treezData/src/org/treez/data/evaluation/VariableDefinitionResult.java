package org.treez.data.evaluation;

/**
 * Represents the results of the evaluation of a variable definition
 */
public class VariableDefinitionResult {

	//#region ATTRIBUTES

	private String value;

	private String unit;

	private String type;

	private String error;

	//#end region

	//#region CONSTRUCTORS

	public VariableDefinitionResult(String value, String unit, String type, String error) {
		this.value = value;
		this.unit = unit;
		this.type = type;
		this.error = error;
	}

	//#end region

	//#region ACCESSORS

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	//#end region

}
