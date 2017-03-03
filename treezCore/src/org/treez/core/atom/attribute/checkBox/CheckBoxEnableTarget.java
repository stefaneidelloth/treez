package org.treez.core.atom.attribute.checkBox;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.attributeContainer.AbstractAttributeContainerAtom;
import org.treez.core.atom.base.annotation.IsParameter;

/**
 * An item example
 */
public class CheckBoxEnableTarget extends AbstractAttributeContainerAtom<CheckBoxEnableTarget> {

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "true")
	private Boolean value;

	@IsParameter(defaultValue = "")
	private String targetPath; //e.g. "properties.mytext" points to root.properites.mytext

	//#end region

	//#region CONSTRUCTORS

	/**
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
	 */
	private CheckBoxEnableTarget(CheckBoxEnableTarget checkBoxEnableTargetToCopy) {
		super(checkBoxEnableTargetToCopy);
		value = checkBoxEnableTargetToCopy.value;
		targetPath = checkBoxEnableTargetToCopy.targetPath;
	}

	//#end region

	//#region METHODS

	@Override
	public CheckBoxEnableTarget getThis() {
		return this;
	}

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
	public void createAtomControl(Composite parent, FocusChangingRefreshable treeViewerRefreshable) {

	}

	//#end region

	//#region ACCESSORS

	private void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public Boolean getValue() {
		return value;
	}

	public void setValue(Boolean value) {
		this.value = value;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public CheckBoxEnableTarget setEnabled(boolean enable) {
		throw new IllegalStateException("not yet implemented");
		//return getThis();
	}

	//#end region

}
