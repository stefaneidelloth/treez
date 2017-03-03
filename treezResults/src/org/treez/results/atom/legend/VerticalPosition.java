package org.treez.results.atom.legend;

public enum VerticalPosition {

	//#region VALUES

	TOP("top"),
	CENTRE("centre"),
	BOTTOM("bottom"),
	MANUAL("manual");

	//#end region

	//#region ATTRIBUTES

	private String value;

	//#end region

	//#region CONSTRUCTORS

	VerticalPosition(String label) {
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

	public boolean isTop() {
		return this.equals(VerticalPosition.TOP);
	}

	public boolean isCentre() {
		return this.equals(VerticalPosition.CENTRE);
	}

	public boolean isBottom() {
		return this.equals(VerticalPosition.BOTTOM);
	}

	public boolean isManual() {
		return this.equals(VerticalPosition.MANUAL);
	}

	//#end region
}
