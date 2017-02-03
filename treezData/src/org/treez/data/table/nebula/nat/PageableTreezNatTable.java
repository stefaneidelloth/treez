package org.treez.data.table.nebula.nat;

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
import org.treez.core.data.row.Row;
import org.treez.core.data.table.TreezTable;

public class PageableTreezNatTable extends AbstractPaginationWidget<NatTable> {

	//#region ATTRIBUTES

	@SuppressWarnings("checkstyle:magicnumber")
	private static Color BACKGROUND_COLOR = new Color(null, 255, 255, 255);

	private TreezTable treezTable;

	protected TreezNatTable treezNatTable;

	//#end region

	//#region CONSTRUCTORS

	public PageableTreezNatTable(
			Composite parent,
			TreezTable treezTable,
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

	@Override
	protected TreezNatTable createWidget(Composite parent) {
		treezNatTable = new TreezNatTable(parent, treezTable);
		treezNatTable.setBackground(BACKGROUND_COLOR);

		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		treezNatTable.setLayoutData(gridData);

		return treezNatTable;
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
