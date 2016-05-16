package org.treez.results.atom.xy;

import java.util.ArrayList;
import java.util.List;

import org.treez.core.atom.attribute.EnumValueProvider;

/**
 * Interpolation mode as used in D3.js
 */
public enum InterpolationMode implements EnumValueProvider<InterpolationMode> {

	//#region VALUES

	/**
	 * piecewise linear segments, as in a polyline.
	 */
	LINEAR("linear"),

	/**
	 * close the linear segments to form a polygon.
	 */
	LINEAR_CLOSED("linear-closed"),

	/**
	 * alternate between horizontal and vertical segments, as in a step function
	 */
	STEP("step"),
	/**
	 * alternate between vertical and horizontal segments, as in a step function.
	 */
	STEP_BEFORE("step-before"),

	/**
	 * alternate between horizontal and vertical segments, as in a step function
	 */
	STEP_AFTER("step-after"),

	/**
	 * a B-spline, with control point duplication on the ends.
	 */
	BASIS("basis"),
	/**
	 * an open B-spline; may not intersect the start or end.
	 */
	BASIS_OPEN("basis-open"),
	/**
	 * a closed B-spline, as in a loop.
	 */
	BASIS_CLOSED("basis-closed"),
	/**
	 * equivalent to basis, except the tension parameter is used to straighten the spline.
	 */
	BUNDLE("bundle"),
	/**
	 * a Cardinal spline, with control point duplication on the ends.
	 */
	CARDINAL("cardinal"),
	/**
	 * an open Cardinal spline; may not intersect the start or end, but will intersect other control points.
	 */
	CARDINAL_OPEN("cardinal-open"),
	/**
	 * a closed Cardinal spline, as in a loop.
	 */
	CARDINAL_CLOSED("cardinal-closed"),
	/**
	 * cubic interpolation that preserves monotonicity in y.
	 */
	MONOTONE("monotone");

	//#end region

	//#region ATTRIBUTES

	private final String value;

	//#end region

	//#region CONSTRUCTORS

	InterpolationMode(final String value) {
		this.value = value;
	}

	//#end region

	//#region METHODS

	public static InterpolationMode fromValue(final String value) {
		return valueOf(value.toUpperCase().replace('-', '_'));
	}

	@Override
	public InterpolationMode fromString(final String value) {
		return fromValue(value);
	}

	@Override
	public List<String> getValues() {
		List<String> values = new ArrayList<>();
		for (InterpolationMode interpolationMode : values()) {
			String stringValue = interpolationMode.value;
			values.add(stringValue);
		}
		return values;
	}

	@Override
	public String toString() {
		return value;
	}

	//#end region

}
