package org.treez.model.tableImport;

import java.util.List;

import org.treez.core.data.column.ColumnType;

/**
 * @author eis
 */
public interface TableData {

	/**
	 * @return
	 */
	List<String> getHeaderData();

	/**
	 * @param header
	 * @return
	 */
	ColumnType getColumnType(String header);

	/**
	 * @return
	 */
	List<List<String>> getRowData();

}
