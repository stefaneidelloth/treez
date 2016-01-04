package org.treez.core.atom.attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents line styles
 */
public enum LineStyleValue {

	//#region VALUES

	/**
	 *
	 */
	NONE("none", null),

	/**
	 *
	 */
	SOLID("solid", "1,0"),

	/**
	 *
	 */
	DASHED("dashed", "6,2"),

	/**
	 *
	 */
	DOTTED("dotted", "2,2"),

	/**
	 *
	 */
	DASH_DOT("dash-dot", "6,2,2,2"),

	/**
	 *
	 */
	DASH_DOT_DOT("dash-dot-dot", "6,2,2,2,2,2"),

	/**
	 *
	 */
	DOTTED_FINE("dotted-fine", "3,5"),

	/**
	 *
	 */
	DASHED_FINE("dashed-fine", "11,5"),

	/**
	 *
	 */
	DASH_DOT_FINE("dash-dot-fine", "10,6,2,6"),

	/**
	 *
	 */
	DOT1("dot1", "2,1"),

	/**
	 *
	 */
	DOT2("dot2", "2,4"),

	/**
	 *
	 */
	DOT3("dot3", "2,7"),

	/**
	 *
	 */
	DOT4("dot4", "2,9"),

	/**
	 *
	 */
	DASH1("dash1", "6,5"),

	/**
	 *
	 */
	DASH2("dash2", "6,24"),

	/**
	 *
	 */
	DASH3("dash3", "11,8"),

	/**
	 *
	 */
	DASH4("dash4", "15,8"),

	/**
	 *
	 */
	DASH5("dash5", "15,15"),

	/**
	 *
	 */
	DASHDOT1("dashdot1", "7,4,2,4"),

	/**
	 *
	 */
	DASHDOT2("dashdot2", "13,4,2,4"),

	/**
	 *
	 */
	DASHDOT3("dashdot3", "5,2,2,2");

	//#end region

	//#region ATTRIBUTES

	private String stringValue;

	private String dashArray;

	//#end region

	//#region CONSTRUCTORS

	LineStyleValue(String stringValue, String dashArray) {
		this.stringValue = stringValue;
		this.dashArray = dashArray;
	}

	//#end region

	//#region ACCESSORS

	@Override
	public String toString() {
		return stringValue;
	}

	/**
	 * Returns a set of all line styles as strings
	 *
	 * @return
	 */
	public static List<String> getAllStringValues() {
		List<String> allStringValues = new ArrayList<>();

		LineStyleValue[] allValues = LineStyleValue.values();
		for (LineStyleValue lineStyleValue : allValues) {
			allStringValues.add(lineStyleValue.toString());
		}
		return allStringValues;
	}

	/**
	 * @param value
	 * @return
	 */
	public static LineStyleValue fromString(String value) {

		for (LineStyleValue lineStyleValue : values()) {
			boolean isWantedValue = lineStyleValue.stringValue.equals(value);
			if (isWantedValue) {
				return lineStyleValue;
			}
		}
		String message = "The value '" + value + "' is not known.";
		throw new IllegalArgumentException(message);
	}

	/**
	 * @return
	 */
	public String getDashArray() {
		return dashArray;
	}

	//#end region
}
