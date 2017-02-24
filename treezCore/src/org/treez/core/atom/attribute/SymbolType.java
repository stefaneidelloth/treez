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
import org.treez.core.atom.attribute.base.AbstractStringAttributeAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.swt.CustomLabel;

/**
 * Allows the user to choose a symbol type
 */
public class SymbolType extends AbstractStringAttributeAtom<SymbolType> {

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "My Symbol Style:")
	private String label;

	@IsParameter(defaultValue = "circle")
	private String defaultValue;

	@IsParameter(defaultValue = "")
	private String tooltip;

	private ImageCombo styleCombo = null;

	private Label imageLabel = null;

	private final String imagePrefix = "symbol_";

	/**
	 * Predefined symbol styles
	 */
	private final List<String> symbolStyles = SymbolStyleValue.getAllStringValues();

	//#end region

	//#region CONSTRUCTORS
	public SymbolType(String name) {
		super(name);
		label = name;
	}

	public SymbolType(String name, String label, String defaultStyle) {
		super(name);
		this.label = label;

		boolean isLineStyle = symbolStyles.contains(defaultStyle);
		if (isLineStyle) {
			attributeValue = defaultStyle;
		} else {
			throw new IllegalArgumentException("The specified symbol style '" + defaultStyle + "' is not known.");
		}
	}

	/**
	 * Copy constructor
	 */
	private SymbolType(SymbolType symbolStyleToCopy) {
		super(symbolStyleToCopy);
		label = symbolStyleToCopy.label;
		defaultValue = symbolStyleToCopy.defaultValue;
		tooltip = symbolStyleToCopy.tooltip;
	}

	//#end region

	//#region METHODS

	@Override
	public SymbolType getThis() {
		return this;
	}

	@Override
	public SymbolType copy() {
		return new SymbolType(this);
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("symbol_style.png");
	}

	@Override
	public AbstractStringAttributeAtom<SymbolType> createAttributeAtomControl(
			Composite parent,
			FocusChangingRefreshable treeViewerRefreshable) {

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
		GridData layoutData = new GridData();
		layoutData.horizontalAlignment = GridData.BEGINNING;
		labelComposite.setLayoutData(layoutData);

		final int preferredLabelWidth = 85;
		labelComposite.setPrefferedWidth(preferredLabelWidth);

		//image label
		imageLabel = toolkit.createLabel(container, "");

		//spacer
		toolkit.createLabel(container, "  ");

		//combo box
		styleCombo = new ImageCombo(container, SWT.DEFAULT);
		styleCombo.setEnabled(isEnabled());
		styleCombo.setEditable(false);

		//set predefined styles
		List<String> styles = getSymbolStyles();
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
				triggerListeners(styleCombo);
			}
		});

		return this;

	}

	@SuppressWarnings("checkstyle:magicnumber")
	private static Composite createContainer(Composite parent, FormToolkit toolkit) {

		//create container control for labels and line style
		Composite container = toolkit.createComposite(parent);
		GridLayout gridLayout = new GridLayout(5, false);
		gridLayout.horizontalSpacing = 0;
		container.setLayout(gridLayout);

		//create grid data to use all horizontal space
		GridData fillHorizontal = new GridData();
		fillHorizontal.grabExcessHorizontalSpace = true;
		fillHorizontal.horizontalAlignment = GridData.FILL;

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
				imageLabel.setImage(Activator.getImage(imagePrefix + style + ".png"));
			}
		}
	}

	@Override
	public SymbolType setBackgroundColor(org.eclipse.swt.graphics.Color backgroundColor) {
		throw new IllegalStateException("Not yet implemented");

	}

	//#end region

	//#region ACCESSORS

	public String getLabel() {
		return label;
	}

	public SymbolType setLabel(String label) {
		this.label = label;
		return getThis();
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	public SymbolType setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return getThis();
	}

	public SymbolType setDefaultValue(SymbolStyleValue symbolStyleValue) {
		setDefaultValue(symbolStyleValue.toString());
		return getThis();
	}

	public String getTooltip() {
		return tooltip;
	}

	public SymbolType setTooltip(String tooltip) {
		this.tooltip = tooltip;
		return getThis();
	}

	@Override
	public String get() {
		return super.get();
	}

	@Override
	public void set(String style) {
		super.set(style);
	}

	/**
	 * Get predefined symbol styles
	 */
	public List<String> getSymbolStyles() {
		return symbolStyles;
	}

	//#end region

}
