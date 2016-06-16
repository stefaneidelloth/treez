package org.treez.core.atom.variablefield;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.variablelist.AbstractVariableListField;

/**
 * Represents a string model variable (-text field)
 */
public class StringVariableField extends AbstractVariableField<StringVariableField, String> {

	//#region CONSTRUCTORS

	public StringVariableField(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 */
	private StringVariableField(StringVariableField fieldToCopy) {
		super(fieldToCopy);
	}

	//#end region

	//#region METHODS

	@Override
	public StringVariableField getThis() {
		return this;
	}

	@Override
	public StringVariableField copy() {
		return new StringVariableField(this);
	}

	@Override
	public Image provideBaseImage() {
		return Activator.getImage("stringVariable.png");
	}

	@Override
	public StringVariableField createAttributeAtomControl(
			Composite parent,
			FocusChangingRefreshable treeViewerRefreshable) {
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
	public AbstractVariableListField<?, String> createVariableListField() {
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
		triggerListeners();
	}

	//#end region

	//#region DEFAULT VALUE

	@Override
	public String getDefaultValue() {
		return getDefaultValueString();
	}

	public StringVariableField setDefaultValue(String defaultValue) {
		if (defaultValue == null) {
			setDefaultValueString("");
		} else {
			setDefaultValueString(defaultValue);
		}
		return getThis();
	}

	//#end region

	@Override
	public StringVariableField setBackgroundColor(org.eclipse.swt.graphics.Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return getThis();
	}

	//#end region

}
