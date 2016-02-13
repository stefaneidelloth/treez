package org.treez.core.atom.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.swt.CustomLabel;

/**
 * An item example
 */
public abstract class AbstractComboBox extends AbstractAttributeAtom<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(AbstractComboBox.class);

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "My ComboBox:")
	protected String label;

	@IsParameter(defaultValue = "item1")
	protected String defaultValue;

	@IsParameter(defaultValue = "item1,item2")
	protected String items;

	@IsParameter(defaultValue = "")
	protected String tooltip;

	/**
	 * Container for label and combo box
	 */
	private Composite contentContainer;

	/**
	 * The label
	 */
	private CustomLabel labelComposite;

	/**
	 * The combo box
	 */
	private Combo combo;

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
	public AbstractComboBox(String name) {
		super(name);
		label = name;
	}

	/**
	 * Copy constructor
	 *
	 * @param comboBoxToCopy
	 */
	protected AbstractComboBox(AbstractComboBox comboBoxToCopy) {
		super(comboBoxToCopy);
		label = comboBoxToCopy.label;
		defaultValue = comboBoxToCopy.defaultValue;
		items = comboBoxToCopy.items;
		tooltip = comboBoxToCopy.tooltip;
	}

	//#end region

	//#region METHODS

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("ComboBox.png");
	}

	@Override
	@SuppressWarnings("checkstyle:magicnumber")
	public AbstractAttributeAtom<String> createAttributeAtomControl(
			Composite parent, Refreshable treeViewerRefreshable) {
		this.attributeAtomParent = parent;
		this.treeViewRefreshable = treeViewerRefreshable;

		//initialize value at the first call
		if (!isInitialized()) {
			if (defaultValue != null) {
				set(defaultValue);
			}
		}

		//toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		//create content composite for label and combo box
		contentContainer = toolkit.createComposite(parent);

		//check label length
		boolean useExtraComboBoxLine = label.length() > CHARACTER_LENGTH_LIMIT;

		//create container layout
		int marginWidth = 4;
		if (useExtraComboBoxLine) {
			createLayoutForIndividualLines(contentContainer, marginWidth);
		} else {
			createLayoutForSingleLine(contentContainer, marginWidth);
		}

		//create label
		createLabel(toolkit);

		//create combo box
		createComboBox();

		return this;

	}

	private void createLabel(FormToolkit toolkit) {
		labelComposite = new CustomLabel(toolkit, contentContainer, label);
		final int prefferedLabelWidth = 80;
		labelComposite.setPrefferedWidth(prefferedLabelWidth);
	}

	private void createComboBox() {

		GridData comboFillHorizontal = new GridData();
		comboFillHorizontal.grabExcessHorizontalSpace = true;
		comboFillHorizontal.horizontalAlignment = GridData.FILL;

		combo = new Combo(contentContainer, SWT.READ_ONLY);
		combo.setEnabled(isEnabled());
		combo.setLayoutData(comboFillHorizontal);

		List<String> availableItems = getItemList();
		combo.setItems(
				availableItems.toArray(new String[availableItems.size()]));
		String value = get();
		if (value != null) {
			combo.setText(value);
		}

		//action listener
		combo.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void widgetSelected(SelectionEvent e) {
				//update value
				String currentValue = combo.getItem(combo.getSelectionIndex());
				set(currentValue);

				//updated enabled states if ComboBoxEnableTarget children exist
				updateTargetsEnabledStates(currentValue);

				//trigger modification listeners
				triggerModificationListeners();
			}

		});
	}

	/**
	 * Updates the enabled/disabled state of other components, dependent on the
	 * current value
	 *
	 * @param currentValue
	 */
	@SuppressWarnings("checkstyle:linelength")
	private void updateTargetsEnabledStates(String currentValue) {
		List<TreeNodeAdaption> enableNodes = createTreeNodeAdaption()
				.getChildren();
		for (TreeNodeAdaption enableNode : enableNodes) {
			org.treez.core.atom.attribute.ComboBoxEnableTarget comboBoxEnableTarget = (org.treez.core.atom.attribute.ComboBoxEnableTarget) enableNode
					.getAdaptable();

			List<String> enableValues = comboBoxEnableTarget.getItems();
			String targetPath = comboBoxEnableTarget.getTargetPath();
			AttributeRoot root = (AttributeRoot) getRoot();
			AbstractAttributeAtom<?> target = (AbstractAttributeAtom<?>) root
					.getChild(targetPath);
			boolean enableTarget = enableValues.contains(currentValue);
			if (enableTarget) {
				target.setEnabled(true);
			} else {
				target.setEnabled(false);
			}
		}
	}

	@Override
	public void refreshAttributeAtomControl() {
		if (isAvailable(combo)) {

			List<String> availableItems = getItemList();
			combo.setItems(
					availableItems.toArray(new String[availableItems.size()]));

			String value = get();
			if (!combo.getText().equals(value)) {
				combo.setText(value);
			}
		}
	}

	@Override
	public void setBackgroundColor(org.eclipse.swt.graphics.Color color) {

		if (isAvailable(contentContainer)) {
			contentContainer.setBackground(color);
		}

		if (isAvailable(labelComposite)) {
			labelComposite.setBackground(color);
		}

	}

	@Override
	public void addModificationConsumer(String key, Consumer<String> consumer) {
		addModifyListener(key,
				(event) -> consumer.accept(event.data.toString()));
	}

	//#end region

	//#region ACCESSORS

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * This method is outsourced to be able to add custom validation for
	 * implementing classes. The String value might represent for example an
	 * Enumeration that is only known by an anonymous implementation of this
	 * abstract class.
	 */
	@Override
	public abstract void set(String value);

	/**
	 * Set the value of the super attribute atom
	 *
	 * @param value
	 */
	protected void setValue(String value) {
		super.set(value);
	}

	/**
	 * Sets the value with an enumeration
	 *
	 * @param enumValue
	 */
	public void setValue(Enum<?> enumValue) {
		String valueString = enumValue.name();
		set(valueString);
	}

	/**
	 * @return the defaultPath
	 */
	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue
	 */
	public void setDefaultValue(String defaultValue) {
		boolean valueAllowed = items.contains(defaultValue);
		if (valueAllowed) {
			this.defaultValue = defaultValue;
		} else {
			String message = "The defaultValue '" + defaultValue
					+ "' is not allowed since it is not contained in the items "
					+ items;
			throw new IllegalArgumentException(message);
		}

	}

	/**
	 * Sets the default value with an enum
	 *
	 * @param defaultEnumValue
	 */
	public void setDefaultValue(Enum<?> defaultEnumValue) {
		String defaultValueString = defaultEnumValue.name();
		setDefaultValue(defaultValueString);
	}

	/**
	 * Returns the value of the combo box as enumeration with the given type
	 *
	 * @param <T>
	 * @param enumClass
	 * @return
	 */
	public <T extends Enum<T>> T getValueAsEnum(Class<T> enumClass) {
		Objects.requireNonNull(enumClass,
				"The given enum class must not be null.");
		String enumString = get();
		T enumValue;
		try {
			enumValue = Enum.valueOf(enumClass, enumString);
			return enumValue;
		} catch (IllegalArgumentException | NullPointerException exception) {
			String message = "Could not convert the current value '" + get()
					+ "' " + "to an Enum of type '" + enumClass.getSimpleName()
					+ "'";
			throw new IllegalArgumentException(message, exception);
		}

	}

	/**
	 * Returns the available items as single, comma separated string
	 *
	 * @return the items
	 */
	public String getItems() {
		return items;
	}

	/**
	 * @param items
	 *            the items to set
	 */
	public void setItems(String items) {
		this.items = items;
	}

	/**
	 * Returns the available items as list
	 *
	 * @return
	 */
	public List<String> getItemList() {
		List<String> itemList = new ArrayList<>();
		if (items == null) {
			return itemList;
		}
		String[] availableItems = items.split(",");
		for (String itemString : availableItems) {
			String item = itemString.trim();
			itemList.add(item);
		}
		return itemList;
	}

	/**
	 * Sets the available items with an enum
	 *
	 * @param enumValue
	 */
	public void setItems(Enum<?> enumValue) {
		Object[] enumValues = enumValue.getDeclaringClass().getEnumConstants();
		List<String> enumNameList = new ArrayList<>();
		for (Object enumObject : enumValues) {
			String currentEnumValue = ((Enum<?>) enumObject).name();
			enumNameList.add(currentEnumValue);
		}
		String itemsString = String.join(",", enumNameList);
		setItems(itemsString);
	}

	/**
	 * @return the tool tip
	 */
	public String getTooltip() {
		return tooltip;
	}

	/**
	 * @param tooltip
	 *            the tool tip to set
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	//#end region

}
