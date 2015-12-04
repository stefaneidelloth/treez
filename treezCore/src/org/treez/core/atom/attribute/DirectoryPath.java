package org.treez.core.atom.attribute;

import org.apache.log4j.Logger;
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
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.annotation.IsParameter;

/**
 * Allows the user to choose a line style
 */
public class DirectoryPath extends AbstractAttributeAtom<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(DirectoryPath.class);

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

	/**
	 * The label
	 */
	private Label labelComposite;

	/**
	 * The text field
	 */
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

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public DirectoryPath(String name) {
		super(name);
		label = name;
	}

	/**
	 * Copy constructor
	 *
	 * @param directoryPathToCopy
	 */
	protected DirectoryPath(DirectoryPath directoryPathToCopy) {
		super(directoryPathToCopy);
		showEnabledCheckBox = directoryPathToCopy.showEnabledCheckBox;
		label = directoryPathToCopy.label;
		defaultValue = directoryPathToCopy.defaultValue;
		tooltip = directoryPathToCopy.tooltip;
		showOpenButton = directoryPathToCopy.showOpenButton;
	}

	/**
	 * Constructor with default filePath
	 *
	 * @param name
	 * @param defaultDirectoryPath
	 */
	public DirectoryPath(String name, String defaultDirectoryPath) {
		super(name);
		label = name;
		attributeValue = defaultDirectoryPath;

	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public DirectoryPath copy() {
		return new DirectoryPath(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("browseDirectory.png");
	}

	@Override
	public AbstractAttributeAtom<String> createAttributeAtomControl(Composite parent, Refreshable treeViewerRefreshable) {

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
		textField.setLayoutData(textFieldFillHorizontal);

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
				triggerModificationListeners();
			}

		});

		validateText(errorDecoration, get());
	}

	private void createDirectorySelectionButton(FormToolkit toolkit) {
		selectButton = toolkit.createLabel(subContainer, "", SWT.NONE);
		selectButton.setEnabled(isEnabled());
		Image browseImage = Activator.getImage("browse.png");
		selectButton.setImage(browseImage);
		selectButton.setToolTipText("Select directory");

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
		Image runImage = Activator.getImage("run_triangle.png");
		openButton.setImage(runImage);
		openButton.setToolTipText("Open directory");

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
	public void setEnabled(boolean state) {

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

	/**
	 * Sets the showEnableCheckBox flag
	 */
	public void setShowEnabledCheckBox(boolean state) {
		showEnabledCheckBox = state;
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
	 * @param defaultDirectoryPath
	 */
	public void setDefaultValue(String defaultDirectoryPath) {
		this.defaultValue = defaultDirectoryPath;
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
	 * Sets the background color
	 *
	 * @param backgroundColor
	 */
	@Override
	public void setBackgroundColor(Color backgroundColor) {

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

	}
	//#end region

}
