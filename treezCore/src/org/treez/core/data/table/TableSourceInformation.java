package org.treez.core.data.table;

/**
 * Provides the information that is required to define a table source
 */
public interface TableSourceInformation {

	/**
	 * Returns the TableSourceType
	 *
	 * @return
	 */
	TableSourceType getSourceType();

	/**
	 * If this is true, the source will be linked to the table. If this is false, only the data will be copied.
	 *
	 * @return
	 */
	boolean isLinked();

	/**
	 * Returns the file path for file based table sources
	 *
	 * @return
	 */
	String getSourceFilePath();

	/**
	 * Returns the host, e.g. localhost for server based table sources
	 *
	 * @return
	 */
	String getHost();

	/**
	 * Returns the port, e.g. 3066 for server based table sources
	 *
	 * @return
	 */
	String getPort();

	/**
	 * Returns the user for table sources that support user accounts
	 *
	 * @return
	 */
	String getUser();

	/**
	 * Returns the password for table sources that support password protection
	 *
	 * @return
	 */
	String getPassword();

	/**
	 * Returns the schema for table sources that include several schemata
	 *
	 * @return
	 */
	String getSchema();

	/**
	 * Returns the name of the table or sheet for table sources that include several tables/sheets
	 *
	 * @return
	 */
	String getTable();

}
