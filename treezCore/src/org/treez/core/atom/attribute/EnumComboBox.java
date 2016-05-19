package org.treez.core.atom.attribute;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.AbstractStringAttributeAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.swt.CustomLabel;
import org.treez.core.utils.Utils;

public class EnumComboBox<T extends EnumValueProvider<?>> extends AbstractStringAttributeAtom {

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "My Enum value:")
	private String label;

	@IsParameter(defaultValue = "error")
	private String defaultValue;

	@IsParameter(defaultValue = "")
	private String tooltip;

	private Composite contentContainer;

	private CustomLabel labelComposite;

	private Combo comboBox;

	private Label imageLabel;

	/**
	 * The enum that provides the available values
	 */
	private final T enumValueProviderInstance;

	//#end region

	//#region CONSTRUCTORS

	public EnumComboBox(T enumValueProvider, String name) {
		super(name);
		label = Utils.firstToUpperCase(name);
		this.enumValueProviderInstance = enumValueProvider;
		setDefaultValue(enumValueProvider);
	}

	public EnumComboBox(T enumValueProvider, String name, String label) {
		super(name);
		this.label = label;
		this.enumValueProviderInstance = enumValueProvider;
		setDefaultValue(enumValueProvider);
	}

	/**
	 * Copy constructor
	 */
	private EnumComboBox(EnumComboBox<T> lineStyleToCopy) {
		super(lineStyleToCopy);
		enumValueProviderInstance = lineStyleToCopy.enumValueProviderInstance;
		label = lineStyleToCopy.label;
		defaultValue = lineStyleToCopy.defaultValue;
		tooltip = lineStyleToCopy.tooltip;
		comboBox = lineStyleToCopy.comboBox;
		imageLabel = lineStyleToCopy.imageLabel;
	}

	//#end region

	//#region METHODS

	@Override
	public EnumComboBox<T> copy() {
		return new EnumComboBox<T>(this);
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("combobox.png");
	}

	@Override
	public AbstractStringAttributeAtom createAttributeAtomControl(
			Composite parent,
			FocusChangingRefreshable treeViewerRefreshable) {

		//initialize value at the first call
		if (!isInitialized()) {
			set(defaultValue);
		}

		//toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		//create content composite for label and check box
		contentContainer = toolkit.createComposite(parent);

		//check label length
		boolean useExtraComboBoxLine = label.length() > CHARACTER_LENGTH_LIMIT;

		//create container layout
		int marginWidth = 0;
		if (useExtraComboBoxLine) {
			createLayoutForIndividualLines(contentContainer, marginWidth);
		} else {
			createLayoutForSingleLine(contentContainer, marginWidth);
		}

		//create label
		createLabel(toolkit);

		//combo box
		comboBox = new Combo(contentContainer, SWT.DEFAULT);
		comboBox.setEnabled(isEnabled());

		//set available values
		List<String> values = enumValueProviderInstance.getValues();
		for (String value : values) {
			comboBox.add(value);
		}

		//initialize selected item
		refreshAttributeAtomControl();

		//action listener for combo box
		comboBox.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = comboBox.getSelectionIndex();
				String currentValue = enumValueProviderInstance.getValues().get(index);
				set(currentValue);
			}
		});

		return this;

	}

	private void createLabel(FormToolkit toolkit) {
		labelComposite = new CustomLabel(toolkit, contentContainer, label);
		final int prefferedLabelWidth = 80;
		labelComposite.setPrefferedWidth(prefferedLabelWidth);
	}

	@Override
	public void refreshAttributeAtomControl() {
		if (isAvailable(comboBox)) {
			List<String> values = enumValueProviderInstance.getValues();
			String vale = get();
			int index = values.indexOf(vale);
			if (comboBox.getSelectionIndex() != index) {
				comboBox.select(index);
			}
		}
	}

	@Override
	public void setBackgroundColor(org.eclipse.swt.graphics.Color backgroundColor) {
		throw new IllegalStateException("Not yet implemented");
	}

	/*
	@Override
	public void addModificationConsumer(String key, Consumer<String> consumer) {
		addModifyListener(key, (event) -> {
			if (event.data == null) {
				consumer.accept(null);
			} else {
				String data = event.data.toString();
				consumer.accept(data);
			}

		});
	}
	*/

	//#end region

	//#region ACCESSORS

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		boolean isAllowedValue = enumValueProviderInstance.getValues().contains(defaultValue);
		if (isAllowedValue) {
			this.defaultValue = defaultValue;
		} else {
			throw new IllegalArgumentException("The specified value '" + defaultValue + "' is not known.");
		}

	}

	public void setDefaultValue(T enumValue) {
		setDefaultValue(enumValue.toString());
	}

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	@Override
	public String get() {
		return super.get();
	}

	public T getValueAsEnum() {
		String value = get();
		@SuppressWarnings("unchecked")
		T enumValue = (T) enumValueProviderInstance.fromString(value);
		return enumValue;
	}

	@Override
	public void set(String value) {
		super.set(value);
	}

	//#end region

}
