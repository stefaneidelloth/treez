package org.treez.data.table;

import java.util.List;

import org.eclipse.swt.SWT;
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
import org.treez.core.data.row.Row;
import org.treez.core.data.table.TreezTable;

/**
 * Shows a table viewer and additional buttons
 */
@SuppressWarnings("restriction")
public class TableControlAdaption extends AbstractControlAdaption {

	//#region ATTRIBUTES

	private TreezTableViewer tableViewer;

	//#end region

	//#region CONSTRUCTORS

	public TableControlAdaption(Composite parent, TreezTable treezTable) {
		super(parent, treezTable);
		createControl(parent, treezTable);
	}

	//#end region

	//#region METHODS

	/**
	 * Creates the control
	 *
	 * @param parent
	 * @param table
	 */
	private void createControl(Composite parent, TreezTable table) {
		//delete old contents
		for (Control child : parent.getChildren()) {
			child.dispose();
		}

		//set parent layout
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 0;

		parent.setLayout(layout); //new GridLayout(1, true));

		//create buttons
		createButtons(parent);

		//get rows and initialize them if they are null or empty
		List<Row> rows = table.getRows();
		if (rows == null || rows.isEmpty()) {
			table.addEmptyRow();
		}

		//create table viewer
		tableViewer = new TreezTableViewer(parent, table);

		//set input
		setInput(table.getRows());

		//optimize column width
		optimizeColumnWidths();
	}

	/**
	 * Creates the control buttons
	 *
	 * @param parent
	 */
	private void createButtons(Composite parent) {

		FormToolkit toolkit = new FormToolkit(Display.getCurrent());
		Composite buttonComposite = toolkit.createComposite(parent);
		final int numberOfColumns = 5;
		GridLayout gridLayout = new GridLayout(numberOfColumns, true);
		buttonComposite.setLayout(gridLayout);

		createAddButton(toolkit, buttonComposite);

		createDeleteButton(toolkit, buttonComposite);

		createUpButton(toolkit, buttonComposite);

		createDownButton(toolkit, buttonComposite);

		createColumnWidthButton(toolkit, buttonComposite);

	}

	private Button createAddButton(FormToolkit toolkit, Composite buttonComposite) {
		Button addButton = toolkit.createButton(buttonComposite, "", 0);
		addButton.setImage(Activator.getImage(ISharedImages.IMG_OBJ_ADD));
		addButton.setToolTipText("Add new defintion.");

		addButton.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event event) {
				int row = tableViewer.getGrid().getSelectionIndex();
				tableViewer.addRow(row);
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
				int row = tableViewer.getGrid().getSelectionIndex();
				tableViewer.deleteRow(row);
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
				int row = tableViewer.getGrid().getSelectionIndex();
				tableViewer.upRow(row);
			}
		});

		return upButton;
	}

	private Button createDownButton(FormToolkit toolkit, Composite buttonComposite) {
		Button downButton = toolkit.createButton(buttonComposite, "", 0);
		downButton.setImage(Activator.getImage("down.png"));
		downButton.setToolTipText("Move down.");

		downButton.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event event) {
				int row = tableViewer.getGrid().getSelectionIndex();
				tableViewer.downRow(row);
			}
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

	/**
	 * Sets the input for the table viewer
	 *
	 * @param rows
	 */
	public void setInput(List<? extends Row> rows) {
		tableViewer.setInput(rows);
		tableViewer.refresh();
	}

	/**
	 * Optimizes the column widths
	 */
	public void optimizeColumnWidths() {
		tableViewer.optimizeColumnWidths();
	}

	//#end region

}
