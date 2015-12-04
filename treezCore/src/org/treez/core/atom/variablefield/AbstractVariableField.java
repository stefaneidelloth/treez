package org.treez.core.atom.variablefield;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.swt.CustomLabel;

/**
 * Abstract parent class for some variable fields
 */
public abstract class AbstractVariableField<T> extends AbstractAttributeAtom<T>
		implements
			VariableField {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger
			.getLogger(AbstractVariableField.class);

	//#region ATTRIBUTES

	/**
	 * Background color
	 */
	protected static final Color BACKGROUND_COLOR = new Color(null, 240, 245,
			249);

	@IsParameter(defaultValue = "MyVariable")
	private String label;

	@IsParameter(defaultValue = "NaN")
	private String defaultValueString;

	@IsParameter(defaultValue = "")
	private String tooltip;

	/**
	 * Contains the actual valueString. This is used together with the
	 * unitString to represent the state of this attribute atom. The
	 * attributeValue is derived from them.
	 */
	private String valueString;

	/**
	 * The value text field
	 */
	protected Text valueField = null;

	/**
	 * Used to avoid update collisions
	 */
	private boolean isUpdating = false;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public AbstractVariableField(String name) {
		super(name);
		label = name;
	}

	/**
	 * Copy constructor
	 *
	 * @param fieldToCopy
	 */
	protected AbstractVariableField(AbstractVariableField<T> fieldToCopy) {
		super(fieldToCopy);
		label = fieldToCopy.label;
		defaultValueString = fieldToCopy.defaultValueString;
		tooltip = fieldToCopy.tooltip;
		valueString = fieldToCopy.valueString;
	}

	//#end region

	//#region METHODS

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		Image baseImage = provideBaseImage();
		Image image;
		if (isEnabled()) {
			image = Activator.getOverlayImageStatic(baseImage,
					"enabledDecoration.png");
		} else {
			image = Activator.getOverlayImageStatic(baseImage,
					"disabledDecoration.png");
		}

		return image;
	}

	protected abstract Image provideBaseImage();

	/**
	 * Checks if the content should be shown in individual lines
	 *
	 * @return
	 */
	protected boolean useIndividualLines() {
		//get sizes
		int valueLabelSize = 0;
		if (label != null) {
			valueLabelSize = label.length();
		}

		String currentValueString = getValueString();
		int valueSize = 0;
		if (currentValueString != null) {
			valueSize = currentValueString.length();
		}

		//check if the content is too long for a single line
		boolean useIndividualLines = (valueLabelSize
				+ valueSize) > CHARACTER_LENGTH_LIMIT;
		return useIndividualLines;
	}

	/**
	 * Creates a container composite for the labels and text fields. If the
	 * labels and entries are short, every thing is displayed in a single line.
	 * If the labels or entries are long, several lines are used.
	 *
	 * @param parent
	 * @param toolkit
	 * @param useIndividualLines
	 * @return
	 */
	protected static Composite createContainerForLabelsAndTextFields(
			Composite parent, FormToolkit toolkit, boolean useIndividualLines) {

		//create container
		Composite container = toolkit.createComposite(parent);

		//create layout
		if (useIndividualLines) {
			createContainerLayoutForIndividualLines(container);
		} else {
			createContainerLayoutForSingleLine(container);
		}

		//set background color
		container.setBackground(BACKGROUND_COLOR);

		return container;
	}

	/**
	 * Creates a container layout that will show all content in a single line
	 *
	 * @param container
	 */
	@SuppressWarnings("checkstyle:magicnumber")
	private static void createContainerLayoutForSingleLine(
			Composite container) {
		org.eclipse.swt.layout.GridLayout gridLayout = new org.eclipse.swt.layout.GridLayout(
				5, false);
		gridLayout.horizontalSpacing = 5;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 2;
		container.setLayout(gridLayout);

		//create grid data to use all horizontal space
		GridData containerfillHorizontal = new GridData();
		containerfillHorizontal.grabExcessHorizontalSpace = true;
		containerfillHorizontal.horizontalAlignment = GridData.FILL;
		container.setLayoutData(containerfillHorizontal);
	}

	/**
	 * Creates a container layout that will show the individual labels and
	 * entries in individual lines
	 *
	 * @param container
	 */
	@SuppressWarnings("checkstyle:magicnumber")
	private static void createContainerLayoutForIndividualLines(
			Composite container) {
		org.eclipse.swt.layout.GridLayout gridLayout = new org.eclipse.swt.layout.GridLayout(
				1, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 5;
		gridLayout.marginHeight = 2;
		container.setLayout(gridLayout);

		//create grid data to use all horizontal space
		GridData containerfillHorizontal = new GridData();
		containerfillHorizontal.grabExcessHorizontalSpace = true;
		containerfillHorizontal.horizontalAlignment = GridData.FILL;
		container.setLayoutData(containerfillHorizontal);

	}

	/**
	 * Creates the label for the value
	 *
	 * @param toolkit
	 * @param container
	 */
	@SuppressWarnings("checkstyle:magicnumber")
	protected void createValueLabel(FormToolkit toolkit, Composite container) {

		CustomLabel labelComposite = new CustomLabel(toolkit, container, label);
		labelComposite.setPrefferedWidth(80);
		labelComposite.setBackground(BACKGROUND_COLOR);

	}

	/**
	 * Creates the text field for the value
	 *
	 * @param toolkit
	 * @param container
	 */
	protected void createValueTextField(FormToolkit toolkit,
			Composite container) {
		String value = getValueString();
		valueField = toolkit.createText(container, value, SWT.BORDER);
		valueField.setToolTipText(tooltip);
		valueField.setEnabled(isEnabled());
		GridData valueFillHorizontal = new GridData();
		valueFillHorizontal.grabExcessHorizontalSpace = true;
		valueFillHorizontal.horizontalAlignment = GridData.FILL;
		valueFillHorizontal.verticalAlignment = GridData.CENTER;
		valueField.setLayoutData(valueFillHorizontal);
		ModifyListener valueModifyListener = (event) -> updateValue(event);
		valueField.addModifyListener(valueModifyListener);
	}

	private synchronized void updateValue(ModifyEvent event) {

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

	/**
	 * Sets the value string without checking it.
	 *
	 * @param valueString
	 */
	private void setValueStringUnchecked(String valueString) {
		this.valueString = valueString;
		if (valueField != null && !valueField.isDisposed()) {
			valueField.setText(valueString);
		}
		setInitialized();
		triggerModificationListeners();
	}

	@Override
	public void setEnabled(boolean state) {
		super.setEnabled(state);
		if (valueField != null && !valueField.isDisposed()) {
			valueField.setEnabled(state);
		}
		if (treeViewRefreshable != null) {
			treeViewRefreshable.refresh();
		}
		refreshAttributeAtomControl();
	}

	@Override
	public void refreshAttributeAtomControl() {
		if (valueField != null && !valueField.isDisposed()) {
			String currentValueString = getValueString();
			if (!valueField.getText().equals(currentValueString)) {
				valueField.setText(currentValueString);
			}
		}
	}

	//#end region

	//#region ACCESSORS

	//#region VALUE STRING

	/**
	 * Gets the value string
	 *
	 * @return
	 */
	@Override
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

	}

	//#end region

	//#region VALUE LABEL

	/**
	 * @return
	 */
	@Override
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

	//#region DEFAULT VALUE

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

	//#end region

}
