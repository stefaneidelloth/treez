package org.treez.core.atom.variablefield;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.fileSystem.DirectoryPath;
import org.treez.core.atom.variablelist.AbstractVariableListField;

public class DirectoryPathVariableField extends DirectoryPath
		implements
		VariableField<DirectoryPathVariableField, String> {

	//#region CONSTRUCTORS

	public DirectoryPathVariableField(String name) {
		super(name);
		this.setShowEnabledCheckBox(true);
	}

	/**
	 * Copy constructor
	 */
	private DirectoryPathVariableField(DirectoryPathVariableField atomToCopy) {
		super(atomToCopy);
	}

	//#end region

	//#region METHODS

	@Override
	public DirectoryPathVariableField getThis() {
		return this;
	}

	@Override
	public DirectoryPathVariableField copy() {
		return new DirectoryPathVariableField(this);
	}

	@Override
	public Image provideImage() {
		Image baseImage = Activator.getImage("directoryPathVariable.png");
		Image image;
		if (isEnabled()) {
			image = Activator.getOverlayImageStatic(baseImage, "enabledDecoration.png");
		} else {
			image = Activator.getOverlayImageStatic(baseImage, "disabledDecoration.png");
		}
		return image;
	}

	@Override
	public DirectoryPathVariableField createAttributeAtomControl(
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
	public DirectoryPathVariableField setEnabled(boolean state) {
		super.setEnabled(state);
		return getThis();
	}

	@Override
	public DirectoryPathVariableField setLabel(String label) {
		super.setLabel(label);
		return getThis();
	}

	@Override
	public String getValueString() {
		String value = this.get();
		return value;
	}

	@Override
	public DirectoryPathVariableField setValueString(String valueString) {
		this.set(valueString);
		return getThis();
	}

	@Override
	public DirectoryPathVariableField setBackgroundColor(Color color) {
		super.setBackgroundColor(color);
		return getThis();
	}

	//#end region

}
