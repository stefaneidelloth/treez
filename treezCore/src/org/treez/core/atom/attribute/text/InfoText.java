package org.treez.core.atom.attribute.text;

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
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.AbstractStringAttributeAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.swt.CustomLabel;

/**
 * Shows a non editable info text
 */
public class InfoText extends AbstractStringAttributeAtom<InfoText> {

	private static final Logger LOG = Logger.getLogger(InfoText.class);

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "Info:")
	private String label;

	@IsParameter(defaultValue = "")
	private String defaultValue;

	@IsParameter(defaultValue = "")
	private String tooltip;

	private Text labelField = null;

	//#end region

	//#region CONSTRUCTORS

	public InfoText(String name) {
		super(name);
		label = name;
	}

	/**
	 * Copy constructor
	 */
	private InfoText(InfoText infoTextToCopy) {
		super(infoTextToCopy);
		label = infoTextToCopy.label;
		defaultValue = infoTextToCopy.defaultValue;
		tooltip = infoTextToCopy.tooltip;
	}

	//#end region

	//#region METHODS

	@Override
	public InfoText getThis() {
		return this;
	}

	@Override
	public InfoText copy() {
		return new InfoText(this);
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("infoText.png");
	}

	@Override
	@SuppressWarnings("checkstyle:magicnumber")
	public AbstractStringAttributeAtom<InfoText> createAttributeAtomControl(
			Composite parent,
			FocusChangingRefreshable treeViewerRefreshable) {

		//initialize value at the first call
		if (!isInitialized()) {
			set(defaultValue);
		}

		//toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		//heading label
		String currentLabel = getLabel();
		CustomLabel labelComposite = new CustomLabel(toolkit, parent, currentLabel);
		final int preferredLabelWidth = 80;
		labelComposite.setPrefferedWidth(preferredLabelWidth);

		labelField = toolkit.createText(parent, get(), SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
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
	public InfoText setEnabled(boolean state) {
		if (isAvailable(labelField)) {
			labelField.setEnabled(state);
		}
		return getThis();
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
				LOG.warn("Could not update InfoText control", exception);
			}
		}
	}

	@Override
	public InfoText setBackgroundColor(org.eclipse.swt.graphics.Color backgroundColor) {
		throw new IllegalStateException("Not yet implemented");

	}

	/**
	 * Highlight the info text to show that represents an error
	 */
	public void highlightError() {
		if (isAvailable(labelField)) {
			final Color errorColor = new Color(Display.getCurrent(), 250, 200, 128);
			labelField.setBackground(errorColor);
		}
	}

	/**
	 * Resets the highlighting of the info text to show normal state
	 */
	public void resetError() {
		if (isAvailable(labelField)) {
			final Color normalColor = new Color(Display.getCurrent(), 250, 250, 250);
			labelField.setBackground(normalColor);
		}
	}

	//#end region

	//#region ACCESSORS

	public String getLabel() {
		return label;
	}

	public InfoText setLabel(String label) {
		this.label = label;
		return getThis();
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	public InfoText setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return getThis();
	}

	public String getTooltip() {
		return tooltip;
	}

	public InfoText setTooltip(String tooltip) {
		this.tooltip = tooltip;
		return getThis();
	}

	//#end region

}
