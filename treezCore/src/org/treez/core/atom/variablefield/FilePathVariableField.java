package org.treez.core.atom.variablefield;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.Activator;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.FilePath;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;

/**
 * An item example
 */
public class FilePathVariableField extends FilePath implements VariableField {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger
			.getLogger(FilePathVariableField.class);

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
	public FilePathVariableField(String name) {
		super(name);
		this.setShowEnabledCheckBox(true);
	}

	/**
	 * Copy constructor
	 *
	 * @param atomToCopy
	 */
	private FilePathVariableField(FilePathVariableField atomToCopy) {
		super(atomToCopy);
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public FilePathVariableField copy() {
		return new FilePathVariableField(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		Image baseImage = Activator.getImage("filePathVariable.png");
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

		throw new NotImplementedException();
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
