package org.treez.core.atom.attribute.comboBox.lineStyle;

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
import org.treez.core.atom.attribute.comboBox.image.ImageCombo;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.swt.CustomLabel;
import org.treez.core.utils.Utils;

/**
 * Allows the user to choose a line style
 */
public class LineStyle extends AbstractStringAttributeAtom<LineStyle> {

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "Line Style:")
	private String label;

	@IsParameter(defaultValue = "solid")
	private String defaultValue;

	@IsParameter(defaultValue = "")
	private String tooltip;

	private ImageCombo styleCombo;

	private Label imageLabel;

	/**
	 * Predefined line styles
	 */
	private final List<String> lineStyles = LineStyleValue.getAllStringValues();

	//#end region

	//#region CONSTRUCTORS

	public LineStyle(String name) {
		super(name);
		label = Utils.firstToUpperCase(name);
	}

	public LineStyle(String name, String defaultStyle) {
		this(name);

		boolean isLineStyle = lineStyles.contains(defaultStyle);
		if (isLineStyle) {
			attributeValue = defaultStyle;
		} else {
			throw new IllegalArgumentException("The specified line style '" + defaultStyle + "' is not known.");
		}
	}

	/**
	 * Copy constructor
	 */
	private LineStyle(LineStyle lineStyleToCopy) {
		super(lineStyleToCopy);
		label = lineStyleToCopy.label;
		defaultValue = lineStyleToCopy.defaultValue;
		tooltip = lineStyleToCopy.tooltip;
		styleCombo = lineStyleToCopy.styleCombo;
		imageLabel = lineStyleToCopy.imageLabel;
	}

	//#end region

	//#region METHODS

	@Override
	public LineStyle getThis() {
		return this;
	}

	@Override
	public LineStyle copy() {
		return new LineStyle(this);
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("line_style.png");
	}

	@Override
	public AbstractStringAttributeAtom<LineStyle> createAttributeAtomControl(
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
		final int preferredLabelWidth = 85;
		labelComposite.setPrefferedWidth(preferredLabelWidth);

		//image label
		imageLabel = toolkit.createLabel(container, "");

		//separator
		toolkit.createLabel(container, "  ");

		//combo box
		styleCombo = new ImageCombo(container, SWT.DEFAULT);
		styleCombo.setEnabled(isEnabled());
		styleCombo.setEditable(false);

		//set predefined colors
		List<String> styles = getLineStyles();
		for (String styleString : styles) {
			styleCombo.add(styleString, Activator.getImage(styleString + ".png"));
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
				triggerListeners();
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
		GridLayout gridLayout = new GridLayout(5, false);
		gridLayout.horizontalSpacing = 0;
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
	public LineStyle setBackgroundColor(org.eclipse.swt.graphics.Color backgroundColor) {
		throw new IllegalStateException("Not yet implemented");

	}

	//#end region

	//#region ACCESSORS

	public String getLabel() {
		return label;
	}

	public LineStyle setLabel(String label) {
		this.label = label;
		return getThis();
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	public LineStyle setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return getThis();
	}

	public void setDefaultValue(LineStyleValue lineStyleValue) {
		setDefaultValue(lineStyleValue.toString());
	}

	public String getTooltip() {
		return tooltip;
	}

	public LineStyle setTooltip(String tooltip) {
		this.tooltip = tooltip;
		return getThis();
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

	@Override
	public void set(String style) {
		super.set(style);
	}

	/**
	 * Get predefined line styles
	 */
	public List<String> getLineStyles() {
		return lineStyles;
	}

	//#end region

}
