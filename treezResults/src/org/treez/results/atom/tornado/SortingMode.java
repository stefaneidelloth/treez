package org.treez.results.atom.tornado;

import java.util.ArrayList;
import java.util.List;

import org.treez.javafxd3.javafx.EnumValueProvider;

public enum SortingMode implements EnumValueProvider<SortingMode> {

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

	@Override
	public SortingMode fromString(final String value) {
		return valueOf(value.toUpperCase().replace("-", "_"));
	}

	@Override
	public List<String> getValues() {
		List<String> values = new ArrayList<>();
		for (SortingMode enumValue : values()) {
			String stringValue = enumValue.value;
			values.add(stringValue);
		}
		return values;
	}

	//#end region

}
