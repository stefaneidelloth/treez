package org.treez.core.quantity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.treez.core.atom.copy.Copiable;

/**
 * Represents a physical quantity with a value and a unit
 */
public class Quantity implements Copiable<Quantity> {

	//#region ATTRIBUTES

	/**
	 * The filePath of the physical quantity, saved as string
	 */
	private String value;

	/**
	 * The unit
	 */
	private String unit;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor with string
	 *
	 * @param value
	 * @param unit
	 */
	public Quantity(String value, String unit) {
		this.value = value;
		this.unit = unit;
	}

	/**
	 * Constructor with double value
	 *
	 * @param value
	 * @param unit
	 */
	public Quantity(Double value, String unit) {
		this.value = "" + value;
		this.unit = unit;
	}

	/**
	 * Copy constructor
	 *
	 * @param quantityToCopy
	 */
	public Quantity(Quantity quantityToCopy) {
		this.value = quantityToCopy.value;
		this.unit = quantityToCopy.unit;
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public Quantity copy() {
		return new Quantity(this);
	}

	//#end region

	/**
	 * Creates a list of Quantity for the given list of Double values and the
	 * given unit that is the same for all Double values.
	 *
	 * @param valueList
	 * @param unit
	 * @return
	 */
	public static List<Quantity> createQuantityList(List<Double> valueList,
			String unit) {
		Objects.requireNonNull(valueList, "Value list must not be null.");
		List<Quantity> quantities = new ArrayList<>();
		for (Double value : valueList) {
			Quantity currentQuantity = new Quantity(value, unit);
			quantities.add(currentQuantity);
		}
		return quantities;
	}

	/**
	 * Checks if all given quantities have the same unit and collects the values
	 * to a double list
	 *
	 * @param quantities
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static List<Double> createDoubleList(List<Quantity> quantities)
			throws IllegalArgumentException {
		List<Double> doubleList = new ArrayList<Double>();
		boolean isEmpty = quantities.isEmpty();
		if (isEmpty) {
			return doubleList;
		} else {

			String firstUnit = quantities.get(0).getUnit();
			for (Quantity quantity : quantities) {
				String unit = quantity.getUnit();
				boolean hasSameUnit = unit.equals(firstUnit);
				if (hasSameUnit) {
					Double value = quantity.getDoubleValue();
					doubleList.add(value);
				} else {
					String message = "The unit of all quantities has to be the same";
					throw new IllegalArgumentException(message);
				}
			}

			return doubleList;
		}
	}

	//#end region

	//#region ACCESSORS

	/**
	 * Gets the value
	 *
	 * @return
	 */
	public String getValue() {
		return value;
	}

	Double getDoubleValue() {
		return Double.parseDouble(value);
	}

	/**
	 * Gets the unit
	 *
	 * @return
	 */
	public String getUnit() {
		return unit;
	}

	@Override
	public String toString() {
		String quantityString = "" + value;
		if (unit != null && !unit.equals("")) {
			quantityString += " " + unit;
		}
		return quantityString;
	}

	//#end region

}
