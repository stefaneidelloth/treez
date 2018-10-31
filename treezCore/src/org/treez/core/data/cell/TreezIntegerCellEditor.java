package org.treez.core.data.cell;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

/**
 * A cell editor for cells that contains Integers. It can handle null values. (The behavior of the call might also
 * depend on the label provider.)
 */
public class TreezIntegerCellEditor extends TreezStringCellEditor {

	private static final Logger LOG = LogManager.getLogger(TreezIntegerCellEditor.class);

	//#region CONSTRUCTORS

	public TreezIntegerCellEditor(Composite parent) {
		super(parent, null);
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
		String message = "The value must be a Integer for this column type but it is '"
				+ value.getClass().getSimpleName() + "'. Avoid illegal values or change the column type.";
		Assert.isTrue(value instanceof Integer, message);
	}

	@Override
	protected Object doGetValue() {
		String valueString = text.getText();
		Integer value = null;

		if (valueString.isEmpty()) {
			return value;
		}

		try {
			value = Integer.parseInt(valueString);
		} catch (NumberFormatException exception) {
			String message = "The value string '" + valueString
					+ "' could not be parsed as Integer. Using null instead.";
			LOG.warn(message);
		}
		return value;
	}

	//#end region

}
