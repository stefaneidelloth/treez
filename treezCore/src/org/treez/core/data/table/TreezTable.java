package org.treez.core.data.table;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.row.Row;

public interface TreezTable extends Adaptable {

	//#region COLUMNS

	/**
	 * Returns the column names (=headers)
	 */
	List<String> getHeaders();

	/**
	 * Returns the column type for the given column header
	 */
	ColumnType getColumnType(String header);

	/**
	 * Returns the class for the data entries of the column with the given column header
	 */
	Class<?> getColumnDataClass(String columnHeader);

	/**
	 * Returns the header tool tip for the given column header
	 */
	String getColumnHeaderTooltip(String header);

	/**
	 * Returns true if the column with the given header is editable
	 */
	Boolean isEditable(String header);

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

	//#end region

	//#region ROWS

	/**
	 * Returns the rows of this table
	 */
	List<Row> getRows();

	/**
	 * Adds a new empty row
	 */
	TreezTable addEmptyRow();

	//#end region

}
