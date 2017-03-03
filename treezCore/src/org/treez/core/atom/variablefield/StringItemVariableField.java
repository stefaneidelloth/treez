package org.treez.core.atom.variablefield;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.attribute.comboBox.ComboBox;
import org.treez.core.atom.variablelist.AbstractVariableListField;

public class StringItemVariableField extends ComboBox implements VariableField<StringItemVariableField, String> {

	//#region CONSTRUCTORS

	public StringItemVariableField(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 */
	private StringItemVariableField(StringItemVariableField checkBoxToCopy) {
		super(checkBoxToCopy);
	}

	//#end region

	//#region METHODS

	@Override
	public StringItemVariableField getThis() {
		return this;
	}

	@Override
	public StringItemVariableField copy() {
		return new StringItemVariableField(this);
	}

	@Override
	public Image provideImage() {
		Image baseImage = Activator.getImage("stringItemVariable.png");
		Image image;
		if (isEnabled()) {
			image = Activator.getOverlayImageStatic(baseImage, "enabledDecoration.png");
		} else {
			image = Activator.getOverlayImageStatic(baseImage, "disabledDecoration.png");
		}
		return image;
	}

	@Override
	public AbstractAttributeAtom<ComboBox, String> createAttributeAtomControl(
			Composite parent,
			FocusChangingRefreshable treeViewerRefreshable) {
		this.treeViewRefreshable = treeViewerRefreshable;

		super.createAttributeAtomControl(parent, treeViewerRefreshable);

		return this;
	}

	@Override
	public AbstractVariableListField<?, String> createVariableListField() {

		throw new IllegalStateException("Not yet implemented");
	}

	//#end region

	//#region ACCESSORS

	@Override
	public StringItemVariableField setEnabled(boolean state) {
		super.setEnabled(state);
		return getThis();
	}

	@Override
	public StringItemVariableField setLabel(String label) {
		super.setLabel(label);
		return getThis();
	}

	@Override
	public String getValueString() {
		String value = this.get();
		return value;
	}

	@Override
	public StringItemVariableField setValueString(String valueString) {
		this.set(valueString);
		return getThis();
	}

	@Override
	public StringItemVariableField setBackgroundColor(Color color) {
		super.setBackgroundColor(color);
		return getThis();
	}

	//#end region

}
