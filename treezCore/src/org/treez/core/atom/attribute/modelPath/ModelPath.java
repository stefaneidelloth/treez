package org.treez.core.atom.attribute.modelPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.AbstractStringAttributeAtom;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.path.FilterDelegate;
import org.treez.core.path.ModelPathSelector;
import org.treez.core.swt.CustomLabel;
import org.treez.core.utils.Utils;

/**
 * Allows the user to choose a model path
 */
public class ModelPath extends AbstractStringAttributeAtom<ModelPath> {

	private static final Logger LOG = LogManager.getLogger(ModelPath.class);

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
	private String targetClassNames;

	@IsParameter(defaultValue = "false")
	private boolean hasToBeEnabled = false;

	private Composite contentContainer;

	private Composite subContainer;

	private CustomLabel labelComposite;

	private FilterDelegate filterDelegate = null;

	/**
	 * If a relative root is present, this label shows its path
	 */
	private CustomLabel rootLabel;

	private Text textField = null;

	/**
	 * The path selection button
	 */
	private Button button;

	/**
	 * The combo box (as alternative to the text field and path selection button)
	 */
	private Combo combo = null;

	/**
	 * A parent atom that can be passed to the constructor to use an alternative entry point for the path navigation. If
	 * this parentAtom is not null, the root of that parent atom will be used instead of the root of this AttributeAtom
	 */
	private AbstractAtom<?> modelEntryAtom = null;

	/**
	 * If this relative root is not null, it will be used as starting atom for the model path selection. The text field
	 * will display the relative path instead of the absolute path, e.g.
	 *
	 * <pre>
	 * realtiveRoot = null
	 * =>root.models.genericModel.xVariable
	 * relativeRoot = root.models
	 * => genericModel.xVariable
	 * </pre>
	 */
	private AbstractAtom<?> relativeRoot = null;

	/**
	 * A parent model path that provides a relative root
	 */
	private ModelPath parentModelPath = null;

	//#end region

	//#region CONSTRUCTORS

	public ModelPath(String name) {
		super(name);
		setLabel(name);
	}

	public ModelPath(String name, AbstractAtom<?> modelEntryAtom) {
		super(name);
		setLabel(name);
		setModelEntryAtom(modelEntryAtom);
	}

	public ModelPath(String name, ModelPathSelectionType selectionType) {
		super(name);
		setLabel(label);
		setSelectionType(selectionType);
	}

	public ModelPath(String name, AbstractAtom<?> modelEntryAtom, ModelPathSelectionType selectionType) {
		super(name);
		setLabel(label);
		setSelectionType(selectionType);
		setModelEntryAtom(modelEntryAtom);
	}

	public ModelPath(String name, ModelPath parentModelPath, Class<?> atomType) {
		super(name);
		setLabel(name);
		setSelectionType(selectionType);
		List<String> targetClassNameList = Arrays.asList(atomType.getName());
		setTargetClassNames(targetClassNameList);
		this.parentModelPath = parentModelPath;
		this.modelEntryAtom = parentModelPath.modelEntryAtom;

		//try to get abstract model with current path value from rootModelPath
		updateRelativeRootAtom();

		//add listener to update relative root if value of parent ModelPath
		//changes
		parentModelPath.addModificationConsumer("updateRelativeRootAtom", () -> updateRelativeRootAtom());

	}

	public ModelPath(String name, ModelPath parentModelPath, Class<?>[] atomTypes) {
		super(name);
		setLabel(name);
		setSelectionType(selectionType);

		List<String> targetClassNameList = new ArrayList<>();
		for (Class<?> atomType : atomTypes) {
			targetClassNameList.add(atomType.getName());
		}
		setTargetClassNames(targetClassNameList);

		this.parentModelPath = parentModelPath;
		this.modelEntryAtom = parentModelPath.modelEntryAtom;

		//try to get abstract model with current path value from rootModelPath
		updateRelativeRootAtom();

		//add listener to update relative root if value of parent ModelPath
		//changes
		parentModelPath.addModificationConsumer("updateRelativeRootAtom", () -> updateRelativeRootAtom());

	}

	public ModelPath(String name, ModelPathSelectionType selectionType, String defaultPath) {
		super(name);
		setLabel(label);
		setDefaultValue(defaultPath);
		set(defaultPath);
		setSelectionType(selectionType);
	}

	public ModelPath(
			String name,
			AbstractAtom<?> modelEntryAtom,
			ModelPathSelectionType selectionType,
			String defaultPath) {
		super(name);
		setLabel(label);
		setDefaultValue(defaultPath);
		set(defaultPath);
		setSelectionType(selectionType);
		setModelEntryAtom(modelEntryAtom);
	}

	public ModelPath(
			String name,
			String defaultPath,
			Class<?> atomType,
			ModelPathSelectionType selectionType,
			AbstractAtom<?> modelEntryAtom,
			boolean hasToBeEnabled) {
		super(name);
		setLabel(label);
		setDefaultValue(defaultPath);
		set(defaultPath);
		setTargetClassName(atomType.getName());
		setSelectionType(selectionType);
		setModelEntryAtom(modelEntryAtom);
		setHasToBeEnabled(hasToBeEnabled);
	}

	public <T> ModelPath(
			String name,
			String defaultPath,
			Class<T> atomType,
			ModelPathSelectionType selectionType,
			AbstractAtom<?> modelEntryAtom,
			FilterDelegate filterDelegate) {
		super(name);
		setLabel(label);
		setDefaultValue(defaultPath);
		set(defaultPath);
		setTargetClassName(atomType.getName());
		setSelectionType(selectionType);
		setModelEntryAtom(modelEntryAtom);
		setFilterDelegate(filterDelegate);
	}

	public ModelPath(
			String name,
			String defaultPath,
			Class<?>[] atomTypes,
			ModelPathSelectionType selectionType,
			AbstractAtom<?> modelEntryAtom,
			boolean hasToBeEnabled) {
		super(name);
		setLabel(label);
		setDefaultValue(defaultPath);
		set(defaultPath);

		List<String> targetClassNameList = new ArrayList<>();
		for (Class<?> atomType : atomTypes) {
			targetClassNameList.add(atomType.getName());
		}
		setTargetClassNames(targetClassNameList);

		setSelectionType(selectionType);
		setModelEntryAtom(modelEntryAtom);
		setHasToBeEnabled(hasToBeEnabled);
	}

	public ModelPath(
			String name,
			String label,
			String defaultPath,
			Class<?> atomType,
			ModelPathSelectionType selectionType,
			AbstractAtom<?> modelEntryAtom,
			AbstractAtom<?> relativeRoot) {
		super(name);
		setLabel(label);
		setDefaultValue(defaultPath);
		set(defaultPath);
		setTargetClassName(atomType.getName());
		setSelectionType(selectionType);
		setModelEntryAtom(modelEntryAtom);
		setModelRelativeRoot(relativeRoot);
	}

	/**
	 * Copy constructor
	 */
	private ModelPath(ModelPath modelPathToCopy) {
		super(modelPathToCopy);
		label = modelPathToCopy.label;
		selectionType = modelPathToCopy.selectionType;
		defaultValue = modelPathToCopy.defaultValue;
		tooltip = modelPathToCopy.tooltip;
		targetClassNames = modelPathToCopy.targetClassNames;
		hasToBeEnabled = modelPathToCopy.hasToBeEnabled;

		//TODO: replace with path to be able to copy them here
		modelEntryAtom = null;
		relativeRoot = null;

	}

	//#end region

	//#region METHODS

	@Override
	public ModelPath getThis() {
		return this;
	}

	@Override
	public ModelPath copy() {
		return new ModelPath(this);
	}

	@Override
	public
			AbstractStringAttributeAtom<ModelPath>
			createAttributeAtomControl(Composite parent, FocusChangingRefreshable treeViewerRefreshable) {

		//initialize value at the first call
		if (!isInitialized()) {
			String currentDefaultValue = getDefaultValue();
			set(currentDefaultValue);
		}

		//toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		//create content composite for label and check box
		contentContainer = toolkit.createComposite(parent);

		//check label length
		boolean useExtraLine = label.length() > CHARACTER_LENGTH_LIMIT;

		//create container layout
		int marginWidth = 0;
		if (useExtraLine) {
			createLayoutForIndividualLines(contentContainer, marginWidth);
		} else {
			createLayoutForSingleLine(contentContainer, marginWidth);
		}

		//create label
		createLabel(toolkit);

		//relative root label
		rootLabel = new CustomLabel(toolkit, contentContainer, "");
		final int grayValue = 190;
		rootLabel.setForeground(new Color(Display.getCurrent(), grayValue, grayValue, grayValue));
		rootLabel.hide();

		//sub container for rest (tree selector or combo box selector)
		subContainer = createSubContainer(toolkit, contentContainer);

		switch (selectionType) {
		case FLAT:
			reCreateComboBoxSelector(subContainer);
			break;
		case TREE:
			reCreateTextFieldAndButtonForTreeSelector(subContainer);
			break;
		default:
			throw new IllegalStateException("The selection type '" + selectionType + "' is not yet implemented.");
		}

		return this;
	}

	private void createLabel(FormToolkit toolkit) {
		labelComposite = new CustomLabel(toolkit, contentContainer, label);
		final int prefferedLabelWidth = 80;
		labelComposite.setPrefferedWidth(prefferedLabelWidth);
	}

	@SuppressWarnings("checkstyle:magicnumber")
	private static Composite createSubContainer(FormToolkit toolkit, Composite container) {
		Composite subContainer = toolkit.createComposite(container);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.horizontalSpacing = 5;
		gridLayout.marginWidth = 0;
		subContainer.setLayout(gridLayout);

		GridData subFillHorizontal = new GridData();
		subFillHorizontal.grabExcessHorizontalSpace = true;
		subFillHorizontal.horizontalAlignment = GridData.FILL;
		subContainer.setLayoutData(subFillHorizontal);
		return subContainer;
	}

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
		availableTargetPaths.add(0, ""); //entry for empty selection
		String[] availableTargetPathsArray = Utils.stringListToArray(availableTargetPaths);

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
					String currentValue = combo.getItem(combo.getSelectionIndex());
					if (relativeRoot != null) {
						currentValue = relativeToAbsolutePath(currentValue);
					}
					set(currentValue); //(will also update modification listeners)

				}
			});
		}

		//initialize combo box
		if (isAvailable(combo)) {
			combo.setEnabled(isEnabled());
			combo.setVisible(isVisible());
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
		AbstractAtom<?> model = null;
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
			availableTargetPaths = ModelPathSelector.getAvailableTargetPaths(model, targetClassNames, hasToBeEnabled,
					filterDelegate);
			if (availableTargetPaths == null) {
				throw new IllegalStateException(
						"No model paths are available for the target class(es) '" + targetClassNames + "' .");
			}

			//convert paths to relative paths
			if (relativeRoot != null) {
				availableTargetPaths = absoluteToRelativePaths(availableTargetPaths);
			}
		}
		return availableTargetPaths;
	}

	/**
	 * Tries to determine the current value and select it in the combo box. If currently no value is set no selection
	 * will be made.
	 *
	 * @param availableTargetPaths
	 */
	private void tryToSelectCurrentValue(List<String> availableTargetPaths) {
		String currentModelPath = get();
		boolean valueIsGiven = currentModelPath != null && !currentModelPath.isEmpty();
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
			if (currentIndex < 0) {
				String message = "Could not select model path '" + currentModelPath + "'.";
				LOG.warn(message);
			}
		}
	}

	/**
	 * Converts the given absolute model paths to relative model paths. If a path does not include the relative root
	 * path it is filtered out.
	 *
	 * @param absolutePaths
	 * @return
	 */
	private List<String> absoluteToRelativePaths(List<String> absolutePaths) {
		Objects.requireNonNull(relativeRoot, "Relative root must not be null when calling this method.");

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
		String relativeRootPath = relativeRoot.createTreeNodeAdaption().getTreePath();
		int startIndex = relativeRootPath.length() + 1;
		int pathLength = absolutePath.length();
		if (pathLength <= startIndex) {
			String message = "Could not convert absolute path '" + absolutePath
					+ "' because it is too short to include the relative root path '" + relativeRootPath + "'";
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
		Objects.requireNonNull(relativeRoot, "Relative root must not be null when calling this method.");

		String relativeRootPath = relativeRoot.createTreeNodeAdaption().getTreePath();
		String absolutePath = relativeRootPath + "." + path;
		return absolutePath;
	}

	/**
	 * Allows the user to select the path with a tree selector
	 *
	 * @param subContainer
	 */
	private void reCreateTextFieldAndButtonForTreeSelector(Composite subContainer) {

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
		textField.setVisible(isVisible());

		//create button if it does not yet exist
		if (!isAvailable(button)) {
			button = toolkit.createButton(subContainer, "Select", SWT.PUSH);

			button.addListener(SWT.Selection, new Listener() {

				@SuppressWarnings("synthetic-access")
				@Override
				public void handleEvent(org.eclipse.swt.widgets.Event event) {

					//get root to use it as model
					AbstractAtom<?> model = getModel();

					//select path
					String modelPath = ModelPathSelector.selectTreePath(model, targetClassNames, defaultValue);

					//update default value and value
					updateDefaultValueAndValue(modelPath);
				}

			});
		}

		button.setEnabled(isEnabled());
		button.setVisible(isVisible());

	}

	private AbstractAtom<?> getModel() {
		AbstractAtom<?> model;
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
			triggerListeners();
		}
	}

	@Override
	public void refreshAttributeAtomControl() {

		if (contentContainer != null) {
			//recreate components if they already exist to refresh them
			switch (selectionType) {
			case FLAT:
				reCreateComboBoxSelector(contentContainer);
				break;
			case TREE:
				reCreateTextFieldAndButtonForTreeSelector(contentContainer);
				break;
			default:
				throw new IllegalStateException("The selection type '" + selectionType + "' is not yet implemented.");
			}
		}

	}

	private void refereshAttributeControlValues() {
		String value = get();
		if (value != null) {

			//update relative root label
			if (isAvailable(rootLabel)) {

				if (relativeRoot != null) {
					String rootPath = relativeRoot.createTreeNodeAdaption().getTreePath();
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
	public ModelPath setBackgroundColor(org.eclipse.swt.graphics.Color backgroundColor) {
		throw new IllegalStateException("Not yet implemented");

	}

	/**
	 * Updates the relative root of this ModelPath with the given parent ModelPath
	 */
	public void updateRelativeRootAtom() {
		if (parentModelPath != null) {
			String parentPath = parentModelPath.get();
			if (parentPath != null && !parentPath.isEmpty()) {

				AbstractAtom<?> relativeRootAtom = null;
				try {
					relativeRootAtom = modelEntryAtom.getChildFromRoot(parentPath);
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
		if (path == null || path.equals("")) {
			return null;
		} else {
			return path;
		}

	}

	@Override
	public ModelPath set(String value) {
		if (value != attributeValue) {
			attributeValue = value;
			setInitialized();
			refreshAttributeAtomControl();
			triggerListeners();
		}
		return getThis();
	}

	/**
	 * If a model entry atom is specified, this method returns the relative path. If no model entry atom is specified,
	 * this method will throw an exception.
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

	public String getLabel() {
		return label;
	}

	public ModelPath setLabel(String label) {
		this.label = label;
		return getThis();
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	public ModelPath setDefaultValue(String defaultFilePath) {
		this.defaultValue = defaultFilePath;
		return getThis();
	}

	public String getTooltip() {
		return tooltip;
	}

	public ModelPath setTooltip(String tooltip) {
		this.tooltip = tooltip;
		return getThis();
	}

	public ModelPathSelectionType getSelectionType() {
		return selectionType;
	}

	public ModelPath setSelectionType(ModelPathSelectionType selectionType) {
		this.selectionType = selectionType;
		return getThis();
	}

	/**
	 * Sets the target class names that are used to filter the items that can be selected in the model tree
	 *
	 * @param targetClassNames
	 */
	public ModelPath setTargetClassNames(List<String> targetClassNames) {
		this.targetClassNames = String.join(",", targetClassNames);
		return getThis();
	}

	private ModelPath setTargetClassName(String singleTargetClassName) {
		this.targetClassNames = singleTargetClassName;
		return getThis();
	}

	private ModelPath setModelEntryAtom(AbstractAtom<?> modelEntryAtom) {
		this.modelEntryAtom = modelEntryAtom;
		return getThis();
	}

	public ModelPath setModelRelativeRoot(AbstractAtom<?> relativeRoot) {
		this.relativeRoot = relativeRoot;
		return getThis();
	}

	public ModelPath setHasToBeEnabled(boolean state) {
		this.hasToBeEnabled = state;
		return getThis();
	}

	@Override
	public ModelPath setEnabled(boolean state) {
		super.setEnabled(state);
		if (isAvailable(combo)) {
			combo.setEnabled(state);
		}
		return getThis();
	}

	private void setFilterDelegate(FilterDelegate filterDelegate) {
		this.filterDelegate = filterDelegate;
	}

	//#end region

}
