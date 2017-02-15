package org.treez.data.table.nebula.nat.pagination;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.pagination.AbstractPaginationWidget;
import org.eclipse.nebula.widgets.pagination.IPageContentProvider;
import org.eclipse.nebula.widgets.pagination.IPageLoader;
import org.eclipse.nebula.widgets.pagination.IPageLoaderHandler;
import org.eclipse.nebula.widgets.pagination.PageLoaderStrategyHelper;
import org.eclipse.nebula.widgets.pagination.PageableController;
import org.eclipse.nebula.widgets.pagination.collections.PageResult;
import org.eclipse.nebula.widgets.pagination.collections.PageResultContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.data.row.Row;
import org.treez.core.data.table.LinkableTreezTable;
import org.treez.core.data.table.PaginatedTreezTable;
import org.treez.core.data.table.TreezTable;
import org.treez.data.table.nebula.nat.TreezNatTable;
import org.treez.data.table.nebula.nat.pageloader.DatabasePageResultLoader;
import org.treez.data.table.nebula.nat.pageloader.ListPageResultLoader;

public class PaginationWidget extends AbstractPaginationWidget<NatTable> implements Refreshable {

	//#region ATTRIBUTES

	@SuppressWarnings("checkstyle:magicnumber")
	private static Color BACKGROUND_COLOR = new Color(null, 255, 255, 255);

	private PaginatedTreezTable treezTable;

	protected TreezNatTable treezNatTable;

	//#end region

	//#region CONSTRUCTORS

	public PaginationWidget(Composite parent, LinkableTreezTable treezTable, int limitForNumberOfRowsPerPage) {
		this(parent, treezTable, limitForNumberOfRowsPerPage, createPageLoader(treezTable));
	}

	public PaginationWidget(
			Composite parent,
			PaginatedTreezTable treezTable,
			int limitForNumberOfRowsPerPage,
			IPageLoader<PageResult<Row>> pageLoader) {
		super(
				parent,
				SWT.NONE,
				limitForNumberOfRowsPerPage,
				PageResultContentProvider.getInstance(),
				null,
				new PaginationComponentFactory(),
				false);

		this.treezTable = treezTable;
		setPageLoader(pageLoader);
		createUI(this);
		setBackgroundColor();

		PageableController controller = getController();
		controller.setCurrentPage(0);

	}

	//#end region

	//#region METHODS

	private static IPageLoader<PageResult<Row>> createPageLoader(LinkableTreezTable table) {
		if (table.isLinkedToSource()) {
			return new DatabasePageResultLoader(table);
		} else {
			List<Row> rows = initializeRows(table);
			return new ListPageResultLoader(rows);
		}
	}

	private static List<Row> initializeRows(TreezTable table) {
		List<Row> rows = table.getRows();
		if (rows == null || rows.isEmpty()) {
			table.addEmptyRow();
		}
		return rows;
	}

	@Override
	protected TreezNatTable createWidget(Composite parent) {
		treezNatTable = new TreezNatTable(parent, treezTable, this);
		treezNatTable.setBackground(BACKGROUND_COLOR);

		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		treezNatTable.setLayoutData(gridData);

		return treezNatTable;
	}

	@Override
	public void refresh() {
		refreshPage();
	}

	@Override
	public void refreshPage() {

		@SuppressWarnings("unchecked")
		IPageLoader<Row> pageLoader = getPageLoader();
		if (pageLoader == null) {
			return;
		}

		PageableController controller = getController();
		IPageContentProvider pageContentProvider = getPageContentProvider();
		IPageLoaderHandler<PageableController> handler = null;
		Object page = PageLoaderStrategyHelper.loadPageAndUpdateTotalElements(controller, pageLoader,
				pageContentProvider, handler);

		if (page != null) {
			List<?> content = pageContentProvider.getPaginatedList(page);
			if (content != null) {
				@SuppressWarnings("unchecked")
				List<Row> castedList = (List<Row>) content;
				treezTable.setPagedRows(castedList);

				int rowIndexOffset = controller.getPageOffset();
				treezTable.setRowIndexOffset(rowIndexOffset);
			}
		}

		treezNatTable.refresh();

	}

	private void setBackgroundColor() {
		setBackground(BACKGROUND_COLOR);
		Composite bottomComposite = getCompositeBottom();
		bottomComposite.setBackground(BACKGROUND_COLOR);
		setBackgroundRecoursivly(bottomComposite);

	}

	private void setBackgroundRecoursivly(org.eclipse.swt.widgets.Control control) {
		control.setBackground(BACKGROUND_COLOR);
		boolean isComposite = control instanceof Composite;
		if (isComposite) {
			Composite composite = (Composite) control;
			org.eclipse.swt.widgets.Control[] children = composite.getChildren();
			for (org.eclipse.swt.widgets.Control child : children) {
				setBackgroundRecoursivly(child);
			}
		}
	}

	//#end region

	//#region ACCESSORS

	public TreezNatTable getTreezNatTable() {
		return treezNatTable;
	}

	//#end region

}
