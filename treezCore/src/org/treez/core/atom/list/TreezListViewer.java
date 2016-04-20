package org.treez.core.atom.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.treez.core.data.cell.EditingProvider;
import org.treez.core.data.cell.TreezComboBoxCellEditor;
import org.treez.core.data.cell.TreezStringCellEditor;
import org.treez.core.data.cell.TreezTableJFaceLabelProvider;
import org.treez.core.data.row.Row;

/**
 * Shows a list with copy paste support
 */
public class TreezListViewer extends Composite {

	private static final Logger LOG = Logger.getLogger(TreezListViewer.class);

	//#region ATTRIBUTES

	/**
	 * Separators for clip board
	 */
	private static final String ROW_SEPARATOR = "\n";

	/**
	 * The TreezListAtom this list viewer represents
	 */
	private TreezListAtom treezList;

	/**
	 * The wrapped table viewer
	 */
	private TableViewer tableViewer;

	/**
	 * The table viewer column
	 */
	private TableViewerColumn tableViewerColumn;

	/**
	 * The table
	 */
	private Table table;

	/**
	 * The table layout
	 */
	private TableColumnLayout columnLayout;

	/**
	 * The single table column
	 */
	private TableColumn tableColumn;

	/**
	 * The label provider for the rows
	 */
	private TreezTableJFaceLabelProvider labelProvider;

	/**
	 * The cell editor for the rows
	 */
	private CellEditor cellEditor;

	/**
	 * Specifies if the column header is shown
	 */
	private boolean showHeader = true;

	/**
	 * Stores a default file path for the file path chooser
	 */
	private String defaultFilePath = null;

	/**
	 * Stores a default directory path for the directory path chooser
	 */
	private String defaultDirectoryPath = null;

	//#end region

	//#region CONSTRUCTORS

	@SuppressWarnings("checkstyle:magicnumber")
	public TreezListViewer(Composite parent, TreezListAtom treezList) {
		super(parent, SWT.NONE);
		this.treezList = treezList;

		this.setLayout(new GridLayout(1, false));
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		//Create an extra composite for the table viewer
		Composite tableViewerContainer = new Composite(this, SWT.NONE);

		//create TableColumnLayout
		columnLayout = new TableColumnLayout();
		tableViewerContainer.setLayout(columnLayout);

		//set layout data
		final GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1);
		layoutData.minimumHeight = 100;

		tableViewerContainer.setLayoutData(layoutData);

		//create table viewer
		createTableViewer(tableViewerContainer);
	}

	//#end region

	//#region METHODS

	private void createTableViewer(Composite tableViewerContainer) {

		tableViewer = new TableViewer(tableViewerContainer,
				SWT.BORDER | SWT.FULL_SELECTION);

		//create column
		tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		tableColumn = tableViewerColumn.getColumn();
		columnLayout.setColumnData(tableColumn, new ColumnWeightData(1, false));
		tableColumn.setResizable(false);

		//set column header
		String columnHeader = treezList.getHeader();
		tableColumn.setText(columnHeader);

		//set label provider
		labelProvider = treezList.getLabelProvider();
		tableViewer.setLabelProvider(labelProvider);

		//set content provider
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());

		//set content
		List<Row> contentRowList = treezList.getRows();
		tableViewer.setInput(contentRowList);

		//enable tool tips
		ColumnViewerToolTipSupport.enableFor(tableViewer, ToolTip.NO_RECREATE);

		//configure table
		table = tableViewer.getTable();
		configureTableViewerTable(table);

		//create cell editor
		createCellEditor();

	}

	private void createCellEditor() {
		List<String> availableItems = treezList.getAvailableStringItems();
		boolean hasAvailableItems = treezList.hasAvailableItems();
		if (hasAvailableItems) {
			//use a combo box cell editor
			cellEditor = new TreezComboBoxCellEditor(table, availableItems);
		} else {
			//use a text cell editor that is able to validate file paths
			cellEditor = new TreezStringCellEditor(table);
		}

		String header = treezList.getHeader();
		EditingSupport editingSupport = new EditingProvider(tableViewer,
				treezList, header, cellEditor);
		tableViewerColumn.setEditingSupport(editingSupport);
	}

	/**
	 * Configures the table of the table viewer
	 *
	 * @param table
	 */
	private void configureTableViewerTable(Table table) {

		//hide/show header
		table.setHeaderVisible(showHeader);
		table.setLinesVisible(true);

		//set table layout and style
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		table.setLayoutData(data);

		//add key shortcuts
		Listener keyListener = (event) -> {
			//LOG.debug("Key event-----------");

			if (event.stateMask == SWT.CTRL && event.keyCode == 'c') {
				copy();
			}

			if (event.stateMask == SWT.CTRL && event.keyCode == 'v') {
				paste();
			}

			if (event.keyCode == SWT.DEL) {
				delete();
			}
		};
		table.addListener(SWT.KeyUp, keyListener);

		//resize the row height using a MeasureItem listener
		final int rowHeight = 23;
		table.addListener(SWT.MeasureItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				event.height = rowHeight;
			}
		});

	}

	/**
	 * Handles the copy action triggered by Ctrl+C
	 */
	void copy() {

		//get map of selected cells
		List<Integer> selectedRowIndices = getSelectedRowIndices();

		//get headers
		String header = treezList.getHeader();

		//collect data
		String copyString = "";

		for (Integer rowIndex : selectedRowIndices) {
			Row row = treezList.getRows().get(rowIndex - 1);

			String entry = row.getEntryAsString(header).trim();
			copyString = copyString + entry + ROW_SEPARATOR;
		}

		//copy data to clip board
		Clipboard cb = new Clipboard(Display.getCurrent());
		TextTransfer textTransfer = TextTransfer.getInstance();
		cb.setContents(new Object[]{copyString}, new Transfer[]{textTransfer});

		LOG.debug(copyString);
	}

	/**
	 * Handles the paste action triggered by Ctrl + V
	 */
	void paste() {

		LOG.debug("paste");
		//get clip board text
		Clipboard cb = new Clipboard(Display.getCurrent());
		TextTransfer textTransfer = TextTransfer.getInstance();
		String text = (String) cb.getContents(textTransfer);

		LOG.debug(text);

		//get row index
		int rowIndex = treezList.getRows().size() - 2;
		ArrayList<Row> selectedRows = getSelectedRows();
		if (selectedRows.size() > 0) {
			rowIndex = selectedRows.get(0).getIndex();
		}

		//get headers
		String header = treezList.getHeader();

		//split text and paste data as new rows
		String[] entries = text.split(ROW_SEPARATOR);
		for (String entry : entries) {
			Row row = new Row(treezList);
			row.setEntry(header, entry);
			treezList.getRows().add(rowIndex, row);
			rowIndex = rowIndex + 1;
		}
		//refresh
		tableViewer.refresh();
	}

	/**
	 * Handles the delete action triggered by delete button
	 */
	void delete() {
		List<Row> selectedRows = getSelectedRows();
		Collections.reverse(selectedRows);
		for (Row row : selectedRows) {
			deleteRow(row.getIndex());
		}
	}

	/**
	 * Gets a map that contains the selected columns for each row
	 *
	 * @return
	 */
	private List<Integer> getSelectedRowIndices() {
		List<Integer> selectedRowIndices = new ArrayList<>();

		Table currentTable = tableViewer.getTable();
		int[] indices = currentTable.getSelectionIndices();
		for (int index : indices) {
			selectedRowIndices.add(index);
		}
		return selectedRowIndices;
	}

	/**
	 * Gets the selected rows
	 *
	 * @return
	 */
	private ArrayList<Row> getSelectedRows() {
		ArrayList<Row> selectedRows = new ArrayList<>();

		List<Row> rows = treezList.getRows();

		List<Integer> selectedIndices = getSelectedRowIndices();
		for (int index : selectedIndices) {
			Row row = rows.get(index);
			selectedRows.add(row);
		}
		return selectedRows;
	}

	/**
	 * Adds a new row by duplicating a row with given row index and inserting it
	 * at the next position
	 *
	 * @param rowIndex
	 */
	public void addRow(int rowIndex) {

		//LOG.debug("add row as duplicate of row with index " + rowIndex);

		if (rowIndex > -1) {
			Row currentRow = treezList.getRows().get(rowIndex);
			Row newRow = currentRow.copy();
			treezList.getRows().add(rowIndex + 1, newRow);
			tableViewer.refresh();

		} else {
			addEmptyRow();
		}
	}

	/**
	 * Adds an empty row at the end of the table
	 */
	public void addEmptyRow() {
		treezList.addEmptyRow();
		tableViewer.refresh();
	}

	/**
	 * Deletes the row with given index
	 *
	 * @param rowIndex
	 */
	public void deleteRow(int rowIndex) {
		if (rowIndex >= 0) {
			treezList.getRows().remove(rowIndex);
			tableViewer.refresh();
		}
	}

	/**
	 * Moves the row with given index down (increases the index)
	 *
	 * @param rowIndex
	 */
	public void downRow(int rowIndex) {
		if (rowIndex + 1 < treezList.getRows().size()) {
			Collections.swap(treezList.getRows(), rowIndex, rowIndex + 1);
			tableViewer.refresh();
		}
	}

	/**
	 * Moves the row with given index up (decreases the index)
	 *
	 * @param rowIndex
	 */
	public void upRow(int rowIndex) {
		if (rowIndex > 0) {
			Collections.swap(treezList.getRows(), rowIndex, rowIndex - 1);
			tableViewer.refresh();
		}
	}

	/**
	 * Edits the currently selected row with a file path chooser
	 *
	 * @param rowIndex
	 */
	public void editRowWidthFilePathChooser(int rowIndex) {
		if (rowIndex >= 0) {
			String header = treezList.getHeader();
			Row row = treezList.getRows().get(rowIndex);
			String oldValue = row.getEntryAsString(header);
			String filePath = selectFilePath(oldValue);
			if (filePath != null) {
				row.setEntry(header, filePath);
			}
			tableViewer.refresh();
		}

	}

	/**
	 * Allows the user to select a file path. Returns null if no file path is
	 * selected.
	 *
	 * @return
	 */
	private String selectFilePath(String defaultValue) {
		FileDialog fileDialog = new FileDialog(
				Display.getCurrent().getActiveShell(), SWT.SINGLE);

		if (defaultValue != null && !defaultValue.isEmpty()) {
			fileDialog.setFilterPath(defaultValue);
		} else if (defaultFilePath != null) {
			fileDialog.setFilterPath(defaultFilePath);
		}

		String firstFile = fileDialog.open();

		if (firstFile != null) {
			defaultFilePath = fileDialog.getFilterPath();
			return firstFile;
		} else {
			return null;
		}
	}

	/**
	 * Edits the currently selected row with a directory path chooser
	 *
	 * @param rowIndex
	 */
	public void editRowWithDirectoryPathChooser(int rowIndex) {
		if (rowIndex >= 0) {
			String header = treezList.getHeader();
			Row row = treezList.getRows().get(rowIndex);
			String oldValue = row.getEntryAsString(header);
			String directoryPath = selectDirectoryPath(oldValue);
			if (directoryPath != null) {
				row.setEntry(header, directoryPath);
			}
			tableViewer.refresh();
		}

	}

	/**
	 * Allows the user to select a directory path. Returns null if no directory
	 * is selected.
	 *
	 * @return
	 */
	private String selectDirectoryPath(String defaultValue) {

		DirectoryDialog directoryDialog = new DirectoryDialog(
				Display.getCurrent().getActiveShell(), SWT.SINGLE);

		if (defaultValue != null && !defaultValue.isEmpty()) {
			directoryDialog.setFilterPath(defaultValue);
		} else if (defaultDirectoryPath != null) {
			directoryDialog.setFilterPath(defaultDirectoryPath);
		}

		String directory = directoryDialog.open();

		if (directory != null) {
			defaultDirectoryPath = directoryDialog.getFilterPath();
			return directory;
		} else {
			return null;
		}
	}

	/**
	 * Enables automatic file path validation
	 */
	public void enablePathValidation() {
		boolean hasAvailableItems = treezList.hasAvailableItems();
		if (hasAvailableItems) {
			String message = "Path validation is not supported if available items are specified.";
			throw new IllegalStateException(message);
		} else {
			if (labelProvider != null) {
				labelProvider.enablePathValidation();
			}
			if (cellEditor != null) {
				TreezStringCellEditor treezCellEditor = (TreezStringCellEditor) cellEditor;
				treezCellEditor.enableValidation();
			}
		}
	}

	/**
	 * Disabled automatic file path validation
	 */
	public void disablePathValidation() {
		boolean hasAvailableItems = treezList.hasAvailableItems();
		if (hasAvailableItems) {
			String message = "This methos must not be used if available items are specified.";
			throw new IllegalStateException(message);
		} else {
			if (labelProvider != null) {
				labelProvider.disablePathValidation();
			}
			if (cellEditor != null) {
				TreezStringCellEditor treezCellEditor = (TreezStringCellEditor) cellEditor;
				treezCellEditor.disableValidation();
			}
		}
	}

	/**
	 * Sets the input for the table viewer
	 *
	 * @param rows
	 */
	public void setInput(List<? extends Row> rows) {
		Control control = tableViewer.getControl();
		if (control != null && !control.isDisposed()) {
			tableViewer.setInput(rows);
		}
	}

	/**
	 * Refreshes the list viewer
	 */
	public void refresh() {
		//re-create cell editor (available items may have changed)
		createCellEditor();

		//refresh table viewer
		tableViewer.refresh(true);
	}

	/**
	 * Returns the selection index
	 *
	 * @return
	 */
	public int getSelectionIndex() {
		int selectionIndex = tableViewer.getTable().getSelectionIndex();
		return selectionIndex;
	}

	public void setShowHeader(boolean showHeader) {
		this.showHeader = showHeader;
		table.setHeaderVisible(showHeader);
	}

	//#end region

}
