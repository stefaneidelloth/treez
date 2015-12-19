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
public class SymbolStyle extends AbstractAttributeAtom<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(SymbolStyle.class);

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "My Symbol Style:")
	private String label;

	@IsParameter(defaultValue = "circle")
	private String defaultValue;

	@IsParameter(defaultValue = "")
	private String tooltip;

	/**
	 * Combo box
	 */
	private ImageCombo styleCombo = null;

	/**
	 * Image label
	 */
	private Label imageLabel = null;

	/**
	 * Prefix for the image file names
	 */
	private final String imagePrefix = "symbol_";

	/**
	 * Predefined symbol styles
	 */
	private final List<String> symbolStyles = SymbolStyleValue
			.getAllStringValues();

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public SymbolStyle(String name) {
		super(name);
		label = name;
	}

	/**
	 * Constructor with default value
	 *
	 * @param name
	 * @param defaultStyle
	 */
	public SymbolStyle(String name, String defaultStyle) {
		super(name);
		label = name;

		boolean isLineStyle = symbolStyles.contains(defaultStyle);
		if (isLineStyle) {
			attributeValue = defaultStyle;
		} else {
			throw new IllegalArgumentException("The specified symbol style '"
					+ defaultStyle + "' is not known.");
		}
	}

	/**
	 * Copy constructor
	 *
	 * @param symbolStyleToCopy
	 */
	private SymbolStyle(SymbolStyle symbolStyleToCopy) {
		super(symbolStyleToCopy);
		label = symbolStyleToCopy.label;
		defaultValue = symbolStyleToCopy.defaultValue;
		tooltip = symbolStyleToCopy.tooltip;
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public SymbolStyle copy() {
		return new SymbolStyle(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("symbol_style.png");
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

		Composite container = crateContainer(parent, toolkit);

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

		//set predefined styles
		List<String> styles = getSymbolStyles();
		for (String styleString : styles) {
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
				String currentStyle = styles.get(index);
				set(currentStyle);
				imageLabel.setImage(Activator
						.getImage(imagePrefix + currentStyle + ".png"));

				//trigger modification listeners
				triggerModificationListeners();
			}
		});

		return this;

	}

	@SuppressWarnings("checkstyle:magicnumber")
	private static Composite crateContainer(Composite parent,
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
		if (styleCombo != null && !styleCombo.isDisposed()) {
			String style = get();
			List<String> styles = getSymbolStyles();
			int index = styles.indexOf(style);
			if (styleCombo.getSelectionIndex() != index) {
				styleCombo.select(index);
				imageLabel.setImage(
						Activator.getImage(imagePrefix + style + ".png"));
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
		addModifyListener((event) -> {
			String value = event.data.toString();
			consumer.accept(value);
		});
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
	 * @param symbolStyleValue
	 */
	public void setDefaultValue(SymbolStyleValue symbolStyleValue) {
		setDefaultValue(symbolStyleValue.toString());
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
	 * Get symbol style as string
	 *
	 * @return the value
	 */
	@Override
	public String get() {
		return super.get();
	}

	/**
	 * Set symbol style
	 *
	 * @param style
	 *            the value to set
	 */
	@Override
	public void set(String style) {
		super.set(style);
	}

	/**
	 * Get predefined symbol styles
	 *
	 * @return the symbol styles
	 */
	public List<String> getSymbolStyles() {
		return symbolStyles;
	}

	//#end region

}
