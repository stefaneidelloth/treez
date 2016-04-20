package org.treez.results.atom.axis;

/**
 * The direction of an axis
 */
public enum Direction {

	//#region VALUES

	/**
	 * The axis is oriented vertically
	 */
	VERTICAL("vertical"),

	/**
	 * The axis is oriented horizontally
	 */
	HORIZONTAL("horizontal");

	//#end region

	//#region ATTRIBUTES

	/**
	 * The string value that corresponds to the direction
	 */
	private String directionString;

	//#end region

	//#region CONSTRUCTORS

	Direction(String directionString) {
		this.directionString = directionString;
	}

	//#end region

	//#region METHODS

	@Override
	public String toString() {
		return directionString;
	}

	//#end region
}
