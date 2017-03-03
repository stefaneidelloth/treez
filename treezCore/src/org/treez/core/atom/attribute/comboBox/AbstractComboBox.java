package org.treez.core.atom.attribute.comboBox;

import java.util.ArrayList;
import java.util.List;

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
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.attribute.base.AbstractStringAttributeAtom;
import org.treez.core.atom.attribute.base.parent.AbstractAttributeParentAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.swt.CustomLabel;
import org.treez.core.utils.Utils;

/**
 * An item example
 */
public abstract class AbstractComboBox<A extends AbstractComboBox<A>> extends AbstractStringAttributeAtom<A> {

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

	private CustomLabel labelComposite;

	private Combo comboBox;

	/**
	 * The parent composite for the attribute atom control can be stored here to be able to refresh it.
	 */
	protected Composite attributeAtomParent = null;

	//#end region

	//#region CONSTRUCTORS

	public AbstractComboBox(String name) {
		super(name);
		label = Utils.firstToUpperCase(name);
	}

	/**
	 * Copy constructor
	 */
	protected AbstractComboBox(AbstractComboBox<A> comboBoxToCopy) {
		super(comboBoxToCopy);
		label = comboBoxToCopy.label;
		defaultValue = comboBoxToCopy.defaultValue;
		items = comboBoxToCopy.items;
		tooltip = comboBoxToCopy.tooltip;
	}

	//#end region

	//#region METHODS

	@Override
	public Image provideImage() {
		return Activator.getImage("ComboBox.png");
	}

	@Override
	@SuppressWarnings("checkstyle:magicnumber")
	public AbstractAttributeAtom<A, String> createAttributeAtomControl(
			Composite parent,
			FocusChangingRefreshable treeViewerRefreshable) {
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
		contentContainer.setBackground(backgroundColor);

		//check label length
		boolean useExtraComboBoxLine = label.length() > CHARACTER_LENGTH_LIMIT;

		//create container layout

		if (useExtraComboBoxLine) {
			int marginWidth = 0;
			createLayoutForIndividualLines(contentContainer, marginWidth);
		} else {
			int marginWidth = 0;
			createLayoutForSingleLine(contentContainer, marginWidth);
		}

		//create label
		createLabel(toolkit);

		//create combo box
		createComboBox();

		if (!useExtraComboBoxLine) {
			//add a little spacer on the right
			//toolkit.createLabel(contentContainer, " ").setBackground(backgroundColor);
		}

		return this;

	}

	private void createLabel(FormToolkit toolkit) {
		labelComposite = new CustomLabel(toolkit, contentContainer, label);
		final int prefferedLabelWidth = 80;
		labelComposite.setPrefferedWidth(prefferedLabelWidth);
		labelComposite.setBackground(backgroundColor);
	}

	private void createComboBox() {

		GridData comboFillHorizontal = new GridData();
		comboFillHorizontal.grabExcessHorizontalSpace = true;
		//comboFillHorizontal.horizontalAlignment = GridData.FILL;

		comboBox = new Combo(contentContainer, SWT.READ_ONLY);
		comboBox.setEnabled(isEnabled());
		comboBox.setLayoutData(comboFillHorizontal);

		List<String> availableItems = getItemList();
		comboBox.setItems(availableItems.toArray(new String[availableItems.size()]));
		String value = get();
		if (value != null) {
			comboBox.setText(value);
		}

		//action listener
		comboBox.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void widgetSelected(SelectionEvent e) {
				//update value
				String currentValue = comboBox.getItem(comboBox.getSelectionIndex());
				set(currentValue);

				//updates enabled states if ComboBoxEnableTarget children exist
				updateTargetsEnabledStates(currentValue);

				//trigger modification listeners
				triggerListeners();
			}

		});

		//updates enabled states if ComboBoxEnableTarget children exist
		updateTargetsEnabledStates(get());
	}

	/**
	 * activeValues: for those values the target will be enabled; for all other values the target will be disabled
	 */
	public void createEnableTarget(String name, String activeValues, String targetPath) {
		ComboBoxEnableTarget enableDomainSection = new ComboBoxEnableTarget(name, activeValues, targetPath);
		addChild(enableDomainSection);
	}

	/**
	 * Updates the enabled/disabled state of other components, dependent on the current value
	 *
	 * @param currentValue
	 */
	@SuppressWarnings("checkstyle:linelength")
	private void updateTargetsEnabledStates(String currentValue) {
		List<TreeNodeAdaption> enableNodes = createTreeNodeAdaption().getChildren();
		for (TreeNodeAdaption enableNode : enableNodes) {
			org.treez.core.atom.attribute.comboBox.ComboBoxEnableTarget comboBoxEnableTarget = (org.treez.core.atom.attribute.comboBox.ComboBoxEnableTarget) enableNode
					.getAdaptable();

			List<String> enableValues = comboBoxEnableTarget.getItems();
			String targetPath = comboBoxEnableTarget.getTargetPath();
			AttributeRoot root = (AttributeRoot) getRoot();
			AbstractAttributeParentAtom<?> target = (AbstractAttributeParentAtom<?>) root.getChild(targetPath);
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
		if (isAvailable(comboBox)) {

			List<String> availableItems = getItemList();
			comboBox.setItems(availableItems.toArray(new String[availableItems.size()]));

			String value = get();
			if (!comboBox.getText().equals(value)) {
				comboBox.setText(value);
			}
		}
	}

	@Override
	public A setBackgroundColor(org.eclipse.swt.graphics.Color color) {
		this.backgroundColor = color;
		if (isAvailable(contentContainer)) {
			contentContainer.setBackground(color);
		}

		if (isAvailable(labelComposite)) {
			labelComposite.setBackground(color);
		}
		return getThis();
	}

	//#end region

	//#region ACCESSORS

	public String getLabel() {
		return label;
	}

	public A setLabel(String label) {
		this.label = label;
		return getThis();
	}

	/**
	 * This method is outsourced to be able to add custom validation for implementing classes.
	 */
	@Override
	public abstract void set(String value);

	/**
	 * Set the value of the super attribute atom
	 *
	 * @param value
	 */
	public void setValue(String value) {
		super.set(value);
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	public A setDefaultValue(String defaultValue) {
		boolean valueAllowed = items.contains(defaultValue);
		if (valueAllowed) {
			this.defaultValue = defaultValue;
		} else {
			String message = "The defaultValue '" + defaultValue
					+ "' is not allowed since it is not contained in the items " + items;
			throw new IllegalArgumentException(message);
		}
		return getThis();

	}

	/**
	 * Returns the available items as single, comma separated string
	 *
	 * @return the items
	 */
	public String getItems() {
		return items;
	}

	public A setItems(String items) {
		this.items = items;
		return getThis();
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

	public String getTooltip() {
		return tooltip;
	}

	public A setTooltip(String tooltip) {
		this.tooltip = tooltip;
		return getThis();
	}

	@SuppressWarnings("unchecked")
	@Override
	public A setEnabled(boolean state) {
		super.setEnabled(state);
		if (isAvailable(comboBox)) {
			comboBox.setEnabled(state);
		}
		return (A) this;

	}

	//#end region

}
