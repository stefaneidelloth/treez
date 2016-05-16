package org.treez.core.atom.attribute;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.parent.AbstractAttributeContainerAtom;
import org.treez.core.atom.base.annotation.IsParameter;

public class ComboBoxEnableTarget extends AbstractAttributeContainerAtom {

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "")
	private String valueString; //e.g. "value1,value2"

	@IsParameter(defaultValue = "")
	private String targetPath; //e.g. "root.properties.mytext

	//#end region

	//#region CONSTRUCTORS

	/**
	 * @param enableValues
	 *            a comma separated list of values for which the target is
	 *            enabled
	 * @param targetPath
	 *            the model path to the target whose enabled state is controlled
	 */
	public ComboBoxEnableTarget(String name, String enableValues,
			String targetPath) {
		super(name);
		setValue(enableValues);
		setTargetPath(targetPath);
	}

	/**
	 * Copy constructor
	 */
	private ComboBoxEnableTarget(
			ComboBoxEnableTarget comboBoxEnableTargetToCopy) {
		super(comboBoxEnableTargetToCopy);
		valueString = comboBoxEnableTargetToCopy.valueString;
		targetPath = comboBoxEnableTargetToCopy.targetPath;
	}

	//#end region

	//#region METHODS

	@Override
	public ComboBoxEnableTarget copy() {
		return new ComboBoxEnableTarget(this);
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("switch.png");
	}

	@Override
	public void createAtomControl(Composite parent,
			FocusChangingRefreshable treeViewerRefreshable) {

	}

	//#end region

	//#region ACCESSORS

	public List<String> getItems() {
		String[] items = valueString.split(",");
		return Arrays.asList(items);
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public void setValue(String value) {
		this.valueString = value;
	}

	//#end region

}
