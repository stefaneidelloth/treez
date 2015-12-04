package org.treez.core.atom.attribute;

import org.treez.core.data.column.ColumnType;

/**
 * An item example
 */
public class ColumnTypeComboBox extends AbstractComboBox {

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public ColumnTypeComboBox(String name) {
		super(name);
		this.setItems(ColumnType.TEXT);
	}

	/**
	 * Copy constructor
	 *
	 * @param comboBoxToCopy
	 */
	public ColumnTypeComboBox(AbstractComboBox comboBoxToCopy) {
		super(comboBoxToCopy);
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public ColumnTypeComboBox copy() {
		return new ColumnTypeComboBox(this);
	}

	//#end region

	//#end region

	//#region ACCESSORS

	@Override
	public void set(String columnTypeString) {
		boolean isColumnType = ColumnType.representsType(columnTypeString);
		if (isColumnType) {
			super.setValue(columnTypeString);
		} else {
			String message = "The specified column type '" + columnTypeString + "' is not known.";
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Sets the column type
	 *
	 * @param columnTypeToSet
	 */
	public void set(ColumnType columnTypeToSet) {
		set(columnTypeToSet.getValue());
	}

	/**
	 * Returns the column type
	 *
	 * @return
	 */
	public ColumnType getType() {
		String columnTypeString = get();
		ColumnType columnTypeValue = ColumnType.getType(columnTypeString);
		return columnTypeValue;
	}

	/**
	 * @param defaultValue
	 */
	@Override
	public void setDefaultValue(String defaultValue) {
		boolean valueAllowed = ColumnType.representsType(defaultValue);
		if (valueAllowed) {
			this.defaultValue = defaultValue;
		} else {
			String message = "The defaultValue '" + defaultValue + "' is not known.";
			throw new IllegalArgumentException(message);
		}

	}

	/**
	 * Sets the default value with an enum
	 *
	 * @param defaultEnumValue
	 */
	@Override
	public void setDefaultValue(Enum<?> defaultEnumValue) {
		boolean isColumnType = defaultEnumValue.getClass().equals(ColumnType.class);
		if (isColumnType) {
			ColumnType columnType = (ColumnType) defaultEnumValue;
			set(columnType);
		} else {
			String message = "The value " + defaultEnumValue + " is no ColumnType.";
			throw new IllegalArgumentException(message);
		}

	}

	//#end region

}
