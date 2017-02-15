package org.treez.data.table.nebula.nat.pageloader;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.pagination.IPageLoader;
import org.eclipse.nebula.widgets.pagination.PageableController;
import org.eclipse.nebula.widgets.pagination.collections.DefaultSortProcessor;
import org.eclipse.nebula.widgets.pagination.collections.PageResult;
import org.eclipse.nebula.widgets.pagination.collections.SortProcessor;
import org.eclipse.swt.SWT;
import org.treez.core.data.row.Row;

public class ListPageResultLoader implements IPageLoader<PageResult<Row>> {

	private List<Row> items;

	public ListPageResultLoader(List<Row> items) {
		this.items = items;
	}

	public void setItems(List<Row> items) {
		this.items = items;
	}

	public List<Row> getItems() {
		return items;
	}

	@Override
	public PageResult<Row> loadPage(PageableController controller) {

		SortProcessor processor = DefaultSortProcessor.getInstance();
		int sortDirection = controller.getSortDirection();
		if (sortDirection != SWT.NONE) {
			// Sort the list
			processor.sort(items, controller.getSortPropertyName(), sortDirection);
		}
		int totalSize = items.size();
		int pageSize = controller.getPageSize();
		int pageIndex = controller.getPageOffset();

		int fromIndex = pageIndex;
		if (fromIndex > totalSize) {
			return new PageResult<Row>(new ArrayList<Row>(), totalSize);
		}

		int toIndex = pageIndex + pageSize;
		if (toIndex > totalSize) {
			toIndex = totalSize;
		}
		List<Row> content = items.subList(fromIndex, toIndex);
		return new PageResult<Row>(content, totalSize);
	}

}
