package org.treez.results.atom.tornado;

public enum SortingMode {

	//#region VALUES

	LARGEST_DIFFERENCE("largestDifference"), //
	SMALLEST_DIFFERENCE("smallestDifference"), //
	LABEL("label"), //
	UNSORTED("unsorted");

	//#end region

	//#region ATTRIBUTES

	private String value;

	//#end region

	//#region CONSTRUCTORS

	SortingMode(String value) {
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
