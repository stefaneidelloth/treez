package org.treez.core.atom.variablefield;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.variablelist.IntegerVariableListField;

/**
 * Represents a model variable (-text field) that is used to enter an Integer value
 */
public class IntegerVariableField extends AbstractVariableField<IntegerVariableField, Integer> {

	//#region ATTRIBUTES

	/**
	 * Container of the label and the spinner
	 */
	private Composite contentContainer;

	/**
	 * This VariableField uses a Spinner instead of a text field
	 */
	private Spinner valueSpinner;

	private Integer minValue = Integer.MIN_VALUE;

	private Integer maxValue = Integer.MAX_VALUE;

	//#end region

	//#region CONSTRUCTORS

	public IntegerVariableField(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 */
	private IntegerVariableField(IntegerVariableField fieldToCopy) {
		super(fieldToCopy);
	}

	//#end region

	//#region METHODS

	@Override
	public IntegerVariableField getThis() {
		return this;
	}

	@Override
	public IntegerVariableField copy() {
		return new IntegerVariableField(this);
	}

	@Override
	public Image provideBaseImage() {
		return Activator.getImage("integerVariable.png");
	}

	@Override
	protected void setAttributeValueWithString(String valueString) {
		if (valueString == null) {
			attributeValue = null;
		} else {
			try {
				attributeValue = Integer.parseInt(valueString);
			} catch (NumberFormatException exception) {
				//Keep previous value
			}
		}
	}

	@Override
	public AbstractAttributeAtom<IntegerVariableField, Integer> createAttributeAtomControl(
			Composite parent,
			FocusChangingRefreshable treeViewerRefreshable) {
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
		contentContainer = createContainerForLabelsAndTextFields(parent, toolkit, useIndividualLines);

		//label
		createValueLabel(toolkit, contentContainer);

		//value
		createValueSpinner(contentContainer);

		return this;
	}

	protected void createValueSpinner(Composite container) {

		valueSpinner = new Spinner(container, SWT.BORDER);
		valueSpinner.setMinimum(minValue);
		valueSpinner.setMaximum(maxValue);
		Integer intValue = get();
		if (intValue == null) {
			intValue = 0;
		}
		valueSpinner.setSelection(intValue);
		valueSpinner.addModifyListener((event) -> updateValue(event));

		valueSpinner.setToolTipText(tooltip);

		valueSpinner.setEnabled(isEnabled());
		GridData valueFillHorizontal = new GridData();
		valueFillHorizontal.grabExcessHorizontalSpace = true;
		valueFillHorizontal.horizontalAlignment = GridData.FILL;
		valueFillHorizontal.verticalAlignment = GridData.CENTER;
		valueSpinner.setLayoutData(valueFillHorizontal);

	}

	private synchronized void updateValue(ModifyEvent event) {

		//avoid update loops
		if (isUpdating) {
			return;
		}

		//set update lock
		isUpdating = true;

		//get Spinner
		Spinner spinner = (Spinner) event.getSource();

		//get value expression from text field
		String currentValueString = spinner.getText();

		//set value expression
		if (currentValueString == null) {
			boolean valueChanged = !"".equals(this.valueString);
			if (valueChanged) {
				this.valueString = "";
				setInitialized();
				triggerListeners();
			}
		} else {
			boolean valueChanged = !currentValueString.equals(this.valueString);
			if (valueChanged) {
				this.valueString = currentValueString;
				setInitialized();
				triggerListeners();
			}
		}

		//release update lock
		isUpdating = false;
	}

	@Override
	protected void setValueStringUnchecked(String valueString) {
		this.valueString = valueString;
		if (isAvailable(valueSpinner)) {
			Integer intValue = Integer.parseInt(valueString);
			valueSpinner.setSelection(intValue);
		}
		setInitialized();
		triggerListeners();
	}

	@Override
	public IntegerVariableField setEnabled(boolean state) {
		super.setEnabled(state);
		if (isAvailable(valueSpinner)) {
			valueSpinner.setEnabled(state);
		}
		if (treeViewRefreshable != null) {
			//treeViewRefreshable.refresh(); //creates flickering when targets are updated
		}
		refreshAttributeAtomControl();
		return getThis();
	}

	@Override
	public void refreshAttributeAtomControl() {
		if (isAvailable(valueSpinner)) {
			String currentValueString = getValueString();
			if (!valueSpinner.getText().equals(currentValueString)) {
				valueSpinner.setSelection(new Integer(currentValueString));
			}
		}
	}

	@Override
	public IntegerVariableField setBackgroundColor(org.eclipse.swt.graphics.Color color) {
		super.setBackgroundColor(color);
		if (isAvailable(contentContainer)) {
			contentContainer.setBackground(color);
		}

		if (isAvailable(valueSpinner)) {
			valueSpinner.setBackground(color);
		}
		return getThis();

	}

	@Override
	public IntegerVariableListField createVariableListField() {

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
	 * Returns the Integer value. This does not use the attributeValue to store the state of this attribute atom but
	 * uses the valueString
	 */
	@Override
	public Integer get() {
		String valueString = getValueString();
		if (valueString == null || valueString.isEmpty()) {
			return getDefaultValue();
		}
		try {
			Integer value = Integer.parseInt(valueString);
			return value;
		} catch (NumberFormatException exception) {
			return null;
		}
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
		triggerListeners();
	}

	//#end region

	//#region DEFAULT VALUE

	@Override
	public Integer getDefaultValue() {
		try {
			Integer value = new Integer(getDefaultValueString());
			return value;
		} catch (NumberFormatException exception) {
			return null;
		}
	}

	public IntegerVariableField setDefaultValue(Integer defaultValue) {
		if (defaultValue == null) {
			setDefaultValueString("");
		} else {
			setDefaultValueString("" + defaultValue);
		}
		return getThis();
	}

	//#end region

	//#region MIN & MAX

	public IntegerVariableField setMinValue(Integer minValue) {
		this.minValue = minValue;
		if (isAvailable(valueSpinner)) {
			valueSpinner.setMinimum(minValue);
		}
		return getThis();
	}

	public IntegerVariableField setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
		if (isAvailable(valueSpinner)) {
			valueSpinner.setMaximum(maxValue);
		}
		return getThis();
	}

	//#end region

}
