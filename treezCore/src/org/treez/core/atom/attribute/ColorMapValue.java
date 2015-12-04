package org.treez.core.atom.attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents color map values
 */
public enum ColorMapValue {

	//#region VALUES

	/**
	 *
	 */
	BLANK("blank"),

	/**
	 *
	 */
	BLUE("blue"),

	/**
	 *
	 */
	BLUE_GREEN("bluegreen"),

	/**
	 *
	 */
	BLUE_GREEN_STEP("bluegreen-step"),

	/**
	 *
	 */
	COMPLEMENT("complement"),

	/**
	 *
	 */
	COMPLEMENT_STEP("complement-step"),

	/**
	 *
	 */
	GREEN("green"),

	/**
	 *
	 */
	GREY("grey");

	//#end region

	//#region ATTRIBUTES

	private String stringValue;

	//#end region

	//#region CONSTRUCTORS

	ColorMapValue(String stringValue) {
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

		ColorMapValue[] allValues = ColorMapValue.values();
		for (ColorMapValue fillStyleValue : allValues) {
			allStringValues.add(fillStyleValue.toString());
		}
		return allStringValues;
	}

	//#end region
}
