package org.treez.core.atom.attribute;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.Activator;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.base.parent.AbstractAttributeContainerAtom;
import org.treez.core.atom.base.annotation.IsParameter;

/**
 * An item example
 */
public class CheckBoxEnableTarget extends AbstractAttributeContainerAtom {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(CheckBoxEnableTarget.class);

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "true")
	private Boolean value;

	@IsParameter(defaultValue = "")
	private String targetPath; //e.g. "root.properties.mytext

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 * @param enableValue
	 *            the boolean value for which the target is enabled
	 * @param targetPath
	 *            the model path to the target whose enabled state is controlled
	 */
	public CheckBoxEnableTarget(String name, Boolean enableValue, String targetPath) {
		super(name);
		setValue(enableValue);
		setTargetPath(targetPath);
	}

	/**
	 * Copy constructor
	 *
	 * @param checkBoxEnableTargetToCopy
	 */
	private CheckBoxEnableTarget(CheckBoxEnableTarget checkBoxEnableTargetToCopy) {
		super(checkBoxEnableTargetToCopy);
		value = checkBoxEnableTargetToCopy.value;
		targetPath = checkBoxEnableTargetToCopy.targetPath;
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public CheckBoxEnableTarget copy() {
		return new CheckBoxEnableTarget(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("switch.png");
	}

	@Override
	public void createAtomControl(Composite parent, Refreshable treeViewerRefreshable) {

	}

	//#end region

	//#region ACCESSORS

	/**
	 * Set target path
	 *
	 * @param targetPath
	 */
	private void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	/**
	 * Get target path
	 *
	 * @return
	 */
	public String getTargetPath() {
		return targetPath;
	}

	/**
	 * @return
	 */
	public Boolean getValue() {
		return value;
	}

	/**
	 * Set value
	 *
	 * @param value
	 */
	public void setValue(Boolean value) {
		this.value = value;
	}

	//#end region

}
