package org.treez.results.atom.tornado;

public enum LabelMode {

	//#region VALUES

	ABSOLUTE("absolute"), //
	PERCENT("percent"), //
	DIFFERENCE("difference"), //
	DIFFERENCE_IN_PERCENT("differenceInPercent");

	//#end region

	//#region ATTRIBUTES

	private String value;

	//#end region

	//#region CONSTRUCTORS

	LabelMode(String value) {
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
