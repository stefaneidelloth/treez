package org.treez.core.atom.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.path.ModelPathSelector;
import org.treez.core.swt.CustomLabel;
import org.treez.core.utils.Utils;

/**
 * Allows the user to choose a model path
 */
public class ModelPath extends AbstractAttributeAtom<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(ModelPath.class);

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "File path:")
	private String label;

	@IsParameter(defaultValue = "FLAT")
	private ModelPathSelectionType selectionType;

	@IsParameter(defaultValue = "root")
	private String defaultValue;

	@IsParameter(defaultValue = "")
	private String tooltip;

	@IsParameter(defaultValue = "org.treez.core.atom.base.AbstractAtom")
	private String targetClassName;

	@IsParameter(defaultValue = "false")
	private boolean hasToBeEnabled;

	/**
	 * Sub container for (text field and button) or combo box
	 */
	private Composite subContainer;

	/**
	 * If a relative root is present, this label shows its path
	 */
	private CustomLabel rootLabel;

	/**
	 * The text field
	 */
	private Text textField = null;

	/**
	 * The path selection button
	 */
	private Button button;

	/**
	 * The combo box (as alternative to the text field and path selection
	 * button)
	 */
	private Combo combo = null;

	/**
	 * A parent atom that can be passed to the constructor to use an alternative
	 * entry point for the path navigation. If this parentAtom is not null, the
	 * root of that parent atom will be used instead of the root of this
	 * AttributeAtom
	 */
	private AbstractAtom modelEntryAtom = null;

	/**
	 * If this relative root is not null, it will be used as starting atom for
	 * the model path selection. The text field will display the relative path
	 * instead of the absolute path, e.g.
	 *
	 * <pre>
	 * realtiveRoot = null
	 * =>root.models.genericModel.xVariable
	 * relativeRoot = root.models
	 * => genericModel.xVariable
	 * </pre>
	 */
	private AbstractAtom relativeRoot = null;

	/**
	 * A parent model path that provides a relative root
	 */
	private ModelPath parentModelPath = null;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public ModelPath(String name) {
		super(name);
		setLabel(name);
	}

	/**
	 * Copy constructor
	 *
	 * @param modelPathToCopy
	 */
	private ModelPath(ModelPath modelPathToCopy) {
		super(modelPathToCopy);
		label = modelPathToCopy.label;
		selectionType = modelPathToCopy.selectionType;
		defaultValue = modelPathToCopy.defaultValue;
		tooltip = modelPathToCopy.tooltip;
		targetClassName = modelPathToCopy.targetClassName;
		hasToBeEnabled = modelPathToCopy.hasToBeEnabled;

		//TODO: replace with path to be able to copy them here
		modelEntryAtom = null;
		relativeRoot = null;

	}

	/**
	 * Constructor with alternative model entry point
	 *
	 * @param name
	 * @param modelEntryAtom
	 *            : used as entry point for the tree navigation
	 */
	public ModelPath(String name, AbstractAtom modelEntryAtom) {
		super(name);
		setLabel(name);
		setModelEntryAtom(modelEntryAtom);
	}

	/**
	 * Constructor with selection type
	 *
	 * @param name
	 * @param selectionType
	 */
	public ModelPath(String name, ModelPathSelectionType selectionType) {
		super(name);
		setLabel(label);
		setSelectionType(selectionType);
	}

	/**
	 * Constructor with selection type and alternative model entry point
	 *
	 * @param name
	 * @param modelEntryAtom
	 * @param selectionType
	 */
	public ModelPath(String name, AbstractAtom modelEntryAtom,
			ModelPathSelectionType selectionType) {
		super(name);
		setLabel(label);
		setSelectionType(selectionType);
		setModelEntryAtom(modelEntryAtom);
	}

	/**
	 * Constructor that uses another ModelPath as root.
	 *
	 * @param name
	 * @param atomType
	 * @param parentModelPath
	 */
	public ModelPath(String name, ModelPath parentModelPath,
			Class<?> atomType) {
		super(name);
		setLabel(name);
		setSelectionType(selectionType);
		setTargetClassName(atomType.getName());
		this.parentModelPath = parentModelPath;
		this.modelEntryAtom = parentModelPath.modelEntryAtom;

		//try to get abstract model with current path value from rootModelPath
		updateRelativeRootAtom();

		//add listener to update relative root if value of parent ModelPath
		//changes
		parentModelPath.addModifyListener((event) -> updateRelativeRootAtom());

	}

	/**
	 * Constructor with selection type and default model path
	 *
	 * @param name
	 * @param selectionType
	 * @param defaultPath
	 */
	public ModelPath(String name, ModelPathSelectionType selectionType,
			String defaultPath) {
		super(name);
		setLabel(label);
		setDefaultValue(defaultPath);
		set(defaultPath);
		setSelectionType(selectionType);
	}

	/**
	 * Constructor with selection type, alternative model entry point and
	 * default model path
	 *
	 * @param name
	 * @param modelEntryAtom
	 * @param selectionType
	 * @param defaultPath
	 */
	public ModelPath(String name, AbstractAtom modelEntryAtom,
			ModelPathSelectionType selectionType, String defaultPath) {
		super(name);
		setLabel(label);
		setDefaultValue(defaultPath);
		set(defaultPath);
		setSelectionType(selectionType);
		setModelEntryAtom(modelEntryAtom);
	}

	/**
	 * Constructor
	 *
	 * @param name
	 * @param label
	 * @param defaultPath
	 * @param atomType
	 * @param selectionType
	 * @param modelEntryAtom
	 */
	public ModelPath(String name, String label, String defaultPath,
			Class<?> atomType, ModelPathSelectionType selectionType,
			AbstractAtom modelEntryAtom, boolean hasToBeEnabled) {
		super(name);
		setLabel(label);
		setDefaultValue(defaultPath);
		set(defaultPath);
		setTargetClassName(atomType.getName());
		setSelectionType(selectionType);
		setModelEntryAtom(modelEntryAtom);
		setHasToBeEnabled(hasToBeEnabled);
	}

	/**
	 * Constructor
	 *
	 * @param name
	 * @param label
	 * @param defaultPath
	 * @param atomType
	 * @param selectionType
	 * @param modelEntryAtom
	 * @param relativeRoot
	 */
	public ModelPath(String name, String label, String defaultPath,
			Class<?> atomType, ModelPathSelectionType selectionType,
			AbstractAtom modelEntryAtom, AbstractAtom relativeRoot) {
		super(name);
		setLabel(label);
		setDefaultValue(defaultPath);
		set(defaultPath);
		setTargetClassName(atomType.getName());
		setSelectionType(selectionType);
		setModelEntryAtom(modelEntryAtom);
		setModelRelativeRoot(relativeRoot);
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public ModelPath copy() {
		return new ModelPath(this);
	}

	//#end region

	@Override
	public AbstractAttributeAtom<String> createAttributeAtomControl(
			Composite parent, Refreshable treeViewerRefreshable) {

		//initialize value at the first call
		if (!isInitialized()) {
			String currentDefaultValue = getDefaultValue();
			set(currentDefaultValue);
		}

		//toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		//container for label and rest
		Composite container = createContainer(parent, toolkit);

		//label
		@SuppressWarnings("unused")
		CustomLabel labelComposite = new CustomLabel(toolkit, container, label);

		//relative root label
		rootLabel = new CustomLabel(toolkit, container, "");
		final int grayValue = 190;
		rootLabel.setForeground(new Color(Display.getCurrent(), grayValue,
				grayValue, grayValue));
		rootLabel.hide();

		//sub container for rest (tree selector or combo box selector)
		subContainer = createSubContainer(toolkit, container);

		switch (selectionType) {
			case FLAT :
				reCreateComboBoxSelector(subContainer);
				break;
			case TREE :
				reCreateTextFieldAndButtonForTreeSelector(subContainer);
				break;
			default :
				throw new IllegalStateException("The selection type '"
						+ selectionType + "' is not yet implemented.");
		}

		return this;
	}

	@SuppressWarnings("checkstyle:magicnumber")
	private static Composite createSubContainer(FormToolkit toolkit,
			Composite container) {
		Composite subContainer = toolkit.createComposite(container);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 5;
		gridLayout.marginWidth = 0;
		subContainer.setLayout(gridLayout);

		GridData subFillHorizontal = new GridData();
		subFillHorizontal.grabExcessHorizontalSpace = true;
		subFillHorizontal.horizontalAlignment = GridData.FILL;
		subContainer.setLayoutData(subFillHorizontal);
		return subContainer;
	}

	@SuppressWarnings("checkstyle:magicnumber")
	private static Composite createContainer(Composite parent,
			FormToolkit toolkit) {
		Composite container = toolkit.createComposite(parent);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 1;
		gridLayout.marginWidth = 0;

		container.setLayout(gridLayout);

		//create grid data to use all horizontal space
		GridData fillHorizontal = new GridData();
		fillHorizontal.grabExcessHorizontalSpace = true;
		fillHorizontal.horizontalAlignment = GridData.FILL;
		container.setLayoutData(fillHorizontal);
		return container;
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("root.png");
	}

	/**
	 * (Re-)creates a combo box that allows the user to select the path
	 *
	 * @param subContainer
	 */
	private void reCreateComboBoxSelector(Composite subContainer) {

		Objects.requireNonNull(subContainer, "Sub container must not be null");

		//get available target paths
		List<String> availableTargetPaths = getAvailableTargetPaths();
		String[] availableTargetPathsArray = Utils
				.stringListToArray(availableTargetPaths);

		//create combo box if it does not already exist
		if (!isAvailable(combo)) {
			combo = new Combo(subContainer, SWT.READ_ONLY);

			//create grid data to use all horizontal space
			GridData fillHorizontal = new GridData();
			fillHorizontal.grabExcessHorizontalSpace = true;
			fillHorizontal.horizontalAlignment = GridData.FILL;

			//set grid data
			combo.setLayoutData(fillHorizontal);

			//create action listener
			combo.addSelectionListener(new SelectionAdapter() {

				@SuppressWarnings("synthetic-access")
				@Override
				public void widgetSelected(SelectionEvent e) {
					//update value
					String currentValue = combo
							.getItem(combo.getSelectionIndex());
					if (relativeRoot != null) {
						currentValue = relativeToAbsolutePath(currentValue);
					}
					set(currentValue);

					//trigger modification listeners
					triggerModificationListeners();
				}
			});
		}

		if (isAvailable(combo)) {
			combo.setEnabled(isEnabled());
			combo.setItems(availableTargetPathsArray);
			tryToSelectCurrentValue(availableTargetPaths);
			refereshAttributeControlValues();
		}

	}

	/**
	 * Returns the available target paths
	 *
	 * @return
	 */
	private List<String> getAvailableTargetPaths() {
		//get root to use it as model
		AbstractAtom model = null;
		if (modelEntryAtom != null) {
			//use the given modelEntryAtom as entry point
			model = modelEntryAtom.getRoot();
		} else {
			//try to use this atom as entry point
			try {
				model = ModelPath.this.getRoot();
			} catch (IllegalStateException exception) {
				//this atom might not have a parent yet.
				//nothing to do here
			}
		}

		//get available target paths
		List<String> availableTargetPaths = new ArrayList<>();
		if (model != null) {
			availableTargetPaths = ModelPathSelector.getAvailableTargetPaths(
					model, targetClassName, hasToBeEnabled);
			if (availableTargetPaths == null) {
				throw new IllegalStateException(
						"No model paths are available for the target class '"
								+ targetClassName + "' .");
			}

			//convert paths to relative paths
			if (relativeRoot != null) {
				availableTargetPaths = absoluteToRelativePaths(
						availableTargetPaths);
			}
		}
		return availableTargetPaths;
	}

	/**
	 * Tries to determine the current value and select it in the combo box. If
	 * currently no value is set no selection will be made.
	 *
	 * @param availableTargetPaths
	 */
	private void tryToSelectCurrentValue(List<String> availableTargetPaths) {
		String currentModelPath = get();
		boolean valueIsGiven = currentModelPath != null
				&& !currentModelPath.isEmpty();
		if (valueIsGiven) {
			if (relativeRoot != null) {
				try {
					currentModelPath = absoluteToRelativePath(currentModelPath);
				} catch (IllegalArgumentException exception) {
					//nothing to do here
				}
			}
			int currentIndex = availableTargetPaths.indexOf(currentModelPath);
			combo.select(currentIndex);
		}
	}

	/**
	 * Converts the given absolute model paths to relative model paths. If a
	 * path does not include the relative root path it is filtered out.
	 *
	 * @param absolutePaths
	 * @return
	 */
	private List<String> absoluteToRelativePaths(List<String> absolutePaths) {
		Objects.requireNonNull(relativeRoot,
				"Relative root must not be null when calling this method.");

		List<String> relativePaths = new ArrayList<>();
		for (String absolutePath : absolutePaths) {

			String relativePath = null;
			try {
				relativePath = absoluteToRelativePath(absolutePath);
			} catch (IllegalArgumentException exception) {
				//nothing to do here; invalid path will be filtered out
			}
			if (relativePath != null) {
				relativePaths.add(relativePath);
			}
		}

		return relativePaths;
	}

	/**
	 * Converts the given absolute model path to a relative model path
	 *
	 * @param absolutePath
	 * @return
	 */
	private String absoluteToRelativePath(String absolutePath) {
		String relativeRootPath = relativeRoot.createTreeNodeAdaption()
				.getTreePath();
		int startIndex = relativeRootPath.length() + 1;
		int pathLength = absolutePath.length();
		if (pathLength <= startIndex) {
			String message = "Could not convert absolute path '" + absolutePath
					+ "' because it is too short to include the relative root path '"
					+ relativeRootPath + "'";
			throw new IllegalArgumentException(message);
		}
		String relativePath = absolutePath.substring(startIndex, pathLength);
		return relativePath;
	}

	/**
	 * Converts the given relative model path to an absolute model path
	 *
	 * @param path
	 * @return
	 */
	private String relativeToAbsolutePath(String path) {
		Objects.requireNonNull(relativeRoot,
				"Relative root must not be null when calling this method.");

		String relativeRootPath = relativeRoot.createTreeNodeAdaption()
				.getTreePath();
		String absolutePath = relativeRootPath + "." + path;
		return absolutePath;
	}

	/**
	 * Allows the user to select the path with a tree selector
	 *
	 * @param subContainer
	 */
	private void reCreateTextFieldAndButtonForTreeSelector(
			Composite subContainer) {

		Objects.requireNonNull(subContainer, "Sub container must not be null");

		//toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		//create text field if it does not yet exist
		if (!isAvailable(textField)) {
			textField = toolkit.createText(subContainer, get());

			//create grid data to use all horizontal space
			GridData fillHorizontal = new GridData();
			fillHorizontal.grabExcessHorizontalSpace = true;
			fillHorizontal.horizontalAlignment = GridData.FILL;

			//set grid data
			textField.setLayoutData(fillHorizontal);
		}

		textField.setEnabled(isEnabled());

		//create button if it does not yet exist
		if (!isAvailable(button)) {
			button = toolkit.createButton(subContainer, "Select", SWT.PUSH);

			button.addListener(SWT.Selection, new Listener() {

				@SuppressWarnings("synthetic-access")
				@Override
				public void handleEvent(org.eclipse.swt.widgets.Event event) {

					//get root to use it as model
					AbstractAtom model = getModel();

					//select path
					String modelPath = ModelPathSelector.selectTreePath(model,
							targetClassName, defaultValue);

					//update default value and value
					updateDefaultValueAndValue(modelPath);
				}

			});
		}

		button.setEnabled(isEnabled());

	}

	private AbstractAtom getModel() {
		AbstractAtom model;
		if (modelEntryAtom != null) {
			//use the given modelEntryAtom as entry point
			model = modelEntryAtom.getRoot();
		} else {
			//use this atom itself as entry point
			model = ModelPath.this.getRoot();
		}
		return model;
	}

	private void updateDefaultValueAndValue(String modelPath) {
		if (modelPath != null) {
			defaultValue = modelPath;
			set(modelPath);
			textField.setText(get());

			//trigger modification listeners
			triggerModificationListeners();
		}
	}

	@Override
	public void refreshAttributeAtomControl() {

		if (subContainer != null) {
			//recreate components if they already exist to refresh them
			switch (selectionType) {
				case FLAT :
					reCreateComboBoxSelector(subContainer);
					break;
				case TREE :
					reCreateTextFieldAndButtonForTreeSelector(subContainer);
					break;
				default :
					throw new IllegalStateException("The selection type '"
							+ selectionType + "' is not yet implemented.");
			}
		}

	}

	private void refereshAttributeControlValues() {
		String value = get();
		if (value != null) {

			//update relative root label
			if (isAvailable(rootLabel)) {

				if (relativeRoot != null) {
					String rootPath = relativeRoot.createTreeNodeAdaption()
							.getTreePath();
					String relativeRootString = rootPath + ".";
					rootLabel.setText(relativeRootString);
					rootLabel.show();
				} else {
					rootLabel.hide();
				}
			}

			//update text field
			if (isAvailable(textField)) {
				if (!textField.getText().equals(value)) {
					textField.setText(value);
				}
			}

			//update combo
			if (isAvailable(combo)) {
				if (!combo.getText().equals(value)) {
					combo.setText(value);
				}
			}
		}
	}

	@Override
	public void setBackgroundColor(
			org.eclipse.swt.graphics.Color backgroundColor) {
		throw new IllegalStateException("Not yet implemented");

	}

	/**
	 * Updates the relative root of this ModelPath with the given parent
	 * ModelPath
	 *
	 */
	public void updateRelativeRootAtom() {
		if (parentModelPath != null) {
			String parentPath = parentModelPath.get();
			if (parentPath != null && !parentPath.isEmpty()) {

				AbstractAtom relativeRootAtom = null;
				try {
					relativeRootAtom = modelEntryAtom
							.getChildFromRoot(parentPath);
				} catch (IllegalArgumentException exception) {
					//nothing to do here
				}

				if (relativeRootAtom != null) {
					this.setModelRelativeRoot(relativeRootAtom);
					refreshAttributeAtomControl();
				}

			}
		}
	}

	//#end region

	//#region ACCESSORS

	/**
	 * Returns the absolute model path
	 */
	@Override
	public String get() {
		String path = super.get();
		return path;
	}

	/**
	 * Sets the value
	 *
	 * @param value
	 */
	@Override
	public void set(String value) {
		if (value != attributeValue) {
			attributeValue = value;
			setInitialized();
			refreshAttributeAtomControl();
			triggerModificationListeners();
		}
	}

	/**
	 * If a model entry atom is specified, this method returns the relative
	 * path. If no model entry atom is specified, this method will throw an
	 * exception.
	 *
	 * @return
	 */
	public String getRelativeValue() {
		String path = get();
		if (relativeRoot != null) {
			path = absoluteToRelativePath(path);
		} else {
			String message = "A relative root has to be specified with a model entry atom before this method is called.";
			throw new IllegalStateException(message);
		}
		return path;
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
	 * Get selection type
	 *
	 * @return
	 */
	public ModelPathSelectionType getSelectionType() {
		return selectionType;
	}

	/**
	 * Set selection type
	 *
	 * @param selectionType
	 */
	public void setSelectionType(ModelPathSelectionType selectionType) {
		this.selectionType = selectionType;
	}

	/**
	 * Sets the target class name that is used to filter the items that can be
	 * selected in the model tree
	 *
	 * @param targetClassName
	 */
	public void setTargetClassName(String targetClassName) {
		this.targetClassName = targetClassName;
	}

	private void setModelEntryAtom(AbstractAtom modelEntryAtom) {
		this.modelEntryAtom = modelEntryAtom;
	}

	/**
	 * @param relativeRoot
	 */
	public void setModelRelativeRoot(AbstractAtom relativeRoot) {
		this.relativeRoot = relativeRoot;
	}

	/**
	 * @param state
	 */
	public void setHasToBeEnabled(boolean state) {
		this.hasToBeEnabled = state;
	}

	//#end region

}
