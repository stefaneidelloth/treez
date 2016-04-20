package org.treez.data.cell;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.row.Row;
import org.treez.core.utils.Utils;

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

		//set background value
		if (columnType == ColumnType.COLOR) {
			RGB rgb = Utils.convertToRGB(value);
			Color color = new Color(Display.getCurrent(), rgb.red, rgb.green, rgb.blue);
			cell.setBackground(color);
		}
	}

	//#end region
}
