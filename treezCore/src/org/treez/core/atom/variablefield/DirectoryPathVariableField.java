package org.treez.core.atom.variablefield;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.DirectoryPath;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.variablelist.AbstractVariableListField;

/**
 * An item example
 */
public class DirectoryPathVariableField extends DirectoryPath
		implements
			VariableField<String> {

	//#region ATTRIBUTES

	/**
	 * Background color
	 */
	private static final Color BACKGROUND_COLOR = new Color(null, 240, 245,
			249);

	//#end region

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

	//#region COPY

	@Override
	public DirectoryPathVariableField copy() {
		return new DirectoryPathVariableField(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		Image baseImage = Activator.getImage("directoryPathVariable.png");
		Image image;
		if (isEnabled()) {
			image = Activator.getOverlayImageStatic(baseImage,
					"enabledDecoration.png");
		} else {
			image = Activator.getOverlayImageStatic(baseImage,
					"disabledDecoration.png");
		}
		return image;
	}

	@Override
	public AbstractAttributeAtom<String> createAttributeAtomControl(
			Composite parent, FocusChangingRefreshable treeViewerRefreshable) {
		this.treeViewRefreshable = treeViewerRefreshable;

		super.createAttributeAtomControl(parent, treeViewerRefreshable);
		super.setBackgroundColor(BACKGROUND_COLOR);

		return this;
	}

	@Override
	public AbstractVariableListField<String> createVariableListField() {

		throw new IllegalStateException("Not yet implemented");
	}

	//#end region

	//#region ACCESSORS

	@Override
	public String getValueString() {
		String value = this.get();
		return value;
	}

	@Override
	public void setValueString(String valueString) {
		this.set(valueString);
	}

	//#end region

}
