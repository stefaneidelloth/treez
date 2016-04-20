package org.treez.core.atom.list;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.Activator;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.copy.CopyHelper;
import org.treez.core.atom.uisynchronizing.AbstractUiSynchronizingAtom;
import org.treez.core.data.cell.CellEditorFactory;
import org.treez.core.data.cell.TreezTableJFaceLabelProvider;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.row.Row;
import org.treez.core.data.table.TableSourceInformation;
import org.treez.core.data.table.TreezTable;
import org.treez.core.treeview.TreeViewerRefreshable;

/**
 * This atom contains a list that consists of Rows (having a single column).
 * This atom implements TreezTable. The corresponding control adaption shows the
 * list and some additional buttons to edit the list.
 */
public class TreezListAtom extends AbstractUiSynchronizingAtom
		implements
			TreezTable {

	//#region ATTRIBUTES

	/**
	 * The header of the single column
	 */
	private String header = "value_header";

	/**
	 * Specifies if the header should be shown
	 */
	private boolean showHeader = true;

	/**
	 * The Column type of the single column
	 */
	private ColumnType columnType = ColumnType.TEXT;

	/**
	 * Row separator for text representations
	 */
	private final String ROW_SEPARATOR = "\n";

	/**
	 * The rows of the list
	 */
	private List<Row> rows = null; //null;

	/**
	 * If this flag is true, and the column type of the treezList is
	 * ColumnType.TEXT, an additional button will be shown that allows to edit
	 * the text entries as file path.
	 */
	private boolean showFilePathButton = false;

	/**
	 * If this flag is true, and the column type of the treezList is
	 * ColumnType.TEXT, an additional button will be shown that allows to edit
	 * the text entries as directory path.
	 */
	private boolean showDirectoryPathButton = false;

	/**
	 * This String specifies the available items as comma separated list, e.g.
	 * item1,item2. If this attribute is not null a combo box will be used as
	 * list cell editor instead of a text field.
	 */
	private String availableStringItems = null;

	/**
	 * If this is true, an initial first row will be created automatically.
	 */
	private boolean firstRowAutoCreation = true;

	/**
	 * The control adaption
	 */
	private TreezListAtomControlAdaption listControlAdaption;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public TreezListAtom(String name) {
		super(name);
	}

	/**
	 * Copy Constructor
	 *
	 * @param tableToCopy
	 */
	private TreezListAtom(TreezListAtom tableToCopy) {
		super(tableToCopy);
		rows = CopyHelper.copyRowsForTargetTable(tableToCopy.rows, this);

	}

	//#end region

	//#region METHODS

	//#region COPY

	/**
	 * Overrides the copy method of AbstractAtom using the copy constructor of
	 * this atom
	 */
	@Override
	public TreezListAtom copy() {
		return new TreezListAtom(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("column.png");
	}

	/**
	 * Provides a control to represent this atom
	 */
	@Override
	public AbstractControlAdaption createControlAdaption(Composite parent,
			Refreshable treeViewRefreshable) {
		//store tree view to be able to update it
		this.treeViewRefreshable = treeViewRefreshable;

		//create list control
		listControlAdaption = new TreezListAtomControlAdaption(parent, this);

		return listControlAdaption;
	}

	private void refreshControlAdaption() {
		if (isAvailable(listControlAdaption)) {
			listControlAdaption.refresh();
		}

	}

	/**
	 * Creates the context menu actions for this atom
	 */
	@Override
	protected List<Object> createContextMenuActions(
			TreeViewerRefreshable treeViewer) {

		List<Object> actions = new ArrayList<>();
		return actions;
	}

	/**
	 * Returns all row entries of the list as a single String, separated by the
	 * given row separator
	 *
	 * @return
	 */
	public String getData(String rowSeparator) {

		List<String> stringEntries = new ArrayList<>();
		if (rows != null) {
			for (Row row : rows) {
				String entry = row.getEntryAsString(header);
				stringEntries.add(entry);
			}
		}

		String allDataString = String.join(rowSeparator, stringEntries);
		return allDataString;
	}

	/**
	 * Returns all data of the list as a single String, separated by the default
	 * row separator
	 *
	 * @return
	 */
	public String getData() {
		String dataString = getData(ROW_SEPARATOR);
		return dataString;
	}

	/**
	 * Adds a new row with the given object (depending on the ColumnType this
	 * can be of different type) *
	 *
	 * @param entry
	 */
	public void addRow(Object entry) {

		//initialize rows if they do not yet exist
		if (rows == null) {
			rows = new ArrayList<Row>();
		}

		//create empty row
		Row row = new Row(this);

		//fill row with entry
		ColumnType columnTypeForHeader = getColumnType(header);
		Class<?> associatedClass = columnTypeForHeader.getAssociatedClass();

		Object formattedValue;
		try {
			formattedValue = associatedClass.cast(entry);
		} catch (ClassCastException exception) {
			String message = "The value '" + entry.toString()
					+ "' does not have the required type '"
					+ associatedClass.getSimpleName()
					+ "'. Please change the type of the list or the type of the value. ";
			throw new IllegalArgumentException(message);
		}

		row.setEntry(header, formattedValue);

		//LOG.debug("new row:" + row);

		//add row
		rows.add(row);

		//LOG.debug("added");
	}

	/**
	 * Adds several rows with a given object array (e.g. {"aaa", "bbb"})
	 *
	 * @param data
	 */
	public void addRows(Object[] data) {
		int size = data.length;
		for (int rowIndex = 0; rowIndex < size; rowIndex++) {
			addRow(data[rowIndex]);
		}
	}

	@Override
	public void addEmptyRow() {
		if (rows == null) {
			rows = new ArrayList<>();
		}
		Row emptyRow = new Row(this);

		boolean hasAvailableItems = hasAvailableItems();
		if (hasAvailableItems) {
			//use first available item as default entry
			String firstItem = this.getAvailableStringItems().get(0);
			emptyRow.setEntry(header, firstItem);
		} else {
			//use null as default entry
			emptyRow.setEntry(header, null);
		}

		rows.add(emptyRow);
	}

	/**
	 * Returns true if the header equals the given header
	 *
	 * @param expectedHeader
	 * @return
	 */
	public boolean checkHeader(String expectedHeader) {
		return header.equals(expectedHeader);
	}

	/**
	 * Deletes all rows of the table
	 */
	public void deleteAllRows() {
		setRows(new ArrayList<Row>());
	}

	//#end region

	//#region ACCESSORS

	//#region rows

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
		refreshControlAdaption();
	}

	/**
	 * Sets all rows with the given object array
	 *
	 * @param data
	 */
	public void setRows(Object[] data) {

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

		refreshControlAdaption();
	}

	//#end region

	//#region HEADER

	/**
	 * Returns the header
	 *
	 * @return
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * Sets the header
	 *
	 * @param header
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * Dummy implementation to fulfill interface
	 *
	 * @return the headers
	 */
	@Override
	public List<String> getHeaders() {
		List<String> headers = new ArrayList<>();
		headers.add(header);
		return headers;
	}

	/**
	 * Sets the visibility of the column header
	 *
	 * @param headerIsVisible
	 */
	public void setShowHeader(boolean headerIsVisible) {
		this.showHeader = headerIsVisible;
	}

	/**
	 * Returns true if the header is shown
	 *
	 * @return
	 */
	public boolean getShowHeader() {
		return showHeader;
	}

	//#end region

	//#region COLUMN TYPE

	/**
	 * Returns the column type of the list
	 *
	 * @return
	 */
	public ColumnType getColumnType() {
		return columnType;
	}

	/**
	 * Sets the column type
	 *
	 * @param columnType
	 */
	public void setColumnType(ColumnType columnType) {
		this.columnType = columnType;
	}

	/**
	 * Dummy implementation to fulfill interface
	 *
	 * @return the columnType
	 */
	@Override
	public ColumnType getColumnType(String header) {
		return columnType;
	}

	@Override
	public Class<?> getColumnDataClass(String columnHeader) {
		ColumnType columnTypeForHeader = getColumnType(columnHeader);
		Class<?> contentClass = columnTypeForHeader.getAssociatedClass();
		return contentClass;
	}

	//#end region

	//#region COLUMN HEADER TOOL TIP

	/**
	 * Define column header tool tips
	 */
	@Override
	public String getColumnHeaderTooltip(String header) {
		return "";
	}

	//#end region

	//#region CELL LABEL PROVIDER

	/**
	 * Returns the label provider
	 *
	 * @return
	 */
	public TreezTableJFaceLabelProvider getLabelProvider() {
		return new TreezTableJFaceLabelProvider(header, columnType);
	}

	@Override
	public CellLabelProvider getLabelProvider(String header,
			ColumnType columnType) {
		CellLabelProvider labelProvider = new TreezTableJFaceLabelProvider(
				header, columnType);
		return labelProvider;
	}

	//#end region

	//#region CELL EDITOR PROVIDER

	/**
	 * Define cell editor
	 */
	@Override
	public CellEditor getCellEditor(String header, ColumnType columnType,
			Composite parent) {
		CellEditor cellEditor = CellEditorFactory.createCellEditor(columnType,
				parent);
		return cellEditor;
	}

	//#end region

	@Override
	public Boolean isEditable(String header) {
		return true;
	}

	//#region ROW SEPARATOR

	/**
	 * Get row separator
	 *
	 * @return
	 */
	public String getRowSeparator() {
		return ROW_SEPARATOR;
	}

	//#end region

	//#region EXTRA BUTTONS

	/**
	 * Returns true if file path button is enabled
	 */
	public boolean isEnabledFilePathButton() {
		return showFilePathButton;

	}

	/**
	 * Enables the file path button
	 */
	public void enableFilePathButton() {
		showFilePathButton = true;

	}

	/**
	 * Returns true if file directory button is enabled
	 */
	public boolean isEnabledDirectoryPathButton() {
		return showDirectoryPathButton;

	}

	/**
	 * Enables the directory path button
	 */
	public void enableDirectoryPathButton() {
		showDirectoryPathButton = true;

	}

	@Override
	public boolean isLinkedToSource() {
		return false;
	}

	@Override
	public boolean checkSourceLink(TableSourceInformation tableSourceInfo)
			throws IllegalStateException {
		return false;
	}

	//#end region

	//#region AVAILABLE STRING ITEMS

	/**
	 * Returns the available items as a list. Returns an empty list if the
	 * available items have not been set.
	 *
	 * @return
	 */
	public List<String> getAvailableStringItems() {
		List<String> itemList = new ArrayList<>();
		if (availableStringItems == null) {
			return itemList;
		}
		String[] itemArray = availableStringItems.split(",");
		for (String item : itemArray) {
			String stringItem = item.trim();
			itemList.add(stringItem);
		}
		return itemList;
	}

	/**
	 * @param availableItems
	 */
	public void setAvailableStringItems(String availableItems) {
		availableStringItems = availableItems;
		refreshControlAdaption();

	}

	/**
	 * @return
	 */
	public boolean hasAvailableItems() {
		boolean hasAvailableItems = availableStringItems != null;
		return hasAvailableItems;
	}

	/**
	 * Clears all rows
	 */
	public void clear() {
		if (rows != null) {
			rows.clear();
		}
	}

	//#end region

	//#region FIRST ROW CREATION

	/**
	 * @return
	 */
	public boolean isFirstRowAutoCreation() {
		return firstRowAutoCreation;
	}

	/**
	 * @param autoCreateFirstRow
	 */
	public void setFirstRowAutoCreation(boolean autoCreateFirstRow) {
		firstRowAutoCreation = autoCreateFirstRow;
	}

	//#end region

	//#end region

}
