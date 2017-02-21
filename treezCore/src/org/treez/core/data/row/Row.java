package org.treez.core.data.row;

import java.util.HashMap;
import java.util.Map;

import org.treez.core.atom.copy.Copiable;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.table.TreezTable;

/**
 * Represents a table row
 */
public class Row implements Copiable<Row> {

	//#region ATTRIBUTES

	private final String NULL_STRING = "(Null)";

	/**
	 * The table this row belongs to
	 */
	protected TreezTable table;

	/**
	 * Maps from column name to value object
	 */
	protected Map<String, Object> entryMap = null;

	private boolean hasValidationErrors = false;

	//#end region

	//#region CONSTRUCTORS

	public Row(TreezTable table) {
		this.table = table;
		entryMap = new HashMap<String, Object>();
	}

	/**
	 * Copy constructor for same table
	 */
	private Row(Row row) {
		this.table = row.table;
		this.entryMap = new HashMap<String, Object>();
		for (String header : row.entryMap.keySet()) {
			Object value = row.entryMap.get(header);
			this.entryMap.put(header, value);
		}
	}

	/**
	 * Copy constructor for new table
	 */
	private Row(Row row, TreezTable newTable) {
		this.table = newTable;
		this.entryMap = new HashMap<String, Object>();
		for (String header : row.entryMap.keySet()) {
			Object value = row.entryMap.get(header);
			this.entryMap.put(header, value);
		}
	}

	//#end region

	//#region METHODS

	@Override
	public Row copy() {
		return new Row(this);
	}

	@Override
	public String toString() {
		String rowCommand = "addRow(";

		Iterable<Object> values = entryMap.values();
		boolean valueAdded = false;
		for (Object value : values) {
			valueAdded = true;
			boolean isString = value instanceof String;
			if (isString) {
				rowCommand += "\"" + value + "\", ";
			} else {
				rowCommand += value.toString() + ", ";
			}
		}

		if (valueAdded) {
			rowCommand = rowCommand.substring(0, rowCommand.length() - 2);
		}
		rowCommand += ");";
		return rowCommand;
	}

	/**
	 * Copies the row for a new table (use this if you copy the complete parent table)
	 *
	 * @param newTable
	 * @return
	 */
	public Row copyForNewTable(TreezTable newTable) {
		return new Row(this, newTable);
	}

	/**
	 * Returns true if all entries of this row are empty
	 *
	 * @return
	 */
	public boolean isEmpty() {
		boolean empty = true;
		for (String header : table.getHeaders()) {
			Object entry = entryMap.get(header);
			if (entry instanceof String) {
				if (!((String) entry).equals("")) {
					empty = false;
					break;
				}
			}

			//TODO: check for other types

		}
		return empty;
	}

	/**
	 * Returns true if this row is the last row
	 *
	 * @return
	 */
	public boolean isLastRow() {
		int index = getIndex();
		int size = table.getRows().size();
		return (index == size - 1);
	}

	/**
	 * Returns the index of the row
	 *
	 * @return
	 */
	public int getIndex() {
		return table.getRows().indexOf(this);
	}

	//#end region

	//#region ACCESSORS

	/**
	 * Returns the value for a given column name
	 *
	 * @param columnHeader
	 * @return
	 */
	public Object getEntry(String columnHeader) {
		return entryMap.get(columnHeader);
	}

	public void setEntryUnchecked(String columnHeader, Object value) {
		this.entryMap.put(columnHeader, value);
	}

	/**
	 * Sets a value in this row for a given column header. The value is checked to be compatible to the column.
	 */
	public void setEntry(String columnHeader, Object value) {

		boolean columnExists = table.getHeaders().contains(columnHeader);
		if (columnExists) {
			if (value != null) {
				Class<?> valueClass = value.getClass();
				ColumnType valueColumnType = ColumnType.getType(valueClass);
				ColumnType columnType = table.getColumnType(columnHeader);

				boolean columnTypeFitsToValue = valueColumnType.equals(columnType);
				if (columnTypeFitsToValue) {
					//set entry
					this.entryMap.put(columnHeader, value);
				} else {
					String message = "The class '" + valueClass.getSimpleName() + "' of the given value '" + value
							+ "' is not compatible to the " + " column type '" + columnType + "'";

					throw new IllegalArgumentException(message);
				}
			} else {
				this.entryMap.put(columnHeader, value);
			}

		} else {
			String message = "The columnHeader '" + columnHeader + "' does not exist and the value '" + value
					+ "' could not be set.";
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Get an object that can be used by the corresponding cell editor
	 *
	 * @param columnHeader
	 * @return
	 */
	public Object getObject(String columnHeader) {

		ColumnType columnType = table.getColumnType(columnHeader);
		Object entry = entryMap.get(columnHeader);

		Object object = null;

		switch (columnType) {
		case INTEGER:
			object = entry;
			break;
		case DOUBLE:
			object = entry;
			break;
		case STRING:
			object = entry;
			break;
		default:
			throw new IllegalStateException("Unknown column type " + columnType);
		}

		return object;
	}

	/**
	 * Returns a string representation of the entry for the given header
	 *
	 * @param header
	 * @return
	 */
	public String getEntryAsString(String header) {
		Object value = getEntry(header);

		if (value != null) {
			return value.toString();
		} else {
			return NULL_STRING;
		}
	}

	/**
	 * Returns true if this row has a validation error
	 */
	public boolean hasValidationErrors() {
		return hasValidationErrors;
	}

	/**
	 * Sets hasValidationErrors to true
	 */
	public void enableValidationError() {
		hasValidationErrors = true;
	}

	/**
	 * Sets hasValidationErrors to false
	 */
	public void disableValidationError() {
		hasValidationErrors = false;
	}

	public String getNullString() {
		return NULL_STRING;
	}

	/**
	 * Returns true if the given value equals the null string
	 */
	public boolean isNullString(String label) {
		boolean isNullString = NULL_STRING.equals(label);
		return isNullString;
	}

	//#end region

}
