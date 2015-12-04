package org.treez.core.atom.variablefield;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;

/**
 * Represents a string model variable (-text field)
 */
public class StringVariableField extends AbstractVariableField<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(StringVariableField.class);

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public StringVariableField(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 *
	 * @param fieldToCopy
	 */
	private StringVariableField(StringVariableField fieldToCopy) {
		super(fieldToCopy);
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public StringVariableField copy() {
		return new StringVariableField(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideBaseImage() {
		return Activator.getImage("stringVariable.png");
	}

	@Override
	public AbstractAttributeAtom<String> createAttributeAtomControl(Composite parent, Refreshable treeViewerRefreshable) {
		this.treeViewRefreshable = treeViewerRefreshable;

		//initialize quantity value at the first call
		if (!isInitialized()) {
			String defaultValue = getDefaultValue();
			set(defaultValue);
		}

		//toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		//check if content should be shown in individual lines
		boolean useIndividualLines = useIndividualLines();

		//create container composite
		//its layout depends on the length of the labels and values
		Composite container = createContainerForLabelsAndTextFields(parent, toolkit, useIndividualLines);

		//label
		createValueLabel(toolkit, container);

		//value
		createValueTextField(toolkit, container);

		return this;
	}

	@Override
	public void setBackgroundColor(org.eclipse.swt.graphics.Color backgroundColor) {
		throw new IllegalStateException("Not yet implemented");

	}

	//#end region

	//#region ACCESSORS

	//#region VALUE

	/**
	 * Returns the quantity. This does not use the attributeValue to store the state of this attribute atom but uses the
	 * valueString and the unitString to do so.
	 */
	@Override
	public String get() {
		return getValueString();
	}

	@Override
	public void set(String value) {
		disableModificationListeners();
		if (value == null) {
			setValueString("");
		} else {
			setValueString(value);
		}
		enableModificationListeners();
		triggerModificationListeners();
	}

	//#end region

	//#region DEFAULT VALUE

	/**
	 * @return
	 */
	@Override
	public String getDefaultValue() {
		return getDefaultValueString();
	}

	/**
	 * @param defaultValue
	 */
	public void setDefaultValue(String defaultValue) {
		if (defaultValue == null) {
			setDefaultValueString("");
		} else {
			setDefaultValueString(defaultValue);
		}
	}

	//#end region

	//#end region

}
