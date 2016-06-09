package org.treez.results.atom.tornado;

import java.util.ArrayList;
import java.util.List;

import org.treez.javafxd3.javafx.EnumValueProvider;

public enum LabelMode implements EnumValueProvider<LabelMode> {

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

	@Override
	public LabelMode fromString(final String value) {
		return valueOf(value.toUpperCase().replace("-", "_"));
	}

	@Override
	public List<String> getValues() {
		List<String> values = new ArrayList<>();
		for (LabelMode enumValue : values()) {
			String stringValue = enumValue.value;
			values.add(stringValue);
		}
		return values;
	}

	//#end region

}
