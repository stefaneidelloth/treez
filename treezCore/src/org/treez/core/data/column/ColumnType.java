package org.treez.core.data.column;

public enum ColumnType {

	//#region VALUES

	INTEGER(Integer.class),

	DOUBLE(Double.class),

	STRING(String.class);

	//#end region

	//#region ATTRIBUTES

	private Class<?> associatedClass;

	//#end region

	//#region CONSTRUCTORS

	ColumnType(Class<?> associatedClass) {
		this.associatedClass = associatedClass;
	}

	//#end region

	//#region METHODS

	public static boolean representsAColumnType(String columnTypeName) {
		for (ColumnType columnType : ColumnType.values()) {
			if (columnType.name().equals(columnTypeName)) {
				return true;
			}
		}

		return false;
	}

	public static ColumnType getType(String columnTypeName) {
		for (ColumnType columnType : ColumnType.values()) {
			if (columnType.name().equals(columnTypeName)) {
				return columnType;
			}
		}
		throw new IllegalArgumentException("Unknown column type name '" + columnTypeName + "'.");
	}

	public static ColumnType getType(Class<?> associatedClass) {
		for (ColumnType columnType : ColumnType.values()) {
			if (columnType.associatedClass.equals(associatedClass)) {
				return columnType;
			}
		}
		throw new IllegalArgumentException(
				"The class '" + associatedClass.getSimpleName() + "' is not associated to a column type.");
	}

	//#end region

	//#region ACCESSORS

	public Class<?> getAssociatedClass() {
		return associatedClass;
	}

	public boolean isNumeric() {
		return !this.equals(STRING);
	}

	//#end region

}
