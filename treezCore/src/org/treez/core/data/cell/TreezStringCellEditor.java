package org.treez.core.data.cell;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.treez.core.attribute.Consumer;

/**
 * A cell editor for cells that contain Strings. It can handle null values. (The behavior of the call might also depend
 * on the label provider.)
 */
public class TreezStringCellEditor extends TextCellEditor {

	//#region ATTRIBUTES

	protected static final Color TEXT_BACKGROUND_COLOR = new Color(Display.getCurrent(), 255, 255, 255);

	protected static final Color TEXT_BACKGROUND_COLOR_ERROR = new Color(Display.getCurrent(), 250, 200, 128);

	protected boolean valueValidation = false;

	//#end region

	//#region CONSTRUCTORS

	public TreezStringCellEditor(Composite parent, Consumer changedConsumer) {
		super(parent);
		text.addModifyListener((event) -> validateValue());
		text.addModifyListener((event) -> {
			if (changedConsumer != null) {
				changedConsumer.consume();
			}
		});
	}

	//#end region

	//#region METHODS

	/**
	 * Sets the cell value.
	 */
	@Override
	protected void doSetValue(Object valueToSet) {
		Object value = valueToSet;
		if (value == null) {
			value = ""; //the editor should display an empty string, while the label provider should display "(Null)"
		} else {
			validateValueType(value);
		}
		String cellEditorString = value.toString();
		text.setText(cellEditorString);
	}

	/**
	 * Validates the type of the cell value
	 *
	 * @param value
	 */
	protected void validateValueType(Object value) {
		String message = "The value must be a String for this column type but it is '"
				+ value.getClass().getSimpleName() + "'. Avoid illegal values or change the column type.";
		Assert.isTrue(value instanceof String, message);
	}

	/**
	 * If the validation is enabled this method validates the value after it has been changed
	 */
	private void validateValue() {
		if (valueValidation) {
			doValidateValue();
		}

	}

	/**
	 * Validates the cell value
	 */
	protected void doValidateValue() {
		//can be overridden by inheriting classes
	}

	//#end region

	//#region ACCESSORS

	/**
	 * Enables the value validation
	 */
	public void enableValidation() {
		valueValidation = true;

	}

	/**
	 * Disables the value validation
	 */
	public void disableValidation() {
		valueValidation = false;

	}

	//#end region

}
