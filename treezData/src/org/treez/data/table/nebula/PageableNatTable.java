package org.treez.data.table.nebula;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.pagination.AbstractPaginationWidget;
import org.eclipse.nebula.widgets.pagination.PageableController;
import org.eclipse.nebula.widgets.pagination.collections.PageResultContentProvider;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.ResultAndNavigationPageLinksRendererFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.data.table.TreezTable;

public class PageableNatTable extends AbstractPaginationWidget<NatTable> {

	//#region ATTRIBUTES

	private static Color BACKGROUND_COLOR = new Color(null, 255, 255, 255);

	private TreezTable table;

	protected TreezNatTable treezNatTable;

	//#end region

	//#region CONSTRUCTORS

	public PageableNatTable(Composite parent, TreezTable table, int pageSize) {
		super(
				parent,
				SWT.NONE,
				pageSize,
				PageResultContentProvider.getInstance(),
				null,
				ResultAndNavigationPageLinksRendererFactory.getFactory(),
				false);

		this.table = table;
		createUI(this);
		setBackground(BACKGROUND_COLOR);

		PageableController controller = getController();
		controller.setCurrentPage(0);

	}

	//#end region

	//#region METHODS

	@Override
	protected TreezNatTable createWidget(Composite parent) {
		treezNatTable = new TreezNatTable(parent, table);
		treezNatTable.setBackground(BACKGROUND_COLOR);

		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		treezNatTable.setLayoutData(gridData);

		return treezNatTable;
	}

	@Override
	public void refreshPage() {
		treezNatTable.refresh();
	}

	//#end region

	//#region ACCESSORS

	public TreezNatTable getTreezNatTable() {
		return treezNatTable;
	}

	//#end region

}
