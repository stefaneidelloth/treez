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
 * Represents a model variable (-text field) that is used to enter a Double value
 */
public class DoubleVariableField extends AbstractVariableField<Double> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(DoubleVariableField.class);

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public DoubleVariableField(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 *
	 * @param fieldToCopy
	 */
	private DoubleVariableField(DoubleVariableField fieldToCopy) {
		super(fieldToCopy);
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public DoubleVariableField copy() {
		return new DoubleVariableField(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideBaseImage() {
		return Activator.getImage("doubleVariable.png");
	}

	@Override
	public AbstractAttributeAtom<Double> createAttributeAtomControl(Composite parent, Refreshable treeViewerRefreshable) {
		this.treeViewRefreshable = treeViewerRefreshable;

		//initialize quantity value at the first call
		if (!isInitialized()) {
			Double defaultValue = getDefaultValue();
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
	public Double get() {
		Double value = new Double(getValueString());
		return value;
	}

	@Override
	public void set(Double value) {
		disableModificationListeners();
		if (value == null) {
			setValueString("");
		} else {
			setValueString("" + value);

		}
		enableModificationListeners();
		triggerModificationListeners();
	}

	//#end region

	//#region DEFAULT VALUE & UNIT

	/**
	 * @return
	 */
	@Override
	public Double getDefaultValue() {
		Double value = new Double(getDefaultValueString());
		return value;
	}

	/**
	 * @param defaultValue
	 */
	public void setDefaultValue(Double defaultValue) {
		if (defaultValue == null) {
			setDefaultValueString("");
		} else {
			setDefaultValueString("" + defaultValue);
		}
	}

	//#end region

	//#end region

}
