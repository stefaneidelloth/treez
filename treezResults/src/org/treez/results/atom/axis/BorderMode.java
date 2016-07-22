package org.treez.results.atom.axis;

import java.util.ArrayList;
import java.util.List;

import org.treez.javafxd3.javafx.EnumValueProvider;

public enum BorderMode implements EnumValueProvider<BorderMode> {

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

	@Override
	public BorderMode fromString(final String value) {
		return from(value);
	}

	public static BorderMode from(final String value) {

		for (BorderMode mode : BorderMode.values()) {
			if (mode.value.equals(value)) {
				return mode;
			}
		}
		throw new IllegalStateException("Could not find value " + value);

	}

	@Override
	public List<String> getValues() {
		List<String> values = new ArrayList<>();
		for (BorderMode enumValue : values()) {
			String stringValue = enumValue.value;
			values.add(stringValue);
		}
		return values;
	}

	//#end region

	//#region ACCESSORS

	public Double getFactor() {
		return factor;
	}

	//#end region

}
