package org.treez.core.atom.variablelist;

import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.atom.variablefield.QuantityVariableField;
import org.treez.core.atom.variablefield.VariableField;
import org.treez.core.quantity.Quantity;
import org.treez.core.scripting.ScriptType;
import org.treez.core.springspel.VectorEvaluation;
import org.treez.core.swt.CustomLabel;

/**
 * Allows a user to enter a string that is interpreted as a list of numeric
 * values and a string that is interpreted as unit. This is use for example by
 * the study atom QuantityVariableRange
 */
public class QuantityVariableListField
		extends
			AbstractVariableListField<Quantity> {

	//#region ATTRIBUTES

	/**
	 * Used to parse string , e.g. "range(1,10,0.5)" to double list *
	 */
	private static VectorEvaluation vectorEvaluation;

	@IsParameter(defaultValue = "MyVariable")
	private String label;

	@IsParameter(defaultValue = "NaN")
	private String defaultValueString;

	@IsParameter(defaultValue = "1")
	private String defaultUnitString;

	@IsParameter(defaultValue = "")
	private String tooltip;

	/**
	 * Contains the actual valueString. This is used together with the
	 * unitString to represent the state of this attribute atom. The
	 * attributeValue is derived from them.
	 */
	private String valueString;

	/**
	 * Contains the actual unitString. This is used together with the
	 * valueString to represent the state of this attribute atom. The
	 * attributeValue is derived from them.
	 */
	private String unitString;

	/**
	 * The value text field, may contain a single number or an expression to
	 * create a list of numbers
	 */
	private Text valueField = null;

	/**
	 * The unit text field
	 */
	private Text unitField = null;

	/**
	 * Used to avoid update collisions
	 */
	private boolean isUpdating = true;

	//#end region

	//#region CONSTRUCTORS

	public QuantityVariableListField(String name) {
		super(name);
		label = name;
		vectorEvaluation = new VectorEvaluation();
	}

	/**
	 * Copy Constructor
	 */
	private QuantityVariableListField(QuantityVariableListField fieldToCopy) {
		super(fieldToCopy);
		label = fieldToCopy.label;
		defaultValueString = fieldToCopy.defaultValueString;
		defaultUnitString = fieldToCopy.defaultUnitString;
		tooltip = fieldToCopy.tooltip;
		valueString = fieldToCopy.valueString;
		unitString = fieldToCopy.unitString;
		vectorEvaluation = new VectorEvaluation();
	}

	//#end region

	//#region METHODS

	@Override
	public QuantityVariableListField copy() {
		return new QuantityVariableListField(this);
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("variable.png");
	}

	@Override
	@SuppressWarnings("checkstyle:magicnumber")
	public AbstractAttributeAtom<List<Quantity>> createAttributeAtomControl(
			Composite parent, FocusChangingRefreshable treeViewerRefreshable) {
		this.treeViewRefreshable = treeViewerRefreshable;

		//initialize quantity list value at the first call
		if (!isInitialized()) {
			set(getDefaultValue());
		}

		//toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		//create grid data to use all horizontal space
		GridData fillHorizontal = new GridData();
		fillHorizontal.grabExcessHorizontalSpace = true;
		fillHorizontal.horizontalAlignment = GridData.FILL;

		//create container control for labels and text fields
		Composite container = createContainerComposite(parent, toolkit,
				fillHorizontal);

		//label
		CustomLabel labelComposite = new CustomLabel(toolkit, container, label);
		labelComposite.setPrefferedWidth(80);

		//value text field
		createValueTextField(toolkit, container);

		//unit start label
		@SuppressWarnings("unused")
		CustomLabel unitStartLabel = new CustomLabel(toolkit, container, "[ ");

		//unit text field
		createUnitTextField(toolkit, container);

		int preferredUnitWidth = 40;
		int unitWidth = unitField.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		if (unitWidth < preferredUnitWidth) {
			GridDataFactory.fillDefaults().hint(preferredUnitWidth, SWT.DEFAULT)
					.applyTo(unitField);
		}

		//unit end label
		@SuppressWarnings("unused")
		CustomLabel unitEndLabel = new CustomLabel(toolkit, container, "]");

		return this;
	}

	private void createUnitTextField(FormToolkit toolkit, Composite container) {
		unitField = toolkit.createText(container, getUnitString());
		unitField.setEnabled(isEnabled());
		ModifyListener unitModifyListener = (event) -> updateUnit(event);
		unitField.addModifyListener(unitModifyListener);
	}

	private synchronized void updateUnit(ModifyEvent event) {

		//avoid update loops
		if (isUpdating) {
			return;
		}

		//set update lock
		isUpdating = true;

		//get text field
		Text textField = (Text) event.getSource();

		//get current caret position
		int caretPosition = textField.getCaretPosition();

		//get unit from text field
		String currentUnitString = textField.getText();

		//set unit
		setUnitString(currentUnitString);

		//restore caret position
		textField.setSelection(caretPosition);

		//release update lock
		isUpdating = false;
	}

	private void createValueTextField(FormToolkit toolkit,
			Composite container) {
		valueField = toolkit.createText(container, getValueString());
		valueField.setToolTipText(tooltip);
		valueField.setEnabled(isEnabled());
		GridData valueFillHorizontal = new GridData();
		valueFillHorizontal.grabExcessHorizontalSpace = true;
		valueFillHorizontal.horizontalAlignment = GridData.FILL;
		valueField.setLayoutData(valueFillHorizontal);
		ModifyListener valueModifyListener = (event) -> updateValue(event);
		valueField.addModifyListener(valueModifyListener);
	}

	private void updateValue(ModifyEvent event) {

		//avoid update loops
		if (isUpdating) {
			return;
		}

		//set update lock
		isUpdating = true;

		//get text field
		Text textField = (Text) event.getSource();

		//get current caret position
		int caretPosition = textField.getCaretPosition();

		//get value expression from text field
		String currentValueString = textField.getText();
		//set value expression
		setValueString(currentValueString);

		//restore caret position
		textField.setSelection(caretPosition);

		//release update lock
		isUpdating = false;
	}

	@SuppressWarnings("checkstyle:magicnumber")
	private static Composite createContainerComposite(Composite parent,
			FormToolkit toolkit, GridData fillHorizontal) {
		Composite container = toolkit.createComposite(parent);
		org.eclipse.swt.layout.GridLayout gridLayout = new org.eclipse.swt.layout.GridLayout(
				8, false);
		gridLayout.horizontalSpacing = 5;
		gridLayout.marginWidth = 0;
		container.setLayout(gridLayout);
		container.setLayoutData(fillHorizontal);
		return container;
	}

	@Override
	public QuantityVariableListFieldCodeAdaption createCodeAdaption(
			ScriptType scriptType) {

		QuantityVariableListFieldCodeAdaption codeAdaption;
		switch (scriptType) {
			case JAVA :
				codeAdaption = new QuantityVariableListFieldCodeAdaption(this);
				break;
			default :
				String message = "The ScriptType " + scriptType
						+ " is not yet implemented.";
				throw new IllegalStateException(message);
		}

		return codeAdaption;
	}

	@Override
	public void setEnabled(boolean state) {
		if (valueField != null) {
			valueField.setEnabled(state);
		}

		if (unitField != null) {
			unitField.setEnabled(state);
		}
	}

	@Override
	public void refreshAttributeAtomControl() {
		if (valueField != null) {
			String currentValueString = getValueString();
			if (!valueField.getText().equals(currentValueString)) {
				valueField.setText(currentValueString);
			}
		}

		if (unitField != null) {
			String currentUnitString = getUnitString();
			if (!unitField.getText().equals(currentUnitString)) {
				unitField.setText(currentUnitString);
			}
		}
	}

	/**
	 * Creates a list of Quantities by evaluating the value string to a double
	 * list and converting the double list and the given unit to a quantity list
	 *
	 * @param valueString
	 * @param unitString
	 * @return
	 */
	private static List<Quantity> createQuantityList(String valueString,
			String unitString) {
		List<Double> values = vectorEvaluation
				.parseStringToDoubleList(valueString);
		List<Quantity> quantityList = Quantity.createQuantityList(values,
				unitString);
		return quantityList;

	}

	/**
	 * Sets the value string without checking it.
	 *
	 * @param valueString
	 */
	private void setValueStringUnchecked(String valueString) {
		this.valueString = valueString;
		if (valueField != null) {
			valueField.setText(valueString);
		}
		triggerListeners();
	}

	/**
	 * Sets the unit string without checking it.
	 *
	 * @param unitString
	 */
	private void setUnitStringUnchecked(String unitString) {
		this.unitString = unitString;
		if (unitField != null) {
			unitField.setText(unitString);
		}
		triggerListeners();
	}

	@Override
	public VariableField<Quantity> createVariableField() {
		QuantityVariableField variableField = new QuantityVariableField(name);
		List<Quantity> currentValues = get();
		if (currentValues == null || currentValues.isEmpty()) {
			variableField.set(null);
		} else {
			Quantity firstValue = currentValues.get(0);
			variableField.set(firstValue);
		}
		return variableField;

	}

	//#end region

	//#region ACCESSORS

	@Override
	public void setBackgroundColor(
			org.eclipse.swt.graphics.Color backgroundColor) {
		throw new IllegalStateException("Not yet implemented");

	}

	//#region VALUE

	/**
	 * Returns the quantity list. This does not use the attributeValue to store
	 * the state of this attribute atom but uses the valueString and the
	 * unitString to do so.
	 */
	@Override
	public List<Quantity> get() {
		List<Quantity> quantities = createQuantityList(getValueString(),
				getUnitString());
		return quantities;
	}

	@Override
	public void set(List<Quantity> valueList) {
		disableModificationListeners();
		if (valueList.isEmpty()) {
			setValueString("");
			setUnitString("");
		} else {
			List<Double> doubleList = Quantity.createDoubleList(valueList);
			String currentValueString = VectorEvaluation
					.doubleListToDisplayString(doubleList);
			String currentUnitString = valueList.get(0).getUnit();
			setValueString(currentValueString);
			setUnitString(currentUnitString);
		}
		enableModificationListeners();
		triggerListeners();
	}

	/**
	 * Returns the numeric values as Double list.
	 *
	 * @return
	 */
	public List<Double> getDoubleValue() {
		List<Double> numericValues = vectorEvaluation
				.parseStringToDoubleList(valueString);
		return numericValues;
	}

	//#end region

	//#region VALUE STRING

	public String getValueString() {
		return valueString;
	}

	/**
	 * Sets the value string. If the given value is null, the value string is
	 * set to "".
	 *
	 * @param valueString
	 */
	public void setValueString(String valueString) {
		if (valueString == null) {
			boolean valueChanged = !"".equals(this.valueString);
			if (valueChanged) {
				setValueStringUnchecked("");
			}
		} else {
			boolean valueChanged = !valueString.equals(this.valueString);
			if (valueChanged) {
				setValueStringUnchecked(valueString);
			}
		}
		setInitialized();
	}

	//#end region

	//#region UNIT STRING

	public String getUnitString() {
		return unitString;
	}

	/**
	 * Sets the unit string. If the given value is null, the unit string is set
	 * to "". Specify the unit without brackets, e.g. "m" instead of "[m]"
	 *
	 * @param unitString
	 */
	public void setUnitString(String unitString) {
		if (unitString == null) {
			boolean unitChanged = !"".equals(this.unitString);
			if (unitChanged) {
				setUnitStringUnchecked("");
			}
		} else {
			boolean unitChanged = !unitString.equals(this.unitString);
			if (unitChanged) {
				setUnitStringUnchecked(unitString);
			}
		}
	}

	//#end region

	//#region VALUE LABEL

	public String getLabel() {
		return label;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	//#end region

	//#region DEFAULT VALUE & UNIT

	@Override
	public List<Quantity> getDefaultValue() {
		List<Quantity> quantities = createQuantityList(defaultValueString,
				defaultUnitString);
		return quantities;
	}

	public void setDefaultValue(List<Quantity> valueList) {
		if (valueList.isEmpty()) {
			setDefaultValueString("");
			setDefaultUnitString("");
		} else {
			List<Double> doubleList = Quantity.createDoubleList(valueList);
			String currentDefaultValueString = VectorEvaluation
					.doubleListToDisplayString(doubleList);
			String currentDefaultUnitString = valueList.get(0).getUnit();
			setDefaultValueString(currentDefaultValueString);
			setDefaultUnitString(currentDefaultUnitString);
		}
	}

	public String getDefaultValueString() {
		return defaultValueString;
	}

	public void setDefaultValueString(String defaultValueString) {
		this.defaultValueString = defaultValueString;
	}

	public String getDefaultUnitString() {
		return defaultUnitString;
	}

	public void setDefaultUnitString(String defaultUnitString) {
		this.defaultUnitString = defaultUnitString;
	}

	//#end region

	//#region TOOL TIP

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	//#end region

	//#end region

}
