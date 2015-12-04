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
	NONE("none"),

	/**
	 *
	 */
	SOLID("solid"),

	/**
	 *
	 */
	DASHED("dashed"),

	/**
	 *
	 */
	DOTTED("dotted"),

	/**
	 *
	 */
	DASH_DOT("dash-dot"),

	/**
	 *
	 */
	DASH_DOT_DOT("dash-dot-dot"),

	/**
	 *
	 */
	DOTTED_FINE("dotted-fine"),

	/**
	 *
	 */
	DASHED_FINE("dashed-fine"),

	/**
	 *
	 */
	DASH_DOT_FINE("dash-dot-fine"),

	/**
	 *
	 */
	DOT1("dot1"),

	/**
	 *
	 */
	DOT2("dot2"),

	/**
	 *
	 */
	DOT3("dot3"),

	/**
	 *
	 */
	DOT4("dot4"),

	/**
	 *
	 */
	DASH1("dash1"),

	/**
	 *
	 */
	DASH2("dash2"),

	/**
	 *
	 */
	DASH3("dash3"),

	/**
	 *
	 */
	DASH4("dash4"),

	/**
	 *
	 */
	DASH5("dash5"),

	/**
	 *
	 */
	DASHDOT1("dashdot1"),

	/**
	 *
	 */
	DASHDOT2("dashdot2"),

	/**
	 *
	 */
	DASHDOT3("dashdot3");

	//#end region

	//#region ATTRIBUTES

	private String stringValue;

	//#end region

	//#region CONSTRUCTORS

	LineStyleValue(String stringValue) {
		this.stringValue = stringValue;
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

	//#end region
}
