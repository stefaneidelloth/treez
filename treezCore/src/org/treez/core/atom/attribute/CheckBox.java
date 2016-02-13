package org.treez.core.atom.attribute;

import java.util.List;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
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

/**
 * An item example
 */
public class CheckBox extends AbstractAttributeAtom<Boolean> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(CheckBox.class);

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
	private Button checkBox;

	/**
	 * The parent composite for the attribute atom control can be stored here to
	 * be able to refresh it.
	 */
	protected Composite attributeAtomParent = null;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public CheckBox(String name) {
		super(name);
		label = Utils.firstToUpperCase(name); //this default label might be overridden by explicitly setting the label
	}

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public CheckBox(String name, boolean state) {
		super(name);
		label = name;
		set(state);
	}

	/**
	 * Copy constructor
	 *
	 * @param checkBoxToCopy
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
		set(checkBox.getSelection());

		return this;
	}

	private void createLabel(FormToolkit toolkit) {
		labelComposite = new CustomLabel(toolkit, contentContainer, label);
		final int prefferedLabelWidth = 80;
		labelComposite.setPrefferedWidth(prefferedLabelWidth);
	}

	private void createCheckBox(FormToolkit toolkit) {
		checkBox = toolkit.createButton(contentContainer, "", SWT.CHECK);
		checkBox.setEnabled(isEnabled());
		checkBox.setSelection(get());
		checkBox.setToolTipText(tooltip);

		//action listener
		checkBox.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean currentValue = checkBox.getSelection();
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
		if (isAvailable(checkBox)) {
			checkBox.setEnabled(state);
		}
		if (treeViewRefreshable != null) {
			treeViewRefreshable.refresh();
		}
		this.refreshAttributeAtomControl();
	}

	@Override
	public void refreshAttributeAtomControl() {
		if (isAvailable(checkBox)) {
			Boolean value = get();
			if (checkBox.getSelection() != value) {
				checkBox.setSelection(value);
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
			if (isAvailable(checkBox)) {
				Boolean value = checkBox.getSelection();
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
	@Override
	public Boolean getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue
	 */
	public void setDefaultValue(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Sets the background color
	 *
	 * @param color
	 */
	@Override
	public void setBackgroundColor(Color color) {
		if (isAvailable(contentContainer)) {
			contentContainer.setBackground(color);
		}

		if (isAvailable(labelComposite)) {
			labelComposite.setBackground(color);
		}
		if (isAvailable(checkBox)) {
			checkBox.setBackground(color);
		}
	}
	//#end region

}
