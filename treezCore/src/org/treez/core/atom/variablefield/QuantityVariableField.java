package org.treez.core.atom.variablefield;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.atom.variablelist.AbstractVariableListField;
import org.treez.core.atom.variablelist.QuantityVariableListField;
import org.treez.core.quantity.Quantity;
import org.treez.core.swt.CustomLabel;

/**
 * Represents a model variable (-text field)
 */
public class QuantityVariableField extends AbstractVariableField<Quantity> {

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "1")
	private String defaultUnitString;

	/**
	 * Contains the actual unitString. This is used together with the
	 * valueString to represent the state of this attribute atom. The
	 * attributeValue is derived from them.
	 */
	private String unitString;

	/**
	 * The unit text field
	 */
	private Text unitField = null;

	//#end region

	//#region CONSTRUCTORS

	public QuantityVariableField(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 */
	private QuantityVariableField(QuantityVariableField fieldToCopy) {
		super(fieldToCopy);
		defaultUnitString = fieldToCopy.defaultUnitString;
		unitString = fieldToCopy.unitString;
	}

	//#end region

	//#region METHODS

	@Override
	public QuantityVariableField copy() {
		return new QuantityVariableField(this);
	}

	@Override
	public Image provideBaseImage() {
		Image baseImage = Activator.getImage("quantityVariable.png");
		return baseImage;
	}

	@Override
	public AbstractAttributeAtom<Quantity> createAttributeAtomControl(
			Composite parent, FocusChangingRefreshable treeViewerRefreshable) {
		this.treeViewRefreshable = treeViewerRefreshable;

		//initialize quantity value at the first call
		if (!isInitialized()) {
			Quantity defaultValue = getDefaultValue();
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

		//unit
		createUnitContainer(toolkit, container, useIndividualLines);

		return this;
	}

	@Override
	protected void restrictInput(VerifyEvent event) {
		String text = event.text;
		try {
			Double.parseDouble(text);
		} catch (NumberFormatException ex) {
			event.doit = false;
		}
	}

	/**
	 * Checks if the content should be shown in individual lines
	 *
	 * @return
	 */
	@Override
	protected boolean useIndividualLines() {
		//get sizes
		int valueLabelSize = 0;
		String label = getLabel();
		if (label != null) {
			valueLabelSize = label.length();
		}

		String currentValueString = getValueString();
		int valueSize = 0;
		if (currentValueString != null) {
			valueSize = currentValueString.length();
		}

		String currentUnitString = getUnitString();
		int unitSize = 0;
		if (currentUnitString != null) {
			unitSize = currentUnitString.length();
		}

		//check if the content is too long for a single line
		boolean useIndividualLines = (valueLabelSize + valueSize
				+ unitSize) > CHARACTER_LENGTH_LIMIT;
		return useIndividualLines;
	}

	/**
	 * Creates a container that shows the unit in square brackets
	 *
	 * @param toolkit
	 * @param container
	 * @param useIndividualLines
	 */
	@SuppressWarnings("checkstyle:magicnumber")
	private void createUnitContainer(FormToolkit toolkit, Composite container,
			boolean useIndividualLines) {

		//create composite and set layout
		Composite unitComposite = toolkit.createComposite(container);
		GridLayout gridLayout = new GridLayout(5, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 2;
		unitComposite.setLayout(gridLayout);
		unitComposite.setBackground(backgroundColor);

		if (useIndividualLines) {
			GridData fillHorizontal = new GridData();
			fillHorizontal.grabExcessHorizontalSpace = true;
			fillHorizontal.horizontalAlignment = GridData.FILL;
			fillHorizontal.verticalAlignment = GridData.CENTER;
			unitComposite.setLayoutData(fillHorizontal);
		}

		//unit start label
		String startLabelText = "  [ ";
		if (useIndividualLines) {
			startLabelText = "unit: [ ";
		}
		CustomLabel unitStartLabel = new CustomLabel(toolkit, unitComposite,
				startLabelText);
		unitStartLabel.setBackground(backgroundColor);

		//unit text field
		createUnitTextField(toolkit, unitComposite, useIndividualLines);

		//unit end label
		CustomLabel unitEndLabel = new CustomLabel(toolkit, unitComposite, "]");
		unitEndLabel.setBackground(backgroundColor);
	}

	/**
	 * Creates the text field for the unit
	 *
	 * @param toolkit
	 * @param container
	 * @param useIndividualLines
	 */

	private void createUnitTextField(FormToolkit toolkit, Composite container,
			boolean useIndividualLines) {

		String currentUnitString = getUnitString();
		unitField = toolkit.createText(container, currentUnitString);
		unitField.setEnabled(isEnabled());

		ModifyListener unitModifyListener = (event) -> {
			//get text field
			Text textField = (Text) event.getSource();

			//get current caret position
			int caretPosition = textField.getCaretPosition();

			//get unit from text field
			String myUnitString = ((Text) event.getSource()).getText();

			//set unit
			setUnitString(myUnitString);

			//restore caret position
			textField.setSelection(caretPosition);

		};
		unitField.addModifyListener(unitModifyListener);

		if (useIndividualLines) {
			GridData fillHorizontal = new GridData();
			fillHorizontal.grabExcessHorizontalSpace = true;
			fillHorizontal.horizontalAlignment = GridData.FILL;
			fillHorizontal.verticalAlignment = GridData.CENTER;
			unitField.setLayoutData(fillHorizontal);
		} else {
			//unitField.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
			//false));
			final int preferredUnitWidth = 40;
			int unitWidth = unitField.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
			if (unitWidth < preferredUnitWidth) {
				GridDataFactory.fillDefaults()
						.hint(preferredUnitWidth, SWT.DEFAULT)
						.applyTo(unitField);
			}
		}
	}

	@Override
	public void setEnabled(boolean state) {
		super.setEnabled(state);
		if (valueField != null && !valueField.isDisposed()) {
			valueField.setEnabled(state);
		}

		if (unitField != null && !unitField.isDisposed()) {
			unitField.setEnabled(state);
		}
		if (treeViewRefreshable != null) {
			treeViewRefreshable.refresh();
		}
		refreshAttributeAtomControl();
	}

	@Override
	public void refreshAttributeAtomControl() {
		super.refreshAttributeAtomControl();
		if (unitField != null && !unitField.isDisposed()) {
			String currentUnitString = getUnitString();
			String oldUnitString = unitField.getText();
			if (oldUnitString == null) {
				if (currentUnitString != null) {
					unitField.setText(currentUnitString);
				}
			} else {
				if (!oldUnitString.equals(currentUnitString)) {
					if (currentUnitString != null) {
						unitField.setText(currentUnitString);
					}
				}
			}
		}
	}

	/**
	 * Sets the unit string without checking it.
	 *
	 * @param unitString
	 */
	private void setUnitStringUnchecked(String unitString) {
		this.unitString = unitString;
		if (unitField != null && !unitField.isDisposed()) {
			unitField.setText(unitString);
		}
		triggerModificationListeners();
	}

	@Override
	public void setBackgroundColor(
			org.eclipse.swt.graphics.Color backgroundColor) {
		throw new IllegalStateException("Not yet implemented");

	}

	@Override
	public AbstractVariableListField<Quantity> createVariableListField() {

		QuantityVariableListField listField = new QuantityVariableListField(
				name);
		List<Quantity> valueList = new ArrayList<>();
		Quantity currentValue = get();
		valueList.add(currentValue);
		listField.set(valueList);

		return listField;
	}

	//#end region

	//#region ACCESSORS

	//#region VALUE

	/**
	 * Returns the quantity. This does not use the attributeValue to store the
	 * state of this attribute atom but uses the valueString and the unitString
	 * to do so.
	 */
	@Override
	public Quantity get() {
		Quantity quantity = new Quantity(getValueString(), getUnitString());
		return quantity;
	}

	@Override
	public void set(Quantity quantity) {
		disableModificationListeners();
		if (quantity == null) {
			setValueString("");
			setUnitString("");
		} else {
			setValueString(quantity.getValue());
			setUnitString(quantity.getUnit());
		}
		enableModificationListeners();
		triggerModificationListeners();
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

	//#region DEFAULT VALUE & UNIT

	@Override
	public Quantity getDefaultValue() {
		Quantity quantity = new Quantity(getDefaultValueString(),
				defaultUnitString);
		return quantity;
	}

	public void setDefaultValue(Quantity quantity) {
		if (quantity == null) {
			setDefaultValueString("");
			setDefaultUnitString("");
		} else {
			setDefaultValueString(quantity.getValue());
			setDefaultUnitString(quantity.getUnit());
		}
	}

	public String getDefaultUnitString() {
		return defaultUnitString;
	}

	public void setDefaultUnitString(String defaultUnitString) {
		this.defaultUnitString = defaultUnitString;
	}

	//#end region

	//#end region

	//#end region

}
