package org.treez.data.table.nebula;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.pagination.AbstractPaginationWidget;
import org.eclipse.nebula.widgets.pagination.PageLoaderStrategyHelper;
import org.eclipse.nebula.widgets.pagination.collections.PageResultContentProvider;
import org.eclipse.nebula.widgets.pagination.renderers.ICompositeRendererFactory;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.ResultAndNavigationPageLinksRendererFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class PageableGridTable extends AbstractPaginationWidget<Grid> {

	protected TableViewer viewer;

	private Table table;

	public PageableGridTable(Composite parent, Table table) {
		super(
				parent,
				SWT.BORDER,
				2,
				PageResultContentProvider.getInstance(),
				null,
				ResultAndNavigationPageLinksRendererFactory.getFactory(),
				false);

		this.table = table;
		createUI(this);
	}

	@Override
	protected Grid createWidget(Composite parent) {
		viewer = new TableViewer(parent, table);
		return viewer.getGrid();
	}

	public TableViewer getViewer() {
		return viewer;
	}

	public static ICompositeRendererFactory getDefaultPageRendererTopFactory() {
		return ResultAndNavigationPageLinksRendererFactory.getFactory();
	}

	@Override
	public void refreshPage() {
		PageLoaderStrategyHelper.loadPageAndReplaceItems(getController(), viewer, getPageLoader(),
				getPageContentProvider(), null);
	}
}
