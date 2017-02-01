package org.treez.data.table.nebula.nat;

import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.treez.core.data.row.Row;
import org.treez.core.data.table.TreezTable;

public class ColumnAccessor implements IColumnAccessor<Row> {

	//#region ATTRIBUTES

	private TreezTable table;

	//#end region

	//#region CONSTRUCTORS

	public ColumnAccessor(TreezTable table) {
		this.table = table;
	}

	//#end region

	//#region ACCESSORS

	@Override
	public Object getDataValue(Row row, int columnIndex) {
		String columnHeader = table.getHeaders().get(columnIndex);
		return row.getEntry(columnHeader);
	}

	@Override
	public void setDataValue(Row row, int columnIndex, Object newValue) {
		String columnHeader = table.getHeaders().get(columnIndex);
		row.setEntry(columnHeader, newValue);
	}

	@Override
	public int getColumnCount() {
		return table.getHeaders().size();
	}

	//#end region

}
