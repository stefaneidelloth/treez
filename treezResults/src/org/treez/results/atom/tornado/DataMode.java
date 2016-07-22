package org.treez.results.atom.tornado;

import java.util.ArrayList;
import java.util.List;

import org.treez.javafxd3.javafx.EnumValueProvider;

public enum DataMode implements EnumValueProvider<DataMode> {

	//#region VALUES

	TABLE("table"), //
	INDIVIDUAL_COLUMNS("individual columns");

	//#end region

	//#region ATTRIBUTES

	private String value;

	//#end region

	//#region CONSTRUCTORS

	DataMode(String value) {
		this.value = value;
	}

	//#end region

	//#region METHODS

	@Override
	public String toString() {
		return value;
	}

	@Override
	public DataMode fromString(final String value) {
		return valueOf(value.toUpperCase().replace("-", "_"));
	}

	@Override
	public List<String> getValues() {
		List<String> values = new ArrayList<>();
		for (DataMode enumValue : values()) {
			String stringValue = enumValue.value;
			values.add(stringValue);
		}
		return values;
	}

	//#end region

}
