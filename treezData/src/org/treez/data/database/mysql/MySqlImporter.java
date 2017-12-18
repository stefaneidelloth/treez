package org.treez.data.database.mysql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.treez.core.data.column.ColumnBlueprint;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.column.ColumnTypeConverter;
import org.treez.core.data.foreignkey.ForeignKeyBlueprint;
import org.treez.core.data.index.IndexBlueprint;
import org.treez.core.data.row.Row;
import org.treez.core.data.table.TreezTable;
import org.treez.data.database.AbstractImporter;
import org.treez.data.database.ResultSetProcessor;
import org.treez.data.database.TableData;
import org.treez.data.database.sqlite.SqLiteColumnTypeConverter;

public final class MySqlImporter extends AbstractImporter {

	private static Logger LOG = LogManager.getLogger(MySqlImporter.class);

	//#region CONSTRUCTORS

	/**
	 * Private Constructor that prevents construction
	 */
	private MySqlImporter() {}

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

	public static int getNumberOfRowsForCustomQuery(
			String url,
			String user,
			String password,
			String customQuery,
			String jobId) {

		MySqlDatabase database = new MySqlDatabase(url, user, password);
		String subQuery = removeTrailingSemicolon(customQuery);
		subQuery = injectJobIdIfIncludesPlaceholder(subQuery, jobId);
		String sizeQuery = "SELECT COUNT(*) FROM (" + subQuery + ");";

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

		List<ColumnBlueprint> columnBlueprints = readTableStructure(url, user, password, tableName);

		List<List<Object>> data = readData(url, user, password, tableName, filterRowsByJobId, jobId, rowLimit,
				rowOffset, columnBlueprints);

		TableData tableData = new TableData(columnBlueprints, data);

		return tableData;
	}

	public static TableData importDataWithCustomQuery(
			String url,
			String user,
			String password,
			String customQuery,
			String jobId,
			Integer rowLimit,
			Integer rowOffset) {

		List<ColumnBlueprint> columnBlueprints = readTableStructureWithCustomQuery(url, user, password, customQuery,
				jobId);

		List<List<Object>> data = readDataWithCustomQuery(url, user, password, customQuery, jobId, rowLimit, rowOffset,
				columnBlueprints);

		TableData tableData = new TableData(columnBlueprints, data);

		return tableData;
	}

	public static List<ColumnBlueprint> readTableStructure(String url, String user, String password, String tableName) {
		MySqlDatabase database = new MySqlDatabase(url, user, password);
		String structureQuery = "SHOW COLUMNS FROM " + tableName + ";";

		List<ColumnBlueprint> tableStructure = new ArrayList<>();

		ColumnTypeConverter columnTypeConverter = new MySqlColumnTypeConverter();

		boolean isLinkedToSource = true;

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

				tableStructure.add(new ColumnBlueprint(
						name,
						type,
						isNullable,
						isPrimaryKey,
						defaultValue,
						legend,
						isLinkedToSource));
			}
		};
		database.executeAndProcess(structureQuery, processor);

		return tableStructure;
	}

	public static List<ColumnBlueprint> readTableStructureWithCustomQuery(
			String url,
			String user,
			String password,
			String customQuery,
			String jobId) {
		MySqlDatabase database = new MySqlDatabase(url, user, password);

		int length = customQuery.length();
		if (length < 1) {
			throw new IllegalStateException("Custom query must not be empty");
		}

		String firstLineQuery = removeTrailingSemicolon(customQuery);
		firstLineQuery = injectJobIdIfIncludesPlaceholder(firstLineQuery, jobId);
		firstLineQuery += " LIMIT 1;";

		List<ColumnBlueprint> tableStructure = new ArrayList<>();

		ColumnTypeConverter columnTypeConverter = new SqLiteColumnTypeConverter();

		boolean isLinkedToSource = true;
		boolean isVirtual = true;

		ResultSetProcessor processor = (ResultSet resultSet) -> {
			resultSet.next();
			ResultSetMetaData metaData = resultSet.getMetaData();
			int numberOfColumns = metaData.getColumnCount();
			for (int columnIndex = 1; columnIndex <= numberOfColumns; columnIndex++) {

				String name = metaData.getColumnName(columnIndex);
				ColumnType type = columnTypeConverter.getType(metaData.getColumnTypeName(columnIndex));
				boolean isNullable = metaData.isNullable(columnIndex) == 1;
				String legend = metaData.getColumnLabel(columnIndex);

				tableStructure.add(new ColumnBlueprint(name, type, isNullable, legend, isLinkedToSource));
			}

		};
		database.executeAndProcess(firstLineQuery, processor);

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
				String currentColumnName = resultSet.getString("field");
				//available columns: cid, name, type, notnull, dflt_value, pk
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
			List<ColumnBlueprint> columnBlueprints) {
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
				for (ColumnBlueprint columnBlueprint : columnBlueprints) {
					Object entry = resultSet.getObject(columnBlueprint.getName());
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

	private static List<List<Object>> readDataWithCustomQuery(
			String url,
			String user,
			String password,
			String customQuery,
			String jobId,
			Integer rowLimit,
			Integer rowOffset,
			List<ColumnBlueprint> columnBlueprints) {
		MySqlDatabase database = new MySqlDatabase(url, user, password);

		int length = customQuery.length();
		if (length < 1) {
			throw new IllegalStateException("Custom query must not be empty");
		}

		String dataQuery = removeTrailingSemicolon(customQuery);
		dataQuery = injectJobIdIfIncludesPlaceholder(dataQuery, jobId);

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
				for (ColumnBlueprint columnBlueprint : columnBlueprints) {
					Object entry = resultSet.getObject(columnBlueprint.getName());
					rowData.add(entry);
				}
				data.add(rowData);
			}
		};
		database.executeAndProcess(dataQuery, processor);

		if (data.isEmpty()) {
			String message = "Could not find any rows with query " + dataQuery;
			LOG.warn(message);
		}

		return data;
	}

	public static Row readRow(
			String url,
			String user,
			String password,
			String tableName,
			boolean filterRowsByJobId,
			String jobId,
			int rowIndex,
			TreezTable table) {

		MySqlDatabase database = new MySqlDatabase(url, user, password);
		String dataQuery = "SELECT * FROM '" + tableName + "'";

		boolean applyFilter = filterRowsByJobId && jobId != null;
		if (applyFilter) {
			dataQuery += " WHERE job_id = '" + jobId + "'";
		}

		dataQuery += " LIMIT 1 OFFSET " + rowIndex + ";";
		return readRow(table, database, dataQuery);
	}

	public static Row readRowWithCustomQuery(
			String url,
			String user,
			String password,
			String customQuery,
			String jobId,
			int rowIndex,
			TreezTable table) {

		MySqlDatabase database = new MySqlDatabase(url, user, password);

		int length = customQuery.length();
		if (length < 1) {
			throw new IllegalStateException("Custom query must not be empty");
		}

		String dataQuery = removeTrailingSemicolon(customQuery);
		dataQuery = injectJobIdIfIncludesPlaceholder(dataQuery, jobId);
		dataQuery += " LIMIT 1 OFFSET " + rowIndex + ";";

		return readRow(table, database, dataQuery);
	}

	private static Row readRow(TreezTable table, MySqlDatabase database, String dataQuery) {
		Row row = new Row(table);
		ResultSetProcessor processor = (ResultSet resultSet) -> {
			while (resultSet.next()) {
				ResultSetMetaData metaData = resultSet.getMetaData();
				int columnCount = metaData.getColumnCount();
				for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
					String columnName = metaData.getColumnName(columnIndex);
					Object value = resultSet.getObject(columnIndex);
					row.setEntry(columnName, value);
				}
			}
		};
		database.executeAndProcess(dataQuery, processor);

		return row;
	}

	//#end region

}
