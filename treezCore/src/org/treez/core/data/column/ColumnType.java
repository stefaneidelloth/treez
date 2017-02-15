package org.treez.core.data.column;

import java.util.ArrayList;
import java.util.List;

/**
 * Column types for the TableEditor
 */
@SuppressWarnings("javadoc")
public enum ColumnType {

	//#region VALUES

	INTEGER("INTEGER", Integer.class),

	REAL("REAL", Double.class),

	TEXT("TEXT", String.class),

	BLOB("BLOB", Object.class),

	DOUBLE("Double", Double.class),

	BOOLEAN("Boolean", Boolean.class),

	COLOR("Color", String.class),

	ENUM("Enum", String.class);

	//#end region

	//#region ATTRIBUTES

	/**
	 * An alternative string value for the enum
	 */
	private String stringRepresentation;

	/**
	 * An associated class that can be used to store the values
	 */
	private Class<?> associatedClass;

	//#end region

	//#region CONSTRUCTORS

	ColumnType(String stringRepresentation, Class<?> associatedClass) {
		this.stringRepresentation = stringRepresentation;
		this.associatedClass = associatedClass;
	}

	//#end region

	//#region ACCESSORS

	/**
	 * Get the string representation
	 *
	 * @return
	 */
	public String getValue() {
		return stringRepresentation;
	}

	/**
	 * Gets the type for the given string representation
	 *
	 * @param type
	 * @return
	 */
	public static ColumnType getType(String type) {
		for (ColumnType columnType : values()) {
			String value = columnType.getValue();
			//LOG.debug("testing for column type " + value);
			boolean isWantedType = value.equals(type);
			if (isWantedType) {
				return columnType;
			}
		}
		throw new IllegalArgumentException("Column type '" + type + "' could not be found.");
	}

	/**
	 * Gets an associated class that can be used to represent the column type
	 *
	 * @return
	 */
	public Class<?> getAssociatedClass() {
		return associatedClass;
	}

	public static String getAllValuesAsString() {
		String allValues = "";
		for (ColumnType columnType : values()) {
			String value = columnType.getValue();
			allValues = allValues + value + ",";
		}
		return allValues.substring(0, allValues.length() - 1);
	}

	/**
	 * Returns true if the given string represents a column type
	 *
	 * @param columnTypeString
	 * @return
	 */
	public static boolean representsType(String columnTypeString) {
		for (ColumnType columnType : values()) {
			String currentTypeString = columnType.getValue();
			boolean isWantedType = currentTypeString.equals(columnTypeString);
			if (isWantedType) {
				return true;
			}
		}
		return false;
	}

	public static List<ColumnType> getAllowedTypesForClass(Class<?> xType) {

		List<ColumnType> allowedTypes = new ArrayList<>();
		boolean isEnum = xType.isEnum();
		if (isEnum) {
			allowedTypes.add(ENUM);
			allowedTypes.add(TEXT);
			allowedTypes.add(BLOB);
			return allowedTypes;

		}

		String className = xType.getSimpleName();
		switch (className) {
		case "Integer":
			allowedTypes.add(INTEGER);
			allowedTypes.add(BLOB);
			return allowedTypes;
		case "Double":
			allowedTypes.add(REAL);
			allowedTypes.add(BLOB);
			return allowedTypes;
		case "String":
			allowedTypes.add(TEXT);
			allowedTypes.add(BLOB);
			return allowedTypes;
		default:
			allowedTypes.add(BLOB);
			return allowedTypes;
		}

	}

	public static ColumnType getDefaultTypeForClass(Class<?> xType) {

		boolean isEnum = xType.isEnum();
		if (isEnum) {
			return TEXT;
		}

		String className = xType.getSimpleName();
		switch (className) {
		case "Integer":
			return INTEGER;
		case "Float":
			return REAL;
		case "Double":
			return REAL;
		case "String":
			return TEXT;
		default:
			return BLOB;
		}

	}
	//#end region

}
