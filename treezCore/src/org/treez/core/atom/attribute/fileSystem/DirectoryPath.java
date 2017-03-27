package org.treez.core.atom.attribute.fileSystem;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.AbstractStringAttributeAtom;
import org.treez.core.atom.attribute.text.TextFieldErrorDecoration;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.utils.Utils;

public class DirectoryPath extends AbstractStringAttributeAtom<DirectoryPath> {

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "Directory:")
	private String label;

	@IsParameter(defaultValue = "C:\\")
	private String defaultValue;

	@IsParameter(defaultValue = "")
	private String tooltip;

	@IsParameter(defaultValue = "true")
	private Boolean showOpenButton;

	/**
	 * If this is true, an additional enabled check box will be shown
	 */
	private boolean showEnabledCheckBox = false;

	/**
	 * Container for label and rest
	 */
	private Composite container;

	/**
	 * Container for check box and label
	 */
	private Composite checkBoxAndLabelContainer;

	/**
	 * Sub container for rest
	 */
	private Composite subContainer;

	private Label labelComposite;

	private Text textField = null;

	/**
	 * Button for selecting the directory
	 */
	private Label selectButton;

	/**
	 * Button for opening the directory
	 */
	private Label openButton;

	//#end region

	//#region CONSTRUCTORS

	public DirectoryPath(String name) {
		super(name);
		label = Utils.firstToUpperCase(name);
	}

	public DirectoryPath(String name, String defaultDirectoryPath) {
		super(name);
		label = Utils.firstToUpperCase(name);
		attributeValue = defaultDirectoryPath;

	}

	/**
	 * Copy constructor
	 */
	protected DirectoryPath(DirectoryPath directoryPathToCopy) {
		super(directoryPathToCopy);
		showEnabledCheckBox = directoryPathToCopy.showEnabledCheckBox;
		label = directoryPathToCopy.label;
		defaultValue = directoryPathToCopy.defaultValue;
		tooltip = directoryPathToCopy.tooltip;
		showOpenButton = directoryPathToCopy.showOpenButton;
	}

	//#end region

	//#region METHODS

	@Override
	public DirectoryPath getThis() {
		return this;
	}

	@Override
	public DirectoryPath copy() {
		return new DirectoryPath(this);
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("browseDirectory.png");
	}

	@Override
	public AbstractStringAttributeAtom<DirectoryPath> createAttributeAtomControl(
			Composite parent,
			FocusChangingRefreshable treeViewerRefreshable) {

		//initialize value at the first call
		if (!isInitialized()) {
			set(defaultValue);
		}

		//toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		//container for label and rest
		container = createVerticalContainer(parent, toolkit);

		checkBoxAndLabelContainer = createHorizontalContainer(container, toolkit);

		//label
		labelComposite = toolkit.createLabel(checkBoxAndLabelContainer, label);
		labelComposite.setBackground(backgroundColor);

		//sub container for rest (text field and buttons)
		createSubContainer(toolkit);

		//text field
		createTextField(toolkit);

		//select directory button
		createDirectorySelectionButton(toolkit);

		if (showOpenButton) {
			//open button
			createDirectoryOpenButton(toolkit);

		}

		return this;
	}

	@SuppressWarnings("checkstyle:magicnumber")
	private void createSubContainer(FormToolkit toolkit) {

		//create grid data to use all horizontal space
		GridData fillHorizontal = new GridData();
		fillHorizontal.grabExcessHorizontalSpace = true;
		fillHorizontal.horizontalAlignment = GridData.FILL;

		subContainer = toolkit.createComposite(container);
		int numberOfColumns = 2;
		if (showOpenButton) {
			numberOfColumns = 3;
		}
		GridLayout gridLayout = new GridLayout(numberOfColumns, false);

		gridLayout.horizontalSpacing = 10;
		subContainer.setLayout(gridLayout);
		subContainer.setLayoutData(fillHorizontal);
		subContainer.setBackground(backgroundColor);
	}

	private void createTextField(FormToolkit toolkit) {
		//create grid data to use all horizontal space and limit preferred width
		GridData textFieldFillHorizontal = new GridData();
		textFieldFillHorizontal.grabExcessHorizontalSpace = true;
		textFieldFillHorizontal.horizontalAlignment = GridData.FILL;
		final int prefferedTextFieldWidth = 200;
		textFieldFillHorizontal.widthHint = prefferedTextFieldWidth;

		//text field
		textField = toolkit.createText(subContainer, get());
		textField.setEnabled(isEnabled());
		textField.setVisible(isVisible());
		textField.setLayoutData(textFieldFillHorizontal);
		textField.setBackground(backgroundColor);

		//create error decoration for text field
		String errorMessage = "Invalid directory path";
		final TextFieldErrorDecoration errorDecoration = new TextFieldErrorDecoration(
				textField,
				errorMessage,
				container);

		//create listener for text field
		textField.addModifyListener(new ModifyListener() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void modifyText(ModifyEvent e) {
				//get current caret position
				int caretPosition = textField.getCaretPosition();

				String text = textField.getText();
				validateText(errorDecoration, text);
				set(text);

				//restore caret position
				textField.setSelection(caretPosition);

				//trigger modification listeners
				triggerListeners();
			}

		});

		validateText(errorDecoration, get());
	}

	private void createDirectorySelectionButton(FormToolkit toolkit) {
		selectButton = toolkit.createLabel(subContainer, "", SWT.NONE);
		selectButton.setEnabled(isEnabled());
		selectButton.setVisible(isVisible());
		Image browseImage = Activator.getImage("browse.png");
		selectButton.setImage(browseImage);
		selectButton.setToolTipText("Select directory");
		selectButton.setBackground(backgroundColor);

		selectButton.addListener(SWT.MouseDown, new Listener() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				DirectoryDialog directoryDialog = new DirectoryDialog(Display.getCurrent().getActiveShell(), SWT.MULTI);

				directoryDialog.setFilterPath(defaultValue);

				String directory = directoryDialog.open();

				if (directory != null) {
					defaultValue = directoryDialog.getFilterPath();
					set(directory);
					textField.setText(get());
				}
			}
		});
	}

	private void createDirectoryOpenButton(FormToolkit toolkit) {
		openButton = toolkit.createLabel(subContainer, "", SWT.NONE);
		openButton.setEnabled(isEnabled());
		openButton.setVisible(isVisible());
		Image runImage = Activator.getImage("run_triangle.png");
		openButton.setImage(runImage);
		openButton.setToolTipText("Open directory");
		openButton.setBackground(backgroundColor);

		//create action listener for open button
		openButton.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				String directoryPath = get();
				FileHelper.openDirectory(directoryPath);
			}
		});
	}

	/**
	 * Validates the directory path
	 *
	 * @param errorDecoration
	 * @param text
	 */
	private void validateText(final TextFieldErrorDecoration errorDecoration, String text) {

		boolean isValid = FileHelper.isValidDirectoryPath(text);
		if (isValid) {
			errorDecoration.hide();
			set(text);
		} else {
			errorDecoration.show();
		}
	}

	@Override
	public DirectoryPath setEnabled(boolean state) {

		super.setEnabled(state);
		if (isAvailable(textField)) {
			textField.setEnabled(state);
		}

		if (isAvailable(selectButton)) {
			selectButton.setEnabled(state);
		}

		if (isAvailable(openButton)) {
			openButton.setEnabled(state);
		}

		if (treeViewRefreshable != null) {
			treeViewRefreshable.refresh();
		}
		return getThis();

	}

	@Override
	public void refreshAttributeAtomControl() {
		if (textField != null && !textField.isDisposed()) {
			String value = get();
			if (!textField.getText().equals(value)) {
				textField.setText(value);
			}
		}
	}

	//#end region

	//#region ACCESSORS

	public DirectoryPath setShowEnabledCheckBox(boolean state) {
		showEnabledCheckBox = state;
		return getThis();
	}

	public String getLabel() {
		return label;
	}

	public DirectoryPath setLabel(String label) {
		this.label = label;
		return getThis();
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	public DirectoryPath setDefaultValue(String defaultDirectoryPath) {
		this.defaultValue = defaultDirectoryPath;
		return getThis();
	}

	public String getTooltip() {
		return tooltip;
	}

	public DirectoryPath setTooltip(String tooltip) {
		this.tooltip = tooltip;
		return getThis();
	}

	@Override
	public DirectoryPath setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		if (isAvailable(container)) {
			container.setBackground(backgroundColor);
		}

		if (isAvailable(checkBoxAndLabelContainer)) {
			checkBoxAndLabelContainer.setBackground(backgroundColor);
		}

		if (isAvailable(labelComposite)) {
			labelComposite.setBackground(backgroundColor);
		}

		if (isAvailable(subContainer)) {
			subContainer.setBackground(backgroundColor);
		}

		if (isAvailable(selectButton)) {
			selectButton.setBackground(backgroundColor);
		}

		if (isAvailable(openButton)) {
			openButton.setBackground(backgroundColor);
		}
		return getThis();

	}

	//#end region

}
