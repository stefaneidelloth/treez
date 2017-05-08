package org.treez.core.atom.list;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

/**
 * Shows a TreezListViewer and additional buttons
 */
@SuppressWarnings("restriction")
public class TreezListAtomControlAdaption extends AbstractControlAdaption {

	//#region ATTRIBUTES

	private TreezListViewer listViewer;

	//#end region

	//#region CONSTRUCTORS

	public TreezListAtomControlAdaption(Composite parent,
			TreezListAtom treezList) {
		super(parent, treezList);
		createControl(parent, treezList);
	}

	//#end region

	//#region METHODS

	/**
	 * Creates the control
	 *
	 * @param parent
	 * @param treezList
	 */
	private void createControl(Composite parent, TreezListAtom treezList) {
		//delete old contents
		for (Control child : parent.getChildren()) {
			child.dispose();
		}

		//set parent layout
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 0;
		parent.setLayout(layout); //new GridLayout(1, true));

		//create buttons
		createButtons(parent, treezList);

		//get rows and initialize them if they are null or empty and
		//if specified so
		if (treezList.isFirstRowAutoCreation()) {
			List<Row> rows = treezList.getRows();
			if (rows == null || rows.isEmpty()) {
				treezList.addEmptyRow();
			}
		}

		//create table viewer
		listViewer = new TreezListViewer(parent, treezList);
		listViewer.setShowHeader(treezList.getShowHeaders());

		//enable file path validation if the file path validation check box
		//should be shown (its default state is enabled)
		boolean showPathValidationCheckBox = treezList.isEnabledFilePathButton()
				|| treezList.isEnabledDirectoryPathButton();
		if (showPathValidationCheckBox) {
			listViewer.enablePathValidation();
		}

		//set input
		setInput(treezList.getRows());

	}

	/**
	 * Creates the control buttons
	 *
	 * @param parent
	 */
	private void createButtons(Composite parent, TreezListAtom treezList) {

		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		Composite buttonComposite = toolkit.createComposite(parent);
		final int numberOfColumns = 7;
		GridLayout gridLayout = new GridLayout(numberOfColumns, false);
		buttonComposite.setLayout(gridLayout);

		createAddButton(toolkit, buttonComposite);

		createDeleteButton(toolkit, buttonComposite);

		createUpButton(toolkit, buttonComposite);

		createDownButton(toolkit, buttonComposite);

		if (treezList.isEnabledFilePathButton()) {
			createFilePathButton(toolkit, buttonComposite);
		}

		if (treezList.isEnabledDirectoryPathButton()) {
			createDirectoryPathButton(toolkit, buttonComposite);
		}

		boolean showPathValidationCheckBox = treezList.isEnabledFilePathButton()
				|| treezList.isEnabledDirectoryPathButton();
		if (showPathValidationCheckBox) {
			createPathValidationCheckBox(toolkit, buttonComposite);
		}

	}

	/**
	 * Creates the add button
	 *
	 * @param toolkit
	 * @param buttonComposite
	 */
	private void createAddButton(FormToolkit toolkit,
			Composite buttonComposite) {
		Button addButton = toolkit.createButton(buttonComposite, "", 0);
		addButton.setImage(Activator.getImage(ISharedImages.IMG_OBJ_ADD));
		addButton.setToolTipText("Add new row.");

		addButton.addListener(SWT.MouseDown, new Listener() {

			@Override
			@SuppressWarnings("synthetic-access")
			public void handleEvent(Event event) {

				int rowIndex = listViewer.getSelectionIndex();
				listViewer.addRow(rowIndex);
			}
		});
	}

	/**
	 * Creates the delte button
	 *
	 * @param toolkit
	 * @param buttonComposite
	 */
	private void createDeleteButton(FormToolkit toolkit,
			Composite buttonComposite) {
		Button deleteButton = toolkit.createButton(buttonComposite, "", 0);
		deleteButton
				.setImage(Activator.getImage(ISharedImages.IMG_TOOL_DELETE));
		deleteButton.setToolTipText("Delete row.");

		deleteButton.addListener(SWT.MouseDown, new Listener() {

			@Override
			@SuppressWarnings("synthetic-access")
			public void handleEvent(Event event) {
				int rowIndex = listViewer.getSelectionIndex();
				listViewer.deleteRow(rowIndex);
			}
		});
	}

	/**
	 * Creates the up button
	 *
	 * @param toolkit
	 * @param buttonComposite
	 */
	private void createUpButton(FormToolkit toolkit,
			Composite buttonComposite) {
		Button upButton = toolkit.createButton(buttonComposite, "", 0);
		upButton.setImage(Activator.getImage("up.png"));
		upButton.setToolTipText("Move up.");

		upButton.addListener(SWT.MouseDown, new Listener() {

			@Override
			@SuppressWarnings("synthetic-access")
			public void handleEvent(Event event) {
				int rowIndex = listViewer.getSelectionIndex();
				listViewer.upRow(rowIndex);
			}
		});
	}

	/**
	 * Creates the down button
	 *
	 * @param toolkit
	 * @param buttonComposite
	 */
	private void createDownButton(FormToolkit toolkit,
			Composite buttonComposite) {
		Button downButton = toolkit.createButton(buttonComposite, "", 0);
		downButton.setImage(Activator.getImage("down.png"));
		downButton.setToolTipText("Move down.");

		downButton.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event event) {
				int row = listViewer.getSelectionIndex();
				listViewer.downRow(row);
			}
		});
	}

	/**
	 * Creates the file path button
	 *
	 * @param toolkit
	 * @param buttonComposite
	 */
	private void createFilePathButton(FormToolkit toolkit,
			Composite buttonComposite) {
		Button filePathButton;
		filePathButton = toolkit.createButton(buttonComposite, "", 0);
		filePathButton.setImage(Activator.getImage("browse.png"));
		filePathButton.setToolTipText(
				"Edit the selected item with a file path chooser.");

		filePathButton.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event event) {
				int row = listViewer.getSelectionIndex();
				listViewer.editRowWidthFilePathChooser(row);
			}
		});
	}

	/**
	 * Creates the directory path button
	 *
	 * @param toolkit
	 * @param buttonComposite
	 */
	private void createDirectoryPathButton(FormToolkit toolkit,
			Composite buttonComposite) {
		Button directoryPathButton;
		directoryPathButton = toolkit.createButton(buttonComposite, "", 0);
		directoryPathButton.setImage(Activator.getImage("browseDirectory.png"));
		directoryPathButton.setToolTipText(
				"Edit the selected item with a directory path chooser.");

		directoryPathButton.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event event) {
				int row = listViewer.getSelectionIndex();
				listViewer.editRowWithDirectoryPathChooser(row);
			}
		});
	}

	/**
	 * Creates the file path validation check box
	 *
	 * @param toolkit
	 * @param buttonComposite
	 */
	private void createPathValidationCheckBox(FormToolkit toolkit,
			Composite buttonComposite) {
		Button pathValidationCheckBox;
		pathValidationCheckBox = toolkit.createButton(buttonComposite,
				"Validate paths", SWT.CHECK);
		pathValidationCheckBox.setToolTipText(
				"If this checkbox is enabled the items are checked to represent valid paths.");

		//set default state (also see creation of list viewer and
		//enable/disable validation)
		pathValidationCheckBox.setSelection(true);

		pathValidationCheckBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {

				Button checkBox = (Button) event.widget;
				if (checkBox.getSelection()) {
					listViewer.enablePathValidation();
				} else {
					listViewer.disablePathValidation();
				}

			}
		});

	}

	/**
	 * Refreshes the list viewer
	 */
	public void refresh() {

		//update row input
		TreezListAtom treezList = (TreezListAtom) getAdaptable();
		List<Row> rows = treezList.getRows();
		setInput(rows);

		listViewer.refresh();
	}

	/**
	 * Sets the input for the table viewer
	 *
	 * @param rows
	 */
	public void setInput(List<? extends Row> rows) {
		listViewer.setInput(rows);
	}

	//#end region

}
