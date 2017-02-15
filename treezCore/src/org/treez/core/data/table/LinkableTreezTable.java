package org.treez.core.data.table;

/**
 * Treez table interface
 */
public interface LinkableTreezTable extends PaginatedTreezTable {

	/**
	 * Returns true if the table is linked to a table source
	 */
	boolean isLinkedToSource();

	/**
	 * Returns the TableSource if this table is linked to a source. Returns null if this table is not linked.
	 */
	TableSource getTableSource();

	/**
	 * Checks if this table is already linked to the given TableSource
	 */
	boolean sourceEquals(TableSource tableSource);

	/**
	 * Returns true if the table currently caches data from the table source
	 */
	public boolean isCached();

	/**
	 * Resets the cache so that the data from the table source must be reloaded for the next usage
	 */
	void resetCache();

	//#end region

}
