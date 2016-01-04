package org.treez.core.atom.attribute;

import java.util.List;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.swt.CustomLabel;

/**
 * Allows the user to choose a line style
 */
public class EnumComboBox<T extends EnumValueProvider<?>>
		extends
			AbstractAttributeAtom<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(EnumComboBox.class);

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "My Line Style:")
	private String label;

	@IsParameter(defaultValue = "solid")
	private String defaultValue;

	@IsParameter(defaultValue = "")
	private String tooltip;

	/**
	 * The combo box
	 */
	private Combo comboBox;

	/**
	 * The image label
	 */
	private Label imageLabel;

	/**
	 * The enum that provides the available values
	 */
	private final T enumInstance;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public EnumComboBox(T enumInstance, String name) {
		super(name);
		label = name;
		this.enumInstance = enumInstance;
		setDefaultValue(enumInstance);
	}

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public EnumComboBox(T enumInstance, String name, String label) {
		super(name);
		this.label = label;
		this.enumInstance = enumInstance;
		setDefaultValue(enumInstance);
	}

	/**
	 * Copy constructor
	 *
	 * @param lineStyleToCopy
	 */
	private EnumComboBox(EnumComboBox<T> lineStyleToCopy) {
		super(lineStyleToCopy);
		enumInstance = lineStyleToCopy.enumInstance;
		label = lineStyleToCopy.label;
		defaultValue = lineStyleToCopy.defaultValue;
		tooltip = lineStyleToCopy.tooltip;
		comboBox = lineStyleToCopy.comboBox;
		imageLabel = lineStyleToCopy.imageLabel;
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public EnumComboBox<T> copy() {
		return new EnumComboBox<T>(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("combobox.png");
	}

	/**
	 * Creates the composite on a given parent
	 *
	 * @param parent
	 */
	@Override
	public AbstractAttributeAtom<String> createAttributeAtomControl(
			Composite parent, Refreshable treeViewerRefreshable) {

		//initialize value at the first call
		if (!isInitialized()) {
			set(defaultValue);
		}

		//toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		Composite container = createContainer(parent, toolkit);

		//label
		String currentLabel = getLabel();
		CustomLabel labelComposite = new CustomLabel(toolkit, container,
				currentLabel);
		final int preferredLabelWidth = 85;
		labelComposite.setPrefferedWidth(preferredLabelWidth);

		//separator
		toolkit.createLabel(container, "  ");

		//combo box
		comboBox = new Combo(container, SWT.DEFAULT);
		comboBox.setEnabled(isEnabled());

		//set available values
		List<String> values = enumInstance.getValues();
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
				String currentValue = enumInstance.getValues().get(index);
				set(currentValue);

				//trigger modification listeners
				triggerModificationListeners();
			}
		});

		return this;

	}

	@SuppressWarnings("checkstyle:magicnumber")
	private static Composite createContainer(Composite parent,
			FormToolkit toolkit) {
		//create grid data to use all horizontal space
		GridData fillHorizontal = new GridData();
		fillHorizontal.grabExcessHorizontalSpace = true;
		fillHorizontal.horizontalAlignment = GridData.FILL;

		//create container control for labels and line style
		Composite container = toolkit.createComposite(parent);
		GridLayout gridLayout = new GridLayout(5, false);
		gridLayout.horizontalSpacing = 0;
		container.setLayout(gridLayout);
		container.setLayoutData(fillHorizontal);
		return container;
	}

	@Override
	public void refreshAttributeAtomControl() {
		if (isAvailable(comboBox)) {
			List<String> values = enumInstance.getValues();
			String vale = get();
			int index = values.indexOf(vale);
			if (comboBox.getSelectionIndex() != index) {
				comboBox.select(index);
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
		addModifyListener(key,
				(event) -> consumer.accept(event.data.toString()));
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
		boolean isAllowedValue = enumInstance.getValues()
				.contains(defaultValue);
		if (isAllowedValue) {
			this.defaultValue = defaultValue;
		} else {
			throw new IllegalArgumentException(
					"The specified value '" + defaultValue + "' is not known.");
		}

	}

	/**
	 * Sets the default line style
	 *
	 * @param enumValue
	 */
	public void setDefaultValue(T enumValue) {
		setDefaultValue(enumValue.toString());
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
	 * Get value as string
	 *
	 * @return the value
	 */
	@Override
	public String get() {
		return super.get();
	}

	/**
	 * @return
	 */
	public T getEnumValue() {
		String value = get();
		@SuppressWarnings("unchecked")
		T enumValue = (T) enumInstance.fromString(value);
		return enumValue;
	}

	/**
	 * Set value
	 *
	 * @param value
	 *            the filePath to set
	 */
	@Override
	public void set(String value) {
		super.set(value);
	}

	//#end region

}
