package org.treez.results.atom.legend;

public enum HorizontalPosition {

	//#region VALUES

	LEFT("left"),
	CENTRE("centre"),
	RIGHT("right"),
	MANUAL("manual");

	//#end region

	//#region ATTRIBUTES

	private String value;

	//#end region

	//#region CONSTRUCTORS

	HorizontalPosition(String label) {
		this.value = label;
	}

	//#end region

	//#region METHODS

	@Override
	public String toString() {
		return value;
	}

	//#end region

	//#region ACCESSORS

	public boolean isLeft() {
		return this.equals(HorizontalPosition.LEFT);
	}

	public boolean isCentre() {
		return this.equals(HorizontalPosition.CENTRE);
	}

	public boolean isRight() {
		return this.equals(HorizontalPosition.RIGHT);
	}

	public boolean isManual() {
		return this.equals(HorizontalPosition.MANUAL);
	}

	//#end region
}
