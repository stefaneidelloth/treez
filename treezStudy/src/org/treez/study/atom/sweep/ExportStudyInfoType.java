package org.treez.study.atom.sweep;

public enum ExportStudyInfoType {

	//#region VALUES

	DISABLED("disabled"),

	TEXT_FILE("txt file"),

	SQLITE("SqLite database"),

	MYSQL("MySQL database");

	//#end region

	//#region ATTRIBUTES

	private String label;

	//#end region

	//#region CONSTRUCTORS

	ExportStudyInfoType(String label) {
		this.label = label;
	}

	//#end region

	//#region METHODS

	@Override
	public String toString() {
		return label;
	}

	//#end region

}
