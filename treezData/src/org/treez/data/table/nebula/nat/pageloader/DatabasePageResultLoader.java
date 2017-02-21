package org.treez.data.table.nebula.nat.pageloader;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.pagination.IPageLoader;
import org.eclipse.nebula.widgets.pagination.PageableController;
import org.eclipse.nebula.widgets.pagination.collections.DefaultSortProcessor;
import org.eclipse.nebula.widgets.pagination.collections.PageResult;
import org.eclipse.nebula.widgets.pagination.collections.SortProcessor;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.row.Row;
import org.treez.core.data.table.AbstractTreezTable;
import org.treez.core.data.table.LinkableTreezTable;
import org.treez.core.data.table.TableSource;
import org.treez.core.data.table.TableSourceType;
import org.treez.data.database.mysql.MySqlDataTableImporter;
import org.treez.data.database.sqlite.SqLiteDataTableImporter;
import org.treez.data.tableImport.TableData;

public class DatabasePageResultLoader implements IPageLoader<PageResult<Row>> {

	//#region ATTRIBUTES

	private LinkableTreezTable treezTable;

	private TableSource tableSource;

	//#end region

	//#region CONSTRUCTORS

	public DatabasePageResultLoader(LinkableTreezTable treezTable) {
		if (!treezTable.isLinkedToSource()) {
			throw new IllegalStateException("The passed table must be linked to a source.");
		}
		this.treezTable = treezTable;
		this.tableSource = treezTable.getTableSource();
	}

	//#end region

	//#region METHODS

	public static List<String> getHeadersFromSource(TableSource tableSource) {

		TableSourceType sourceType = tableSource.getSourceType();
		if (sourceType.equals(TableSourceType.SQLITE)) {
			String sqLiteFilePath = tableSource.getSourceFilePath();
			String tableName = tableSource.getTableName();
			String password = tableSource.getPassword();

			return SqLiteDataTableImporter.readHeaders(sqLiteFilePath, password, tableName);
		}

		return null;
	}

	public static ColumnType getColumnType(TableSource tableSource, String columnName) {
		TableSourceType sourceType = tableSource.getSourceType();
		if (sourceType.equals(TableSourceType.SQLITE)) {
			String sqLiteFilePath = tableSource.getSourceFilePath();
			String tableName = tableSource.getTableName();
			String password = tableSource.getPassword();

			return SqLiteDataTableImporter.getColumnType(sqLiteFilePath, password, tableName, columnName);
		}

		return null;
	}

	@Override
	public PageResult<Row> loadPage(PageableController controller) {

		SortProcessor processor = DefaultSortProcessor.getInstance();

		int sortDirection = controller.getSortDirection();
		//if (sortDirection != SWT.NONE) {
		//	// Sort the list
		//	processor.sort(items, controller.getSortPropertyName(), sortDirection);
		//}

		TableSourceType sourceType = tableSource.getSourceType();
		if (sourceType.equals(TableSourceType.SQLITE)) {

			String sqLiteFilePath = tableSource.getSourceFilePath();
			String tableName = tableSource.getTableName();
			String password = tableSource.getPassword();

			int totalSize = SqLiteDataTableImporter.getNumberOfRows(sqLiteFilePath, tableName);
			int pageOffset = controller.getPageOffset();
			if (pageOffset > totalSize) {
				return new PageResult<Row>(new ArrayList<Row>(), totalSize);
			}

			int pageSize = controller.getPageSize();

			TableData tableData = SqLiteDataTableImporter.importData(sqLiteFilePath, password, tableName, false, null,
					pageSize, pageOffset);

			List<Row> rows = new ArrayList<>();
			for (List<Object> rowEntries : tableData.getRowData()) {
				Row row = AbstractTreezTable.createRow(rowEntries, treezTable);
				rows.add(row);
			}

			return new PageResult<Row>(rows, totalSize);

		} else if (sourceType.equals(TableSourceType.MYSQL)) {

			String host = tableSource.getHost();
			String port = tableSource.getPort();
			String schema = tableSource.getSchema();
			String url = host + ":" + port + "/" + schema;

			String user = tableSource.getUser();
			String password = tableSource.getPassword();

			String tableName = tableSource.getTableName();

			int totalSize = MySqlDataTableImporter.getNumberOfRows(url, user, password, tableName);
			int pageOffset = controller.getPageOffset();
			if (pageOffset > totalSize) {
				return new PageResult<Row>(new ArrayList<Row>(), totalSize);
			}

			int pageSize = controller.getPageSize();

			TableData tableData = MySqlDataTableImporter.importData(url, user, password, tableName, false, null,
					pageSize, pageOffset);

			List<Row> rows = new ArrayList<>();
			for (List<Object> rowEntries : tableData.getRowData()) {
				Row row = AbstractTreezTable.createRow(rowEntries, treezTable);
				rows.add(row);
			}

			return new PageResult<Row>(rows, totalSize);
		}

		String message = "The TableSourceType " + sourceType + " is not yet implemented.";
		throw new IllegalStateException(message);

	}

	//#end region

}
