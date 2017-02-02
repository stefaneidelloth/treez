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
import org.eclipse.nebula.widgets.pagination.renderers.navigation.ResultAndNavigationPageLinksRendererFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.data.row.Row;
import org.treez.core.data.table.TreezTable;

public class PageableTreezNatTable extends AbstractPaginationWidget<NatTable> {

	//#region ATTRIBUTES

	@SuppressWarnings("checkstyle:magicnumber")
	private static Color BACKGROUND_COLOR = new Color(null, 255, 255, 255);

	private TreezTable treezTable;

	protected TreezNatTable treezNatTable;

	private Text pageSizeField;

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
				ResultAndNavigationPageLinksRendererFactory.getFactory(),
				false);

		this.treezTable = treezTable;
		setPageLoader(pageLoader);
		createUI(this);
		setBackgroundColor();
		extendPageControlWithExtraInputFields(limitForNumberOfRowsPerPage);

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

	private void extendPageControlWithExtraInputFields(int limitForNumberOfRowsPerPage) {
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());
		Composite bottomComposite = getCompositeBottom();

		Composite pageNumberComposite = (Composite) bottomComposite.getChildren()[1];
		addPageNumberField(pageNumberComposite, toolkit);

		createPageSizeControl(bottomComposite, limitForNumberOfRowsPerPage, toolkit);

	}

	private static void addPageNumberField(Composite pageNumberComposite, FormToolkit toolkit) {
		addExtraColumn(pageNumberComposite);
		Text pageNumberField = toolkit.createText(pageNumberComposite, "1");
		GridData pageNumberFieldGridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		pageNumberField.setLayoutData(pageNumberFieldGridData);

		Control firstChild = pageNumberComposite.getChildren()[0];
		pageNumberField.moveAbove(firstChild);

	}

	private void createPageSizeControl(
			Composite bottomComposite,
			int limitForNumberOfRowsPerPage,
			FormToolkit toolkit) {

		addExtraColumn(bottomComposite);

		Composite pageSizeContainer = toolkit.createComposite(bottomComposite);
		pageSizeContainer.setLayout(new GridLayout(2, true));

		toolkit.createLabel(pageSizeContainer, "Page size");
		pageSizeField = toolkit.createText(pageSizeContainer, "" + limitForNumberOfRowsPerPage);
		GridData pageSizeFieldGridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		pageSizeField.setLayoutData(pageSizeFieldGridData);
	}

	private static void addExtraColumn(Composite compositeWithGridLayout) {
		GridLayout gridLayout = (GridLayout) compositeWithGridLayout.getLayout();
		gridLayout.numColumns += 1;
	}

	//#end region

	//#region ACCESSORS

	public TreezNatTable getTreezNatTable() {
		return treezNatTable;
	}

	//#end region

}
