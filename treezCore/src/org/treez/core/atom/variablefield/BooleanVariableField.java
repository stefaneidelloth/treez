package org.treez.core.atom.variablefield;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.Activator;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.CheckBox;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;

/**
 * An item example
 */
public class BooleanVariableField extends CheckBox implements VariableField {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(BooleanVariableField.class);

	/**
	 * Background color
	 */
	private static final Color BACKGROUND_COLOR = new Color(null, 240, 245,
			249);

	//#region ATTRIBUTES

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public BooleanVariableField(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 *
	 * @param checkBoxToCopy
	 */
	private BooleanVariableField(BooleanVariableField checkBoxToCopy) {
		super(checkBoxToCopy);
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public BooleanVariableField copy() {
		return new BooleanVariableField(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		Image baseImage = Activator.getImage("booleanVariable.png");
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
	public AbstractAttributeAtom<Boolean> createAttributeAtomControl(
			Composite parent, Refreshable treeViewerRefreshable) {
		this.treeViewRefreshable = treeViewerRefreshable;

		super.createAttributeAtomControl(parent, treeViewerRefreshable);
		super.setBackgroundColor(BACKGROUND_COLOR);

		return this;
	}

	@Override
	public String getValueString() {
		Boolean value = this.get();
		String valueString = value.toString();
		return valueString;
	}

	//#end region

}
