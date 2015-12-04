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
		case TEXT:
			cellEditor = new TreezStringCellEditor(parent);
			break;
		case BOOLEAN:
			cellEditor = new TreezBooleanCellEditor(parent);
			break;
		case COLOR:
			String message = "The cell editor for column type " + columnType + " is not yet implemented.";
			throw new IllegalArgumentException(message);
			//break;
		case DOUBLE:
			cellEditor = new TreezDoubleCellEditor(parent);
			break;
		case ENUM:
			String messagee = "The cell editor for column type " + columnType + " is not yet implemented.";
			throw new IllegalArgumentException(messagee);
			//break;
		case INTEGER:
			cellEditor = new TreezIntegerCellEditor(parent);
			break;
		default:
			String messagedef = "The column type " + columnType + " is not known.";
			throw new IllegalArgumentException(messagedef);

		}
		return cellEditor;

	}

	//#end region

}
