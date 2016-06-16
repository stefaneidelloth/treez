package org.treez.results.atom.axis;

import java.util.ArrayList;
import java.util.List;

import org.treez.javafxd3.javafx.EnumValueProvider;

public enum AxisMode implements EnumValueProvider<AxisMode> {

	//#region VALUES

	QUANTITATIVE("quantitative"), //
	ORDINAL("ordinal"), //
	TIME("time");

	//#end region

	//#region ATTRIBUTES

	private String value;

	//#end region

	//#region CONSTRUCTORS

	AxisMode(String value) {
		this.value = value;
	}

	//#end region

	//#region METHODS

	@Override
	public String toString() {
		return value;
	}

	@Override
	public AxisMode fromString(final String value) {
		return valueOf(value.toUpperCase().replace("-", "_"));
	}

	public static AxisMode from(final String value) {
		return valueOf(value.toUpperCase().replace("-", "_"));
	}

	@Override
	public List<String> getValues() {
		List<String> values = new ArrayList<>();
		for (AxisMode enumValue : values()) {
			String stringValue = enumValue.value;
			values.add(stringValue);
		}
		return values;
	}

	//#end region

}
