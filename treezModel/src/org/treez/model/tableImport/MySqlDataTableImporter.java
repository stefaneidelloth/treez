package org.treez.model.tableImport;

/**
 * @author eis
 */
public final class MySqlDataTableImporter {

	//#region CONSTRUCTORS

	/**
	 * Private Constructor that prevents construction
	 */
	private MySqlDataTableImporter() {}

	//#end region

	//#region METHODS

	/**
	 * @param tableName
	 * @return
	 */
	public static TableData importData(
			String host,
			String port,
			String user,
			String password,
			String schemaName,
			String tableName,
			int rowLimit) {

		return null;
	}

	//#end region

	//#region ACCESSORS

	//#end region

}
