package org.treez.core.atom.attribute.comboBox;

public class ComboBox extends AbstractComboBox<ComboBox> {

	//#region CONSTRUCTORS

	public ComboBox(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 */
	public ComboBox(ComboBox comboBoxToCopy) {
		super(comboBoxToCopy);

	}

	//#end region

	//#region METHODS

	@Override
	public ComboBox getThis() {
		return this;
	}

	@Override
	public ComboBox copy() {
		return new ComboBox(this);
	}

	//#end region

	//#region ACCESSORS

	@Override
	public ComboBox set(String value) {
		boolean valueAllowed = items.contains(value);
		if (valueAllowed) {
			super.setValue(value);
		} else {
			String message = "The value '" + value + "' is not allowed since it is not contained in the items " + items;
			throw new IllegalArgumentException(message);
		}
		return getThis();
	}

	//#end region

}
