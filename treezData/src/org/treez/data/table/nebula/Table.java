package org.treez.data.table.nebula;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.Activator;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.attribute.base.EmptyControlAdaption;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.data.cell.CellEditorFactory;
import org.treez.core.data.column.ColumnBlueprint;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.row.Row;
import org.treez.core.data.table.AbstractTreezTable;
import org.treez.core.data.table.TableSource;
import org.treez.core.data.table.TableSourceType;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.AddChildAtomTreeViewerAction;
import org.treez.core.treeview.action.TreeViewerAction;
import org.treez.data.cell.TreezTableNebulaLabelProvider;
import org.treez.data.column.Column;
import org.treez.data.column.Columns;
import org.treez.data.database.mysql.MySqlImporter;
import org.treez.data.database.sqlite.SqLiteImporter;
import org.treez.data.table.nebula.nat.pageloader.DatabasePageResultLoader;

public class Table extends AbstractTreezTable<Table> {

	//#region ATTRIBUTES

	//#end region

	//#region CONSTRUCTORS

	public Table(String name) {
		super(name);
	}

	/**
	 * Copy Constructor
	 */
	private Table(Table tableToCopy) {
		super(tableToCopy);
	}

	//#end region

	//#region METHODS

	@Override
	public Table getThis() {
		return this;
	}

	@Override
	public Table copy() {
		Table newTable = new Table(this);
		return newTable;
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("table.png");
	}

	@Override
	public TreeNodeAdaption createTreeNodeAdaption() {
		TreeNodeAdaption treeNodeAdaption = new TableTreeNodeAdaption(this);
		return treeNodeAdaption;
	}

	@Override
	public AbstractControlAdaption createControlAdaption(
			Composite parent,
			FocusChangingRefreshable treeViewRefreshable) {
		this.treeViewRefreshable = treeViewRefreshable;

		if (isLinkedToSource()) {
			if (!hasColumns()) {
				reload();
			}
		}

		List<String> headers = null;
		try {
			headers = getHeaders();
		} catch (IllegalStateException exception) {
			//columns do not exist yet; EmptyControlAdaption will be created
		}
		boolean columnsExist = headers != null && headers.size() > 0;
		if (columnsExist) {

			//create table control
			TableControlAdaption tableControlAdaption = new TableControlAdaption(parent, this);

			if (rows != null) {
				//LOG.debug("Created new table control with " + rows.size() + " rows.");
			}
			return tableControlAdaption;
		} else {
			return new EmptyControlAdaption(parent, this, "This table does not contain any column yet.");
		}

	}

	@Override
	protected List<Object> createContextMenuActions(TreeViewerRefreshable treeViewer) {

		List<Object> actions = new ArrayList<>();

		Action addColumns = new AddChildAtomTreeViewerAction(
				Columns.class,
				"columns",
				Activator.getImage("columns.png"),
				this,
				treeViewer);
		actions.add(addColumns);

		actions.add(new TreeViewerAction(
				"Delete",
				org.treez.core.Activator.getImage("delete.png"),
				treeViewer,
				() -> Table.this.createTreeNodeAdaption().delete()));

		return actions;
	}

	@Override
	public CodeAdaption createCodeAdaption(ScriptType scriptType) {

		CodeAdaption codeAdaption;
		switch (scriptType) {
		case JAVA:
			codeAdaption = new TableCodeAdaption(this);
			break;
		default:
			String message = "The ScriptType " + scriptType + " is not yet implemented.";
			throw new IllegalStateException(message);
		}

		return codeAdaption;

	}

	public void addColumn(String header, ColumnType type) {
		initializeColumns();
		getColumns().createColumn(header, type);
	}

	/**
	 * Creates a Columns child if it does not yet exist
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private void initializeColumns() {

		try {
			getColumns();
		} catch (Exception exception) {
			//the child columns does not exist: create it
			Columns columns = new Columns("columns");
			this.addChild(columns);
		}
	}

	public void addColumn(String header, String type) {
		initializeColumns();
		getColumns().createColumn(header, type);
	}

	/**
	 * Creates a Columns child
	 */
	public Columns createColumns(String name) {
		Columns child = new Columns(name);
		addChild(child);
		return child;
	}

	/**
	 * Returns the first TableSource that can be found in the children of this atom or null
	 */
	@Override
	public TableSource getTableSource() {

		List<TableSource> sources = this.getChildrenByInterface(TableSource.class);
		if (sources.isEmpty()) {
			return null;
		} else {
			return sources.get(0);
		}

	}

	public org.treez.data.tableSource.TableSource createTableSource(String name) {
		org.treez.data.tableSource.TableSource tableSource = new org.treez.data.tableSource.TableSource(name);
		addChild(tableSource);
		return tableSource;
	}

	@Override
	public void reload() {
		this.resetCache();
		loadTableStructureIfLinkedToSource();
		refresh();
	}

	private void loadTableStructureIfLinkedToSource() {
		if (isLinkedToSource()) {
			loadTableStructureFromSource();
		}
	}

	private void loadTableStructureFromSource() {
		TableSource tableSource = this.getTableSource();
		TableSourceType sourceType = tableSource.getSourceType();

		if (sourceType.equals(TableSourceType.SQLITE)) {

			deleteColumnsIfExist();
			List<ColumnBlueprint> tableStructure = readTableStructureForSqLiteTable(tableSource);
			createColumns(tableStructure);
		} else if (sourceType.equals(TableSourceType.MYSQL)) {

			deleteColumnsIfExist();
			List<ColumnBlueprint> tableStructure = readTableStructureForMySqlTable(tableSource);
			createColumns(tableStructure);
		} else {
			throw new IllegalStateException("not yet implemented for current source type " + sourceType);
		}

	}

	private void deleteColumnsIfExist() {
		List<Columns> columnsToDelete = this.getChildrenByClass(Columns.class);
		for (Columns columns : columnsToDelete) {
			children.remove(columns);
		}
	}

	private static List<ColumnBlueprint> readTableStructureForSqLiteTable(TableSource tableSource) {
		String sqLiteFilePath = tableSource.getSourceFilePath();

		String password = tableSource.getPassword();

		Boolean isUsingCustomQuery = tableSource.isUsingCustomQuery();
		if (isUsingCustomQuery) {
			String customQuery = tableSource.getCustomQuery();
			String jobId = tableSource.getJobId();
			List<ColumnBlueprint> tableStructure = SqLiteImporter.readTableStructureWithCustomQuery(sqLiteFilePath,
					password, customQuery, jobId);
			return tableStructure;
		} else {
			String tableName = tableSource.getTableName();
			List<ColumnBlueprint> tableStructure = SqLiteImporter.readTableStructure(sqLiteFilePath, password,
					tableName);
			return tableStructure;
		}

	}

	private static List<ColumnBlueprint> readTableStructureForMySqlTable(TableSource tableSource) {
		String host = tableSource.getHost();
		String port = tableSource.getPort();
		String schema = tableSource.getSchema();
		String url = host + ":" + port + "/" + schema;

		String user = tableSource.getUser();
		String password = tableSource.getPassword();

		Boolean isUsingCustomQuery = tableSource.isUsingCustomQuery();
		if (isUsingCustomQuery) {
			String customQuery = tableSource.getCustomQuery();
			String jobId = tableSource.getJobId();
			List<ColumnBlueprint> tableStructure = MySqlImporter.readTableStructureWithCustomQuery(url, user, password,
					customQuery, jobId);
			return tableStructure;
		} else {
			String tableName = tableSource.getTableName();
			List<ColumnBlueprint> tableStructure = MySqlImporter.readTableStructure(url, user, password, tableName);
			return tableStructure;
		}
	}

	private void createColumns(List<ColumnBlueprint> columnBlueprints) {
		Columns columns = createColumns("columns");
		for (ColumnBlueprint columnBlueprint : columnBlueprints) {
			columns.createColumn(columnBlueprint);
		}
	}

	private Row readRowFromTableSource(TableSource tableSource, int rowIndex) {
		TableSourceType sourceType = tableSource.getSourceType();

		if (sourceType.equals(TableSourceType.SQLITE)) {
			return readRowFromSqLiteTable(tableSource, rowIndex);
		} else if (sourceType.equals(TableSourceType.MYSQL)) {
			return readRowFromMySqlTable(tableSource, rowIndex);
		} else {
			throw new IllegalStateException("not yet implemented for current source type " + sourceType);
		}
	}

	private Row readRowFromSqLiteTable(TableSource tableSource, int rowIndex) {
		String sqLiteFilePath = tableSource.getSourceFilePath();

		String password = tableSource.getPassword();
		String jobId = tableSource.getJobId();

		Boolean isUsingCustomQuery = tableSource.isUsingCustomQuery();
		if (isUsingCustomQuery) {
			String customQuery = tableSource.getCustomQuery();
			Row row = SqLiteImporter.readRowWithCustomQuery(sqLiteFilePath, password, customQuery, jobId, rowIndex,
					this);
			return row;
		} else {
			String tableName = tableSource.getTableName();
			Boolean filterRowsByJobId = tableSource.isFilteringForJob();

			Row row = SqLiteImporter.readRow(sqLiteFilePath, password, tableName, filterRowsByJobId, jobId, rowIndex,
					this);
			return row;
		}
	}

	private Row readRowFromMySqlTable(TableSource tableSource, int rowIndex) {
		String host = tableSource.getHost();
		String port = tableSource.getPort();
		String schema = tableSource.getSchema();
		String url = host + ":" + port + "/" + schema;

		String user = tableSource.getUser();
		String password = tableSource.getPassword();

		String jobId = tableSource.getJobId();

		Boolean isUsingCustomQuery = tableSource.isUsingCustomQuery();
		if (isUsingCustomQuery) {
			String customQuery = tableSource.getCustomQuery();

			Row row = MySqlImporter.readRowWithCustomQuery(url, user, password, customQuery, jobId, rowIndex, this);
			return row;
		} else {
			String tableName = tableSource.getTableName();
			Boolean filterRowsByJobId = tableSource.isFilteringForJob();
			Row row = MySqlImporter.readRow(url, user, password, tableName, filterRowsByJobId, jobId, rowIndex, this);
			return row;
		}
	}

	//#end region

	//#region ACCESSORS

	@Override
	public List<String> getHeaders() {
		return getColumns().getHeaders();
	}

	public Columns getColumns() {

		try {
			Columns columns = getChildByClass(Columns.class);
			return columns;
		} catch (ClassCastException | IllegalArgumentException exception) {

			if (isLinkedToSource()) {
				reload();
				try {
					Columns columns = getChildByClass(Columns.class);
					return columns;
				} catch (ClassCastException | IllegalArgumentException secondException) {
					throw new IllegalStateException("Could not get columns of table '" + getName() + "';");
				}
			}

			throw new IllegalStateException("Could not get columns of table '" + getName() + "';");
		}
	}

	public void addColumn(Column newColumn) {
		createColumnsIfNotExist();
		Columns columns = getColumns();
		columns.addChild(newColumn);
	}

	private void createColumnsIfNotExist() {
		boolean columnsExist = columnsExist();
		if (!columnsExist) {
			createColumns("columns");
		}

	}

	private boolean columnsExist() {
		for (AbstractAtom<?> currentChild : children) {
			boolean isWantedChild = currentChild.getClass().equals(Columns.class);
			if (isWantedChild) {
				return true;
			}
		}
		return false;

	}

	@Override
	public ColumnType getColumnType(String columnHeader) {
		if (isLinkedToSource()) {
			TableSource tableSource = this.getTableSource();
			return DatabasePageResultLoader.getColumnType(tableSource, columnHeader);
		} else {
			ColumnType columnType = getColumns().getColumnType(columnHeader);
			return columnType;
		}

	}

	@Override
	public String getColumnHeaderTooltip(String header) {
		return getColumns().getColumnHeaderTooltip(header);
	}

	@Override
	public CellLabelProvider getLabelProvider(String header, ColumnType columnType) {
		CellLabelProvider labelProvider = new TreezTableNebulaLabelProvider(header, columnType);
		return labelProvider;
	}

	@Override
	public CellEditor getCellEditor(String header, ColumnType columnType, Composite parent) {
		CellEditor cellEditor = CellEditorFactory.createCellEditor(columnType, parent);
		return cellEditor;
	}

	@SuppressWarnings("checkstyle:illegalcatch")
	public boolean hasColumns() {
		try {
			Columns columns = getColumns();
			return columns.hasColumns();
		} catch (Exception exception) {
			return false;
		}
	}

	@SuppressWarnings("checkstyle:illegalcatch")
	public int getNumberOfColumns() {
		try {
			Columns columns = getColumns();
			return columns.getNumberOfColumns();
		} catch (Exception exception) {
			return 0;
		}
	}

	/**
	 * Returns true if the headers of the columns equal the given headers
	 *
	 * @param expectedHeaders
	 * @return
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	public boolean checkHeaders(List<String> expectedHeaders) {
		try {
			Columns columns = getColumns();
			return columns.checkHeaders(expectedHeaders);
		} catch (Exception exception) {
			return false;
		}
	}

	public Row getRow(int rowIndex) {

		if (isLinkedToSource()) {
			TableSource tableSource = getTableSource();
			return readRowFromTableSource(tableSource, rowIndex);
		} else {
			return getRows().get(rowIndex);
		}
	}

	//#end region

}
