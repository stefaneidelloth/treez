package org.treez.results.atom.tornado;

public enum DataMode {

	//#region VALUES

	TABLE("table"), //
	INDIVIDUAL_COLUMNS("individual columns");

	//#end region

	//#region ATTRIBUTES

	private String value;

	//#end region

	//#region CONSTRUCTORS

	DataMode(String value) {
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
