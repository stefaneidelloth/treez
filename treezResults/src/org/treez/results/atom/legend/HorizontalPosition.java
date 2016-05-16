package org.treez.results.atom.legend;

import java.util.ArrayList;
import java.util.List;

import org.treez.core.atom.attribute.EnumValueProvider;

public enum HorizontalPosition implements EnumValueProvider<HorizontalPosition> {

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

	@Override
	public HorizontalPosition fromString(final String value) {
		return valueOf(value.toUpperCase().replace('-', '_'));
	}

	@Override
	public List<String> getValues() {
		List<String> values = new ArrayList<>();
		for (HorizontalPosition enumValue : values()) {
			String stringValue = enumValue.value;
			values.add(stringValue);
		}
		return values;
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
