package org.treez.core.atom.attribute.comboBox.font;

import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.AbstractStringAttributeAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.swt.CustomLabel;
import org.treez.core.utils.Utils;

/**
 * Allows the user to choose a font
 */
public class Font extends AbstractStringAttributeAtom<Font> {

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "My Font:")
	private String label;

	@IsParameter(defaultValue = "Arial")
	private String defaultValue;

	@IsParameter(defaultValue = "")
	private String tooltip;

	private Composite contentContainer;

	private CustomLabel labelComposite;

	private Combo fontCombo = null;

	/**
	 * Available font names
	 */
	private List<String> fonts = null;

	//#end region

	//#region CONSTRUCTORS

	public Font(String name) {
		super(name);
		label = Utils.firstToUpperCase(name);
	}

	public Font(String name, String defaultFont) {
		this(name);
		boolean isFont = getFonts().contains(defaultFont);
		if (isFont) {
			set(defaultFont);
		} else {
			throw new IllegalArgumentException("The specified font '" + defaultFont + "' is not known.");
		}
	}

	/**
	 * Copy constructor
	 */
	private Font(Font fontToCopy) {
		super(fontToCopy);
		label = fontToCopy.label;
		defaultValue = fontToCopy.defaultValue;
		tooltip = fontToCopy.tooltip;
	}

	//#end region

	//#region METHODS

	@Override
	public Font getThis() {
		return this;
	}

	@Override
	public Font copy() {
		return new Font(this);
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("font.png");
	}

	@Override
	public AbstractStringAttributeAtom<Font> createAttributeAtomControl(
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

		//create container layout
		boolean useExtraComboBoxLine = label.length() > CHARACTER_LENGTH_LIMIT;
		int marginWidth = 0;
		if (useExtraComboBoxLine) {
			createLayoutForIndividualLines(contentContainer, marginWidth);
		} else {
			createLayoutForSingleLine(contentContainer, marginWidth);
		}

		//create label
		createLabel(toolkit);

		//combo box
		fontCombo = new Combo(contentContainer, SWT.NONE);
		fontCombo.setEnabled(isEnabled());

		//set available fronts
		final List<String> currentFonts = getFonts();
		for (String fontString : currentFonts) {
			fontCombo.add(fontString);
		}

		//initialize selected item
		String value = get();
		int index = currentFonts.indexOf(value);
		fontCombo.select(index);

		//action listener for combo box
		fontCombo.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = fontCombo.getSelectionIndex();
				String currentFont = currentFonts.get(index);
				set(currentFont);

				//trigger modification listeners
				triggerListeners();
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
		if (isAvailable(fontCombo)) {
			String font = get();
			int index = fonts.indexOf(font);
			if (fontCombo.getSelectionIndex() != index) {
				fontCombo.select(index);
			}
		}
	}

	@Override
	public Font setBackgroundColor(org.eclipse.swt.graphics.Color backgroundColor) {
		throw new IllegalStateException("Not yet implemented");

	}

	//#end region

	//#region ACCESSORS

	/**
	 * Returns the available fonts
	 *
	 * @return
	 */
	public List<String> getFonts() {

		if (fonts == null) {
			GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
			String[] fontNames = graphicsEnvironment.getAvailableFontFamilyNames();
			fonts = Arrays.asList(fontNames);
		}
		return fonts;
	}

	public String getLabel() {
		return label;
	}

	public Font setLabel(String label) {
		this.label = label;
		return getThis();
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	public Font setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return getThis();
	}

	public String getTooltip() {
		return tooltip;
	}

	public Font setTooltip(String tooltip) {
		this.tooltip = tooltip;
		return getThis();
	}

	@Override
	public String get() {
		return super.get();
	}

	@Override
	public void set(String font) {
		super.set(font);
	}

	//#end region

}
