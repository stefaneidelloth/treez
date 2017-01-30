
package org.treez.data.table.nebula;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.style.theme.ModernNatTableThemeConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.treez.core.data.row.Row;
import org.treez.core.data.table.TreezTable;
import org.treez.data.table.TreezTableViewer;

public class TreezNatTable extends NatTable {

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
	private TreezTable table;

	//#end region

	//#region CONSTRUCTORS

	public TreezNatTable(Composite parent, TreezTable table) {
		super(parent, createNatLayers(table));
		this.table = table;

		this.setTheme(new ModernNatTableThemeConfiguration());

		Listener keyListener = createKeyListener();
		this.addListener(SWT.KeyUp, keyListener);
	}

	//#end region

	//#region METHODS

	private static ILayer createNatLayers(TreezTable table) {
		IDataProvider bodyDataProvider = createBodyDataProvider(table);
		BodyLayerStack bodyLayerStack = new BodyLayerStack(bodyDataProvider);

		List<String> headers = table.getHeaders();
		String[] headersArray = headers.toArray(new String[headers.size()]);
		DefaultColumnHeaderDataProvider colHeaderDataProvider = new DefaultColumnHeaderDataProvider(headersArray);
		ColumnHeaderLayerStack columnHeaderLayerStack = new ColumnHeaderLayerStack(
				colHeaderDataProvider,
				bodyLayerStack);

		DefaultRowHeaderDataProvider rowHeaderDataProvider = new DefaultRowHeaderDataProvider(bodyDataProvider);
		RowHeaderLayerStack rowHeaderLayerStack = new RowHeaderLayerStack(rowHeaderDataProvider, bodyLayerStack);

		DefaultCornerDataProvider cornerDataProvider = new DefaultCornerDataProvider(
				colHeaderDataProvider,
				rowHeaderDataProvider);

		CornerLayer cornerLayer = new CornerLayer(
				new DataLayer(cornerDataProvider),
				rowHeaderLayerStack,
				columnHeaderLayerStack);

		GridLayer gridLayer = new GridLayer(bodyLayerStack, columnHeaderLayerStack, rowHeaderLayerStack, cornerLayer);
		return gridLayer;
	}

	private static IDataProvider createBodyDataProvider(TreezTable table) {

		IColumnAccessor<Row> columnAccessor = new IColumnAccessor<Row>() {

			@Override
			public Object getDataValue(Row row, int columnIndex) {
				String columnHeader = table.getHeaders().get(columnIndex);
				return row.getEntry(columnHeader);
			}

			@Override
			public void setDataValue(Row row, int columnIndex, Object newValue) {
				String columnHeader = table.getHeaders().get(columnIndex);
				row.setEntry(columnHeader, newValue);
			}

			@Override
			public int getColumnCount() {
				return table.getHeaders().size();
			}

		};
		return new ListDataProvider<Row>(table.getRows(), columnAccessor);
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
		for (String rowString : rowStrings) {
			String[] entries = rowString.split(COLUMN_SEPARATOR);
			Row row = new Row(table);
			for (int index = 0; index < entries.length; index++) {
				row.setEntry(headers.get(index), entries[index]);
			}
			table.getRows().add(rowIndex, row);
			rowIndex = rowIndex + 1;
		}
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

		SelectionLayer selectionLayer = getSelectionLayer();

		Collection<ILayerCell> selectedCells = selectionLayer.getSelectedCells();
		for (ILayerCell cell : selectedCells) {
			int rowIndex = cell.getRowIndex();
			int columnIndex = cell.getColumnIndex();
			boolean rowExists = selectedColumnsMap.containsKey(rowIndex);
			if (rowExists) {
				List<Integer> columns = selectedColumnsMap.get(rowIndex);
				columns.add(columnIndex);
			} else {
				List<Integer> columns = new ArrayList<>();
				columns.add(columnIndex);
				selectedColumnsMap.put(rowIndex, columns);
			}
		}

		return selectedColumnsMap;
	}

	private ArrayList<Row> getSelectedRows() {

		ArrayList<Row> selectedRows = new ArrayList<>();

		SelectionLayer selectionLayer = getSelectionLayer();
		int[] indices = selectionLayer.getFullySelectedRowPositions();
		List<Row> rows = table.getRows();
		for (int rowIndex : indices) {
			Row row = rows.get(rowIndex);
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

		//TODO

		//see auto resizing columns:
		//http://www.eclipse.org/nattable/documentation.php?page=faq

		//GridColumn[] tableColumns = getColumns();
		//for (GridColumn column : tableColumns) {
		//	column.pack();
		//}

	}

	//#end region

	//#region ACCESSORS

	private SelectionLayer getSelectionLayer() {
		GridLayer gridLayer = (GridLayer) this.getLayer();
		BodyLayerStack bodyLayer = (BodyLayerStack) gridLayer.getBodyLayer();
		SelectionLayer selectionLayer = bodyLayer.getSelectionLayer();
		return selectionLayer;
	}

	public Integer getSelectionIndex() {
		SelectionLayer selectionLayer = getSelectionLayer();
		int[] indices = selectionLayer.getFullySelectedRowPositions();
		if (indices.length > 0) {
			return indices[0];
		}
		return 0;
	}

	//#end region
}
