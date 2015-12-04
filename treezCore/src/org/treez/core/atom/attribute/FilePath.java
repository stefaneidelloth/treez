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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.annotation.IsParameter;

/**
 * Allows the user to choose a file path
 */
public class FilePath extends AbstractAttributeAtom<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(FilePath.class);

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "File path:")
	private String label;

	@IsParameter(defaultValue = "C:\\")
	private String defaultValue;

	@IsParameter(defaultValue = "")
	private String tooltip;

	@IsParameter(defaultValue = "*.*")
	//comma separated file extensions, e.g. "*.txt,*.xls"
	private String fileExtensions;

	@IsParameter(defaultValue = "Any")
	//comma separated file names, e.g. "Rich Text Format,HTML Document,Any"
	private String fileExtensionNames;

	@IsParameter(defaultValue = "true")
	private Boolean showOpenButton;

	@IsParameter(defaultValue = "true")
	private Boolean validatePath;

	/**
	 * Container
	 */
	private Composite container;

	/**
	 * Container for enabled check box and label
	 */
	private Composite checkBoxAndLabelContainer;

	/**
	 * If this is true, an additional enabled check box will be shown
	 */
	private boolean showEnabledCheckBox = false;

	/**
	 * The label control
	 */
	private Label labelComposite;

	/**
	 * Sub container for rest
	 */
	private Composite subContainer;

	/**
	 * The select button
	 */
	private Label selectButton;

	/**
	 * The open button
	 */
	private Label openButton;

	/**
	 * The text field
	 */
	private Text textField = null;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public FilePath(String name) {
		super(name);
		label = name;
	}

	/**
	 * Copy constructor
	 *
	 * @param filePathToCopy
	 */
	protected FilePath(FilePath filePathToCopy) {
		super(filePathToCopy);
		showEnabledCheckBox = filePathToCopy.showEnabledCheckBox;
		label = filePathToCopy.label;
		defaultValue = filePathToCopy.defaultValue;
		tooltip = filePathToCopy.tooltip;
		fileExtensions = filePathToCopy.fileExtensions;
		fileExtensionNames = filePathToCopy.fileExtensionNames;
		showOpenButton = filePathToCopy.showOpenButton;
		validatePath = filePathToCopy.validatePath;
	}

	/**
	 * Constructor with default filePath
	 *
	 * @param name
	 * @param defaultPath
	 */
	public FilePath(String name, String defaultPath) {
		super(name);
		label = name;
		setDefaultValue(defaultPath);
		set(defaultPath);

	}

	/**
	 * Constructor with label and default filePath
	 *
	 * @param name
	 * @param label
	 * @param defaultPath
	 */
	public FilePath(String name, String label, String defaultPath) {
		super(name);
		setLabel(label);
		setDefaultValue(defaultPath);
		set(defaultPath);

	}

	/**
	 * Constructor with label, default filePath and validation flag
	 *
	 * @param name
	 * @param label
	 * @param defaultPath
	 * @param validatePath
	 */
	public FilePath(String name, String label, String defaultPath, Boolean validatePath) {
		super(name);

		setLabel(label);
		setDefaultValue(defaultPath);
		set(defaultPath);
		setValidatePath(validatePath);

	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public FilePath copy() {
		return new FilePath(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("browse.png");
	}

	@Override
	public AbstractAttributeAtom<String> createAttributeAtomControl(Composite parent, Refreshable treeViewerRefreshable) {

		//initialize value at the first call
		if (!isInitialized()) {
			set(defaultValue);
		}

		//toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		container = createVerticalContainer(parent, toolkit);

		checkBoxAndLabelContainer = createHorizontalContainer(container, toolkit);

		//label
		labelComposite = toolkit.createLabel(checkBoxAndLabelContainer, label);

		//sub container for rest (text field and buttons)
		createSubContainer(toolkit);

		//text field
		createTextField(toolkit);

		//select button
		createSelectionButton(toolkit);

		if (showOpenButton) {
			//open button
			openButton = toolkit.createLabel(subContainer, "", SWT.NONE);
			openButton.setEnabled(isEnabled());
			Image runImage = Activator.getImage("run_triangle.png");
			openButton.setImage(runImage);
			openButton.setToolTipText("Open/Run");

			//create action listener for open button
			openButton.addListener(SWT.MouseDown, new Listener() {

				@Override
				public void handleEvent(org.eclipse.swt.widgets.Event event) {
					String filePath = get();
					FileHelper.openFile(filePath);
				}
			});

		}

		return this;
	}

	@SuppressWarnings("checkstyle:magicnumber")
	private void createSubContainer(FormToolkit toolkit) {
		subContainer = toolkit.createComposite(container);
		int numberOfColumns = 2;
		if (showOpenButton) {
			numberOfColumns = 3;
		}
		GridLayout gridLayout = new GridLayout(numberOfColumns, false);
		gridLayout.horizontalSpacing = 10;
		subContainer.setLayout(gridLayout);

		//create grid data to use all horizontal space
		GridData fillHorizontal = new GridData();
		fillHorizontal.grabExcessHorizontalSpace = true;
		fillHorizontal.horizontalAlignment = GridData.FILL;

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
		//initialize value
		refreshAttributeAtomControl();

		//create error decoration for text field
		String errorMessage = "Invalid file path";
		final TextFieldErrorDecoration errorDecoration = new TextFieldErrorDecoration(
				textField,
				errorMessage,
				container);

		//path validation

		textField.addModifyListener(new ModifyListener() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void modifyText(ModifyEvent e) {

				//get current caret position
				int caretPosition = textField.getCaretPosition();

				String text = textField.getText();
				if (validatePath) {
					validateFilePath(errorDecoration, text);
				}
				set(text);

				//restore caret position
				textField.setSelection(caretPosition);

				triggerModificationListeners();

			}

		});

		if (validatePath) {
			validateFilePath(errorDecoration, get());
		}
	}

	private void createSelectionButton(FormToolkit toolkit) {
		selectButton = toolkit.createLabel(subContainer, "", SWT.PUSH);
		selectButton.setEnabled(isEnabled());
		Image browseImage = Activator.getImage("browse.png");
		selectButton.setImage(browseImage);
		selectButton.setToolTipText("Select file path");

		//create action listener for select button
		final String[] extensionsArray = fileExtensions.split(",");
		final String[] extensionNamesArray = fileExtensionNames.split(",");

		selectButton.addListener(SWT.MouseDown, new Listener() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.MULTI);

				fileDialog.setFilterPath(defaultValue);

				fileDialog.setFilterExtensions(extensionsArray);
				fileDialog.setFilterNames(extensionNamesArray);

				String firstFile = fileDialog.open();

				if (firstFile != null) {
					defaultValue = fileDialog.getFilterPath();
					set(firstFile);
					textField.setText(get());
				}
			}
		});
	}

	/**
	 * Validates the file path
	 *
	 * @param errorDecoration
	 * @param text
	 */
	private void validateFilePath(final TextFieldErrorDecoration errorDecoration, String text) {

		boolean isValid = FileHelper.isValidFilePath(text);
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
		this.refreshAttributeAtomControl();

	}

	@Override
	public void refreshAttributeAtomControl() {
		if (isAvailable(textField)) {
			String value = get();
			if (!textField.getText().equals(value)) {
				textField.setText(get());
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
	 * @param defaultFilePath
	 */
	public void setDefaultValue(String defaultFilePath) {
		this.defaultValue = defaultFilePath;
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
	 * Get file extensions as comma separated list, e.g. '*.txt, *.xml'
	 *
	 * @return the extensions
	 */
	public String getFileExtensions() {
		return fileExtensions;
	}

	/**
	 * Set file extensions as comma separated list, e.g. '*.txt, *.xml'
	 *
	 * @param extensions
	 *            the extensions to set
	 */
	public void setFileExtensions(String extensions) {
		this.fileExtensions = extensions;
	}

	/**
	 * Get file extension names as comma separated list, e.g. 'TestFile, ImageFile'
	 *
	 * @return the extensionNames
	 */
	public String getFileExtensionNames() {
		return fileExtensionNames;
	}

	/**
	 * Set file extension names as comma separated list, e.g. 'TestFile, ImageFile'
	 *
	 * @param extensionNames
	 *            the extensionNames to set
	 */
	public void setFileExtensionNames(String extensionNames) {
		this.fileExtensionNames = extensionNames;
	}

	/**
	 * If set to false the file path is not validated/no error decoration is shown
	 *
	 * @param validatePath
	 */
	public void setValidatePath(Boolean validatePath) {
		this.validatePath = validatePath;

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
