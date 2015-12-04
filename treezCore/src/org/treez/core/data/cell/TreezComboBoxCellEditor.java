package org.treez.core.data.cell;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A combo box cell editor for cells that contain Strings. It can handle null
 * values. (The behavior of the call might also depend on the label provider.)
 */
public class TreezComboBoxCellEditor extends CellEditor {

	//#region ATTRIBUTES

	/**
	 * The list of items
	 */
	private List<String> items;

	/**
	 * The selected item
	 */
	private String selectedItem;

	/**
	 * The combo box
	 */
	private Combo comboBox;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 */
	public TreezComboBoxCellEditor(Composite parent,
			List<String> availableItems) {
		super(parent);
		setItems(availableItems);
	}

	//#end region

	//#region METHODS

	/**
	 * Sets the cell value.
	 */
	@Override
	protected void doSetValue(Object value) {
		Assert.isTrue(comboBox != null, "The comboBox must not be null.");

		if (value == null) {
			selectedItem = null;
			comboBox.deselectAll();
			return;
		}

		boolean isString = value instanceof String;
		if (!isString) {
			String message = "The value class must be String but is "
					+ value.getClass().getSimpleName();
			throw new IllegalArgumentException(message);
		}

		boolean itemIsAllowed = items.contains(value);
		if (!itemIsAllowed) {
			String message = "The value '" + value
					+ "' is not contained in the list of allowed items.";
			throw new IllegalArgumentException(message);
		}

		selectedItem = (String) value;
		int itemIndex = items.indexOf(selectedItem);
		comboBox.select(itemIndex);

	}

	@Override
	protected Object doGetValue() {
		return selectedItem;
	}

	@Override
	protected void doSetFocus() {
		comboBox.setFocus();
	}

	@Override
	protected Control createControl(Composite parent) {
		comboBox = new Combo(parent, SWT.READ_ONLY);
		populateComboBoxItems();
		comboBox.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				keyReleaseOccured(e);
			}
		});

		comboBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				applyEditorValueAndDeactivate();
			}

			@Override
			public void widgetSelected(SelectionEvent event) {
				int itemIndex = comboBox.getSelectionIndex();
				selectedItem = items.get(itemIndex);
				applyEditorValueAndDeactivate();
			}
		});

		comboBox.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE
						|| e.detail == SWT.TRAVERSE_RETURN) {
					e.doit = false;
				}
			}
		});

		comboBox.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				TreezComboBoxCellEditor.this.focusLost();
			}
		});
		return comboBox;
	}

	private void populateComboBoxItems() {
		if (comboBox != null && items != null) {
			comboBox.removeAll();

			for (int i = 0; i < items.size(); i++) {
				comboBox.add(items.get(i), i);
			}
			setValueValid(true);
			selectedItem = null;
		}
	}

	/**
	 * Apply currently selected value and deactivate the cell editor
	 */
	void applyEditorValueAndDeactivate() {

		int itemIndex = comboBox.getSelectionIndex();
		selectedItem = items.get(itemIndex);

		Object newValue = doGetValue();
		markDirty();
		boolean isValid = isCorrect(newValue);
		setValueValid(isValid);

		if (!isValid) {
			setErrorMessage(itemIndex);
		}

		fireApplyEditorValue();
		deactivate();

	}

	private void setErrorMessage(int itemIndex) {
		boolean itemIsAvailable = items.size() > 0 && itemIndex >= 0
				&& itemIndex < items.size();
		if (itemIsAvailable) {
			//use item
			setErrorMessage(MessageFormat.format(getErrorMessage(),
					new Object[]{items.get(itemIndex)}));
		} else {
			//use text value
			setErrorMessage(MessageFormat.format(getErrorMessage(),
					new Object[]{comboBox.getText()}));
		}
	}

	@Override
	public void activate(ColumnViewerEditorActivationEvent activationEvent) {
		super.activate(activationEvent);

		boolean dropDown = false;
		if ((activationEvent.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
				|| activationEvent.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION)) {
			dropDown = true;
		} else
			if (activationEvent.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED) {
			dropDown = true;
		} else
				if (activationEvent.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC) {
			dropDown = true;
		} else
					if (activationEvent.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL) {
			dropDown = true;
		}

		if (dropDown) {
			getControl().getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					comboBox.setListVisible(true);
				}

			});

		}

	}

	@Override
	protected void focusLost() {
		if (isActivated()) {
			applyEditorValueAndDeactivate();
		}
	}

	@Override
	protected void keyReleaseOccured(KeyEvent keyEvent) {
		if (keyEvent.character == '\u001b') { //Escape character
			fireCancelEditor();
		} else if (keyEvent.character == '\t') { //tab key
			applyEditorValueAndDeactivate();
		}
	}

	//#end region

	//#region ACCESSORS

	/**
	 * Sets the available items
	 */
	public void setItems(List<String> items) {
		Assert.isNotNull(items, "Items must not be null.");
		this.items = items;
		populateComboBoxItems();
	}

	//#end region

}
