package org.treez.core.atom.graphics.length;

import java.util.Objects;

/**
 * The possible units for geometric length that are used with treez graphics
 */
public enum LengthUnit {

	//#region VALUES

	/**
	 * centimeter
	 */
	CM("cm", LengthUnit.DEFAULT_RESOLUTION / 2.54),

	/**
	 * millimeter
	 */
	MM("mm", LengthUnit.DEFAULT_RESOLUTION / 25.4),

	/**
	 * inch
	 */
	IN("in", LengthUnit.DEFAULT_RESOLUTION),

	/**
	 * points
	 */
	PT("pt", LengthUnit.DEFAULT_RESOLUTION / 72),

	/**
	 * pixels
	 */
	PX("px", 1);

	//#end region

	//#region ATTRIBUTES

	private static final int DEFAULT_RESOLUTION = 96; //dpi

	private String equivalentString;

	/**
	 * The conversion factor to calculate the length in px (assuming the default resolution of 96 dpi)
	 */
	private double conversionFactor;

	//#end region

	//#region CONSTRUCTORS

	LengthUnit(String equivalentString, double conversionFactor) {
		this.equivalentString = equivalentString;
		this.conversionFactor = conversionFactor;
	}

	//#end region

	//#region METHODS

	/**
	 * Gets the LengthUnit for the given equivalent string
	 *
	 * @param unitString
	 * @return
	 */
	public static LengthUnit get(String unitString) {
		Objects.requireNonNull(unitString, "UnitString must not be null");
		for (LengthUnit unit : LengthUnit.values()) {
			String currentUnitString = unit.equivalentString;
			boolean isWantedUnit = unitString.toLowerCase().equals(currentUnitString);
			if (isWantedUnit) {
				return unit;
			}
		}

		String message = "The unit " + unitString + " is not yet implemented";
		throw new IllegalArgumentException(message);
	}

	@Override
	public String toString() {
		return equivalentString;
	}

	//#end region

	//#region ACCESSORS

	/**
	 * Returns the conversion factor for calculating length in px (assuming the default resolution of 96 dpi)
	 *
	 * @return
	 */
	public double getConversionFactor() {
		return conversionFactor;
	}

	//#end region
}
