package org.treez.core.data.table;

/**
 * Provides the information that is required to define a table source
 */
public interface TableSource {

	/**
	 * Returns the TableSourceType
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
	 */
	String getSourceFilePath();

	/**
	 * Returns the column separator for text file based table sources (e.g. "," for CSV)
	 */
	String getColumnSeparator();

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
	String getTableName();

	/**
	 * If the table source is used for a parameter variation, the data can be filtered by the job of the parameter
	 * variation. If the job filter is enabled this will return true.
	 */
	Boolean isFilteringForJob();

	String getJobName();

	void setJobName(String jobName);

	Boolean isUsingCustomQuery();

	String getCustomQuery();

}
