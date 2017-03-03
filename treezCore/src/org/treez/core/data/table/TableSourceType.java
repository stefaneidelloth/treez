package org.treez.core.data.table;

/**
 * The source type for tables
 */
public enum TableSourceType {

	CSV("csv"),

	SQLITE("sqlite"),

	MYSQL("mysql");

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

	//#end region

}
