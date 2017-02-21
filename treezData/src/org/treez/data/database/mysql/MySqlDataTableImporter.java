package org.treez.data.database.mysql;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.treez.core.data.column.ColumnBlueprint;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.column.ColumnTypeConverter;
import org.treez.core.data.foreignkey.ForeignKeyBlueprint;
import org.treez.core.data.index.IndexBlueprint;
import org.treez.data.database.ResultSetProcessor;
import org.treez.data.tableImport.TableData;

public final class MySqlDataTableImporter {

	private static Logger LOG = Logger.getLogger(MySqlDataTableImporter.class);

	//#region CONSTRUCTORS

	/**
	 * Private Constructor that prevents construction
	 */
	private MySqlDataTableImporter() {}

	//#end region

	//#region METHODS

	public static int getNumberOfRows(String url, String user, String password, String tableName) {

		MySqlDatabase database = new MySqlDatabase(url, user, password);
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
			String url,
			String user,
			String password,
			String tableName,
			boolean filterRowsByJobId,
			String jobId,
			Integer rowLimit,
			Integer rowOffset) {

		List<String> headers = readHeaders(url, user, password, tableName);

		List<ColumnBlueprint> columnBlueprints = readTableStructure(url, user, password, tableName);

		List<List<Object>> data = readData(url, user, password, tableName, filterRowsByJobId, jobId, rowLimit,
				rowOffset, headers);

		TableData tableData = new TableData() {

			@Override
			public List<String> getHeaderData() {
				return headers;
			}

			@Override
			public ColumnType getColumnType(String header) {

				for (ColumnBlueprint columnBlueprint : columnBlueprints) {
					if (header.equals(columnBlueprint.getName())) {
						return columnBlueprint.getType();
					}
				}
				throw new IllegalStateException("Could not determine ColumnType for column '" + header + "'");
			}

			@Override
			public List<List<Object>> getRowData() {
				return data;
			}
		};

		return tableData;
	}

	public static List<String> readHeaders(String url, String user, String password, String tableName) {
		MySqlDatabase database = new MySqlDatabase(url, user, password);
		String structureQuery = "SHOW COLUMNS FROM " + tableName + ";";

		List<String> columnNames = new ArrayList<>();

		ResultSetProcessor processor = (ResultSet resultSet) -> {
			while (resultSet.next()) {
				String columnName = resultSet.getString("field"); //available columns: field, type, null, key, default, extra
				columnNames.add(columnName);
			}
		};
		database.executeAndProcess(structureQuery, processor);

		return columnNames;
	}

	public static List<ColumnBlueprint> readTableStructure(String url, String user, String password, String tableName) {
		MySqlDatabase database = new MySqlDatabase(url, user, password);
		String structureQuery = "SHOW COLUMNS FROM " + tableName + ";";

		List<ColumnBlueprint> tableStructure = new ArrayList<>();

		ColumnTypeConverter columnTypeConverter = new MySqlColumnTypeConverter();

		ResultSetProcessor processor = (ResultSet resultSet) -> {
			while (resultSet.next()) {
				String name = resultSet.getString("field"); //available columns: field, type, null, key, default, extra

				String typeString = resultSet.getString("type");
				int columnIndex = typeString.indexOf("(");
				String plainType = typeString.substring(0, columnIndex);
				ColumnType type = columnTypeConverter.getType(plainType);
				boolean isNullable = resultSet.getBoolean("null");
				boolean isPrimaryKey = resultSet.getString("key").equals("PRI");
				Object defaultValue = resultSet.getObject("default");
				String legend = name;

				tableStructure.add(new ColumnBlueprint(name, type, isNullable, isPrimaryKey, defaultValue, legend));
			}
		};
		database.executeAndProcess(structureQuery, processor);

		return tableStructure;
	}

	public static
			List<ForeignKeyBlueprint>
			readForeignKeys(String url, String user, String password, String tableName) {

		//TODO:
		//PRAGMA foreign_key_list('table_name')
		//
		//The pragma command does not yield the name of the foreign key.
		//The name can be extracted from column sql in sqlite_master table.
		//That would however require some extra parsing. The parsing would
		//have to consider the existance of several foreign keys. Also see
		//http://stackoverflow.com/questions/41595152/how-to-get-the-names-of-foreign-key-constraints-in-sqlite
		//
		//SELECT sql FROM sqlite_master WHERE name = 'table_name'

		return null;
	}

	public static List<IndexBlueprint> readIndices(String url, String user, String password, String tableName) {

		//TODO:
		//select * from sqlite_master where type='index' and tbl_name = 'table_name'
		//and
		//PRAGMA index_list('table_name')
		return null;

	}

	public static
			ColumnType
			getColumnType(String url, String user, String password, String tableName, String columnName) {

		MySqlDatabase database = new MySqlDatabase(url, user, password);
		String structureQuery = "SHOW COLUMNS FROM " + tableName + ";";

		List<ColumnType> columnTypeContainer = new ArrayList<>();
		ColumnTypeConverter columnTypeConverter = new MySqlColumnTypeConverter();

		ResultSetProcessor processor = (ResultSet resultSet) -> {
			while (resultSet.next()) {
				String currentColumnName = resultSet.getString("field"); //available columns: cid, name, type, notnull, dflt_value, pk
				boolean isWantedColumn = columnName.equals(currentColumnName);
				if (isWantedColumn) {
					String typeString = resultSet.getString("type");
					int columnIndex = typeString.indexOf("(");
					String plainType = typeString.substring(0, columnIndex);
					ColumnType columnType = columnTypeConverter.getType(plainType);
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
			String url,
			String user,
			String password,
			String tableName,
			boolean filterRowsByJobId,
			String jobId,
			Integer rowLimit,
			Integer rowOffset,
			List<String> headers) {
		MySqlDatabase database = new MySqlDatabase(url, user, password);
		String dataQuery = "SELECT * FROM `" + tableName + "`";

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
