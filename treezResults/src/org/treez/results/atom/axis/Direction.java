package org.treez.results.atom.axis;

public enum Direction {

	//#region VALUES

	VERTICAL("vertical"),

	HORIZONTAL("horizontal");

	//#end region

	//#region ATTRIBUTES

	private String value;

	//#end region

	//#region CONSTRUCTORS

	Direction(String directionString) {
		this.value = directionString;
	}

	//#end region

	//#region METHODS

	@Override
	public String toString() {
		return value;
	}

	//#end region
}
