package org.treez.core.atom.attribute;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.AbstractStringAttributeAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.utils.Utils;

public class TextArea extends AbstractStringAttributeAtom<TextArea> {

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "My TextArea:")
	private String label;

	@IsParameter(defaultValue = "80")
	private Integer prefferedLabelWidth;

	@IsParameter(defaultValue = "")
	private String defaultValue;

	@IsParameter(defaultValue = "")
	private String tooltip;

	private Composite contentContainer;

	private Text textArea = null;

	//#end region

	//#region CONSTRUCTORS

	public TextArea(String name) {
		super(name);
		label = Utils.firstToUpperCase(name); //this default label might be overridden by explicitly setting the label
	}

	/**
	 * Copy constructor
	 */
	private TextArea(TextArea areaToCopy) {
		super(areaToCopy);
		label = areaToCopy.label;
		prefferedLabelWidth = areaToCopy.prefferedLabelWidth;
		defaultValue = areaToCopy.defaultValue;
		tooltip = areaToCopy.tooltip;
	}

	//#end region

	//#region METHODS

	@Override
	public TextArea getThis() {
		return this;
	}

	@Override
	public TextArea copy() {
		return new TextArea(this);
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("textArea.png");
	}

	@Override
	@SuppressWarnings("checkstyle:magicnumber")
	public AbstractStringAttributeAtom<TextArea> createAttributeAtomControl(
			Composite parent,
			FocusChangingRefreshable treeViewerRefreshable) {

		initializeValue();

		//toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		//create content composite for label and check box
		contentContainer = toolkit.createComposite(parent);

		GridData fillHorizontal = new GridData();
		fillHorizontal.grabExcessHorizontalSpace = true;
		fillHorizontal.horizontalAlignment = GridData.FILL;
		contentContainer.setLayoutData(fillHorizontal);

		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.horizontalSpacing = 2;
		gridLayout.verticalSpacing = 4;
		gridLayout.marginHeight = 2;
		gridLayout.marginWidth = 4;
		contentContainer.setLayout(gridLayout);

		//label
		String currentLabel = getLabel();
		toolkit.createLabel(contentContainer, currentLabel);

		//text area
		Composite textContainer = toolkit.createComposite(contentContainer);
		textContainer.setLayout(new GridLayout());

		GridData textContainerData = new GridData();
		textContainerData.grabExcessHorizontalSpace = true;
		textContainerData.grabExcessVerticalSpace = true;
		textContainerData.horizontalAlignment = GridData.FILL;
		textContainerData.verticalAlignment = GridData.FILL;
		textContainer.setLayoutData(textContainerData);

		textArea = toolkit.createText(textContainer, get(), SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		textArea.setEnabled(isEnabled());
		textArea.setToolTipText(tooltip);

		GridData areaData = new GridData();
		areaData.grabExcessHorizontalSpace = true;
		areaData.grabExcessVerticalSpace = true;
		areaData.horizontalAlignment = GridData.FILL;
		areaData.verticalAlignment = GridData.FILL;
		areaData.widthHint = 200;
		areaData.heightHint = 80;
		textArea.setLayoutData(areaData);

		//Utils.registerHelpId(currentHelpId, parent);

		//validation & update

		//currently disabled since the error decoration causes flickering in
		//the property view
		//when a page is updated

		ModifyListener modifyListener = (event) -> {

			//get current caret position
			int caretPosition = textArea.getCaretPosition();

			//get text
			String text = ((Text) event.getSource()).getText();

			//set text
			set(text);

			//restore caret position
			textArea.setSelection(caretPosition);

			//trigger modification listeners
			triggerListeners();

		};

		textArea.addModifyListener(modifyListener);

		return this;
	}

	private void initializeValue() {
		//initialize value at the first call
		if (!isInitialized()) {
			set(defaultValue);
		}
	}

	@Override
	public TextArea setEnabled(boolean state) {
		super.setEnabled(state);
		if (isAvailable(textArea)) {
			textArea.setEnabled(state);
		}
		return getThis();
	}

	@Override
	public void refreshAttributeAtomControl() {
		if (isAvailable(textArea)) {
			String value = get();
			if (!textArea.getText().equals(value)) {
				textArea.setText(get());
			}
		}
	}

	@Override
	public TextArea setBackgroundColor(org.eclipse.swt.graphics.Color backgroundColor) {
		throw new IllegalStateException("Not yet implemented");
		//return getThis();
	}

	//#end region

	//#region ACCESSORS

	public String getLabel() {
		return label;
	}

	public TextArea setLabel(String label) {
		this.label = label;
		return getThis();
	}

	public TextArea setPrefferedLabelWidth(int width) {
		prefferedLabelWidth = width;
		return getThis();
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	public TextArea setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return getThis();
	}

	public String getTooltip() {
		return tooltip;
	}

	public TextArea setTooltip(String tooltip) {
		this.tooltip = tooltip;
		return getThis();
	}

	//#end region

}
