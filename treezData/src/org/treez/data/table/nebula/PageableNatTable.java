package org.treez.data.table.nebula;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.pagination.AbstractPaginationWidget;
import org.eclipse.nebula.widgets.pagination.collections.PageResultContentProvider;
import org.eclipse.nebula.widgets.pagination.renderers.ICompositeRendererFactory;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.ResultAndNavigationPageLinksRendererFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public class PageableNatTable extends AbstractPaginationWidget<NatTable> {

	//#region ATTRIBUTES

	private Table table;

	protected TreezNatTable treezNatTable;

	//#end region

	//#region CONSTRUCTORS

	public PageableNatTable(Composite parent, Table table) {
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

	//#end region

	//#region METHODS

	@Override
	protected TreezNatTable createWidget(Composite parent) {
		treezNatTable = new TreezNatTable(parent, table);

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

	public static ICompositeRendererFactory getDefaultPageRendererTopFactory() {
		return ResultAndNavigationPageLinksRendererFactory.getFactory();
	}

	//#end region

}
