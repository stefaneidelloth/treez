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
 * Allows the user to choose a fill style
 */
public class FillStyle extends AbstractAttributeAtom<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(FillStyle.class);

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "My Symbol Style:")
	private String label;

	@IsParameter(defaultValue = "cross")
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
	private static final String IMAGE_PREFIX = "fill_";

	/**
	 * Predefined symbol styles
	 */
	private static final List<String> FILL_STYLES = FillStyleValue
			.getAllStringValues();

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public FillStyle(String name) {
		super(name);
		label = name;
	}

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public FillStyle(String name, String label) {
		super(name);
		this.label = label;
	}

	/**
	 * Constructor with default value
	 *
	 * @param name
	 * @param defaultStyle
	 */
	public FillStyle(String name, String label, String defaultStyle) {
		super(name);
		this.label = label;

		boolean isFillStyle = FILL_STYLES.contains(defaultStyle);
		if (isFillStyle) {
			attributeValue = defaultStyle;
		} else {
			throw new IllegalArgumentException("The specified fill style '"
					+ defaultStyle + "' is not known.");
		}
	}

	/**
	 * Copy constructor
	 *
	 * @param fillStyleToCopy
	 */
	private FillStyle(FillStyle fillStyleToCopy) {
		super(fillStyleToCopy);
		label = fillStyleToCopy.label;
		defaultValue = fillStyleToCopy.defaultValue;
		tooltip = fillStyleToCopy.tooltip;
	}

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

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public FillStyle copy() {
		return new FillStyle(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("fill_cross.png");
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
		final int prefferredLabelWidth = 85;
		labelComposite.setPrefferedWidth(prefferredLabelWidth);

		//image label
		imageLabel = toolkit.createLabel(container, "");

		//separator
		toolkit.createLabel(container, "  ");

		//combo box
		styleCombo = new ImageCombo(container, SWT.DEFAULT);
		styleCombo.setEnabled(isEnabled());
		styleCombo.setEditable(false);

		//set predefined styles
		final List<String> styles = getFillStyles();
		for (String styleString : styles) {
			styleCombo.add(styleString,
					Activator.getImage(IMAGE_PREFIX + styleString + ".png"));
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
				imageLabel.setImage(Activator
						.getImage(IMAGE_PREFIX + currentStyle + ".png"));

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

		//create container control for labels and file style
		Composite container = toolkit.createComposite(parent);
		GridLayout gridLayout = new GridLayout(5, false);
		gridLayout.horizontalSpacing = 0;
		container.setLayout(gridLayout);
		container.setLayoutData(fillHorizontal);
		return container;
	}

	@Override
	public void refreshAttributeAtomControl() {
		if (isAvailable(styleCombo)) {
			String style = get();
			final List<String> styles = getFillStyles();
			int index = styles.indexOf(style);
			if (styleCombo.getSelectionIndex() != index) {
				styleCombo.select(index);
				imageLabel.setImage(
						Activator.getImage(IMAGE_PREFIX + style + ".png"));
			}
		}
	}

	@Override
	public void setBackgroundColor(
			org.eclipse.swt.graphics.Color backgroundColor) {
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
	 * Sets the default fill style
	 *
	 * @param style
	 */
	public void setDefaultValue(FillStyleValue style) {
		setDefaultValue(style.toString());
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
	 * Get fill style as string
	 *
	 * @return the value
	 */
	@Override
	public String get() {
		return super.get();
	}

	/**
	 * Set fill style
	 *
	 * @param style
	 *            the value to set
	 */
	@Override
	public void set(String style) {
		super.set(style);
	}

	/**
	 * Get predefined fill styles
	 *
	 * @return the fill styles
	 */
	public List<String> getFillStyles() {
		return FILL_STYLES;
	}

	//#end region

}
