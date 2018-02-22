package org.treez.study.atom.exportStudyInfo;

public enum StudyInfoExportType {

	//#region VALUES

	DISABLED("Disabled"),

	TEXT_FILE("Text file *.txt"),

	SQLITE("SqLite database *.sqlite"),

	MYSQL("MySQL database");

	//#end region

	//#region ATTRIBUTES

	private String label;

	//#end region

	//#region CONSTRUCTORS

	StudyInfoExportType(String label) {
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
