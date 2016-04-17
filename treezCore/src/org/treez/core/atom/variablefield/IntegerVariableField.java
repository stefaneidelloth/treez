package org.treez.core.atom.variablefield;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.variablelist.AbstractVariableListField;
import org.treez.core.atom.variablelist.IntegerVariableListField;

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
		contentContainer = createContainerForLabelsAndTextFields(parent,
				toolkit, useIndividualLines);

		//label
		createValueLabel(toolkit, contentContainer);

		//value
		createValueSpinner(contentContainer);

		return this;
	}

	/**
	 * Creates the text field for the value
	 *
	 * @param toolkit
	 * @param container
	 */
	protected void createValueSpinner(Composite container) {

		valueSpinner = new Spinner(container, SWT.BORDER);
		valueSpinner.setMinimum(minValue);
		valueSpinner.setMaximum(maxValue);
		Integer intValue = get();
		valueSpinner.setData(intValue);
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
		setValueString(currentValueString);

		//release update lock
		isUpdating = false;
	}

	/**
	 * Sets the value string without checking it.
	 *
	 * @param valueString
	 */
	@Override
	protected void setValueStringUnchecked(String valueString) {
		this.valueString = valueString;
		if (isAvailable(valueSpinner)) {
			valueSpinner.setData(new Integer(valueString));
		}
		setInitialized();
		triggerModificationListeners();
	}

	@Override
	public void setEnabled(boolean state) {
		super.setEnabled(state);
		if (isAvailable(valueSpinner)) {
			valueSpinner.setEnabled(state);
		}
		if (treeViewRefreshable != null) {
			treeViewRefreshable.refresh();
		}
		refreshAttributeAtomControl();
	}

	@Override
	public void refreshAttributeAtomControl() {
		if (isAvailable(valueSpinner)) {
			String currentValueString = getValueString();
			if (!valueSpinner.getText().equals(currentValueString)) {
				valueSpinner.setData(new Integer(currentValueString));
			}
		}
	}

	@Override
	public void setBackgroundColor(org.eclipse.swt.graphics.Color color) {
		super.setBackgroundColor(color);
		if (isAvailable(contentContainer)) {
			contentContainer.setBackground(color);
		}

		if (isAvailable(valueSpinner)) {
			valueSpinner.setBackground(color);
		}

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

	//#region MIN & MAX

	/**
	 * @param minValue
	 */
	public void setMinValue(Integer minValue) {
		this.minValue = minValue;
		if (isAvailable(valueSpinner)) {
			valueSpinner.setMinimum(minValue);
		}

	}

	/**
	 * @param maxValue
	 */
	public void setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
		if (isAvailable(valueSpinner)) {
			valueSpinner.setMaximum(maxValue);
		}
	}

	//#end region

}