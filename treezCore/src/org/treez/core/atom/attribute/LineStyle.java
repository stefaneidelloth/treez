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
public class LineStyle extends AbstractAttributeAtom<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(LineStyle.class);

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
	private ImageCombo styleCombo;

	/**
	 * The image label
	 */
	private Label imageLabel;

	/**
	 * Predefined line styles
	 */
	private final List<String> lineStyles = LineStyleValue.getAllStringValues();

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public LineStyle(String name) {
		super(name);
		label = name;
	}

	/**
	 * Copy constructor
	 *
	 * @param lineStyleToCopy
	 */
	private LineStyle(LineStyle lineStyleToCopy) {
		super(lineStyleToCopy);
		label = lineStyleToCopy.label;
		defaultValue = lineStyleToCopy.defaultValue;
		tooltip = lineStyleToCopy.tooltip;
		styleCombo = lineStyleToCopy.styleCombo;
		imageLabel = lineStyleToCopy.imageLabel;
	}

	/**
	 * Constructor with default value
	 *
	 * @param name
	 * @param defaultStyle
	 */
	public LineStyle(String name, String defaultStyle) {
		super(name);
		label = name;

		boolean isLineStyle = lineStyles.contains(defaultStyle);
		if (isLineStyle) {
			attributeValue = defaultStyle;
		} else {
			throw new IllegalArgumentException("The specified line style '"
					+ defaultStyle + "' is not known.");
		}
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public LineStyle copy() {
		return new LineStyle(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("line_style.png");
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
		final int preferredLabelWidth = 80;
		labelComposite.setPrefferedWidth(preferredLabelWidth);

		//image label
		imageLabel = toolkit.createLabel(container, "");

		//combo box
		styleCombo = new ImageCombo(container, SWT.DEFAULT);
		styleCombo.setEnabled(isEnabled());
		styleCombo.setEditable(false);

		//set predefined colors
		List<String> styles = getLineStyles();
		for (String styleString : styles) {
			styleCombo.add(styleString,
					Activator.getImage(styleString + ".png"));
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
				imageLabel.setImage(Activator.getImage(currentStyle + ".png"));

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
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.horizontalSpacing = 10;
		container.setLayout(gridLayout);
		container.setLayoutData(fillHorizontal);
		return container;
	}

	@Override
	public void refreshAttributeAtomControl() {
		if (isAvailable(styleCombo)) {
			List<String> styles = getLineStyles();
			String style = get();
			int index = styles.indexOf(style);
			if (styleCombo.getSelectionIndex() != index) {
				styleCombo.select(index);
				imageLabel.setImage(Activator.getImage(style + ".png"));
			}
		}
	}

	@Override
	public void setBackgroundColor(
			org.eclipse.swt.graphics.Color backgroundColor) {
		throw new IllegalStateException("Not yet implemented");

	}

	@Override
	public void addModificationConsumer(Consumer<String> consumer) {

		throw new IllegalStateException("not yet implemented");
		//treezList.addModifyListener(	(event) -> consumer.accept(event.data.toString()));
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
	 * Sets the default line style
	 *
	 * @param lineStyleValue
	 */
	public void setDefaultValue(LineStyleValue lineStyleValue) {
		setDefaultValue(lineStyleValue.toString());
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
	 * Get line style as string
	 *
	 * @return the value
	 */
	@Override
	public String get() {
		return super.get();
	}

	/**
	 * Set line style
	 *
	 * @param style
	 *            the filePath to set
	 */
	@Override
	public void set(String style) {
		super.set(style);
	}

	/**
	 * Get predefined line styles
	 *
	 * @return the colorsHex
	 */
	public List<String> getLineStyles() {
		return lineStyles;
	}

	//#end region

}
