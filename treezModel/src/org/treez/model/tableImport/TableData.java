package org.treez.model.tableImport;

import java.util.List;

import org.treez.core.data.column.ColumnType;

public interface TableData {

	List<String> getHeaderData();

	ColumnType getColumnType(String header);

	List<List<String>> getRowData();

}
