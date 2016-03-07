package org.treez.core.atom.variablefield;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;

/**
 * Represents a model variable (-text field) that is used to enter an Integer
 * value
 */
public class IntegerVariableField extends AbstractVariableField<Integer> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(IntegerVariableField.class);

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public IntegerVariableField(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 *
	 * @param fieldToCopy
	 */
	private IntegerVariableField(IntegerVariableField fieldToCopy) {
		super(fieldToCopy);
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public IntegerVariableField copy() {
		return new IntegerVariableField(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideBaseImage() {
		return Activator.getImage("integerVariable.png");
	}

	@Override
	public AbstractAttributeAtom<Integer> createAttributeAtomControl(
			Composite parent, Refreshable treeViewerRefreshable) {
		this.treeViewRefreshable = treeViewerRefreshable;

		//initialize integer value at the first call
		if (!isInitialized()) {
			Integer defaultValue = getDefaultValue();
			set(defaultValue);
		}

		//toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		//check if content should be shown in individual lines
		boolean useIndividualLines = useIndividualLines();

		//create container composite
		//its layout depends on the length of the labels and values
		Composite container = createContainerForLabelsAndTextFields(parent,
				toolkit, useIndividualLines);

		//label
		createValueLabel(toolkit, container);

		//value
		createValueTextField(toolkit, container);

		return this;
	}

	@Override
	public void setBackgroundColor(
			org.eclipse.swt.graphics.Color backgroundColor) {
		throw new IllegalStateException("Not yet implemented");

	}

	@Override
	public AbstractVariableListField<Integer> createVariableListField() {

		IntegerVariableListField listField = new IntegerVariableListField(name);
		List<Integer> valueList = new ArrayList<>();
		Integer currentValue = get();
		valueList.add(currentValue);
		listField.set(valueList);

		return listField;
	}

	//#end region

	//#region ACCESSORS

	//#region VALUE

	/**
	 * Returns the Integer value. This does not use the attributeValue to store
	 * the state of this attribute atom but uses the valueString
	 */
	@Override
	public Integer get() {
		Integer value = new Integer(getValueString());
		return value;
	}

	@Override
	public void set(Integer value) {
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

	//#region DEFAULT VALUE

	/**
	 * @return
	 */
	@Override
	public Integer getDefaultValue() {
		Integer value = new Integer(getDefaultValueString());
		return value;
	}

	/**
	 * @param defaultValue
	 */
	public void setDefaultValue(Integer defaultValue) {
		if (defaultValue == null) {
			setDefaultValueString("");
		} else {
			setDefaultValueString("" + defaultValue);
		}
	}

	//#end region

	//#end region

}
