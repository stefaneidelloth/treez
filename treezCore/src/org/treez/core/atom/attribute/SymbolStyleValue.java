package org.treez.core.atom.attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents line styles
 */
public enum SymbolStyleValue {

	//#region VALUES

	/**
	 *
	 */
	NONE("none"),

	/**
	 *
	 */
	CIRCLE("circle"),

	/**
	 *
	 */
	DIAMOND("diamond"),

	/**
	 *
	 */
	SQUARE("square"),

	/**
	 *
	 */
	CROSS("cross"),

	/**
	 *
	 */
	PLUS("plus");

	//	/**
	//	 * 
	//	 */
	//	STAR("star"),
	//	
	//	/**
	//	 * 
	//	 */
	//	BARHORZ("barhorz"), 
	//	
	//	/**
	//	 * 
	//	 */
	//	BARVERT("barvert"), 
	//	
	//	/**
	//	 * 
	//	 */
	//	PENTAGON("pentagon"),
	//	
	//	/**
	//	 * 
	//	 */
	//	HEXAGON("hexagon"), 
	//	
	//	/**
	//	 * 
	//	 */
	//	OCTAGON("octagon"), 
	//	
	//	/**
	//	 * 
	//	 */
	//	TIEVERT("tievert"), 
	//	
	//	/**
	//	 * 
	//	 */
	//	TIEHORZ("tiehorz"), 
	//	
	//	/**
	//	 * 
	//	 */
	//	TRIANGLE("triangle"),
	//	
	//	/**
	//	 * 
	//	 */
	//	TRIANGLE_DOWN("triangledown"),
	//	
	//	/**
	//	 * 
	//	 */
	//	TRIANGLE_LEFT("triangleleft"),
	//	
	//	/**
	//	 * 
	//	 */
	//	TRIANGLE_RIGHT("triangleright"), 
	//	
	//	/**
	//	 * 
	//	 */
	//	DOT("dot"),
	//	
	//	/**
	//	 * 
	//	 */
	//	CIRCLE_DOT("circledot"),
	//	
	//	/**
	//	 * 
	//	 */
	//	BULLS_EYE("bullseye"),
	//	
	//	/**
	//	 * 
	//	 */
	//	CIRCLE_HOLE("circlehole"),
	//	
	//	/**
	//	 * 
	//	 */
	//	SQUARE_HOLE("squarehole"), 
	//	
	//	/**
	//	 * 
	//	 */
	//	DIAMOND_HOLE("diamondhole"), 
	//	
	//	/**
	//	 * 
	//	 */
	//	PENTAGON_HOLE("pentagonhole"), 
	//	
	//	/**
	//	 * 
	//	 */
	//	SQUARE_ROUNDED("squarerounded"), 
	//	
	//	/**
	//	 * 
	//	 */
	//	SQUASH_BOX("squashbox"),
	//	
	//	/**
	//	 * 
	//	 */
	//	ELLIPSE_HORZ("ellipsehorz"),
	//	
	//	/**
	//	 * 
	//	 */
	//	ELLIPSE_VERT("ellipsevert"),
	//	
	//	/**
	//	 * 
	//	 */
	//	LOSENGE_HORZ("losengehorz"), 
	//	
	//	/**
	//	 * 
	//	 */
	//	LOSENGE_VERT("losengevert"),
	//		
	//	/**
	//	 * 
	//	 */
	//	PLUS_NARROW("plusnarrow"),
	//	
	//	/**
	//	 * 
	//	 */
	//	CROSS_NARROW("crossnarrow");

	//#end region

	//#region ATTRIBUTES

	private String stringValue;

	//#end region

	//#region CONSTRUCTORS

	SymbolStyleValue(String stringValue) {
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

		SymbolStyleValue[] allValues = SymbolStyleValue.values();
		for (SymbolStyleValue lineStyleValue : allValues) {
			allStringValues.add(lineStyleValue.toString());
		}
		return allStringValues;
	}

	//#end region
}
