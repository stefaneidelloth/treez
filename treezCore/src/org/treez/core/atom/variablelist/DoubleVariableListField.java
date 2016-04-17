package org.treez.core.atom.variablelist;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.atom.variablefield.DoubleVariableField;
import org.treez.core.atom.variablefield.VariableField;
import org.treez.core.scripting.ScriptType;
import org.treez.core.springspel.VectorEvaluation;
import org.treez.core.swt.CustomLabel;

/**
 * Allows a user to enter a string that is interpreted as a list of numeric
 * values. This is use for example by the study atom DoubleVariableRange
 */
public class DoubleVariableListField extends AbstractVariableListField<Double> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger
			.getLogger(DoubleVariableListField.class);

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "MyVariable")
	private String label;

	@IsParameter(defaultValue = "{0.0}")
	private String defaultValueString;

	@IsParameter(defaultValue = "")
	private String tooltip;

	/**
	 * Contains the actual valueString.
	 */
	private String valueString;

	/**
	 * The value text field, may contain a single number or an expression to
	 * create a list of numbers
	 */
	private Text valueField = null;

	/**
	 * Used to parse string , e.g. "range(1,10,0.5)" to double list *
	 */
	private static VectorEvaluation vectorEvaluation;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public DoubleVariableListField(String name) {
		super(name);
		label = name;
		vectorEvaluation = new VectorEvaluation();
	}

	/**
	 * Copy Constructor
	 *
	 * @param fieldToCopy
	 */
	private DoubleVariableListField(DoubleVariableListField fieldToCopy) {
		super(fieldToCopy);
		label = fieldToCopy.label;
		defaultValueString = fieldToCopy.defaultValueString;
		tooltip = fieldToCopy.tooltip;
		valueString = fieldToCopy.valueString;
		vectorEvaluation = new VectorEvaluation();
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public DoubleVariableListField copy() {
		return new DoubleVariableListField(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("doubleVariable.png");
	}

	@Override
	@SuppressWarnings("checkstyle:magicnumber")
	public AbstractAttributeAtom<List<Double>> createAttributeAtomControl(
			Composite parent, Refreshable treeViewerRefreshable) {
		this.treeViewRefreshable = treeViewerRefreshable;

		//initialize Double list value at the first call
		if (!isInitialized()) {
			set(getDefaultValue());
		}

		//toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		//create grid data to use all horizontal space
		GridData fillHorizontal = new GridData();
		fillHorizontal.grabExcessHorizontalSpace = true;
		fillHorizontal.horizontalAlignment = GridData.FILL;

		//create container control for labels  and text fields
		Composite container = createContainerComposite(parent, toolkit,
				fillHorizontal);

		//label
		CustomLabel labelComposite = new CustomLabel(toolkit, container, label);
		labelComposite.setPrefferedWidth(80);

		//value text field
		createValueTextField(toolkit, container);

		return this;
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
		ModifyListener valueModifyListener = (event) -> {

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

		};
		valueField.addModifyListener(valueModifyListener);
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
	public DoubleVariableListFieldCodeAdaption createCodeAdaption(
			ScriptType scriptType) {

		DoubleVariableListFieldCodeAdaption codeAdaption;
		switch (scriptType) {
			case JAVA :
				codeAdaption = new DoubleVariableListFieldCodeAdaption(this);
				break;
			default :
				String message = "The ScriptType " + scriptType
						+ " is not yet implemented.";
				throw new IllegalStateException(message);
		}

		return codeAdaption;
	}

	@Override
	public void refreshAttributeAtomControl() {
		if (valueField != null) {
			String currentValueString = getValueString();
			if (!valueField.getText().equals(currentValueString)) {
				valueField.setText(currentValueString);
			}
		}

	}

	/**
	 * Creates a list of Doubles by evaluating the value string to a double list
	 *
	 * @param valueString
	 * @param unitString
	 * @return
	 */
	private static List<Double> createDoubleList(String valueString) {
		List<Double> values = vectorEvaluation
				.parseStringToDoubleList(valueString);
		return values;
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
		triggerModificationListeners();
	}

	@Override
	public VariableField<Double> createVariableField() {
		DoubleVariableField variableField = new DoubleVariableField(name);
		List<Double> currentValues = get();
		if (currentValues == null || currentValues.isEmpty()) {
			variableField.set(null);
		} else {
			Double firstValue = currentValues.get(0);
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

	@Override
	public void setEnabled(boolean state) {
		super.setEnabled(state);
		if (isAvailable(valueField)) {
			valueField.setEnabled(state);
		}
	}

	//#region VALUE

	/**
	 * Returns the Double list. This does not use the attributeValue to store
	 * the state of this attribute atom but uses the valueString
	 */
	@Override
	public List<Double> get() {
		List<Double> doubleList = createDoubleList(getValueString());
		return doubleList;
	}

	@Override
	public void set(List<Double> valueList) {
		disableModificationListeners();
		if (valueList.isEmpty()) {
			setValueString("");
		} else {
			String currentValueString = VectorEvaluation
					.doubleListToDisplayString(valueList);
			setValueString(currentValueString);
		}
		enableModificationListeners();
		triggerModificationListeners();
	}

	//#end region

	//#region VALUE STRING

	/**
	 * Gets the value string
	 *
	 * @return
	 */
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

	//#region VALUE LABEL

	/**
	 * @return
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 */
	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	//#end region

	//#region DEFAULT VALUE & UNIT

	/**
	 * @return
	 */
	@Override
	public List<Double> getDefaultValue() {
		List<Double> defaultValues = createDoubleList(defaultValueString);
		return defaultValues;
	}

	/**
	 * @param valueList
	 */
	public void setDefaultValue(List<Double> valueList) {
		if (valueList.isEmpty()) {
			setDefaultValueString("");
		} else {
			String currentDefaultValueString = VectorEvaluation
					.doubleListToDisplayString(valueList);
			setDefaultValueString(currentDefaultValueString);
		}
	}

	/**
	 * @return
	 */
	public String getDefaultValueString() {
		return defaultValueString;
	}

	/**
	 * @param defaultValueString
	 */
	public void setDefaultValueString(String defaultValueString) {
		this.defaultValueString = defaultValueString;
	}

	//#end region

	//#region TOOL TIP

	/**
	 * @return
	 */
	public String getTooltip() {
		return tooltip;
	}

	/**
	 * @param tooltip
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	//#end region

	//#end region

}
