package org.treez.results.atom.legend;

import java.util.ArrayList;
import java.util.List;

import org.treez.core.atom.attribute.EnumValueProvider;

public enum PositionReference implements EnumValueProvider<PositionReference> {

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

	@Override
	public PositionReference fromString(final String value) {
		return valueOf(value.toUpperCase().replace('-', '_'));
	}

	@Override
	public List<String> getValues() {
		List<String> values = new ArrayList<>();
		for (PositionReference enumValue : values()) {
			String stringValue = enumValue.value;
			values.add(stringValue);
		}
		return values;
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
