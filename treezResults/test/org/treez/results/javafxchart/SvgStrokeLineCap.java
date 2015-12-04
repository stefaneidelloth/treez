package org.treez.results.javafxchart;

/**
 * Defines how line edges look like, see http://www.w3.org/TR/SVG/painting.html#StrokeProperties
 */
public enum SvgStrokeLineCap {

	//#region VALUES

	/**
	 * The edge is not rounded and the line ends exactly at the line coordinates
	 */
	BUTT,

	/**
	 * The edges are rounded and extend a half line width beyond the line coordinates
	 */
	ROUND,

	/**
	 * The edges are not rounded and extend a half line width beyond the line coordinates
	 */
	SQUARE,

	/**
	 * The node uses the line cap of its parent node
	 */
	INHERIT;

	//#end region

	//#region ATTRIBUTES

	//#end region

	//#region CONSTRUCTORS

	//#end region
}
