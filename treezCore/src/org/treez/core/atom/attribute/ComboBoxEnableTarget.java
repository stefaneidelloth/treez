package org.treez.core.atom.attribute;

import java.util.Arrays;
import java.util.List;

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
public class ComboBoxEnableTarget extends AbstractAttributeContainerAtom {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(ComboBoxEnableTarget.class);

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "")
	private String valueString; //e.g. "value1,value2"

	@IsParameter(defaultValue = "")
	private String targetPath; //e.g. "root.properties.mytext

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 * @param enableValues
	 *            a comma separated list of values for which the target is enabled
	 * @param targetPath
	 *            the model path to the target whose enabled state is controlled
	 */
	public ComboBoxEnableTarget(String name, String enableValues, String targetPath) {
		super(name);
		setValue(enableValues);
		setTargetPath(targetPath);
	}

	/**
	 * Copy constructor
	 *
	 * @param comboBoxEnableTargetToCopy
	 */
	private ComboBoxEnableTarget(ComboBoxEnableTarget comboBoxEnableTargetToCopy) {
		super(comboBoxEnableTargetToCopy);
		valueString = comboBoxEnableTargetToCopy.valueString;
		targetPath = comboBoxEnableTargetToCopy.targetPath;
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public ComboBoxEnableTarget copy() {
		return new ComboBoxEnableTarget(this);
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
	 * @return the items
	 */
	public List<String> getItems() {
		String[] items = valueString.split(",");
		return Arrays.asList(items);
	}

	/**
	 * Set target path
	 *
	 * @param targetPath
	 */
	public void setTargetPath(String targetPath) {
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
	 * Set value
	 *
	 * @param value
	 */
	public void setValue(String value) {
		this.valueString = value;
	}

	//#end region

}
