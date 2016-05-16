package org.treez.core.atom.attribute;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.swt.CustomLabel;

public class Label extends AbstractAttributeAtom<String> {

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "Label")
	private String label;

	private Composite contentContainer;

	private CustomLabel labelComposite;

	//#end region

	//#region CONSTRUCTORS

	public Label(String name) {
		super(name);
		label = name;
	}

	/**
	 * Copy constructor
	 */
	protected Label(Label atomToCopy) {
		super(atomToCopy);
		label = atomToCopy.label;
	}

	//#end region

	//#region METHODS

	@Override
	public Label copy() {
		return new Label(this);
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("label.png");
	}

	@Override
	public AbstractAttributeAtom<String> createAttributeAtomControl(
			Composite parent,
			FocusChangingRefreshable treeViewerRefreshable) {

		//initialize value at the first call
		if (!isInitialized()) {
			set(label);
		}

		//create toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		//create content composite for label and check box
		contentContainer = toolkit.createComposite(parent);

		createLayoutForSingleLine(contentContainer, 0);

		//create label
		labelComposite = new CustomLabel(toolkit, contentContainer, label);

		//set the enabled states of the controls
		setEnabled(isEnabled());

		return this;
	}

	@Override
	public void refreshAttributeAtomControl() {
		if (isAvailable(labelComposite)) {
			String value = get();
			labelComposite.setText(value);
		}
	}

	/*
	@Override
	public void addModificationConsumer(String key, Consumer<String> consumer) {
		throw new IllegalStateException(
				"Labels to not support modification listeners");
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

	/**
	 * Returns the object that represents the property value. Might be overridden by implementing classes.
	 *
	 * @return
	 */
	@Override
	public String get() {
		if (isInitialized()) {
			return attributeValue;
		} else {
			return getLabel();
		}

	}

	@Override
	public String getDefaultValue() {
		return get();
	}

	public void setDefaultValue(String defaultValue) {
		set(defaultValue);
	}

	@Override
	public void setBackgroundColor(Color color) {
		if (isAvailable(contentContainer)) {
			contentContainer.setBackground(color);
		}
		if (isAvailable(labelComposite)) {
			labelComposite.setBackground(color);
		}
	}

	//#end region

}
