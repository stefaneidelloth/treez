package org.treez.core.atom.attribute;

/**
 * An item example
 */
public class ComboBox extends AbstractComboBox {

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public ComboBox(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 *
	 * @param comboBoxToCopy
	 */
	public ComboBox(AbstractComboBox comboBoxToCopy) {
		super(comboBoxToCopy);

	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public ComboBox copy() {
		return new ComboBox(this);
	}

	//#end region

	//#end region

	//#region ACCESSORS

	@Override
	public void set(String value) {
		boolean valueAllowed = items.contains(value);
		if (valueAllowed) {
			super.setValue(value);
		} else {
			String message = "The value '" + value
					+ "' is not allowed since it is not contained in the items "
					+ items;
			throw new IllegalArgumentException(message);
		}
	} //#end region

}
