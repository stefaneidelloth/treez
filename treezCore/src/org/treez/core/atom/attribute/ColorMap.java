package org.treez.core.atom.attribute;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.color.ColorMapValue;
import org.treez.core.swt.CustomLabel;

/**
 * Allows the user to choose a color map
 */
public class ColorMap extends AbstractAttributeAtom<String> {

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "My Color Map:")
	private String label;

	@IsParameter(defaultValue = "complement")
	private String defaultValue;

	@IsParameter(defaultValue = "")
	private String tooltip;

	private Label imageLabel = null;

	private ImageCombo styleCombo = null;

	/**
	 * Prefix for the image file names
	 */
	private final String imagePrefix = "color_map_";

	/**
	 * Predefined colors
	 */
	private final List<String> colorMaps = ColorMapValue.getAllStringValues();

	//#end region

	//#region CONSTRUCTORS

	public ColorMap(String name) {
		super(name);
		label = name;
	}

	public ColorMap(String name, String defaultStyle) {
		super(name);
		label = name;

		boolean isColorMap = colorMaps.contains(defaultStyle);
		if (isColorMap) {
			attributeValue = defaultStyle;
		} else {
			throw new IllegalArgumentException("The specified color map '"
					+ defaultStyle + "' is not known.");
		}
	}

	/**
	 * Copy constructor
	 */
	private ColorMap(ColorMap colorMapToCopy) {
		super(colorMapToCopy);
		label = colorMapToCopy.label;
		defaultValue = colorMapToCopy.defaultValue;
		tooltip = colorMapToCopy.tooltip;
	}

	//#end region

	//#region METHODS

	@Override
	public ColorMap copy() {
		return new ColorMap(this);
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("color_map_grey.png");
	}

	/**
	 * Creates the composite on a given parent
	 */
	@Override
	public AbstractAttributeAtom<String> createAttributeAtomControl(
			Composite parent, FocusChangingRefreshable treeViewerRefreshable) {

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

		//create container control for labels and line style
		Composite container = createContainer(parent, toolkit, fillHorizontal);

		//label
		String currentLabel = getLabel();
		CustomLabel labelComposite = new CustomLabel(toolkit, container,
				currentLabel);
		final int prefferedLabelWidth = 80;
		labelComposite.setPrefferedWidth(prefferedLabelWidth);

		//image label
		imageLabel = toolkit.createLabel(container, "");

		//combo box
		styleCombo = new ImageCombo(container, SWT.DEFAULT);
		styleCombo.setEnabled(isEnabled());
		styleCombo.setEditable(false);

		//set predefined color map
		final List<String> currentColorMaps = getColorMaps();
		for (String styleString : currentColorMaps) {
			styleCombo.add(styleString,
					Activator.getImage(imagePrefix + styleString + ".png"));
		}

		//initialize selected item
		refreshAttributeAtomControl();

		//action listener for combo box
		styleCombo.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = styleCombo.getSelectionIndex();
				String currentStyle = currentColorMaps.get(index);
				set(currentStyle);

				//trigger modification listeners
				triggerModificationListeners();
			}
		});

		return this;

	}

	@SuppressWarnings("checkstyle:magicnumber")
	private static Composite createContainer(Composite parent,
			FormToolkit toolkit, GridData fillHorizontal) {
		Composite container = toolkit.createComposite(parent);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.horizontalSpacing = 10;
		container.setLayout(gridLayout);
		container.setLayoutData(fillHorizontal);
		return container;
	}

	@Override
	public void refreshAttributeAtomControl() {
		if (isAvailable(styleCombo)) {
			final String currentStyle = get();
			int index = colorMaps.indexOf(currentStyle);
			if (styleCombo.getSelectionIndex() != index) {
				styleCombo.select(index);
				imageLabel.setImage(Activator
						.getImage(imagePrefix + currentStyle + ".png"));
			}
		}
	}

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
		this.defaultValue = defaultValue;
	}

	public void setDefaultValue(ColorMapValue colorMapValue) {
		setDefaultValue(colorMapValue.toString());
	}

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	/**
	 * Get color map value as string
	 *
	 * @return the value
	 */
	@Override
	public String get() {
		return super.get();
	}

	/**
	 * Set color map with string
	 *
	 * @param value
	 */
	@Override
	public void set(String value) {
		super.set(value);
	}

	/**
	 * Get predefined color maps
	 */
	public List<String> getColorMaps() {
		return colorMaps;
	}

	@Override
	public void setBackgroundColor(
			org.eclipse.swt.graphics.Color backgroundColor) {
		throw new IllegalStateException("Not yet implemented");
	}

	//#end region

}
