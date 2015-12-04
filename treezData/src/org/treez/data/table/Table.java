package org.treez.data.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.base.EmptyControlAdaption;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.copy.CopyHelper;
import org.treez.core.atom.uisynchronizing.AbstractUiSynchronizingAtom;
import org.treez.core.data.cell.CellEditorFactory;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.row.Row;
import org.treez.core.data.table.TableSourceInformation;
import org.treez.core.data.table.TreezTable;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.AddChildAtomTreeViewerAction;
import org.treez.core.treeview.action.TreeViewerAction;
import org.treez.data.Activator;
import org.treez.data.cell.TreezTableNebulaLabelProvider;
import org.treez.data.column.Column;
import org.treez.data.column.Columns;

/**
 * Represents a table
 */
public class Table extends AbstractUiSynchronizingAtom implements TreezTable {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(Table.class);

	//#region ATTRIBUTES

	/**
	 * Column separator for text representations
	 */
	private final String COLUMN_SEPARATOR = ";";

	/**
	 * Row separator for text representations
	 */
	private final String ROW_SEPARATOR = "\n";

	/**
	 * The rows of the table
	 */
	private List<Row> rows = null; //null;

	/**
	 * For columns that use enums as value, this maps from column name to EnumSet (=allowed values)
	 */
	private Map<String, List<String>> comboTextMap = null; //null;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public Table(String name) {
		super(name);
	}

	/**
	 * Copy Constructor
	 *
	 * @param tableToCopy
	 */
	private Table(Table tableToCopy) {
		super(tableToCopy);
		rows = CopyHelper.copyRowsForTargetTable(tableToCopy.rows, this);
		comboTextMap = CopyHelper.copyNestedStringMap(tableToCopy.comboTextMap);
	}

	//#end region

	//#region METHODS

	//#region COPY

	/**
	 * Overrides the copy method of AbstractAtom using the copy constructor of this atom
	 */
	@Override
	public Table copy() {
		return new Table(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("table.png");
	}

	/**
	 * Provides a control to represent this atom
	 */
	@Override
	public AbstractControlAdaption createControlAdaption(Composite parent, Refreshable treeViewRefreshable) {
		this.treeViewRefreshable = treeViewRefreshable;
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
				//sysLog.debug("Created new table control with " + rows.size() + " rows.");
			}
			return tableControlAdaption;
		} else {
			return new EmptyControlAdaption(parent, this, "This table does not contain any column yet.");
		}

	}

	/**
	 * Creates the context menu actions for this atom
	 */
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

	/**
	 * Returns the data of the table as string
	 *
	 * @return
	 */
	public String getData() {

		String allDataString = "";
		String entry = "";
		String header;
		List<String> headers = getHeaders();
		int numberOfColumns = headers.size();

		for (Row row : rows) {
			for (int columnIndex = 0; columnIndex < numberOfColumns - 1; columnIndex++) {
				header = headers.get(columnIndex);
				entry = row.getEntryAsString(header);
				allDataString = allDataString + entry + COLUMN_SEPARATOR;
			}
			//last column entry gets no column separator
			header = headers.get(numberOfColumns - 1);
			entry = row.getEntryAsString(header);
			allDataString = allDataString + entry + ROW_SEPARATOR;
		}
		return allDataString;
	}

	/**
	 * Adds a new column
	 *
	 * @param header
	 * @param type
	 */
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

	/**
	 * Adds a new column
	 *
	 * @param header
	 * @param type
	 */
	public void addColumn(String header, String type) {
		initializeColumns();
		getColumns().createColumn(header, type);
	}

	/**
	 * Adds the given row to the table
	 * 
	 * @param row
	 */
	public void addRow(Row row) {
		if (rows == null) {
			rows = new ArrayList<Row>();
		}
		rows.add(row);
	}

	/**
	 * Adds a new row with a given object array
	 *
	 * @param data
	 */
	public void addRow(List<Object> data) {

		//create empty row
		Row row = new Row(this);

		//fill row with data from NativeArray
		int size = data.size();
		//sysLog.debug("size:" + size);

		for (int columnIndex = 0; columnIndex < size; columnIndex++) {
			String header = getHeaders().get(columnIndex);
			Object value = data.get(columnIndex);
			ColumnType columnType = getColumnType(header);
			Class<?> associatedClass = columnType.getAssociatedClass();

			Object formattedValue;
			try {
				formattedValue = associatedClass.cast(value);
			} catch (ClassCastException exception) {
				String message = "The value '" + value.toString() + "' does not have the required type '"
						+ associatedClass.getSimpleName() + "'. Please change the type of the column '" + header
						+ "' or the type of the value. ";
				throw new IllegalArgumentException(message);
			}

			row.setEntry(header, formattedValue);
		}

		//sysLog.debug("new row:" + row);

		addRow(row);

		//sysLog.debug("added");
	}

	/**
	 * Adds several rows with a given java script Native Array (e.g. [[0,0],[1,1]]
	 *
	 * @param data
	 */
	public void addRows(List<List<Object>> data) {

		int size = data.size();
		for (int rowIndex = 0; rowIndex < size; rowIndex++) {

			addRow(data.get(rowIndex));
		}
	}

	/**
	 * Adds several rows with a given java script Native Array (e.g. [[0,0],[1,1]]
	 *
	 * @param header
	 * @param data
	 */
	public void setColumn(String header, Object[] data) {

		Class<?> associatedClass = getColumnType(header).getAssociatedClass();

		int size = data.length;
		for (int rowIndex = 0; rowIndex < size; rowIndex++) {
			Object value = associatedClass.cast(data[rowIndex]);

			//get current row or create a new one if it does not exist
			Row currentRow;
			if (rows == null) {
				rows = new ArrayList<Row>();
			}
			if (rowIndex >= rows.size()) {
				currentRow = new Row(this);
				rows.add(currentRow);
			} else {
				currentRow = rows.get(rowIndex);
			}

			//set value
			currentRow.setEntry(header, value);
		}
	}

	@Override
	public void addEmptyRow() {
		if (rows == null) {
			rows = new ArrayList<>();
		}
		rows.add(new Row(this));
	}

	//#region CREATE CHILD ATOMS

	/**
	 * Creates a Columns child
	 *
	 * @param name
	 * @return
	 */
	public Columns createColumns(String name) {
		Columns child = new Columns(name);
		addChild(child);
		return child;
	}

	//#end region

	//#end region

	//#region ACCESSORS

	/**
	 * Get rows
	 *
	 * @return rows
	 */
	@Override
	public List<Row> getRows() {
		return rows;
	}

	/**
	 * Set rows
	 *
	 * @param rows
	 */
	public void setRows(List<Row> rows) {
		this.rows = rows;
	}

	/**
	 * Get headers
	 *
	 * @return the headers
	 */
	@Override
	public List<String> getHeaders() {
		return getColumns().getHeaders();
	}

	/**
	 * Returns the Columns child
	 *
	 * @return
	 */
	public Columns getColumns() {
		try {
			Columns columns = getChildByClass(Columns.class);
			return columns;
		} catch (ClassCastException | IllegalArgumentException exception) {
			throw new IllegalStateException("Could not get columns of table '" + getName() + "';");
		}
	}

	/**
	 * Adds a new column to the table. If the Columns child does not
	 *
	 * @param newColumn
	 */
	public void addColumn(Column newColumn) {
		createColumnsIfNotExists();
		Columns columns = getColumns();
		columns.addChild(newColumn);
	}

	private void createColumnsIfNotExists() {
		boolean columnsExist = columnsExist();
		if (!columnsExist) {
			createColumns("columns");
		}

	}

	/**
	 * Returns true if a child of type Columns exists
	 *
	 * @return
	 */
	private boolean columnsExist() {
		for (AbstractAtom currentChild : children) {
			boolean isWantedChild = currentChild.getClass().equals(Columns.class);
			if (isWantedChild) {
				return true;
			}
		}
		return false;

	}

	/**
	 * Get row separator
	 *
	 * @return
	 */
	public String getRowSeparator() {
		return ROW_SEPARATOR;
	}

	/**
	 * Get column separator
	 *
	 * @return
	 */
	public String getColumnSeparator() {
		return COLUMN_SEPARATOR;
	}

	/**
	 * Get columnType
	 *
	 * @return the columnType
	 */
	@Override
	public ColumnType getColumnType(String header) {
		ColumnType columnType = getColumns().getColumnType(header);
		return columnType;
	}

	/**
	 * Get header tool tip
	 *
	 * @param header
	 * @return
	 */
	@Override
	public String getColumnHeaderTooltip(String header) {
		return getColumns().getColumnHeaderTooltip(header);
	}

	/**
	 * Define label provider
	 */
	@Override
	public CellLabelProvider getLabelProvider(String header, ColumnType columnType) {
		CellLabelProvider labelProvider = new TreezTableNebulaLabelProvider(header, columnType);
		return labelProvider;
	}

	/**
	 * Define cell editor
	 */
	@Override
	public CellEditor getCellEditor(String header, ColumnType columnType, Composite parent) {
		CellEditor cellEditor = CellEditorFactory.createCellEditor(columnType, parent);
		return cellEditor;
	}

	/**
	 * Get map from column name to allowed string values. This is used for combo text columns.
	 *
	 * @return the allowedValuesMap
	 */
	public Map<String, List<String>> getComboTextMap() {
		return comboTextMap;
	}

	@Override
	public Boolean isEditable(String header) {
		return true;
	}

	@Override
	public Class<?> getColumnDataClass(String columnHeader) {
		ColumnType columnType = getColumnType(columnHeader);
		Class<?> contentClass = columnType.getAssociatedClass();
		return contentClass;
	}

	/**
	 * Deletes all rows of the table
	 */
	public void deleteAllRows() {
		setRows(new ArrayList<Row>());
	}

	/**
	 * Returns true if the column has at least one column
	 *
	 * @return
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	public boolean hasColumns() {
		try {
			Columns columns = getColumns();
			return columns.hasColumns();
		} catch (Exception exception) {
			return false;
		}
	}

	/**
	 * Returns the number of columns
	 *
	 * @return
	 */
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

	@Override
	public boolean isLinkedToSource() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkSourceLink(TableSourceInformation tableSourceInfo) {
		// TODO Auto-generated method stub
		return false;
	}

	//#end region

}
