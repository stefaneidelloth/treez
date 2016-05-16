package org.treez.core.atom.attribute;

import java.awt.Color;
import java.util.List;
import java.util.Objects;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.color.ColorValue;
import org.treez.core.swt.CustomLabel;
import org.treez.core.utils.Utils;

/**
 * Allows the user to choose a value
 */
public class ColorChooser extends AbstractAttributeAtom<String> {

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "My Color:")
	private String label;

	@IsParameter(defaultValue = "#ffffff")
	private String defaultValue;

	@IsParameter(defaultValue = "")
	private String tooltip;

	private ImageCombo colorCombo = null;

	/**
	 * Predefined color names
	 */
	private final List<String> colors = ColorValue.getAllStringValues();

	/**
	 * Predefined color values
	 */
	private final List<String> colorsHex = ColorValue.getAllHexCodes();

	//#end region

	//#region CONSTRUCTORS

	public ColorChooser(String name) {
		super(name);
		label = Utils.firstToUpperCase(name);
	}

	public ColorChooser(String name, String defaultColor) {
		this(name);
		setDefaultValue(defaultColor);
	}

	/**
	 * Copy constructor
	 */
	private ColorChooser(ColorChooser colorChooserToCopy) {
		super(colorChooserToCopy);
		label = colorChooserToCopy.label;
		defaultValue = colorChooserToCopy.defaultValue;
		tooltip = colorChooserToCopy.tooltip;
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public ColorChooser copy() {
		return new ColorChooser(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("ColorChooser.png");
	}

	@Override
	public AbstractAttributeAtom<String> createAttributeAtomControl(
			Composite parent,
			FocusChangingRefreshable treeViewerRefreshable) {

		//initialize value at the first call
		if (!isInitialized()) {
			set(defaultValue);
		}

		//toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		//create grid data to use all horizontal space
		GridData fillHorizontal = new GridData();
		fillHorizontal.grabExcessHorizontalSpace = true;
		fillHorizontal.horizontalAlignment = GridData.FILL;

		//create container control for label and text field
		Composite container = createContainer(parent, toolkit, fillHorizontal);

		//label
		createLabel(toolkit, container);

		//button value
		//chooser-----------------------------------------------------
		final ColorSelector colorSelector = new ColorSelector(container);
		Button colorButton = colorSelector.getButton();
		RGB rgb = getColorRgb();
		colorSelector.setColorValue(rgb);

		//separator
		toolkit.createLabel(container, "  ");

		//combo box value
		//chooser-------------------------------------------------
		colorCombo = new ImageCombo(container, SWT.DEFAULT);
		colorCombo.setEnabled(isEnabled());
		colorCombo.setEditable(false);

		//set predefined colors
		final List<String> currentColors = getColors();

		final List<String> currentColorsHex = getColorsHex();

		for (String colorString : currentColors) {
			colorCombo.add(colorString, Activator.getImage(colorString + ".png"));
		}
		colorCombo.add("#custom#", null);

		//initialize selected item
		initializeSelectedItem(currentColors, currentColorsHex);

		//action
		//listeners----------------------------------------------------------
		//action listener for combo box
		SelectionAdapter colorComboListener = createColorComboSelectionListener(colorSelector, currentColors,
				currentColorsHex);
		colorCombo.addSelectionListener(colorComboListener);

		//action listener for value button
		SelectionAdapter colorButtonListener = createColorButtonSelectionListener(colorSelector);
		colorButton.addSelectionListener(colorButtonListener);

		return this;
	}

	private void createLabel(FormToolkit toolkit, Composite container) {
		String currentLabel = getLabel();
		CustomLabel labelComposite = new CustomLabel(toolkit, container, currentLabel);
		final int prefferedLabelWidth = 85;
		labelComposite.setPrefferedWidth(prefferedLabelWidth);
	}

	@SuppressWarnings("checkstyle:magicnumber")
	private static Composite createContainer(Composite parent, FormToolkit toolkit, GridData fillHorizontal) {
		Composite container = toolkit.createComposite(parent);
		GridLayout gridLayout = new GridLayout(5, false);
		gridLayout.horizontalSpacing = 0;
		container.setLayout(gridLayout);
		container.setLayoutData(fillHorizontal);
		return container;
	}

	private void initializeSelectedItem(final List<String> currentColors, final List<String> currentColorsHex) {
		String colorHex = get();
		boolean colorExists = currentColorsHex.contains(colorHex);
		if (colorExists) {
			//select value from existing colors in combo box
			int index = currentColorsHex.indexOf(colorHex);
			colorCombo.select(index);
		} else {
			//select #selector#
			int index = currentColors.size();
			colorCombo.select(index);
		}
	}

	/**
	 * Creates the listener for the color combo box
	 *
	 * @param colorSelector
	 * @param currentColors
	 * @param currentColorsHex
	 * @return
	 */
	private SelectionAdapter createColorComboSelectionListener(
			final ColorSelector colorSelector,
			final List<String> currentColors,
			final List<String> currentColorsHex) {
		return new SelectionAdapter() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = colorCombo.getSelectionIndex();
				if (index < currentColors.size()) {
					//apply value from combo box and update value button
					String colorHex = currentColorsHex.get(index);
					set(colorHex);
					RGB rgb = getColorRgb();
					colorSelector.setColorValue(rgb);
				} else {
					//apply value from button
					RGB color = colorSelector.getColorValue();
					setColorRGB(color);
				}

				//trigger modification listeners
				triggerModificationListeners();
			}
		};
	}

	/**
	 * Creates the listener for the color button
	 *
	 * @param colorSelector
	 * @return
	 */
	private SelectionAdapter createColorButtonSelectionListener(final ColorSelector colorSelector) {
		return new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				//apply value
				RGB color = colorSelector.getColorValue();
				setColorRGB(color);

				refreshAttributeAtomControl();
			}
		};
	}

	@Override
	public void refreshAttributeAtomControl() {
		//update combo box
		if (isAvailable(colorCombo)) {
			String currentColorHex = get();
			boolean colorExists = colorsHex.contains(currentColorHex);
			if (colorExists) {
				//select value from existing colors in combo box
				int index = colorsHex.indexOf(currentColorHex);
				if (colorCombo.getSelectionIndex() != index) {
					colorCombo.select(index);
				}
			} else {
				//select #selector#
				int index = colors.size();
				if (colorCombo.getSelectionIndex() != index) {
					colorCombo.select(index);
				}
			}
		}
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

	/**
	 * @param defaultColor
	 *            :hex color string or color name
	 */
	public void setDefaultValue(String defaultColor) {

		boolean isHexColor = defaultColor.substring(0, 1).equals("#");
		if (isHexColor) {
			attributeValue = defaultColor;
		} else {
			if (colors.contains(defaultColor)) {
				int index = colors.indexOf(defaultColor);
				attributeValue = colorsHex.get(index);
			} else {
				throw new IllegalArgumentException("The specified value '" + defaultColor + "' is not know.");
			}
		}

		this.defaultValue = attributeValue;
	}

	public void setDefaultValue(ColorValue colorValue) {
		setDefaultValue(colorValue.getHexCode());
	}

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public String getColorString() {
		return attributeValue;
	}

	public Color getColor() {
		Objects.requireNonNull(attributeValue, "The value has not been set.");
		String colorString = get();
		try {
			Color color = Color.decode(colorString);
			return color;
		} catch (NumberFormatException exception) {
			throw new IllegalStateException("Could not decode color value '" + colorString + "'");
		}
	}

	public RGB getColorRgb() {
		Color colorObj = getColor();
		int red = colorObj.getRed();
		int green = colorObj.getGreen();
		int blue = colorObj.getBlue();
		return new RGB(red, green, blue);
	}

	public void setColorRGB(RGB rgb) {
		String valueString = String.format("#%02x%02x%02x", rgb.red, rgb.green, rgb.blue);
		super.set(valueString);
	}

	/**
	 * Returns the color as hex-string
	 */
	@Override
	public String get() {
		return super.get();
	}

	@Override
	public void set(String value) {
		boolean isHexColor = value.substring(0, 1).equals("#");
		if (isHexColor) {
			super.set(value);
		} else {
			boolean isTextColor = colors.contains(value);
			if (isTextColor) {
				String hexColor = ColorValue.getHexCode(value);
				super.set(hexColor);
			} else {
				throw new IllegalArgumentException("The string '" + value + "' could not be interpreted as color.");
			}
		}
	}

	/**
	 * Get predefined color names
	 *
	 * @return the colors
	 */
	public List<String> getColors() {
		return colors;
	}

	/**
	 * Get predefined color hex strings
	 *
	 * @return the colorsHex
	 */
	public List<String> getColorsHex() {
		return colorsHex;
	}

	@Override
	public void setBackgroundColor(org.eclipse.swt.graphics.Color backgroundColor) {
		throw new IllegalStateException("Not yet implemented");

	}

	//#end region

}
