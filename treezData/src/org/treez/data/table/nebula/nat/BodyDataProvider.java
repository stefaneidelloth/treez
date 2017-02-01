package org.treez.data.table.nebula.nat;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.treez.core.data.row.Row;
import org.treez.core.data.table.TreezTable;

public class BodyDataProvider implements IDataProvider {

	//#region ATTRIBUTES

	private TreezTable table;

	private IColumnAccessor<Row> columnAccessor;

	//#end region

	//#region CONSTRUCTORS

	public BodyDataProvider(TreezTable table) {
		this.table = table;
		columnAccessor = new ColumnAccessor(table);
	}

	//#end region

	//#region ACCESSORS

	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {
		Row row = getRow(rowIndex);
		return columnAccessor.getDataValue(row, columnIndex);
	}

	@Override
	public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
		Row row = getRow(rowIndex);
		columnAccessor.setDataValue(row, columnIndex, newValue);
	}

	@Override
	public int getColumnCount() {
		return columnAccessor.getColumnCount();
	}

	@Override
	public int getRowCount() {
		return getRows().size();
	}

	private List<Row> getRows() {
		return table.getPagedRows();
	}

	private Row getRow(int rowIndex) {
		return getRows().get(rowIndex);
	}

	//#end region

}
