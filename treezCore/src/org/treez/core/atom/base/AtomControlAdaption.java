package org.treez.core.atom.base;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.atom.base.annotation.IsParameters;

/**
 * Control adaption for an adaptable that only has "primitive parameters". The
 * primitive parameters are defined using the @IsParameter annotation.
 */
public class AtomControlAdaption extends AbstractControlAdaption {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(AtomControlAdaption.class);

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param parent
	 * @param adaptable
	 */
	public AtomControlAdaption(Composite parent, final Adaptable adaptable) {
		super(parent, adaptable);
		setLayout();
		createControlsForAnnotatedAttributes(adaptable);
	}

	//#end region

	//#region METHODS

	/**
	 * Set the layout of the parent composite
	 *
	 * @param parent
	 */
	private void setLayout() {
		this.setLayout(new GridLayout(2, false));
	}

	/**
	 * Creates controls for all attributes that are annotated with the
	 * IsParameter annotation.
	 *
	 * @param adaptable
	 */
	private void createControlsForAnnotatedAttributes(
			final Adaptable adaptable) {

		//get all attributes from super super class, super class and class
		List<Field> allAttributes = getAllAttributes(adaptable);

		for (final Field attribute : allAttributes) {
			//sysLog.debug("Creating control for attribute: " +
			//attribute.getName());
			attribute.setAccessible(true);
			boolean isAnnotated = attribute
					.isAnnotationPresent(IsParameter.class);
			if (isAnnotated) {
				//sysLog.debug("The attribute " + attribute.getName() + " is
				//annotated.");
				createAttributeControl(attribute);
			}
		}
	}

	/**
	 * Get all attributes from super super class, super class and class
	 *
	 * @param adaptable
	 * @return
	 */
	private static List<Field> getAllAttributes(final Adaptable adaptable) {
		List<Field> allAttributes = new ArrayList<>();

		Class<?> adaptableClass = adaptable.getClass();
		Class<?> superClass = adaptableClass.getSuperclass();

		if (superClass != null) {

			Class<?> superSuperClass = superClass.getSuperclass();

			if (superSuperClass != null) {
				Field[] superSuperAttributes = superSuperClass
						.getDeclaredFields();
				for (Field attribute : superSuperAttributes) {
					allAttributes.add(attribute);
				}
			}

			Field[] superAttributes = superClass.getDeclaredFields();
			for (Field attribute : superAttributes) {
				allAttributes.add(attribute);
			}
		}

		Field[] attributes = adaptableClass.getDeclaredFields();
		for (Field attribute : attributes) {
			allAttributes.add(attribute);
		}
		return allAttributes;
	}

	/**
	 * Creates an attribute control
	 *
	 * @param attribute
	 */
	private void createAttributeControl(final Field attribute) {

		//get data for attribute control

		//get name
		String name = attribute.getName();

		String typeName = determineTypeName(attribute);

		//get the items for the combo box (will be null if the attribute will
		//not be represented by a combo box)
		String[] comboItems = IsParameters.getComboItems(attribute, adaptable);
		boolean comboItemsExist = comboItems != null && comboItems.length > 0;

		//get the default value as string
		String defaultValueString = IsParameters
				.getDefaultValueString(attribute);
				//sysLog.debug("default value: " + defaultValueString);

		//get the current value of the attribute as string
		//(if the attribute is an Enum or Boolean, the value will be converted
		//to a string)
		String valueString = IsParameters.getCurrentValueString(attribute,
				adaptable);
				//sysLog.debug("current value: " + valueString);

		//initialize the value with the default value if the current value is
		//null
		if (valueString == null) {
			valueString = defaultValueString;
		}

		//create control
		createControl(attribute, name, typeName, comboItems, comboItemsExist,
				valueString);

	}

	private static String determineTypeName(final Field attribute) {
		//get the type name of the attribute
		String typeName = attribute.getType().getSimpleName().toUpperCase();

		//check if the attribute is an enum
		Class<?> clazz = attribute.getType();
		boolean isEnum = clazz.isEnum();
		if (isEnum) {
			typeName = "ENUM";
		}
		return typeName;
	}

	private void createControl(final Field attribute, String name,
			String typeName, String[] comboItems, boolean comboItemsExist,
			String valueString) {
		switch (typeName) {
			case "BOOLEAN" :
				createCheckBox(name, valueString, attribute);
				break;
			case "ENUM" :
				createComboBox(name, valueString, comboItems, attribute);
				break;
			case "DOUBLE" :
			case "FLOAT" :
			case "INTEGER" :
			case "STRING" :
				if (comboItemsExist) {
					createComboBox(name, valueString, comboItems, attribute);
				} else {
					createTextField(name, valueString, attribute);
				}
				break;
			default :
				throw new IllegalStateException(
						"The type " + typeName + " is not known.");
		}
	}

	/**
	 * Creates a combo box control
	 *
	 * @param name
	 * @param value
	 * @param comboItems
	 * @param attribute
	 */
	private void createComboBox(String name, String value, String[] comboItems,
			final Field attribute) {
		Label label = new Label(this, SWT.NONE);
		label.setText(name.substring(0, 1).toUpperCase()
				+ name.substring(1, name.length()) + ":");

		Combo combo = new Combo(this, SWT.READ_ONLY);
		combo.setLayoutData(
				new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		combo.setItems(comboItems);
		combo.setText(value);

		SelectionListener comboListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				Combo combo = (Combo) event.widget;
				String valueString = combo.getText();

				//convert value string to right format for the attribute
				Object attributeParent = AtomControlAdaption.this
						.getAdaptable();
				IsParameters.setAttributeValue(attribute, attributeParent,
						valueString);
				//sysLog.debug("Set attribute '" + attribute.getName() + "' to
				//new value " + valueString);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				//not used here
			}
		};
		combo.addSelectionListener(comboListener);
	}

	/**
	 * Creates a check box control
	 *
	 * @param name
	 * @param value
	 * @param field
	 */
	private void createCheckBox(String name, String value, final Field field) {
		boolean state = true;
		if (value.equals("false")) {
			state = false;
		}

		Label label = new Label(this, SWT.NONE);
		label.setText(name.substring(0, 1).toUpperCase()
				+ name.substring(1, name.length()) + ":");

		Button checkBox = new Button(this, SWT.CHECK);
		checkBox.setSelection(state);

		SelectionListener checkBoxListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				Button checkBox = (Button) event.widget;
				boolean value = checkBox.getSelection();

				//sysLog.debug("new checkbox value: " + value);
				try {
					field.set(getAdaptable(), value);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					sysLog.error("Could not set value.");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//not used here
			}
		};
		checkBox.addSelectionListener(checkBoxListener);
	}

	/**
	 * Creates a text field control
	 *
	 * @param name
	 * @param value
	 * @param field
	 */
	private void createTextField(String name, String value, final Field field) {
		Label label = new Label(this, SWT.NONE);
		label.setText(name.substring(0, 1).toUpperCase()
				+ name.substring(1, name.length()) + ":");

		Text text = new Text(this, SWT.BORDER);
		text.setLayoutData(
				new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text.setText(value);

		Listener textListener = new Listener() {

			@Override
			public void handleEvent(Event event) {
				Text text = (Text) event.widget;
				String value = text.getText();
				try {
					field.set(getAdaptable(), value);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					sysLog.error("Could not set value.");
				}
			}
		};
		text.addListener(SWT.CHANGED, textListener);
	}

	//#end region

}
