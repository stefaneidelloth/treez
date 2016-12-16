package org.treez.model.atom.tableImport;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.treez.core.data.column.ColumnType;
import org.treez.data.sqlite.ResultSetProcessor;
import org.treez.data.sqlite.SqLiteDatabase;

public final class SqLiteDataTableImporter {

	private static Logger LOG = Logger.getLogger(SqLiteDataTableImporter.class);

	//#region CONSTRUCTORS

	/**
	 * Private Constructor that prevents construction
	 */
	private SqLiteDataTableImporter() {}

	//#end region

	//#region METHODS

	/**
	 * @param filePath
	 * @param password
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

		//read headers
		List<String> headers = readHeaders(filePath, password, tableName);

		//read data
		List<List<String>> data = readData(filePath, password, tableName, filterRowsByJobId, jobId, rowLimit, 0,
				headers);

		TableData tableData = new TableData() {

			@Override
			public List<String> getHeaderData() {
				return headers;
			}

			@Override
			public ColumnType getColumnType(String header) {
				return ColumnType.TEXT;
			}

			@Override
			public List<List<String>> getRowData() {
				return data;
			}
		};

		return tableData;
	}

	private static List<String> readHeaders(String filePath, String password, String tableName) {
		SqLiteDatabase database = new SqLiteDatabase(filePath);
		String structureQuery = "PRAGMA table_info('" + tableName + "');";

		List<String> columnNames = new ArrayList<>();

		ResultSetProcessor processor = (ResultSet resultSet) -> {
			while (resultSet.next()) {
				String columnName = resultSet.getString("name"); //available columns: cid, name, notnull, dflt_value, pk
				columnNames.add(columnName);
			}
		};
		database.executeAndProcess(structureQuery, processor);

		return columnNames;
	}

	private static List<List<String>> readData(
			String filePath,
			String password,
			String tableName,
			boolean filterRowsByJobId,
			String jobId,
			int rowLimit,
			int rowOffset,
			List<String> headers) {
		SqLiteDatabase database = new SqLiteDatabase(filePath);
		String dataQuery = "SELECT * FROM '" + tableName + "'";

		boolean applyFilter = filterRowsByJobId && jobId != null;
		if (applyFilter) {
			dataQuery += " WHERE job_id = '" + jobId + "'";
		}

		dataQuery += " LIMIT " + rowLimit + " OFFSET " + rowOffset + ";";

		List<List<String>> data = new ArrayList<>();
		ResultSetProcessor processor = (ResultSet resultSet) -> {
			while (resultSet.next()) {
				List<String> rowData = new ArrayList<>();
				for (String header : headers) {
					Object entry = resultSet.getObject(header);
					rowData.add(entry.toString());
				}
				data.add(rowData);
			}
		};
		database.executeAndProcess(dataQuery, processor);

		if (data.isEmpty()) {
			String message = "Could not find any rows";
			if (applyFilter) {
				message += " for jobId '" + jobId + "'";
			}
			LOG.warn(message);

		}

		return data;
	}

	//#end region

}
