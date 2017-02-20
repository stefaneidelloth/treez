package org.treez.data.table.nebula;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.data.table.LinkableTreezTable;
import org.treez.data.table.nebula.nat.TreezNatTable;
import org.treez.data.table.nebula.nat.pagination.PaginationWidget;

/**
 * Shows a table and additional buttons
 */
public class TableControlAdaption extends AbstractControlAdaption {

	//#region ATTRIBUTES

	private TreezNatTable treezNatTable;

	//#end region

	//#region CONSTRUCTORS

	public TableControlAdaption(Composite parent, Table table) {
		super(parent, table);
		createControl(parent, table);
	}

	//#end region

	//#region METHODS

	private void createControl(Composite parent, Table table) {
		deleteOldContent(parent);
		setParentLayout(parent);
		createButtons(parent);
		createPageableTable(parent, table);

	}

	private static void deleteOldContent(Composite parent) {
		for (Control child : parent.getChildren()) {
			child.dispose();
		}
	}

	private static void setParentLayout(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 0;
		parent.setLayout(layout);
	}

	private void createButtons(Composite parent) {

		FormToolkit toolkit = new FormToolkit(Display.getCurrent());
		Composite buttonComposite = toolkit.createComposite(parent);

		final int numberOfColumns = 6;
		GridLayout gridLayout = new GridLayout(numberOfColumns, true);
		buttonComposite.setLayout(gridLayout);

		createAddButton(toolkit, buttonComposite);
		createDeleteButton(toolkit, buttonComposite);
		createUpButton(toolkit, buttonComposite);
		createDownButton(toolkit, buttonComposite);
		createColumnWidthButton(toolkit, buttonComposite);

		Table table = (Table) getAdaptable();
		if (table.isLinkedToSource()) {
			createRefreshButton(toolkit, buttonComposite, table);
		}

	}

	private Button createAddButton(FormToolkit toolkit, Composite buttonComposite) {
		Button addButton = toolkit.createButton(buttonComposite, "", 0);
		addButton.setImage(Activator.getImage(ISharedImages.IMG_OBJ_ADD));
		addButton.setToolTipText("Add new defintion.");

		addButton.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event event) {
				int rowIndex = treezNatTable.getSelectionIndex();
				treezNatTable.addRow(rowIndex);
			}
		});

		return addButton;
	}

	private Button createDeleteButton(FormToolkit toolkit, Composite buttonComposite) {
		Button deleteButton = toolkit.createButton(buttonComposite, "", 0);
		deleteButton.setImage(Activator.getImage(ISharedImages.IMG_TOOL_DELETE));
		deleteButton.setToolTipText("Delete defintion.");

		deleteButton.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event event) {
				List<Integer> selectedIndices = treezNatTable.getSelectionIndices();
				treezNatTable.deletePagedRows(selectedIndices);
			}
		});

		return deleteButton;
	}

	private Button createUpButton(FormToolkit toolkit, Composite buttonComposite) {
		Button upButton = toolkit.createButton(buttonComposite, "", 0);
		upButton.setImage(Activator.getImage("up.png"));
		upButton.setToolTipText("Move up.");

		upButton.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event event) {
				List<Integer> selectedIndices = treezNatTable.getSelectionIndices();
				treezNatTable.moveRowsUpAndSelect(selectedIndices);
			}
		});

		return upButton;
	}

	private Button createDownButton(FormToolkit toolkit, Composite buttonComposite) {
		Button downButton = toolkit.createButton(buttonComposite, "", 0);
		downButton.setImage(Activator.getImage("down.png"));
		downButton.setToolTipText("Move down.");

		downButton.addListener(SWT.MouseDown, (e) -> {
			List<Integer> selectedIndices = treezNatTable.getSelectionIndices();
			treezNatTable.moveRowsDownAndSelect(selectedIndices);
		});

		return downButton;
	}

	private Button createColumnWidthButton(FormToolkit toolkit, Composite buttonComposite) {
		Button optimizeColumnWidthsButton = toolkit.createButton(buttonComposite, "", 0);
		optimizeColumnWidthsButton.setImage(Activator.getImage("resize_width.png"));
		optimizeColumnWidthsButton.setToolTipText("Optimize column widths.");

		optimizeColumnWidthsButton.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event event) {
				optimizeColumnWidths();
			}
		});

		return optimizeColumnWidthsButton;
	}

	private static Button createRefreshButton(FormToolkit toolkit, Composite buttonComposite, Table table) {
		Button refreshButton = toolkit.createButton(buttonComposite, "", 0);
		refreshButton.setImage(Activator.getImage("refresh.png"));
		refreshButton.setToolTipText("Refresh data from source.");

		refreshButton.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event event) {
				table.reload();
			}
		});

		return refreshButton;
	}

	private void createPageableTable(Composite parent, LinkableTreezTable table) {

		final int defaultPageSize = 1000;

		PaginationWidget pageableTable = new PaginationWidget(parent, table, defaultPageSize);

		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		pageableTable.setLayoutData(gridData);

		treezNatTable = pageableTable.getTreezNatTable();

		optimizeColumnWidths();
	}

	public void optimizeColumnWidths() {
		treezNatTable.optimizeColumnWidths();
	}

	//#end region

}
