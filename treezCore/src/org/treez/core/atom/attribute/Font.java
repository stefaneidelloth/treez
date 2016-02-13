package org.treez.core.atom.attribute;

import java.awt.GraphicsEnvironment;
import java.util.Arrays;
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
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.swt.CustomLabel;

/**
 * Allows the user to choose a font
 */
public class Font extends AbstractAttributeAtom<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(Font.class);

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "My Font:")
	private String label;

	@IsParameter(defaultValue = "Arial")
	private String defaultValue;

	@IsParameter(defaultValue = "")
	private String tooltip;

	/**
	 * The combo box
	 */
	private Combo fontCombo = null;

	/**
	 * Available font names
	 */
	private List<String> fonts = null;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public Font(String name) {
		super(name);
		label = name;
	}

	/**
	 * Copy constructor
	 *
	 * @param fontToCopy
	 */
	private Font(Font fontToCopy) {
		super(fontToCopy);
		label = fontToCopy.label;
		defaultValue = fontToCopy.defaultValue;
		tooltip = fontToCopy.tooltip;
	}

	/**
	 * Constructor with default value
	 *
	 * @param name
	 * @param defaultFont
	 */
	public Font(String name, String defaultFont) {
		super(name);
		label = name;
		boolean isFont = getFonts().contains(defaultFont);
		if (isFont) {
			attributeValue = defaultFont;
		} else {
			throw new IllegalArgumentException(
					"The specified font '" + defaultFont + "' is not known.");
		}
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public Font copy() {
		return new Font(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("font.png");
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

		//combo box
		fontCombo = new Combo(container, SWT.NONE);
		fontCombo.setEnabled(isEnabled());

		//set available fronts
		final List<String> currentFonts = getFonts();
		for (String fontString : currentFonts) {
			fontCombo.add(fontString);
		}

		//initialize selected item

		//action listener for combo box
		fontCombo.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = fontCombo.getSelectionIndex();
				String currentFont = currentFonts.get(index);
				set(currentFont);

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
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.horizontalSpacing = 10;
		container.setLayout(gridLayout);
		container.setLayoutData(fillHorizontal);
		return container;
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
	public void setBackgroundColor(
			org.eclipse.swt.graphics.Color backgroundColor) {
		throw new IllegalStateException("Not yet implemented");

	}

	@Override
	public void addModificationConsumer(String key, Consumer<String> consumer) {
		addModifyListener(key, (event) -> {
			if (isAvailable(fontCombo)) {
				String value = fontCombo.getText();
				consumer.accept(value);
			}
		});
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
			GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			String[] fontNames = graphicsEnvironment
					.getAvailableFontFamilyNames();
			fonts = Arrays.asList(fontNames);
		}
		return fonts;
	}

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
	 * Get font
	 *
	 * @return the value
	 */
	@Override
	public String get() {
		return super.get();
	}

	/**
	 * Set font
	 *
	 * @param font
	 *            the font to set
	 */
	@Override
	public void set(String font) {
		super.set(font);
	}

	//#end region

}
