package org.treez.core.atom.attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents fill styles
 */
public enum FillStyleValue {

	//#region VALUES

	/**
	 *
	 */
	SOLID("solid"),

	/**
	 *
	 */
	VERTICAL("vertical"),

	/**
	 *
	 */
	HORIZONTAL("horizontal"),

	/**
	 *
	 */
	CROSS("cross");

	//#end region

	//#region ATTRIBUTES

	private String stringValue;

	//#end region

	//#region CONSTRUCTORS

	FillStyleValue(String stringValue) {
		this.stringValue = stringValue;
	}

	//#end region

	//#region ACCESSORS

	@Override
	public String toString() {
		return stringValue;
	}

	/**
	 * Returns a set of all fill styles as strings
	 * 
	 * @return
	 */
	public static List<String> getAllStringValues() {
		List<String> allStringValues = new ArrayList<>();

		FillStyleValue[] allValues = FillStyleValue.values();
		for (FillStyleValue fillStyleValue : allValues) {
			allStringValues.add(fillStyleValue.toString());
		}
		return allStringValues;
	}

	//#end region
}
