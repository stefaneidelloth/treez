package org.treez.results.atom.legend;

public enum PositionReference {

	//#region VALUES

	GRAPH("graph"),
	PAGE("page");

	//#end region

	//#region ATTRIBUTES

	private String value;

	//#end region

	//#region CONSTRUCTORS

	PositionReference(String label) {
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

	public boolean isGraph() {
		return this.equals(PositionReference.GRAPH);
	}

	public boolean isPage() {
		return this.equals(PositionReference.PAGE);
	}

	//#end region
}
