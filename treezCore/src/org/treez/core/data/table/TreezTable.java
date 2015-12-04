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
	 *
	 * @return
	 */
	List<String> getHeaders();

	/**
	 * Returns the column type for the given column header
	 *
	 * @param header
	 * @return
	 */
	ColumnType getColumnType(String header);

	/**
	 * Returns the header tool tip for the given column header
	 *
	 * @param header
	 * @return
	 */
	String getColumnHeaderTooltip(String header);

	/**
	 * Returns true if the column with the given header is editable
	 *
	 * @param header
	 * @return
	 */
	Boolean isEditable(String header);

	/**
	 * Returns the rows of this table
	 *
	 * @return
	 */
	List<Row> getRows();

	/**
	 * Adds a new empty row
	 */
	void addEmptyRow();

	/**
	 * Returns a label provider for the given header and column type
	 *
	 * @param header
	 * @param columnType
	 * @return
	 */
	CellLabelProvider getLabelProvider(String header, ColumnType columnType);

	/**
	 * Returns a cell editor for the given header and column type
	 *
	 * @param header
	 * @param columnType
	 * @return
	 */
	CellEditor getCellEditor(String header, ColumnType columnType, Composite parentComposite);

	/**
	 * Returns the class for the data entries of the column with the given column header
	 *
	 * @param columnHeader
	 * @return
	 */
	Class<?> getColumnDataClass(String columnHeader);

	/**
	 * Returns true if the table is linked to a table source
	 *
	 * @return
	 */
	boolean isLinkedToSource();

	/**
	 * Checks if the linked table source is compatible to the given TableSourceInformaiton
	 *
	 * @param tableSourceInfo
	 * @return
	 */
	boolean checkSourceLink(TableSourceInformation tableSourceInfo) throws IllegalStateException;

}
