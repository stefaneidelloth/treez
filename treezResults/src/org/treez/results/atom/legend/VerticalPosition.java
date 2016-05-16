package org.treez.results.atom.legend;

import java.util.ArrayList;
import java.util.List;

import org.treez.core.atom.attribute.EnumValueProvider;

public enum VerticalPosition implements EnumValueProvider<VerticalPosition> {

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

	@Override
	public VerticalPosition fromString(final String value) {
		return valueOf(value.toUpperCase().replace('-', '_'));
	}

	@Override
	public List<String> getValues() {
		List<String> values = new ArrayList<>();
		for (VerticalPosition enumValue : values()) {
			String stringValue = enumValue.value;
			values.add(stringValue);
		}
		return values;
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
