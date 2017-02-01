package org.treez.core.data.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.treez.core.atom.copy.CopyHelper;
import org.treez.core.atom.uisynchronizing.AbstractUiSynchronizingAtom;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.row.Row;

public abstract class AbstractTreezTable<A extends AbstractTreezTable<A>> extends AbstractUiSynchronizingAtom<A>
		implements
		TreezTable {

	//#region ATTRIBUTES

	protected List<Row> rows = null;

	private List<Row> pagedRows = null;

	private int rowIndexOffset = 0;

	/**
	 * Column separator for text representations
	 */
	protected final String COLUMN_SEPARATOR = ";";

	/**
	 * Row separator for text representations
	 */
	protected final String ROW_SEPARATOR = "\n";

	//#end region

	//#region CONSTRUCTORS

	public AbstractTreezTable(String name) {
		super(name);
	}

	public AbstractTreezTable(AbstractTreezTable<A> tableToCopy) {
		super(tableToCopy);
		rows = CopyHelper.copyRowsForTargetTable(tableToCopy.rows, this);
	}

	//#end region

	//#region METHODS

	public AbstractTreezTable<A> addRow(Row row) {
		if (rows == null) {
			rows = new ArrayList<Row>();
		}
		rows.add(row);
		return this;
	}

	/**
	 * Adds a new row with a given object entries
	 */
	public AbstractTreezTable<A> addRow(Object... entries) {
		List<Object> entryList = Arrays.asList(entries);
		addRow(entryList);
		return this;
	}

	/**
	 * Adds a new row with a given object array
	 */
	public AbstractTreezTable<A> addRow(List<Object> data) {

		//create empty row
		Row row = new Row(this);

		//fill row with data from NativeArray
		int size = data.size();
		//LOG.debug("size:" + size);

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

		//LOG.debug("new row:" + row);

		addRow(row);

		//LOG.debug("added");

		return this;
	}

	/**
	 * Adds several rows with a given java script Native Array (e.g. [[0,0],[1,1]]
	 *
	 * @param data
	 */
	public AbstractTreezTable<A> addRows(List<List<Object>> data) {

		int size = data.size();
		for (int rowIndex = 0; rowIndex < size; rowIndex++) {

			addRow(data.get(rowIndex));
		}
		return this;
	}

	/**
	 * Adds several rows with a given java script Native Array (e.g. [[0,0],[1,1]]
	 *
	 * @param header
	 * @param data
	 */
	public AbstractTreezTable<A> setColumn(String header, Object[] data) {

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
		return this;
	}

	@Override
	public AbstractTreezTable<A> addEmptyRow() {
		if (rows == null) {
			rows = new ArrayList<>();
		}
		rows.add(new Row(this));
		return this;
	}

	public AbstractTreezTable<A> deleteAllRows() {
		setRows(new ArrayList<Row>());
		return this;
	}

	@Override
	public boolean checkSourceLink(TableSourceInformation tableSourceInfo) throws IllegalStateException {
		return false;
	}

	//#end region

	//#region ACCESSORS

	@Override
	public List<Row> getRows() {
		if (rows == null) {
			return new ArrayList<>();
		}
		return rows;
	}

	public AbstractTreezTable<A> setRows(List<Row> rows) {
		this.rows = rows;
		return this;
	}

	@Override
	public List<Row> getPagedRows() {
		if (pagedRows != null) {
			return pagedRows;
		} else {
			return rows;
		}
	}

	@Override
	public AbstractTreezTable<A> setPagedRows(List<Row> pagedRows) {
		this.pagedRows = pagedRows;
		return this;
	}

	@Override
	public int getRowIndexOffset() {
		return rowIndexOffset;
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

	@Override
	public AbstractTreezTable<A> setRowIndexOffset(int rowIndexOffset) {
		this.rowIndexOffset = rowIndexOffset;
		return this;
	}

	public String getRowSeparator() {
		return ROW_SEPARATOR;
	}

	public String getColumnSeparator() {
		return COLUMN_SEPARATOR;
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

	@Override
	public boolean isLinkedToSource() {
		return false;
	}

	//#end region

}
