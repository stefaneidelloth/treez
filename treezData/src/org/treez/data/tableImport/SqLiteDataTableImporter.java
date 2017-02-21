package org.treez.data.tableImport;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.treez.core.data.column.ColumnBlueprint;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.foreignkey.ForeignKeyBlueprint;
import org.treez.core.data.index.IndexBlueprint;
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

	public static int getNumberOfRows(String filePath, String tableName) {

		SqLiteDatabase database = new SqLiteDatabase(filePath);
		String sizeQuery = "SELECT COUNT(*) FROM " + tableName + ";";

		int[] size = { 0 };
		ResultSetProcessor processor = (ResultSet resultSet) -> {
			resultSet.getFetchSize();
			while (resultSet.next()) {
				int result = resultSet.getInt("COUNT(*)");
				size[0] = result;
				return;
			}
		};
		database.executeAndProcess(sizeQuery, processor);

		return size[0];

	}

	public static TableData importData(
			String filePath,
			String password,
			String tableName,
			boolean filterRowsByJobId,
			String jobId,
			Integer rowLimit,
			Integer rowOffset) {

		//read headers
		List<String> headers = readHeaders(filePath, password, tableName);

		//read data
		List<List<Object>> data = readData(filePath, password, tableName, filterRowsByJobId, jobId, rowLimit, rowOffset,
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
			public List<List<Object>> getRowData() {
				return data;
			}
		};

		return tableData;
	}

	public static List<String> readHeaders(String filePath, String password, String tableName) {
		SqLiteDatabase database = new SqLiteDatabase(filePath);
		String structureQuery = "PRAGMA table_info('" + tableName + "');";

		List<String> columnNames = new ArrayList<>();

		ResultSetProcessor processor = (ResultSet resultSet) -> {
			while (resultSet.next()) {
				String columnName = resultSet.getString("name"); //available columns: cid, name, type, notnull, dflt_value, pk
				columnNames.add(columnName);
			}
		};
		database.executeAndProcess(structureQuery, processor);

		return columnNames;
	}

	public static List<ColumnBlueprint> readTableStructure(String filePath, String password, String tableName) {
		SqLiteDatabase database = new SqLiteDatabase(filePath);
		String structureQuery = "PRAGMA table_info('" + tableName + "');";

		List<ColumnBlueprint> tableStructure = new ArrayList<>();

		ResultSetProcessor processor = (ResultSet resultSet) -> {
			while (resultSet.next()) {
				String name = resultSet.getString("name"); //available columns: cid, name, notnull, dflt_value, pk

				ColumnType type = ColumnType.getType(resultSet.getString("type"));
				boolean isNullable = !resultSet.getBoolean("notnull");
				boolean isPrimaryKey = resultSet.getBoolean("pk");
				Object defaultValue = resultSet.getObject("dflt_value");
				String legend = name;

				tableStructure.add(new ColumnBlueprint(name, type, isNullable, isPrimaryKey, defaultValue, legend));
			}
		};
		database.executeAndProcess(structureQuery, processor);

		return tableStructure;
	}

	public static List<ForeignKeyBlueprint> readForeignKeys(String filePath, String password, String tableName) {

		//PRAGMA foreign_key_list('table')
		return null;
	}

	public static List<IndexBlueprint> readIndices(String filePath, String password, String tableName) {

		//PRAGMA index_list('table')
		return null;

	}

	public static ColumnType getColumnType(String filePath, String password, String tableName, String columnName) {

		SqLiteDatabase database = new SqLiteDatabase(filePath);
		String structureQuery = "PRAGMA table_info('" + tableName + "');";

		List<ColumnType> columnTypeContainer = new ArrayList<>();

		ResultSetProcessor processor = (ResultSet resultSet) -> {
			while (resultSet.next()) {
				String currentColumnName = resultSet.getString("name"); //available columns: cid, name, type, notnull, dflt_value, pk
				boolean isWantedColumn = columnName.equals(currentColumnName);
				if (isWantedColumn) {
					String type = resultSet.getString("type");
					ColumnType columnType = ColumnType.getType(type);
					columnTypeContainer.add(columnType);
					return;
				}
			}
		};
		database.executeAndProcess(structureQuery, processor);

		if (columnTypeContainer.isEmpty()) {
			return null;
		} else {
			return columnTypeContainer.get(0);
		}

	}

	private static List<List<Object>> readData(
			String filePath,
			String password,
			String tableName,
			boolean filterRowsByJobId,
			String jobId,
			Integer rowLimit,
			Integer rowOffset,
			List<String> headers) {
		SqLiteDatabase database = new SqLiteDatabase(filePath);
		String dataQuery = "SELECT * FROM '" + tableName + "'";

		boolean applyFilter = filterRowsByJobId && jobId != null;
		if (applyFilter) {
			dataQuery += " WHERE job_id = '" + jobId + "'";
		}

		int offset = 0;
		if (rowOffset != null) {
			offset = rowOffset;
		}

		//if OFFSET is not efficient enough, also see
		//http://stackoverflow.com/questions/14468586/efficient-paging-in-sqlite-with-millions-of-records
		dataQuery += " LIMIT " + rowLimit + " OFFSET " + offset + ";";

		List<List<Object>> data = new ArrayList<>();
		ResultSetProcessor processor = (ResultSet resultSet) -> {
			while (resultSet.next()) {
				List<Object> rowData = new ArrayList<>();
				for (String header : headers) {
					Object entry = resultSet.getObject(header);
					rowData.add(entry);
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
