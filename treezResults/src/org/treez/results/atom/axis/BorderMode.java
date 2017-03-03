package org.treez.results.atom.axis;

public enum BorderMode {

	//#region VALUES

	NONE("none", 0.0), //
	TWO("2%", 0.02), //
	FIVE("5%", 0.05), //
	TEN("10%", 0.1), //
	FIVETEEN("15%", 0.15); //

	//#end region

	//#region ATTRIBUTES

	private String value;

	private Double factor;

	//#end region

	//#region CONSTRUCTORS

	BorderMode(String value, Double factor) {
		this.value = value;
		this.factor = factor;
	}

	//#end region

	//#region METHODS

	@Override
	public String toString() {
		return value;
	}

	//#end region

	//#region ACCESSORS

	public Double getFactor() {
		return factor;
	}

	//#end region

}
