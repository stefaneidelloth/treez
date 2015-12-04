package org.treez.core.atom.attribute;

import java.util.List;

import org.apache.log4j.Logger;
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
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.swt.CustomLabel;

/**
 * Allows the user to choose a error bar style
 */
public class ErrorBarStyle extends AbstractAttributeAtom<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(ErrorBarStyle.class);

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "My Error Bar Style:")
	private String label;

	@IsParameter(defaultValue = "bar")
	private String defaultValue;

	@IsParameter(defaultValue = "")
	private String tooltip;

	/**
	 * The combo box
	 */
	private ImageCombo styleCombo = null;

	/**
	 * The image label
	 */
	private Label imageLabel = null;

	/**
	 * Prefix for the image file names
	 */
	private final String imagePrefix = "errorbar_";

	/**
	 * Predefined error bar styles
	 */
	private final List<String> errorBarStyles = ErrorBarStyleValue.getAllStringValues();

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public ErrorBarStyle(String name) {
		super(name);
		label = name;
	}

	/**
	 * Copy constructor
	 *
	 * @param errorBarStyleToCopy
	 */
	private ErrorBarStyle(ErrorBarStyle errorBarStyleToCopy) {
		super(errorBarStyleToCopy);
		label = errorBarStyleToCopy.label;
		defaultValue = errorBarStyleToCopy.defaultValue;
		tooltip = errorBarStyleToCopy.tooltip;
	}

	/**
	 * Constructor with default value
	 *
	 * @param name
	 * @param defaultStyle
	 */
	public ErrorBarStyle(String name, String defaultStyle) {
		super(name);
		label = name;

		boolean isErrorBarStyle = errorBarStyles.contains(defaultStyle);
		if (isErrorBarStyle) {
			attributeValue = defaultStyle;
		} else {
			throw new IllegalArgumentException("The specified error bar style '" + defaultStyle + "' is not known.");
		}
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public ErrorBarStyle copy() {
		return new ErrorBarStyle(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("errorbar_bar.png");
	}

	/**
	 * Creates the composite on a given parent
	 *
	 * @param parent
	 */
	@Override
	public AbstractAttributeAtom<String> createAttributeAtomControl(Composite parent, Refreshable treeViewerRefreshable) {

		//initialize value at the first call
		if (!isInitialized()) {
			set(defaultValue);
		}

		//toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		Composite container = createContainer(parent, toolkit);

		//label
		String currentLabel = getLabel();
		CustomLabel labelComposite = new CustomLabel(toolkit, container, currentLabel);
		final int preferredLabelWidth = 80;
		labelComposite.setPrefferedWidth(preferredLabelWidth);

		//image label
		imageLabel = toolkit.createLabel(container, "");

		//combo box
		styleCombo = new ImageCombo(container, SWT.DEFAULT);
		styleCombo.setEnabled(isEnabled());
		styleCombo.setEditable(false);

		//set predefined styles
		final List<String> styles = getErrorBarStyles();
		for (String styleString : styles) {
			styleCombo.add(styleString, Activator.getImage(imagePrefix + styleString + ".png"));
		}

		//initialize selected item
		refreshAttributeAtomControl();

		//action listener for combo box
		styleCombo.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = styleCombo.getSelectionIndex();
				String currentStyle = styles.get(index);
				set(currentStyle);
				imageLabel.setImage(Activator.getImage(imagePrefix + currentStyle + ".png"));

				//trigger modification listeners
				triggerModificationListeners();
			}
		});

		return this;

	}

	@SuppressWarnings("checkstyle:magicnumber")
	private static Composite createContainer(Composite parent, FormToolkit toolkit) {
		//create grid data to use all horizontal space
		GridData fillHorizontal = new GridData();
		fillHorizontal.grabExcessHorizontalSpace = true;
		fillHorizontal.horizontalAlignment = GridData.FILL;

		//create container control for labels and line style
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
			String style = get();
			List<String> styles = getErrorBarStyles();
			int index = styles.indexOf(style);
			if (styleCombo.getSelectionIndex() != index) {
				styleCombo.select(index);
				imageLabel.setImage(Activator.getImage(imagePrefix + style + ".png"));
			}
		}
	}

	@Override
	public void setBackgroundColor(org.eclipse.swt.graphics.Color backgroundColor) {
		throw new IllegalStateException("Not yet implemented");

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
		this.defaultValue = defaultValue;
	}

	/**
	 * Sets the default error bar style
	 *
	 * @param defaultStyle
	 */
	public void setDefaultValue(ErrorBarStyleValue defaultStyle) {
		setDefaultValue(defaultStyle.toString());
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
	 * Get predefined styles
	 *
	 * @return the styles
	 */
	public List<String> getErrorBarStyles() {
		return errorBarStyles;
	}

	//#end region

}
