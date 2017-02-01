package org.treez.core.data.table;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.row.Row;

/**
 * Treez table interface
 */
public interface TreezTable extends Adaptable {

	/**
	 * Returns the column names (=headers)
	 */
	List<String> getHeaders();

	/**
	 * Returns the column type for the given column header
	 */
	ColumnType getColumnType(String header);

	/**
	 * Returns the header tool tip for the given column header
	 */
	String getColumnHeaderTooltip(String header);

	/**
	 * Returns true if the column with the given header is editable
	 */
	Boolean isEditable(String header);

	/**
	 * Returns the rows of this table
	 */
	List<Row> getRows();

	/**
	 * Adds a new empty row
	 */
	TreezTable addEmptyRow();

	/**
	 * Returns a label provider for the given header and column type TODO: This interface should not depend on classes
	 * of JFace
	 */
	CellLabelProvider getLabelProvider(String header, ColumnType columnType);

	/**
	 * Returns a cell editor for the given header and column type TODO: This interface should not depend on classes of
	 * JFace
	 */
	CellEditor getCellEditor(String header, ColumnType columnType, Composite parentComposite);

	/**
	 * Returns the class for the data entries of the column with the given column header
	 */
	Class<?> getColumnDataClass(String columnHeader);

	/**
	 * Returns true if the table is linked to a table source
	 */
	boolean isLinkedToSource();

	/**
	 * Checks if the linked table source is compatible to the given TableSourceInformaiton
	 */
	boolean checkSourceLink(TableSourceInformation tableSourceInfo) throws IllegalStateException;

	/**
	 * Returns a list of rows. If no paged rows have been explicitly set before, all rows will be returned.
	 */
	List<Row> getPagedRows();

	/**
	 * Stores a list of rows that can be retrieved with getPagedRows();
	 */
	TreezTable setPagedRows(List<Row> pagedRows);

	/**
	 * Returns offset for the display of row numbers
	 */
	int getRowIndexOffset();

	/**
	 * Sets offset for the display of row numbers
	 */
	TreezTable setRowIndexOffset(int offset);

}
