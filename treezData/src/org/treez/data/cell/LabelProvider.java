package org.treez.data.cell;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.row.Row;

/**
 * Label provider for table entries
 */
public class LabelProvider extends CellLabelProvider {

	//#region ATTRIBUTES

	private String header;

	private ColumnType columnType;

	//#end region

	//#region CONSTRUCTORS

	public LabelProvider(String header, ColumnType columnType) {
		super();
		this.header = header;
		this.columnType = columnType;
	}

	//#end region

	//#region METHODS

	@Override
	public void update(ViewerCell cell) {

		//get element
		Row row = (Row) cell.getElement();

		//set label
		String value = row.getEntryAsString(header);
		cell.setText(value);

	}

	//#end region
}
