package org.treez.results.atom.axis;

import java.util.ArrayList;
import java.util.List;

import org.treez.javafxd3.javafx.EnumValueProvider;

public enum Direction implements EnumValueProvider<Direction> {

	//#region VALUES

	VERTICAL("vertical"),

	HORIZONTAL("horizontal");

	//#end region

	//#region ATTRIBUTES

	private String value;

	//#end region

	//#region CONSTRUCTORS

	Direction(String directionString) {
		this.value = directionString;
	}

	//#end region

	//#region METHODS

	@Override
	public String toString() {
		return value;
	}

	@Override
	public Direction fromString(final String value) {
		return valueOf(value.toUpperCase().replace('-', '_'));
	}

	@Override
	public List<String> getValues() {
		List<String> values = new ArrayList<>();
		for (Direction enumValue : values()) {
			String stringValue = enumValue.value;
			values.add(stringValue);
		}
		return values;
	}

	//#end region
}
