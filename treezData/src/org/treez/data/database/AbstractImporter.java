package org.treez.data.database;

public abstract class AbstractImporter {

	//#region ATTRIBUTES

	protected static String JOB_ID_PLACEHOLDER = "{$jobId$}";

	//#end region

	//#region METHODS

	protected static String removeTrailingSemicolon(String customQuery) {

		int length = customQuery.length();
		if (length < 1) {
			return customQuery;
		}

		boolean endsWithSemicolon = customQuery.substring(length - 1).equals(";");
		if (endsWithSemicolon) {
			return customQuery.substring(0, length - 1);
		} else {
			return customQuery;
		}
	}

	protected static String injectjobNameIfIncludesPlaceholder(String customQuery, String jobName) {
		return customQuery.replace(JOB_ID_PLACEHOLDER, jobName);
	}

	//#end region

}
