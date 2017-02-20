
package org.treez.data.table.nebula.nat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.resize.command.InitializeAutoResizeColumnsCommand;
import org.eclipse.nebula.widgets.nattable.resize.command.InitializeAutoResizeRowsCommand;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.style.theme.ModernNatTableThemeConfiguration;
import org.eclipse.nebula.widgets.nattable.util.GCFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.data.row.Row;
import org.treez.core.data.table.PaginatedTreezTable;
import org.treez.data.table.TreezTableViewer;

public class TreezNatTable extends NatTable {

	private static final Logger LOG = Logger.getLogger(TreezTableViewer.class);

	//#region ATTRIBUTES

	private PaginatedTreezTable treezTable;

	private Refreshable pagination;

	//#end region

	//#region CONSTRUCTORS

	public TreezNatTable(Composite parent, PaginatedTreezTable table, Refreshable pagination) {
		super(parent, createNatLayers(table));
		this.treezTable = table;
		this.pagination = pagination;

		this.setTheme(new ModernNatTableThemeConfiguration());

		Listener keyListener = createKeyListener();
		this.addListener(SWT.KeyUp, keyListener);
	}

	//#end region

	//#region METHODS

	private static ILayer createNatLayers(PaginatedTreezTable table) {
		IDataProvider bodyDataProvider = new BodyDataProvider(table);
		BodyLayerStack bodyLayerStack = new BodyLayerStack(bodyDataProvider);

		List<String> headers = table.getHeaders();
		String[] headersArray = headers.toArray(new String[headers.size()]);
		DefaultColumnHeaderDataProvider colHeaderDataProvider = new DefaultColumnHeaderDataProvider(headersArray);
		ColumnHeaderLayerStack columnHeaderLayerStack = new ColumnHeaderLayerStack(
				colHeaderDataProvider,
				bodyLayerStack);

		IDataProvider rowHeaderDataProvider = new RowHeaderDataProvider(bodyDataProvider, table);
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

	private Listener createKeyListener() {
		return new Listener() {

			@Override
			public void handleEvent(Event event) {

				//LOG.debug("Key event-----------");

				if (event.stateMask == SWT.CTRL && event.keyCode == 'v') {
					paste();
				}

				if (event.keyCode == SWT.DEL) {
					delete();
				}

				if (event.keyCode == SWT.F5) {
					treezTable.reload();
				}

			}

		};
	}

	/**
	 * Handles the paste action triggered by Ctrl + V
	 */
	void paste() {

		final String columnSeparator = "\t";
		final String rowSeparator = "\n";

		LOG.debug("paste");
		//get clip board text
		Clipboard cb = new Clipboard(Display.getCurrent());
		TextTransfer textTransfer = TextTransfer.getInstance();
		String text = (String) cb.getContents(textTransfer);

		LOG.debug(text);

		//get row index
		int rowIndex = treezTable.getRows().size() - 2;
		ArrayList<Row> selectedRows = getSelectedRows();
		if (selectedRows.size() > 0) {
			rowIndex = selectedRows.get(0).getIndex();
		}

		//get headers
		List<String> headers = treezTable.getHeaders();

		//split text and paste data as new row
		String[] rowStrings = text.split(rowSeparator);
		for (String rowString : rowStrings) {
			String[] entries = rowString.split(columnSeparator);
			Row row = new Row(treezTable);
			for (int index = 0; index < entries.length; index++) {
				row.setEntry(headers.get(index), entries[index]);
			}
			treezTable.getRows().add(rowIndex, row);
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
			int pagedRowIndex = row.getIndex();
			deletePagedRow(pagedRowIndex);
		}
		pagination.refresh();
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
		int numberOfColumns = this.treezTable.getHeaders().size();
		for (Integer rowIndex : selectedRowIndices) {
			List<Integer> columnIndice = selectedColumnsMap.get(rowIndex);
			boolean isFullySelected = (columnIndice.size() == numberOfColumns);
			if (isFullySelected) {
				fullySelectedRows.add(treezTable.getRows().get(rowIndex));
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
		List<Row> rows = treezTable.getRows();
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
			Row currentRow = treezTable.getRows().get(rowIndex);
			Row newRow = currentRow.copy();
			treezTable.getPagedRows().add(rowIndex + 1, newRow);
			refresh();
		} else {
			addEmptyRow();
		}
	}

	/**
	 * Adds an empty row at the end of the table
	 */
	public void addEmptyRow() {
		treezTable.addEmptyRow();
		refresh();
	}

	/**
	 * Deletes the rows with given indice
	 */
	public void deletePagedRows(List<Integer> rowIndice) {
		treezTable.getPagedRows().removeAll(rowIndice);
		refresh();
	}

	/**
	 * Deletes the row with given index
	 */
	public void deletePagedRow(int rowIndex) {
		treezTable.getPagedRows().remove(rowIndex);
		refresh();
	}

	/**
	 * Deletes the row with given index
	 */
	public void deleteRow(int rowIndex) {
		treezTable.getRows().remove(rowIndex);
		refresh();
	}

	public void moveRowsDownAndSelect(List<Integer> selectedIndices) {
		int numberOfRows = treezTable.getRows().size();
		for (int index = selectedIndices.size() - 1; index > -1; index--) {
			int rowIndex = selectedIndices.get(index);
			downRow(rowIndex);
			if (rowIndex + 1 < numberOfRows) {
				selectedIndices.set(index, rowIndex + 1);
			}
		}
		refresh();
		selectRows(selectedIndices);
	}

	/**
	 * Moves the row with given index down
	 *
	 * @param rowIndex
	 */
	public void downRow(int rowIndex) {
		if (rowIndex + 1 < treezTable.getRows().size()) {
			Collections.swap(treezTable.getRows(), rowIndex, rowIndex + 1);
			refresh();
		}
	}

	public void moveRowsUpAndSelect(List<Integer> selectedIndices) {

		for (int index = 0; index < selectedIndices.size(); index++) {
			int rowIndex = selectedIndices.get(index);
			upRow(rowIndex);
			if (rowIndex > 0) {
				selectedIndices.set(index, rowIndex - 1);
			}
		}
		refresh();
		selectRows(selectedIndices);
	}

	/**
	 * Moves the row with given index up
	 *
	 * @param rowIndex
	 */
	public void upRow(int rowIndex) {
		if (rowIndex > 0) {
			Collections.swap(treezTable.getRows(), rowIndex, rowIndex - 1);
			refresh();
		}
	}

	/**
	 * Sets the optimum width for the columns
	 */
	public void optimizeColumnWidths() {

		IConfigRegistry registry = getConfigRegistry();
		GCFactory gcFactory = new GCFactory(this);

		for (int columnIndex = 0; columnIndex < getColumnCount(); columnIndex++) {
			InitializeAutoResizeColumnsCommand columnCommand = new InitializeAutoResizeColumnsCommand(
					this,
					columnIndex,
					registry,
					gcFactory);

			doCommand(columnCommand);
		}

		for (int rowIndex = 0; rowIndex < getRowCount(); rowIndex++) {
			InitializeAutoResizeRowsCommand rowCommand = new InitializeAutoResizeRowsCommand(
					this,
					rowIndex,
					registry,
					gcFactory);
			doCommand(rowCommand);
		}

	}

	private void selectRows(List<Integer> selectedIndices) {
		SelectionLayer selectionLayer = getSelectionLayer();
		for (int rowIndex : selectedIndices) {
			selectionLayer.selectRow(0, rowIndex, false, true);
		}
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

	public List<Integer> getSelectionIndices() {
		SelectionLayer selectionLayer = getSelectionLayer();
		int[] indices = selectionLayer.getFullySelectedRowPositions();
		if (indices.length > 0) {
			return IntStream.of(indices).boxed().collect(Collectors.toList());
		}
		List<Integer> listContaindingFirstIndex = new ArrayList<>();
		listContaindingFirstIndex.add(0);
		return listContaindingFirstIndex;
	}

	//#end region
}
