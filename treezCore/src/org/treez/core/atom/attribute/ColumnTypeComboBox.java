package org.treez.core.atom.attribute;

import org.treez.core.data.column.ColumnType;

public class ColumnTypeComboBox extends AbstractComboBox<ColumnTypeComboBox> {

	//#region CONSTRUCTORS

	public ColumnTypeComboBox(String name) {
		super(name);
		this.setItems(ColumnType.STRING);
	}

	/**
	 * Copy constructor
	 */
	public ColumnTypeComboBox(ColumnTypeComboBox comboBoxToCopy) {
		super(comboBoxToCopy);
	}

	//#end region

	//#region METHODS

	@Override
	public ColumnTypeComboBox copy() {
		return new ColumnTypeComboBox(this);
	}

	//#end region

	//#region ACCESSORS

	@Override
	public ColumnTypeComboBox getThis() {
		return this;
	}

	@Override
	public void set(String columnTypeString) {
		boolean isColumnType = ColumnType.representsAColumnType(columnTypeString);
		if (isColumnType) {
			super.setValue(columnTypeString);
		} else {
			String message = "The specified column type '" + columnTypeString + "' is not known.";
			throw new IllegalArgumentException(message);
		}
	}

	public void set(ColumnType columnTypeToSet) {
		set(columnTypeToSet.name());
	}

	public ColumnType getType() {
		String columnTypeName = get();
		ColumnType columnTypeValue = ColumnType.getType(columnTypeName);
		return columnTypeValue;
	}

	@Override
	public ColumnTypeComboBox setDefaultValue(String defaultValue) {
		boolean valueAllowed = ColumnType.representsAColumnType(defaultValue);
		if (valueAllowed) {
			this.defaultValue = defaultValue;
		} else {
			String message = "The defaultValue '" + defaultValue + "' is not known.";
			throw new IllegalArgumentException(message);
		}
		return getThis();

	}

	@Override
	public ColumnTypeComboBox setDefaultValue(Enum<?> defaultEnumValue) {
		boolean isColumnType = defaultEnumValue.getClass().equals(ColumnType.class);
		if (isColumnType) {
			ColumnType columnType = (ColumnType) defaultEnumValue;
			set(columnType);
		} else {
			String message = "The value " + defaultEnumValue + " is no ColumnType.";
			throw new IllegalArgumentException(message);
		}
		return getThis();

	}

	//#end region

}
