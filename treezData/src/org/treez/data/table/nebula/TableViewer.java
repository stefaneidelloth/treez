
package org.treez.data.table.nebula;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.row.Row;
import org.treez.data.column.EmptyHeaderRenderer;
import org.treez.data.column.HeaderRenderer;
import org.treez.data.row.EmptyRowHeaderRenderer;
import org.treez.data.row.RowHeaderRenderer;
import org.treez.data.table.TreezTableViewer;

/**
 * Shows a table with row headers and copy paste support
 */
public class TableViewer extends GridTableViewer {

	private static final Logger LOG = Logger.getLogger(TreezTableViewer.class);

	//#region ATTRIBUTES

	/**
	 * Separators for clip board
	 */
	static final String COLUMN_SEPARATOR = "\t";

	static final String ROW_SEPARATOR = "\n";

	/**
	 * The table (definition of the columns) this table viewer represents
	 */
	private Table table;

	//#end region

	//#region CONSTRUCTORS

	public TableViewer(Composite parent, Table table) {
		super(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		this.table = table;
		configureTableViewer();
	}

	//#end region

	//#region METHODS

	private void configureTableViewer() {

		//get headers
		List<String> headers = table.getHeaders();

		//get number of columns
		int numberOfColumns = headers.size();

		//enable tool tips
		ColumnViewerToolTipSupport.enableFor(this, ToolTip.NO_RECREATE);

		//get grid of table viewer
		final Grid grid = getGrid();

		//set table layout and style
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		grid.setLayoutData(data);
		grid.setHeaderVisible(true);
		grid.setRowHeaderVisible(true);
		grid.setCellSelectionEnabled(true);

		//create and set header renderer
		HeaderRenderer headerRenderer = createHeaderRenderers(grid);

		//configure columns of table viewer
		configureColumns(headers, numberOfColumns, headerRenderer, grid);

		//set content provider
		setContentProvider(new ArrayContentProvider());

		//add key shortcuts
		Listener keyListener = createKeyListener();
		grid.addListener(SWT.KeyUp, keyListener);

	}

	private Listener createKeyListener() {
		return new Listener() {

			@Override
			public void handleEvent(Event event) {

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
			}

		};
	}

	private void configureColumns(List<String> headers, int numberOfColumns, HeaderRenderer headerRenderer, Grid grid) {

		for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
			String header = headers.get(columnIndex);
			ColumnType columnType = table.getColumnType(header);
			GridViewerColumn column = new GridViewerColumn(this, SWT.NONE);

			CellLabelProvider labelProvider = table.getLabelProvider(header, columnType);
			column.setLabelProvider(labelProvider);

			// CellEditor cellEditor = table.getCellEditor(header, columnType, grid);
			// column.setEditingSupport(new EditingProvider(this, table, header, cellEditor));

			GridColumn gridColumn = column.getColumn();
			gridColumn.setText(header);
			gridColumn.setHeaderRenderer(headerRenderer);
			String columnToolTip = table.getColumnHeaderTooltip(header);
			gridColumn.setHeaderTooltip(columnToolTip);
			gridColumn.setCellSelectionEnabled(true);
		}
	}

	private static HeaderRenderer createHeaderRenderers(final Grid grid) {
		grid.setEmptyRowHeaderRenderer(new EmptyRowHeaderRenderer());
		grid.setRowHeaderRenderer(new RowHeaderRenderer());
		grid.setEmptyColumnHeaderRenderer(new EmptyHeaderRenderer());
		grid.setTopLeftRenderer(new EmptyHeaderRenderer());
		HeaderRenderer headerRenderer = new HeaderRenderer();
		return headerRenderer;
	}

	/**
	 * Handles the copy action triggered by Ctrl+C
	 */
	void copy() {

		//get map of selected cells
		Map<Integer, List<Integer>> selectedColumnsMap = getSelectedColumnsMap();

		//get headers
		List<String> headers = table.getHeaders();

		//collect column headers
		String copyString = "";
		Set<Integer> usedColumns = getUsedColums(selectedColumnsMap);
		for (int columnIndex : usedColumns) {
			String header = headers.get(columnIndex - 1);
			copyString = copyString + header + COLUMN_SEPARATOR;
		}
		copyString = copyString.substring(0, copyString.length() - COLUMN_SEPARATOR.length()); //remove last separator
		copyString = copyString + ROW_SEPARATOR;

		//collect data
		Set<Integer> selectedRowIndices = selectedColumnsMap.keySet();
		for (Integer rowIndex : selectedRowIndices) {
			Row row = table.getRows().get(rowIndex - 1);
			List<Integer> selectedColumns = selectedColumnsMap.get(rowIndex);
			for (int columnIndex : usedColumns) {
				boolean columnSelected = selectedColumns.contains(columnIndex);
				if (columnSelected) {
					String entry = row.getEntryAsString(headers.get(columnIndex - 1)).trim();
					copyString = copyString + entry + COLUMN_SEPARATOR;
				} else {
					copyString = copyString + "" + COLUMN_SEPARATOR;
				}
			}
			copyString = copyString.substring(0, copyString.length() - COLUMN_SEPARATOR.length()); //remove last separator
			copyString = copyString + ROW_SEPARATOR;
		}

		//copy data to clip board
		Clipboard cb = new Clipboard(Display.getCurrent());
		TextTransfer textTransfer = TextTransfer.getInstance();
		cb.setContents(new Object[] { copyString }, new Transfer[] { textTransfer });

		LOG.debug(copyString);
	}

	/**
	 * Returns all column indices that exist in a row=>columnIndice map
	 *
	 * @param selectedColumnsMap
	 * @return
	 */

	private static Set<Integer> getUsedColums(Map<Integer, List<Integer>> selectedColumnsMap) {
		Set<Integer> selectedRowIndices = selectedColumnsMap.keySet();
		Set<Integer> usedColumns = new HashSet<>();
		for (Integer rowIndex : selectedRowIndices) {
			List<Integer> columnIndice = selectedColumnsMap.get(rowIndex);
			for (int columnIndex : columnIndice) {
				usedColumns.add(columnIndex);
			}
		}
		return usedColumns;
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
		int rowIndex = table.getRows().size() - 2;
		ArrayList<Row> selectedRows = getSelectedRows();
		if (selectedRows.size() > 0) {
			rowIndex = selectedRows.get(0).getIndex();
		}

		//get headers
		List<String> headers = table.getHeaders();

		//split text and paste data as new row
		String[] rowStrings = text.split(ROW_SEPARATOR);
		//		for (String rowString : rowStrings) {
		//			String[] entries = rowString.split(COLUMN_SEPARATOR);
		//Row row = new Row(table);
		//			for (int index = 0; index < entries.length; index++) {
		//				row.setEntry(headers.get(index), entries[index]);
		//			}
		//			table.getRows().add(rowIndex, row);
		//			rowIndex = rowIndex + 1;
		//		}
		//refresh
		refresh();
	}

	/**
	 * Handles the delete action triggered by delete button
	 */
	void delete() {
		List<Row> selectedRows = getFullySelectedRows();
		Collections.reverse(selectedRows);
		for (Row row : selectedRows) {
			deleteRow(row.getIndex());
		}
	}

	/**
	 * Gets the rows from the selection that are fully selected
	 *
	 * @return
	 */
	@SuppressWarnings("checkstyle:magicnumber")
	private List<Row> getFullySelectedRows() {
		List<Row> fullySelectedRows = new ArrayList<>();
		Map<Integer, List<Integer>> selectedColumnsMap = getSelectedColumnsMap();
		Set<Integer> selectedRowIndices = selectedColumnsMap.keySet();
		for (Integer rowIndex : selectedRowIndices) {
			List<Integer> columnIndice = selectedColumnsMap.get(rowIndex);
			boolean isFullySelected = (columnIndice.size() == 6);
			if (isFullySelected) {
				fullySelectedRows.add(table.getRows().get(rowIndex - 1));
			}
		}
		return fullySelectedRows;
	}

	/**
	 * Gets a map that contains the selected columns for each row
	 *
	 * @return
	 */
	private Map<Integer, List<Integer>> getSelectedColumnsMap() {
		HashMap<Integer, List<Integer>> selectedColumnsMap = new HashMap<>();

		Grid grid = getGrid();
		Point[] selectionCoordinates = grid.getCellSelection();
		for (Point coordinates : selectionCoordinates) {
			int row = coordinates.y + 1;
			int column = coordinates.x + 1;

			boolean rowExists = selectedColumnsMap.containsKey(row);
			if (rowExists) {
				List<Integer> columns = selectedColumnsMap.get(row);
				columns.add(column);
			} else {
				List<Integer> columns = new ArrayList<>();
				columns.add(column);
				selectedColumnsMap.put(row, columns);
			}
		}

		return selectedColumnsMap;
	}

	/**
	 * Gets the selected rows
	 *
	 * @return
	 */
	private ArrayList<Row> getSelectedRows() {
		ArrayList<Row> selectedRows = new ArrayList<>();
		GridItem[] selectedItems = getGrid().getSelection();
		for (GridItem gridItem : selectedItems) {
			Row row = (Row) gridItem.getData();
			selectedRows.add(row);
		}
		return selectedRows;
	}

	/**
	 * Adds a new row by duplicating a row with given row index and inserting it at the next position
	 *
	 * @param rowIndex
	 */
	public void addRow(int rowIndex) {

		LOG.debug("add row with index " + rowIndex);

		if (rowIndex > -1) {
			Row currentRow = table.getRows().get(rowIndex);
			Row newRow = currentRow.copy();
			table.getRows().add(rowIndex + 1, newRow);
			refresh();
		} else {
			addEmptyRow();
		}
	}

	/**
	 * Adds an empty row at the end of the table
	 */
	public void addEmptyRow() {
		table.addEmptyRow();
		refresh();
	}

	/**
	 * Deletes the row with given index
	 *
	 * @param rowIndex
	 */
	public void deleteRow(int rowIndex) {
		table.getRows().remove(rowIndex);
		refresh();
	}

	/**
	 * Moves the row with given index down
	 *
	 * @param rowIndex
	 */
	public void downRow(int rowIndex) {
		if (rowIndex + 1 < table.getRows().size()) {
			Collections.swap(table.getRows(), rowIndex, rowIndex + 1);
			refresh();
		}
	}

	/**
	 * Moves the row with given index up
	 *
	 * @param rowIndex
	 */
	public void upRow(int rowIndex) {
		if (rowIndex > 0) {
			Collections.swap(table.getRows(), rowIndex, rowIndex - 1);
			refresh();
		}
	}

	/**
	 * Sets the optimum width for the columns
	 */
	public void optimizeColumnWidths() {

		GridColumn[] tableColumns = getGrid().getColumns();
		for (GridColumn column : tableColumns) {
			column.pack();
		}

	}

	//#end region
}
