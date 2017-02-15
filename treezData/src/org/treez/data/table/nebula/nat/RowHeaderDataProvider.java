package org.treez.data.table.nebula.nat;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.treez.core.data.table.PaginatedTreezTable;

public class RowHeaderDataProvider implements IDataProvider {

	//#region ATTRIBUTES

	private PaginatedTreezTable table;

	private IDataProvider bodyDataProvider;

	//#end region

	//#region CONSTRUCTORS

	public RowHeaderDataProvider(IDataProvider bodyDataProvider, PaginatedTreezTable table) {
		this.table = table;
		this.bodyDataProvider = bodyDataProvider;
	}

	//#end region

	//#region ACCESSORS

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public int getRowCount() {
		return bodyDataProvider.getRowCount();
	}

	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {
		return Integer.valueOf(rowIndex + table.getRowIndexOffset() + 1);
	}

	@Override
	public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
		throw new UnsupportedOperationException();
	}

	//#end region

}
