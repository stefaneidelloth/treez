package org.treez.core.data.table;

import java.util.List;

import org.treez.core.data.row.Row;

/**
 * Treez table interface
 */
public interface PaginatedTreezTable extends TreezTable {

	/**
	 * Returns a list of rows. If no paged rows have been explicitly set before, all rows will be returned.
	 */
	List<Row> getPagedRows();

	/**
	 * Stores a list of rows that can be retrieved with getPagedRows();
	 */
	PaginatedTreezTable setPagedRows(List<Row> pagedRows);

	/**
	 * Returns offset for the display of row numbers
	 */
	int getRowIndexOffset();

	/**
	 * Sets offset for the display of row numbers
	 */
	PaginatedTreezTable setRowIndexOffset(int offset);

	/**
	 * Reloads the data
	 */
	void reload();

}
