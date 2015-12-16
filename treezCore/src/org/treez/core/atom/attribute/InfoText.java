package org.treez.core.atom.attribute;

import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
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

/**
 * Shows a non editable info text
 */
public class InfoText extends AbstractAttributeAtom<String> {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(InfoText.class);

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "Info:")
	private String label;

	@IsParameter(defaultValue = "")
	private String defaultValue;

	@IsParameter(defaultValue = "")
	private String tooltip;

	/**
	 * The text field
	 */
	private Text labelField = null;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public InfoText(String name) {
		super(name);
		label = name;
	}

	/**
	 * Copy constructor
	 *
	 * @param infoTextToCopy
	 */
	private InfoText(InfoText infoTextToCopy) {
		super(infoTextToCopy);
		label = infoTextToCopy.label;
		defaultValue = infoTextToCopy.defaultValue;
		tooltip = infoTextToCopy.tooltip;
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public InfoText copy() {
		return new InfoText(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("infoText.png");
	}

	@Override
	@SuppressWarnings("checkstyle:magicnumber")
	public AbstractAttributeAtom<String> createAttributeAtomControl(
			Composite parent, Refreshable treeViewerRefreshable) {

		//initialize value at the first call
		if (!isInitialized()) {
			set(defaultValue);
		}

		//toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		//heading label
		String currentLabel = getLabel();
		CustomLabel labelComposite = new CustomLabel(toolkit, parent,
				currentLabel);
		final int preferredLabelWidth = 80;
		labelComposite.setPrefferedWidth(preferredLabelWidth);

		labelField = toolkit.createText(parent, get(),
				SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		labelField.setEditable(false);
		labelField.setToolTipText(tooltip);

		GridData labelFillData = new GridData();
		labelFillData.grabExcessHorizontalSpace = true;
		labelFillData.horizontalAlignment = GridData.FILL;
		labelFillData.verticalAlignment = GridData.FILL;
		labelFillData.grabExcessVerticalSpace = true;
		labelFillData.heightHint = 80;
		labelFillData.widthHint = 200;

		labelField.setLayoutData(labelFillData);
		resetError();

		//initialize text
		refreshAttributeAtomControl();

		return this;
	}

	@Override
	public void setEnabled(boolean state) {
		if (isAvailable(labelField)) {
			labelField.setEnabled(state);
		}
	}

	@Override
	public void refreshAttributeAtomControl() {
		if (isAvailable(labelField)) {
			String value = get();
			try {
				String text = labelField.getText();
				if (!text.equals(value)) {
					labelField.setText(value);
				}
			} catch (SWTException exception) {
				sysLog.warn("Could not update InfoText control", exception);
			}
		}
	}

	@Override
	public void setBackgroundColor(
			org.eclipse.swt.graphics.Color backgroundColor) {
		throw new IllegalStateException("Not yet implemented");

	}

	/**
	 * Highlight the info text to show that represents an error
	 */
	public void highlightError() {
		if (isAvailable(labelField)) {
			final Color errorColor = new Color(Display.getCurrent(), 250, 200,
					128);
			labelField.setBackground(errorColor);
		}
	}

	/**
	 * Resets the highlighting of the info text to show normal state
	 */
	public void resetError() {
		if (isAvailable(labelField)) {
			final Color normalColor = new Color(Display.getCurrent(), 250, 250,
					250);
			labelField.setBackground(normalColor);
		}
	}

	@Override
	public void addModificationConsumer(Consumer<String> consumer) {

		throw new IllegalStateException(
				"InfoText does not support modification listeners");
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

	//#end region

}
