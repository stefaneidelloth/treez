package org.treez.data.database;

import java.util.ArrayList;
import java.util.List;

import org.treez.core.data.column.ColumnBlueprint;
import org.treez.core.data.column.ColumnType;

public class TableData implements org.treez.data.tableImport.TableData {

	//#region ATTRIBUTES

	private List<ColumnBlueprint> columnBlueprints;

	private List<String> headers;

	private List<List<Object>> rowData;

	//#end region

	//#region CONSTRUCTORS

	public TableData(List<ColumnBlueprint> columnBlueprints, List<List<Object>> rowData) {
		this.columnBlueprints = columnBlueprints;
		this.rowData = rowData;
		extractHeadersFromColumnBlueprints(columnBlueprints);
	}

	//#end region

	//#region METHODS

	private void extractHeadersFromColumnBlueprints(List<ColumnBlueprint> columnBlueprints) {
		headers = new ArrayList<>();
		for (ColumnBlueprint columnBlueprint : columnBlueprints) {
			headers.add(columnBlueprint.getName());
		}
	}

	//#end region

	//#region ACCESSORS

	@Override
	public List<String> getHeaderData() {
		return headers;
	}

	@Override
	public ColumnType getColumnType(String header) {

		for (ColumnBlueprint columnBlueprint : columnBlueprints) {
			if (header.equals(columnBlueprint.getName())) {
				return columnBlueprint.getType();
			}
		}
		throw new IllegalStateException("Could not determine ColumnType for column '" + header + "'");
	}

	@Override
	public List<List<Object>> getRowData() {
		return rowData;
	}

	//#end region

}
