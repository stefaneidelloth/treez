package org.treez.core.data.table;

import java.util.ArrayList;
import java.util.List;

import org.treez.javafxd3.javafx.EnumValueProvider;

/**
 * The source type for tables
 */
public enum TableSourceType implements EnumValueProvider<TableSourceType> {

	/**
	 * A comma separated text file
	 */
	CSV("csv"),

	/**
	 * An excel file
	 */
	EXCEL("excel"),

	/**
	 * An SqLite database
	 */
	SQLITE("sqlite"),

	/**
	 * A MySQL database
	 */
	MYSQL("mysql"),

	/**
	 * A Microsoft Access file
	 */
	ACCESS("access");

	//#region ATTRIBUTES

	private String value;

	//#end region

	//#region CONSTRUCTORS

	TableSourceType(String value) {
		this.value = value;
	}

	//#end region

	//#region METHODS

	@Override
	public String toString() {
		return value;
	}

	@Override
	public TableSourceType fromString(final String value) {
		return valueOf(value.toUpperCase().replace('-', '_'));
	}

	@Override
	public List<String> getValues() {
		List<String> values = new ArrayList<>();
		for (TableSourceType enumValue : values()) {
			String stringValue = enumValue.value;
			values.add(stringValue);
		}
		return values;
	}

	//#end region

}
