package org.treez.results.length;

import java.util.Objects;

/**
 * Represents a geometric length consisting of a value and a LengthUnit.
 */
public class Length {

	//#region ATTRIBUTES

	private Double value;

	private LengthUnit unit;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param value
	 * @param unit
	 */
	public Length(Double value, LengthUnit unit) {
		this.value = value;
		this.unit = unit;
	}

	/**
	 * Constructor with unit as string
	 *
	 * @param value
	 * @param unitString
	 */
	public Length(Double value, String unitString) {
		this.value = value;
		this.unit = LengthUnit.get(unitString);
	}

	//#end region

	//#region METHODS

	/**
	 * Tries to parse the given String (e.g. "1cm") to a Length. If the given String can not be parsed, and
	 * IllegalArgumentException is thrown.
	 *
	 * @param lengthExpression
	 * @return
	 */
	public static Length parse(String lengthExpression) {
		Objects.requireNonNull(lengthExpression, "Length expression must not be null");
		try {
			double value = Double.parseDouble(lengthExpression);
			return new Length(value, LengthUnit.PX);
		} catch (NumberFormatException exception) {
			Length length = parseFullLength(lengthExpression);
			return length;
		}
	}

	private static Length parseFullLength(String lengthExpression) {

		String expression = lengthExpression.trim().replace(" ", "");
		String unitRegularExpression = "[a-zA-Z]{2,}";
		String[] subStrings = expression.split(unitRegularExpression);

		if (subStrings.length != 1) {
			throw new IllegalArgumentException("Could not parse the length expression '" + lengthExpression + "'");
		}

		String valueString = subStrings[0];
		String unitString = expression.substring(valueString.length());
		try {
			Double value = Double.parseDouble(valueString);
			LengthUnit unit = LengthUnit.get(unitString);
			return new Length(value, unit);
		} catch (NumberFormatException exception) {
			throw new IllegalArgumentException("Could not parse the length expression '" + lengthExpression + "'");
		}
	}

	/**
	 * Parses the given length expression as Length and returns its value in px (assuming a default resolution of 96
	 * dpi)
	 *
	 * @return
	 */
	public static Double toPx(String lengthExpression) {
		Length length = parse(lengthExpression);
		Double pxValue = length.getPx();
		return pxValue;
	}

	/**
	 * Returns the value of the Length in px (assuming a default resolution of 96 dpi)
	 *
	 * @return
	 */
	public Double getPx() {
		Double pxValue = value * unit.getConversionFactor();
		return pxValue;
	}

	/**
	 * Returns the value of the Length in the given LengthUnit (assuming a default resolution of 96 dpi)
	 *
	 * @return
	 */
	public Double getValue(LengthUnit unit) {

		if (unit.equals(this.unit)) {
			return value;
		}

		Double pxValue = getPx();
		Double result = pxValue / unit.getConversionFactor();
		return result;
	}

	@Override
	public String toString() {
		String lengthString = "" + value + unit;
		return lengthString;
	}

	//#end region

	//#region ACCESSORS

	/**
	 * @return
	 */
	public Double getValue() {
		return value;
	}

	/**
	 * @param value
	 */
	public void setValue(Double value) {
		this.value = value;
	}

	/**
	 * @return
	 */
	public LengthUnit getUnit() {
		return unit;
	}

	/**
	 * @param unit
	 */
	public void setUnit(LengthUnit unit) {
		this.unit = unit;
	}

	//#end region

}
