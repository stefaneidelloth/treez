package org.treez.core.data.cell;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

/**
 * A cell editor for cells that contains Booleans. It can handle null values. (The behavior of the call might also
 * depend on the label provider.)
 */
public class TreezBooleanCellEditor extends TreezStringCellEditor {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(TreezBooleanCellEditor.class);

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 */
	public TreezBooleanCellEditor(Composite parent) {
		super(parent);
	}

	//#end region

	//#region METHODS

	/**
	 * Validates the type of the cell value
	 *
	 * @param value
	 */
	@Override
	protected void validateValueType(Object value) {
		String message = "The value must be a Boolean for this column type but it is '"
				+ value.getClass().getSimpleName() + "'. Avoid illegal values or change the column type.";
		Assert.isTrue(value instanceof Boolean, message);
	}

	@Override
	protected Object doGetValue() {
		String valueString = text.getText();
		Boolean value = null;

		if (valueString.isEmpty()) {
			return value;
		}

		try {
			value = Boolean.parseBoolean(valueString);
		} catch (NumberFormatException exception) {
			String message = "The value string '" + valueString
					+ "' could not be parsed as Boolean. Using null instead.";
			sysLog.warn(message);
		}
		return value;
	}

	//#end region

}
