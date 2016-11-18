package org.treez.model.atom.tableImport;

public final class AccessDataTableImporter {

	//#region CONSTRUCTORS

	/**
	 * Private Constructor that prevents construction
	 */
	private AccessDataTableImporter() {}

	//#end region

	//#region METHODS

	/**
	 * @param filePath
	 * @param tableName
	 * @return
	 */
	public static TableData importData(
			String filePath,
			String password,
			String tableName,
			boolean filterRowsByJobId,
			String jobId,
			int rowLimit) {
		return null;
	}

	//#end region

}
