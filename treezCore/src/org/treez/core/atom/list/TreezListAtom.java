package org.treez.core.atom.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.Activator;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.copy.CopyHelper;
import org.treez.core.attribute.Consumer;
import org.treez.core.data.cell.CellEditorFactory;
import org.treez.core.data.cell.TreezTableJFaceLabelProvider;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.row.Row;
import org.treez.core.data.table.AbstractTreezTable;
import org.treez.core.treeview.TreeViewerRefreshable;

/**
 * This atom contains a list that consists of Rows (having a single column). This atom implements TreezTable. The
 * corresponding control adaption shows the list and some additional buttons to edit the list.
 */
public class TreezListAtom extends AbstractTreezTable<TreezListAtom> {

	//#region ATTRIBUTES

	private String valueHeader = "Value";

	private boolean showHeaders = true;

	private ColumnType valueColumnType = ColumnType.STRING;

	/**
	 * If this flag is true, and the column type of the treezList is ColumnType.TEXT, an additional button will be shown
	 * that allows to edit the text entries as file path.
	 */
	private boolean showFilePathButton = false;

	/**
	 * If this flag is true, and the column type of the treezList is ColumnType.TEXT, an additional button will be shown
	 * that allows to edit the text entries as directory path.
	 */
	private boolean showDirectoryPathButton = false;

	/**
	 * This String specifies the available items as comma separated list, e.g. item1,item2. If this attribute is not
	 * null a combo box will be used as list cell editor instead of a text field.
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

	private boolean hasInfoColumn = false;

	/**
	 * The header of the info column
	 */
	private String infoHeader = "Info";

	private Map<Object, String> infoMap = new HashMap<>();

	private Map<String, Consumer> modificationConsumers = new HashMap<>();

	//#end region

	//#region CONSTRUCTORS

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

	@Override
	protected TreezListAtom getThis() {
		return this;
	}

	@Override
	public TreezListAtom copy() {
		return new TreezListAtom(this);
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("column.png");
	}

	@Override
	public AbstractControlAdaption createControlAdaption(
			Composite parent,
			FocusChangingRefreshable treeViewRefreshable) {
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

	@Override
	protected List<Object> createContextMenuActions(TreeViewerRefreshable treeViewer) {

		List<Object> actions = new ArrayList<>();
		return actions;
	}

	/**
	 * Returns all value entries of the list as a single String, separated by the given row separator
	 */
	public String getData(String rowSeparator) {

		List<String> stringEntries = new ArrayList<>();
		if (rows != null) {
			for (Row row : rows) {
				String entry = row.getEntryAsString(valueHeader);
				stringEntries.add(entry);
			}
		}

		String allDataString = String.join(rowSeparator, stringEntries);
		return allDataString;
	}

	/**
	 * Returns all data of the list as a single String, separated by the default row separator
	 *
	 * @return
	 */
	@Override
	public String getData() {
		String dataString = getData(ROW_SEPARATOR);
		return dataString;
	}

	/**
	 * Adds a new row with the given object (depending on the ColumnType this can be of different type) *
	 *
	 * @param entry
	 */
	public TreezListAtom addRow(Object entry) {

		//initialize rows if they do not yet exist
		if (rows == null) {
			rows = new ArrayList<Row>();
		}

		//create empty row
		Row row = new Row(this);

		//fill row with entry
		ColumnType columnTypeForHeader = getColumnType(valueHeader);
		Class<?> associatedClass = columnTypeForHeader.getAssociatedClass();

		Object formattedValue;
		try {
			formattedValue = associatedClass.cast(entry);
		} catch (ClassCastException exception) {
			String message = "The value '" + entry.toString() + "' does not have the required type '"
					+ associatedClass.getSimpleName()
					+ "'. Please change the type of the list or the type of the value. ";
			throw new IllegalArgumentException(message);
		}

		row.setEntry(valueHeader, formattedValue);

		String info = infoMap.get(formattedValue);
		row.setEntry(infoHeader, info);

		//LOG.debug("new row:" + row);

		//add row
		rows.add(row);

		//LOG.debug("added");

		return getThis();
	}

	/**
	 * Adds several rows with a given object array (e.g. {"aaa", "bbb"})
	 *
	 * @param data
	 */
	public TreezListAtom addRows(Object[] data) {
		int size = data.length;
		for (int rowIndex = 0; rowIndex < size; rowIndex++) {
			addRow(data[rowIndex]);
		}
		return getThis();
	}

	@Override
	public TreezListAtom addEmptyRow() {
		if (rows == null) {
			rows = new ArrayList<>();
		}
		Row emptyRow = new Row(this);

		boolean hasAvailableItems = hasAvailableItems();
		if (hasAvailableItems) {
			//use first available item as default entry
			String firstItem = this.getAvailableStringItems().get(0);
			emptyRow.setEntry(valueHeader, firstItem);
			emptyRow.setEntry(infoHeader, infoMap.get(firstItem));
		} else {
			//use null as default entry
			emptyRow.setEntry(valueHeader, null);
		}

		rows.add(emptyRow);
		return this;
	}

	/**
	 * Returns true if the header equals the given header
	 *
	 * @param expectedHeader
	 * @return
	 */
	public boolean checkHeader(String expectedHeader) {
		return valueHeader.equals(expectedHeader);
	}

	@Override
	public TreezListAtom deleteAllRows() {
		super.deleteAllRows();
		return this;
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
	@Override
	public TreezListAtom setRows(List<Row> rows) {
		this.rows = rows;
		refreshControlAdaption();
		return getThis();
	}

	public TreezListAtom setItemInfo(Object itemValue, String info) {

		infoMap.put(itemValue, info);
		//updateInfoColumnAndInformListeners();
		return getThis();
	}

	public void updateInfoColumnAndInformListeners() {
		for (Row row : rows) {
			Object value = row.getEntry(valueHeader);
			String info = infoMap.get(value);
			row.setEntry(infoHeader, info);
		}
		if (listControlAdaption != null) {
			listControlAdaption.refresh();
		}

		triggerModificationConsumers();

	}

	public void addModificationConsumer(String key, Consumer consumer) {
		modificationConsumers.put(key, consumer);
	}

	public void triggerModificationConsumers() {
		for (Consumer consumer : modificationConsumers.values()) {
			consumer.consume();
		}
	}

	/**
	 * Sets all rows with the given object array
	 *
	 * @param data
	 */
	public TreezListAtom setRows(Object[] data) {

		Class<?> associatedClass = getColumnType(valueHeader).getAssociatedClass();

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
			currentRow.setEntry(valueHeader, value);
		}

		refreshControlAdaption();
		return getThis();
	}

	//#end region

	//#region HEADER

	public String getValueHeader() {
		return valueHeader;
	}

	public String getInfoHeader() {
		return infoHeader;
	}

	public TreezListAtom setHeader(String header) {
		this.valueHeader = header;
		return getThis();
	}

	public TreezListAtom setInfoHeader(String header) {
		this.infoHeader = header;
		return getThis();
	}

	@Override
	public List<String> getHeaders() {
		List<String> headers = new ArrayList<>();
		headers.add(valueHeader);
		if (hasInfoColumn) {
			headers.add(infoHeader);
		}
		return headers;
	}

	/**
	 * Sets the visibility of the column header
	 *
	 * @param headersAreVisible
	 */
	public TreezListAtom setShowHeaders(boolean headersAreVisible) {
		this.showHeaders = headersAreVisible;
		return getThis();
	}

	/**
	 * Returns true if the header is shown
	 *
	 * @return
	 */
	public boolean getShowHeaders() {
		return showHeaders;
	}

	//#end region

	//#region COLUMN TYPE

	/**
	 * Returns the column type of the list
	 *
	 * @return
	 */
	public ColumnType getColumnType() {
		return valueColumnType;
	}

	/**
	 * Sets the column type
	 *
	 * @param columnType
	 */
	public TreezListAtom setColumnType(ColumnType columnType) {
		this.valueColumnType = columnType;
		return getThis();
	}

	/**
	 * Dummy implementation to fulfill interface
	 *
	 * @return the columnType
	 */
	@Override
	public ColumnType getColumnType(String header) {
		return valueColumnType;
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
		return new TreezTableJFaceLabelProvider(valueHeader, valueColumnType, infoHeader);
	}

	@Override
	public CellLabelProvider getLabelProvider(String header, ColumnType columnType) {
		CellLabelProvider labelProvider = new TreezTableJFaceLabelProvider(header, columnType, infoHeader);
		return labelProvider;
	}

	//#end region

	//#region CELL EDITOR PROVIDER

	/**
	 * Define cell editor
	 */
	@Override
	public CellEditor getCellEditor(String header, ColumnType columnType, Composite parent) {
		CellEditor cellEditor = CellEditorFactory.createCellEditor(columnType, parent);
		return cellEditor;
	}

	//#end region

	@Override
	public Boolean isEditable(String header) {
		return header.equals(valueHeader);
	}

	//#region ROW SEPARATOR

	/**
	 * Get row separator
	 *
	 * @return
	 */
	@Override
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
	public TreezListAtom enableFilePathButton() {
		showFilePathButton = true;
		return getThis();
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
	public TreezListAtom enableDirectoryPathButton() {
		showDirectoryPathButton = true;
		return getThis();

	}

	//#end region

	//#region AVAILABLE STRING ITEMS

	/**
	 * Returns the available items as a list. Returns an empty list if the available items have not been set.
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
	public TreezListAtom setAvailableStringItems(String availableItems) {
		availableStringItems = availableItems;
		refreshControlAdaption();
		return getThis();
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
	public TreezListAtom setFirstRowAutoCreation(boolean autoCreateFirstRow) {
		firstRowAutoCreation = autoCreateFirstRow;
		return getThis();
	}

	@Override
	public void reload() {}

	//#end region

	//#region Info Column

	public boolean hasInfoColumn() {
		return hasInfoColumn;
	}

	public void enableInfoColumn() {
		hasInfoColumn = true;
	}

	//#end region

	//#end region

}
