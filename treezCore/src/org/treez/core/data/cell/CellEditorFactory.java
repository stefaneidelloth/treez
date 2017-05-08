package org.treez.core.data.cell;

import org.eclipse.swt.widgets.Composite;
import org.treez.core.data.column.ColumnType;

/**
 * Create cell editor for specific ColumnTypes
 */
public final class CellEditorFactory {

	//#region CONSTRUCTORS

	/**
	 * Private Constructor that prevents construction
	 */
	private CellEditorFactory() {}

	//#end region

	//#region METHODS

	/**
	 * Creates a cell editor for the given column type
	 *
	 * @param columnType
	 * @param parent
	 * @return
	 */
	public static TreezStringCellEditor createCellEditor(ColumnType columnType, Composite parent) {

		TreezStringCellEditor cellEditor = null;

		switch (columnType) {

		case INTEGER:
			cellEditor = new TreezIntegerCellEditor(parent);
			break;
		case DOUBLE:
			cellEditor = new TreezDoubleCellEditor(parent);
			break;
		case STRING:
			cellEditor = new TreezStringCellEditor(parent, null);
			break;
		//case BOOLEAN:
		//	cellEditor = new TreezBooleanCellEditor(parent);
		//	break;
		default:
			String messagedef = "The column type " + columnType + " is not known.";
			throw new IllegalArgumentException(messagedef);

		}
		return cellEditor;

	}

	//#end region

}
