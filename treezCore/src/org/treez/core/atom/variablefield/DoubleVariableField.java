package org.treez.core.atom.variablefield;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.variablelist.AbstractVariableListField;
import org.treez.core.atom.variablelist.DoubleVariableListField;

/**
 * Represents a model variable (-text field) that is used to enter a Double value
 */
public class DoubleVariableField extends AbstractVariableField<Double> {

	//#region CONSTRUCTORS

	public DoubleVariableField(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 */
	private DoubleVariableField(DoubleVariableField fieldToCopy) {
		super(fieldToCopy);
	}

	//#end region

	//#region METHODS

	@Override
	public DoubleVariableField copy() {
		return new DoubleVariableField(this);
	}

	/*
	@Override
	public void addModificationConsumer(String key, Consumer<Double> consumer) {
	
		addModifyListener(key, (event) -> {
			Double doubleValue = Double.parseDouble(event.data.toString());
			consumer.accept(doubleValue);
		});
	}*/

	@Override
	public Image provideBaseImage() {
		return Activator.getImage("doubleVariable.png");
	}

	@Override
	public AbstractAttributeAtom<Double> createAttributeAtomControl(
			Composite parent,
			FocusChangingRefreshable treeViewerRefreshable) {
		this.treeViewRefreshable = treeViewerRefreshable;

		//initialize double value at the first call
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
	protected void restrictInput(VerifyEvent event) {
		String allowedCharacters = "0123456789.,eE+-";
		String text = event.text;
		for (int index = 0; index < text.length(); index++) {
			char character = text.charAt(index);
			boolean isAllowed = allowedCharacters.indexOf(character) > -1;
			if (!isAllowed) {
				event.doit = false;
				return;
			}
		}
	}

	@Override
	protected void validateValueOnChange(String text) {
		try {
			Double.parseDouble(valueField.getText());
			valueErrorDecorator.hide();
		} catch (NumberFormatException exception) {
			//expressions like "5e-" are allowed while typing
		}
	}

	@Override
	protected void validateValueOnFocusLoss(String value) {
		try {
			Double.parseDouble(valueField.getText());
			valueErrorDecorator.hide();
		} catch (NumberFormatException exception) {
			valueErrorDecorator.show();
		}
	}

	@Override
	public void setBackgroundColor(org.eclipse.swt.graphics.Color backgroundColor) {
		throw new IllegalStateException("Not yet implemented");

	}

	@Override
	public AbstractVariableListField<Double> createVariableListField() {

		DoubleVariableListField listField = new DoubleVariableListField(name);
		List<Double> valueList = new ArrayList<>();
		Double currentValue = get();
		valueList.add(currentValue);
		listField.set(valueList);

		return listField;
	}

	//#end region

	//#region ACCESSORS

	//#region VALUE

	/**
	 * Returns the Double value. This does not use the attributeValue to store the state of this attribute atom but uses
	 * the valueString
	 */
	@Override
	public Double get() {
		String valueString = getValueString();
		if (valueString.isEmpty()) {
			return null;
		}
		try {
			Double value = new Double(valueString);
			return value;
		} catch (NumberFormatException exception) {
			//this happens  for example if "e" is entered as string value
			return null;
		}
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

	//#region DEFAULT VALUE

	@Override
	public Double getDefaultValue() {
		Double value = new Double(getDefaultValueString());
		return value;
	}

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
