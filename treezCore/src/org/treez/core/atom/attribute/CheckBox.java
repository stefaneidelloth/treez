package org.treez.core.atom.attribute;

import java.util.List;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.swt.CustomLabel;
import org.treez.core.utils.Utils;

public class CheckBox extends AbstractAttributeAtom<Boolean> {

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "Label")
	private String label;

	@IsParameter(defaultValue = "Tooltip")
	private String tooltip;

	@IsParameter(defaultValue = "false")
	private Boolean defaultValue;

	/**
	 * Container for label and check box
	 */
	private Composite contentContainer;

	/**
	 * The label
	 */
	private CustomLabel labelComposite;

	/**
	 * The check box
	 */
	private Button valueCheckBox;

	/**
	 * The parent composite for the attribute atom control can be stored here to
	 * be able to refresh it.
	 */
	protected Composite attributeAtomParent = null;

	//#end region

	//#region CONSTRUCTORS

	public CheckBox(String name) {
		super(name);
		label = Utils.firstToUpperCase(name); //this default label might be overridden by explicitly setting the label
	}

	public CheckBox(String name, boolean state) {
		super(name);
		label = name;
		set(state);
	}

	/**
	 * Copy constructor
	 */
	protected CheckBox(CheckBox checkBoxToCopy) {
		super(checkBoxToCopy);
		label = checkBoxToCopy.label;
		tooltip = checkBoxToCopy.tooltip;
		defaultValue = checkBoxToCopy.defaultValue;

	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public CheckBox copy() {
		return new CheckBox(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		Image baseImage = Activator.getImage("CheckBox.png");
		return baseImage;
	}

	@Override
	@SuppressWarnings("checkstyle:magicnumber")
	public AbstractAttributeAtom<Boolean> createAttributeAtomControl(
			Composite parent, Refreshable treeViewerRefreshable) {
		this.attributeAtomParent = parent;
		this.treeViewRefreshable = treeViewerRefreshable;

		//initialize value at the first call
		if (!isInitialized()) {
			set(defaultValue);
		}

		//create toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		//create content composite for label and check box
		contentContainer = toolkit.createComposite(parent);

		//check label length
		boolean useExtraCheckBoxLine = label.length() > CHARACTER_LENGTH_LIMIT;

		//create container layout
		int marginWidth = 0;
		if (useExtraCheckBoxLine) {
			createLayoutForIndividualLines(contentContainer, marginWidth);
		} else {
			createLayoutForSingleLine(contentContainer, marginWidth);
		}

		//create label
		createLabel(toolkit);

		//create check box
		createCheckBox(toolkit);

		//initialize filePath
		set(valueCheckBox.getSelection());

		return this;
	}

	private void createLabel(FormToolkit toolkit) {
		labelComposite = new CustomLabel(toolkit, contentContainer, label);
		final int prefferedLabelWidth = 80;
		labelComposite.setPrefferedWidth(prefferedLabelWidth);
	}

	private void createCheckBox(FormToolkit toolkit) {
		valueCheckBox = toolkit.createButton(contentContainer, "", SWT.CHECK);
		valueCheckBox.setEnabled(isEnabled());
		valueCheckBox.setSelection(get());
		valueCheckBox.setToolTipText(tooltip);

		//action listener
		valueCheckBox.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean currentValue = valueCheckBox.getSelection();
				set(currentValue);
				//updated enabled states if ComboBoxEnableTarget children exist
				updateTargetsEnabledStates(currentValue);

				//trigger modification listeners
				triggerModificationListeners();
			}

		});
	}

	@Override
	public void setEnabled(boolean state) {
		super.setEnabled(state);
		if (isAvailable(valueCheckBox)) {
			valueCheckBox.setEnabled(state);
		}
		if (treeViewRefreshable != null) {
			treeViewRefreshable.refresh();
		}
		this.refreshAttributeAtomControl();
	}

	@Override
	public void refreshAttributeAtomControl() {
		if (isAvailable(valueCheckBox)) {
			Boolean value = get();
			if (valueCheckBox.getSelection() != value) {
				valueCheckBox.setSelection(value);
			}
		}
	}

	/**
	 * Updates the enabled/disabled state of other components, dependent on the
	 * current value
	 *
	 * @param currentValue
	 */
	@SuppressWarnings("checkstyle:linelength")
	private void updateTargetsEnabledStates(Boolean currentValue) {
		List<TreeNodeAdaption> enableNodes = createTreeNodeAdaption()
				.getChildren();
		for (TreeNodeAdaption enableNode : enableNodes) {
			org.treez.core.atom.attribute.CheckBoxEnableTarget enableProperty = (org.treez.core.atom.attribute.CheckBoxEnableTarget) enableNode
					.getAdaptable();

			Boolean enableValue = enableProperty.getValue();
			String targetPath = enableProperty.getTargetPath();
			AttributeRoot root = (AttributeRoot) getRoot();
			AbstractAttributeAtom<?> target = (AbstractAttributeAtom<?>) root
					.getChild(targetPath);
			boolean enableTarget = enableValue.equals(currentValue);
			if (enableTarget) {
				target.setEnabled(true);
			} else {
				target.setEnabled(false);
			}
		}
	}

	@Override
	public void addModificationConsumer(String key,
			Consumer<Boolean> consumer) {
		addModifyListener(key, (event) -> {
			if (isAvailable(valueCheckBox)) {
				Boolean value = valueCheckBox.getSelection();
				consumer.accept(value);
			}
		});
	}

	//#end region

	//#region ACCESSORS

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	@Override
	public Boolean getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public void setBackgroundColor(Color color) {
		if (isAvailable(contentContainer)) {
			contentContainer.setBackground(color);
		}

		if (isAvailable(labelComposite)) {
			labelComposite.setBackground(color);
		}
		if (isAvailable(valueCheckBox)) {
			valueCheckBox.setBackground(color);
		}
	}

	//#end region

}
