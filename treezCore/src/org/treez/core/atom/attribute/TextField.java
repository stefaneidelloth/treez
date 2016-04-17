package org.treez.core.atom.attribute;

import java.util.function.Consumer;

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
import org.treez.core.swt.CustomLabel;
import org.treez.core.utils.Utils;

/**
 * An item example
 */
public class TextField extends AbstractAttributeAtom<String> {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(TextField.class);

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "My TextField:")
	private String label;

	@IsParameter(defaultValue = "80")
	private Integer prefferedLabelWidth;

	@IsParameter(defaultValue = "")
	private String defaultValue;

	@IsParameter(defaultValue = "")
	private String tooltip;

	@IsParameter(defaultValue = "false")
	private boolean usingPatternValidation;

	@IsParameter(defaultValue = "\\d*")
	private String validationPattern;

	@IsParameter(defaultValue = "false")
	private boolean usingNumberRangeValidation;

	@IsParameter(defaultValue = "null")
	private String min;

	@IsParameter(defaultValue = "1e3")
	private String max;

	@IsParameter(defaultValue = "The value is not valid.")
	private String errorMessage;

	/**
	 * Container for label and text field
	 */
	private Composite contentContainer;

	/**
	 * The text field
	 */
	private Text textField = null;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public TextField(String name) {
		super(name);
		label = Utils.firstToUpperCase(name); //this default label might be overridden by explicitly setting the label
	}

	/**
	 * Copy constructor
	 *
	 * @param fieldToCopy
	 */
	private TextField(TextField fieldToCopy) {
		super(fieldToCopy);
		label = fieldToCopy.label;
		prefferedLabelWidth = fieldToCopy.prefferedLabelWidth;
		defaultValue = fieldToCopy.defaultValue;
		tooltip = fieldToCopy.tooltip;
		usingPatternValidation = fieldToCopy.usingPatternValidation;
		validationPattern = fieldToCopy.validationPattern;
		usingNumberRangeValidation = fieldToCopy.usingNumberRangeValidation;
		min = fieldToCopy.min;
		max = fieldToCopy.max;
		errorMessage = fieldToCopy.errorMessage;
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public TextField copy() {
		return new TextField(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("TextField.png");
	}

	@Override
	public AbstractAttributeAtom<String> createAttributeAtomControl(
			Composite parent, Refreshable treeViewerRefreshable) {

		initializeValue();

		//toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		//create content composite for label and check box
		contentContainer = toolkit.createComposite(parent);

		//create container layout
		createContainerLayout();

		//label
		String currentLabel = getLabel();
		CustomLabel labelComposite = new CustomLabel(toolkit, contentContainer,
				currentLabel);
		labelComposite.setPrefferedWidth(prefferedLabelWidth);

		//text field
		textField = toolkit.createText(contentContainer, get());
		textField.setEnabled(isEnabled());
		textField.setToolTipText(tooltip);

		//create grid data to use all horizontal space
		GridData textFieldFillHorizontal = new GridData();

		final int prefferedTextWidth = 200;
		textFieldFillHorizontal.widthHint = prefferedTextWidth;

		textFieldFillHorizontal.grabExcessHorizontalSpace = true;
		textFieldFillHorizontal.horizontalAlignment = GridData.FILL;

		textField.setLayoutData(textFieldFillHorizontal);

		//Utils.registerHelpId(currentHelpId, parent);

		//validation & update

		//currently disabled since the error decoration causes flickering in
		//the property view
		//when a page is updated

		final TextFieldErrorDecoration errorDecoration = new TextFieldErrorDecoration(
				textField, errorMessage, contentContainer);

		ModifyListener modifyListener = (event) -> {

			//get current caret position
			int caretPosition = textField.getCaretPosition();

			//get text
			String text = ((Text) event.getSource()).getText();

			//validate text
			validateText(errorDecoration, text);

			//set text
			set(text);

			//restore caret position
			textField.setSelection(caretPosition);

			//trigger modification listeners
			triggerModificationListeners();

		};

		textField.addModifyListener(modifyListener);

		validateText(errorDecoration, get());

		return this;
	}

	private void createContainerLayout() {
		boolean useExtraTextBoxLine = label.length() > CHARACTER_LENGTH_LIMIT;
		if (useExtraTextBoxLine) {
			createLayoutForIndividualLines(contentContainer, 0);
		} else {
			createLayoutForSingleLine(contentContainer, 0);
		}
	}

	private void initializeValue() {
		//initialize value at the first call
		if (!isInitialized()) {
			set(defaultValue);
		}
	}

	/**
	 * Validates the text of the text field
	 *
	 * @param errorDecoration
	 * @param text
	 */
	private void validateText(final TextFieldErrorDecoration errorDecoration,
			String text) {

		String extraMessage = "";

		if (isPatternValidation()) {
			extraMessage = validateTextWidthPattern(text);
		}
		if (extraMessage.isEmpty()) {
			if (isNumberValidation() && text.length() > 0) {
				extraMessage = validateTextWidthNumberValidation(text);
			}
		}

		//show or hide error decoration
		showOrHideErrorDecoration(errorDecoration, text, extraMessage);
	}

	private String validateTextWidthPattern(String text) {
		String extraMessage = "";
		if (!text.matches(validationPattern)) {
			extraMessage = " The value does not match the regular expression '"
					+ validationPattern + "'.";
			sysLog.debug("does not match regexp");
		}
		return extraMessage;
	}

	private String validateTextWidthNumberValidation(String text) {
		String extraMessage = "";
		try {
			double number = Double.parseDouble(text);
			if (!min.equals("null")) {
				double minValue = Double.parseDouble(min);
				if (number < minValue) {
					extraMessage = " The value is smaller than the limit of "
							+ minValue + ".";
					sysLog.debug("is too small");
				}
			}
			if (!max.equals("null")) {
				double maxValue = Double.parseDouble(max);
				if (number > maxValue) {
					extraMessage = " The value is larger than the limit of "
							+ maxValue + ".";
					sysLog.debug("is too large");
				}
			}
		} catch (NumberFormatException e) {
			//double, min or max could not be parsed to a number
			extraMessage = " The number validation is active but the value could not be parsed as a number.";
			sysLog.debug("something could not be parsed");
		}
		return extraMessage;
	}

	private void showOrHideErrorDecoration(
			final TextFieldErrorDecoration errorDecoration, String text,
			String extraMessage) {
		if (extraMessage.isEmpty()) {
			errorDecoration.hide();
			set(text);
		} else {
			errorDecoration.show(extraMessage);
		}
	}

	@Override
	public void setEnabled(boolean state) {
		super.setEnabled(state);
		if (isAvailable(textField)) {
			textField.setEnabled(state);
		}
	}

	@Override
	public void refreshAttributeAtomControl() {
		if (isAvailable(textField)) {
			String value = get();
			if (!textField.getText().equals(value)) {
				textField.setText(get());
			}
		}
	}

	@Override
	public void setBackgroundColor(
			org.eclipse.swt.graphics.Color backgroundColor) {
		throw new IllegalStateException("Not yet implemented");

	}

	@Override
	public void addModificationConsumer(String key, Consumer<String> consumer) {
		addModifyListener(key, (event) -> {
			if (isAvailable(textField)) {
				String value = textField.getText();
				consumer.accept(value);
			}
		});
	}

	//#end region

	//#region ACCESSORS

	/**
	 * @return
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @param width
	 */
	public void setPrefferedLabelWidth(int width) {
		prefferedLabelWidth = width;
	}

	/**
	 * @return
	 */
	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

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

	/**
	 * @return
	 */
	public boolean isPatternValidation() {
		return usingPatternValidation;
	}

	/**
	 * @param patternValidation
	 */
	public void setPatternValidation(boolean patternValidation) {
		this.usingPatternValidation = patternValidation;
	}

	/**
	 * @return
	 */
	public String getValidationPattern() {
		return validationPattern;
	}

	/**
	 * @param validationPattern
	 */
	public void setValidationPattern(String validationPattern) {
		this.validationPattern = validationPattern;
	}

	/**
	 * @return
	 */
	public boolean isNumberValidation() {
		return usingNumberRangeValidation;
	}

	/**
	 * @param numberValidation
	 */
	public void setNumberValidation(boolean numberValidation) {
		this.usingNumberRangeValidation = numberValidation;
	}

	/**
	 * @return
	 */
	public String getMin() {
		return min;
	}

	/**
	 * @param min
	 */
	public void setMin(String min) {
		this.min = min;
	}

	/**
	 * @return
	 */
	public String getMax() {
		return max;
	}

	/**
	 * @param max
	 */
	public void setMax(String max) {
		this.max = max;
	}

	/**
	 * @return
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	//#end region

}
