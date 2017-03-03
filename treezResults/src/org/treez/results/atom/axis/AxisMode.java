package org.treez.results.atom.axis;

public enum AxisMode {

	//#region VALUES

	QUANTITATIVE("quantitative"), //
	ORDINAL("ordinal"); //
	//TIME("time"); //maybe implement date time picker before implementing this

	//#end region

	//#region ATTRIBUTES

	private String value;

	//#end region

	//#region CONSTRUCTORS

	AxisMode(String value) {
		this.value = value;
	}

	//#end region

	//#region METHODS

	@Override
	public String toString() {
		return value;
	}

	//#end region

}
