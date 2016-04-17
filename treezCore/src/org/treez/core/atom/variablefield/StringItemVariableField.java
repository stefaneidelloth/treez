package org.treez.core.atom.variablefield;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.Activator;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.ComboBox;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.variablelist.AbstractVariableListField;

/**
 * An item example
 */
public class StringItemVariableField extends ComboBox
		implements
			VariableField<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger
			.getLogger(StringItemVariableField.class);

	/**
	 * Background color
	 */
	private static final Color BACKGROUND_COLOR = new Color(null, 240, 245,
			249);

	// #region ATTRIBUTES

	// #end region

	// #region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public StringItemVariableField(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 *
	 * @param checkBoxToCopy
	 */
	private StringItemVariableField(StringItemVariableField checkBoxToCopy) {
		super(checkBoxToCopy);
	}

	// #end region

	// #region METHODS

	//#region COPY

	@Override
	public StringItemVariableField copy() {
		return new StringItemVariableField(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		Image baseImage = Activator.getImage("stringItemVariable.png");
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
			Composite parent, Refreshable treeViewerRefreshable) {
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

	// #end region

}
