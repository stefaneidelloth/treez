package org.treez.core.atom.attribute;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.treez.core.Activator;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.attribute.base.parent.AbstractAttributeContainerAtom;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.base.AtomTreeNodeAdaption;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.atom.variablefield.DoubleVariableField;
import org.treez.core.atom.variablefield.DoubleVariableListField;
import org.treez.core.atom.variablefield.QuantityVariableField;
import org.treez.core.atom.variablefield.QuantityVariableListField;
import org.treez.core.atom.variablefield.VariableField;
import org.treez.core.atom.variablelist.VariableList;
import org.treez.core.attribute.Attribute;
import org.treez.core.data.column.ColumnType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.TreeViewerAction;
import org.treez.core.utils.Utils;

/**
 * An item example
 */
@SuppressWarnings({"checkstyle:classfanoutcomplexity",
		"checkstyle:cyclomaticcomplexity"})
public class Section extends AbstractAttributeContainerAtom {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(Section.class);

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "Section Title")
	private String title;

	@IsParameter(defaultValue = "")
	private String description;

	@IsParameter(defaultValue = "VERTICAL", comboItems = {"VERTICAL",
			"HORIZONTAL"})
	private String layout;

	@IsParameter(defaultValue = "true")
	private boolean expanded;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public Section(String name) {
		super(name);
		title = Utils.firstToUpperCase(name); //this default title might be overridden by explicitly setting the label
	}

	/**
	 * Copy constructor
	 *
	 * @param sectionToCopy
	 */
	private Section(Section sectionToCopy) {
		super(sectionToCopy);
		title = sectionToCopy.title;
		description = sectionToCopy.description;
		layout = sectionToCopy.layout;
		expanded = sectionToCopy.expanded;
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public Section copy() {
		return new Section(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("Section.png");
	}

	/**
	 * Creates the context menu actions
	 *
	 * @return
	 */
	@Override
	protected ArrayList<Object> createContextMenuActions(
			final TreeViewerRefreshable treeViewer) {
		ArrayList<Object> actions = new ArrayList<>();

		//add section
		actions.add(new TreeViewerAction("Add Section",
				Activator.getImage("Section.png"), treeViewer,
				() -> addSection(treeViewer)));

		//add text field
		actions.add(new TreeViewerAction("Add TextField",
				Activator.getImage("TextField.png"), treeViewer,
				() -> addTextField(treeViewer)));

		//add check box
		actions.add(new TreeViewerAction("Add CheckBox",
				Activator.getImage("CheckBox.png"), treeViewer,
				() -> addCheckBox(treeViewer)));

		//add combo box
		actions.add(new TreeViewerAction("Add ComboBox",
				Activator.getImage("ComboBox.png"), treeViewer,
				() -> addComboBox(treeViewer)));

		//add spacer
		actions.add(new TreeViewerAction("Add Spacer",
				Activator.getImage("Spacer.png"), treeViewer,
				() -> addSpacer(treeViewer)));

		//delete
		actions.add(new TreeViewerAction("Delete",
				Activator.getImage(ISharedImages.IMG_TOOL_DELETE), treeViewer,
				() -> createTreeNodeAdaption().delete()));

		return actions;
	}

	//#region CONTROL

	@Override
	public void createAtomControl(Composite parent,
			Refreshable treeViewerRefreshable) {

		SectionControlProvider controlProvider = new SectionControlProvider(
				this, parent, treeViewerRefreshable);
		controlProvider.createAtomControl();

	}

	/**
	 * Adds a new section
	 *
	 * @param treeViewer
	 */
	void addSection(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this,
				"mySection");
		createSection(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	/**
	 * Creates a new section
	 *
	 * @param name
	 * @return
	 */
	public Section createSection(String name) {
		Section section = new Section(name);
		addChild(section);
		return section;
	}

	/**
	 * Adds a new check box
	 *
	 * @param treeViewer
	 */
	void addCheckBox(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this,
				"myCheckBox");
		createCheckBox(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	/**
	 * Creates a check box
	 *
	 * @param name
	 * @return
	 */
	public CheckBox createCheckBox(String name) {
		CheckBox checkBox = new CheckBox(name);
		addChild(checkBox);
		return checkBox;
	}

	/**
	 * Creates a check box
	 *
	 * @param name
	 * @return
	 */
	public CheckBox createCheckBox(Attribute<Boolean> wrap, String name) {
		CheckBox checkBox = new CheckBox(name);
		addChild(checkBox);
		checkBox.wrap(wrap);
		return checkBox;
	}

	/**
	 * Creates a check box
	 *
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public CheckBox createCheckBox(Attribute<Boolean> wrap, String name,
			boolean defaultValue) {
		CheckBox checkBox = createCheckBox(wrap, name);
		checkBox.setDefaultValue(defaultValue);
		checkBox.set(defaultValue);
		return checkBox;
	}

	/**
	 * Adds a new combo box
	 *
	 * @param treeViewer
	 */
	void addComboBox(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this,
				"myComboBox");
		createComboBox(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	/**
	 * Create a new value chooser
	 *
	 * @param name
	 * @return
	 */
	public ColorChooser createColorChooser(String name) {
		ColorChooser colorChooser = new ColorChooser(name);
		addChild(colorChooser);
		return colorChooser;
	}

	/**
	 * Create a new value chooser
	 *
	 * @param name
	 * @return
	 */
	public ColorChooser createColorChooser(Attribute<String> wrap,
			String name) {
		ColorChooser colorChooser = new ColorChooser(name);
		addChild(colorChooser);
		colorChooser.wrap(wrap);
		return colorChooser;
	}

	/**
	 * Create a new value chooser with given name, label and default value
	 *
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public ColorChooser createColorChooser(Attribute<String> wrap, String name,
			String defaultValue) {
		ColorChooser colorChooser = createColorChooser(wrap, name);
		colorChooser.setDefaultValue(defaultValue);
		return colorChooser;
	}

	/**
	 * Creates a color map
	 *
	 * @param name
	 * @return
	 */
	public AbstractAttributeAtom<String> createColorMap(Attribute<String> wrap,
			String name) {
		ColorMap colorMap = new ColorMap(name);
		addChild(colorMap);
		colorMap.wrap(wrap);
		return colorMap;
	}

	/**
	 * Adds a new value chooser
	 *
	 * @param treeViewer
	 */
	void addColorChooser(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this,
				"myColor");
		createColorChooser(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	/**
	 * Creates a new combo box
	 *
	 * @param name
	 * @return
	 */
	public ComboBox createComboBox(String name) {
		ComboBox comboBox = new ComboBox(name);
		addChild(comboBox);
		return comboBox;
	}

	/**
	 * Creates a new combo box
	 *
	 * @param name
	 * @param items
	 * @param defaultValue
	 * @return
	 */
	public ComboBox createComboBox(Attribute<String> wrap, String name,
			String items, String defaultValue) {
		ComboBox comboBox = new ComboBox(name);
		comboBox.setItems(items);
		comboBox.setDefaultValue(defaultValue);
		comboBox.setValue(defaultValue);
		addChild(comboBox);
		comboBox.wrap(wrap);
		return comboBox;
	}

	/**
	 * Creates a new combo box with a given enum that provides the available
	 * values
	 *
	 * @param name
	 * @param defaultEnumValue
	 * @return
	 */
	public ComboBox createComboBox(Attribute<String> wrap, String name,
			Enum<?> defaultEnumValue) {
		@SuppressWarnings({"unchecked", "rawtypes"})
		ComboBox comboBox = new ComboBox(name);
		comboBox.setDefaultValue(defaultEnumValue);
		addChild(comboBox);
		comboBox.wrap(wrap);
		return comboBox;
	}

	/**
	 * Creates a new combo box with enum values
	 *
	 * @param <T>
	 *
	 * @param name
	 * @param defaultEnumValue
	 * @return
	 */
	public <T extends EnumValueProvider<?>> EnumComboBox<T> createEnumComboBox(
			Attribute<String> wrap, String name,
			EnumValueProvider<?> defaultEnumValue) {
		@SuppressWarnings({"unchecked", "rawtypes"})
		EnumComboBox<T> comboBox = new EnumComboBox(defaultEnumValue, name);
		addChild(comboBox);
		comboBox.wrap(wrap);
		return comboBox;
	}

	/**
	 * Creates a new ComboBoxEnableTarget
	 *
	 * @param name
	 * @param enableValues
	 * @param targetPath
	 * @return
	 */
	public ComboBoxEnableTarget createComboBoxEnableTarget(String name,
			String enableValues, String targetPath) {
		ComboBoxEnableTarget comboBoxEnableTarget = new ComboBoxEnableTarget(
				name, enableValues, targetPath);
		addChild(comboBoxEnableTarget);
		return comboBoxEnableTarget;
	}

	/**
	 * Creates a new ColumnTypeComboBox
	 *
	 * @param wrap
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public ColumnTypeComboBox createColumnTypeComboBox(Attribute<String> wrap,
			String name, ColumnType defaultValue) {
		ColumnTypeComboBox combo = new ColumnTypeComboBox(name);
		combo.setDefaultValue(defaultValue.getValue());
		combo.wrap(wrap);
		return combo;
	}

	/**
	 * Creates a new info text
	 *
	 * @param name
	 * @return
	 */
	public InfoText createInfoText(String name) {
		InfoText infoText = new InfoText(name);
		addChild(infoText);
		return infoText;
	}

	/**
	 * Creates a new info text with default value
	 *
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public InfoText createInfoText(String name, String defaultValue) {
		InfoText infoText = new InfoText(name);
		infoText.setDefaultValue(defaultValue);
		infoText.set(defaultValue);
		addChild(infoText);
		return infoText;
	}

	/**
	 * Creates a new info text with given name, label and default value
	 *
	 * @param name
	 * @param label
	 * @param defaultValue
	 * @return
	 */
	public InfoText createInfoText(Attribute<String> wrap, String name,
			String label, String defaultValue) {
		InfoText infoText = createInfoText(name, defaultValue);
		infoText.setLabel(label);
		infoText.wrap(wrap);
		return infoText;
	}

	/**
	 * Create a new line style chooser
	 *
	 * @param name
	 * @return
	 */
	public LineStyle createLineStyle(String name) {
		LineStyle lineStyle = new LineStyle(name);
		addChild(lineStyle);
		return lineStyle;
	}

	/**
	 * Create a new line style chooser
	 *
	 * @param name
	 * @return
	 */
	public LineStyle createLineStyle(Attribute<String> wrap, String name) {
		LineStyle lineStyle = new LineStyle(name);
		addChild(lineStyle);
		lineStyle.wrap(wrap);
		return lineStyle;
	}

	/**
	 * Create a new line style chooser with a default filePath
	 *
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public LineStyle createLineStyle(Attribute<String> wrap, String name,
			String defaultValue) {
		LineStyle lineStyle = new LineStyle(name, defaultValue);
		addChild(lineStyle);
		lineStyle.wrap(wrap);
		return lineStyle;
	}

	//#region FUNCTION PLOTTER

	/**
	 * Create a new function plotter
	 *
	 * @param name
	 * @return
	 */
	public FunctionPlotter createFunctionPlotter(Attribute<String> wrap,
			String name) {
		FunctionPlotter plotter = new FunctionPlotter(name);
		addChild(plotter);
		plotter.wrap(wrap);
		return plotter;
	}

	//#end region

	//#region MODEL_PATH

	/**
	 * Creates a model path chooser
	 *
	 * @param name
	 * @param defaultPath
	 * @param atomType
	 * @return
	 */
	public ModelPath createModelPath(Attribute<String> wrap, String name,
			String defaultPath, Class<?> atomType) {
		ModelPathSelectionType selectionType = ModelPathSelectionType.FLAT;
		ModelPath modelPath = new ModelPath(name, defaultPath, atomType,
				selectionType, null, false);
		addChild(modelPath);
		modelPath.wrap(wrap);
		return modelPath;
	}

	/**
	 * Creates a model path chooser with a parent model path chooser
	 *
	 * @param name
	 * @param parentModelPath
	 * @param atomType
	 * @return
	 */
	public ModelPath createModelPath(Attribute<String> wrap, String name,
			ModelPath parentModelPath, Class<?> atomType) {
		ModelPath modelPath = new ModelPath(name, parentModelPath, atomType);
		addChild(modelPath);
		modelPath.wrap(wrap);
		return modelPath;
	}

	/**
	 * Creates a model path chooser
	 *
	 * @param name
	 * @param defaultPath
	 * @param atomType
	 * @param selectionType
	 * @return
	 */
	public ModelPath createModelPath(Attribute<String> wrap, String name,
			String defaultPath, Class<?> atomType,
			ModelPathSelectionType selectionType) {
		ModelPath modelPath = new ModelPath(name, defaultPath, atomType,
				selectionType, null, false);
		addChild(modelPath);
		modelPath.wrap(wrap);
		return modelPath;
	}

	/**
	 * Creates a model path chooser
	 *
	 * @param name
	 * @param defaultPath
	 * @param atomType
	 * @param modelEntryAtom
	 * @return
	 */
	public ModelPath createModelPath(Attribute<String> wrap, String name,
			String defaultPath, Class<?> atomType,
			AbstractAtom modelEntryAtom) {
		ModelPathSelectionType selectionType = ModelPathSelectionType.FLAT;
		ModelPath modelPath = new ModelPath(name, defaultPath, atomType,
				selectionType, modelEntryAtom, false);
		addChild(modelPath);
		modelPath.wrap(wrap);
		return modelPath;
	}

	/**
	 * Create a model path chooser
	 *
	 * @param name
	 * @param label
	 * @param defaultPath
	 * @param atomType
	 * @param selectionType
	 * @param modelEntryPoint
	 * @return
	 */
	@SuppressWarnings("checkstyle:parameternumber")
	public ModelPath createModelPath(Attribute<String> wrap, String name,
			String defaultPath, Class<?> atomType,
			ModelPathSelectionType selectionType, AbstractAtom modelEntryPoint,
			boolean hasToBeEnabled) {
		ModelPath modelPath = new ModelPath(name, defaultPath, atomType,
				selectionType, modelEntryPoint, hasToBeEnabled);
		addChild(modelPath);
		modelPath.wrap(wrap);
		return modelPath;
	}

	//#end region

	/**
	 * Adds a new line style chooser
	 *
	 * @param treeViewer
	 */
	void addLineStyle(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this,
				"myColor");
		createLineStyle(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	//#region TEXT FIELD

	/**
	 * Adds a new text field
	 *
	 * @param treeViewer
	 */
	void addTextField(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this,
				"myTextField");
		createTextField(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	/**
	 * Creates a new text field
	 *
	 * @param name
	 * @return
	 */
	public TextField createTextField(String name) {
		TextField textField = new TextField(name);
		addChild(textField);
		return textField;
	}

	/**
	 * Creates a new text field
	 *
	 * @param name
	 * @return
	 */
	public TextField createTextField(Attribute<String> wrap, String name) {
		TextField textField = new TextField(name);
		addChild(textField);
		textField.wrap(wrap);
		return textField;
	}

	/**
	 * Creates a new text field with default value
	 *
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public TextField createTextField(Attribute<String> wrap, String name,
			String defaultValue) {
		TextField textField = new TextField(name);
		textField.setDefaultValue(defaultValue);
		textField.set(defaultValue);
		addChild(textField);
		textField.wrap(wrap);
		return textField;
	}

	//#end region

	//#region LABEL

	/**
	 * Creates a new label
	 *
	 * @param name
	 * @return
	 */
	public org.treez.core.atom.attribute.Label createLabel(String name) {
		org.treez.core.atom.attribute.Label label = new org.treez.core.atom.attribute.Label(
				name);
		addChild(label);
		return label;
	}

	/**
	 * Creates a new label with given label text
	 *
	 * @param name
	 * @param labelText
	 * @return
	 */
	public org.treez.core.atom.attribute.Label createLabel(String name,
			String labelText) {
		org.treez.core.atom.attribute.Label label = new org.treez.core.atom.attribute.Label(
				name);
		label.setLabel(labelText);
		addChild(label);
		return label;
	}

	//#end region

	//#region QUANTITY VARIABLE FIELD

	/**
	 * Creates a variable field with the given name
	 *
	 * @param name
	 * @return
	 */
	public QuantityVariableField createQuantityVariableField(String name) {
		QuantityVariableField variableField = new QuantityVariableField(name);
		addChild(variableField);
		return variableField;
	}

	/**
	 * Creates a variable list field with the given name
	 *
	 * @param name
	 * @return
	 */
	public QuantityVariableListField createQuantityVariableListField(
			String name) {
		QuantityVariableListField variableListField = new QuantityVariableListField(
				name);
		addChild(variableListField);
		return variableListField;
	}

	/**
	 * Creates a variable list field with the given name and label
	 *
	 * @param name
	 * @param label
	 * @return
	 */
	public QuantityVariableListField createQuantityVariableListField(
			String name, String label) {
		QuantityVariableListField variableListField = new QuantityVariableListField(
				name);
		variableListField.setLabel(label);
		addChild(variableListField);
		return variableListField;
	}

	//#end region

	//#region DOUBLE VARIABLE FIELD

	/**
	 * Creates a Double variable field with the given name
	 *
	 * @param name
	 * @return
	 */
	public DoubleVariableField createDoubleVariableField(String name) {
		DoubleVariableField variableField = new DoubleVariableField(name);
		addChild(variableField);
		return variableField;
	}

	/**
	 * Creates a Double variable list field with the given name
	 *
	 * @param name
	 * @return
	 */
	public DoubleVariableListField createDoubleListField(String name) {
		DoubleVariableListField variableListField = new DoubleVariableListField(
				name);
		addChild(variableListField);
		return variableListField;
	}

	/**
	 * Creates a Double variable list field with the given name and label
	 *
	 * @param name
	 * @param label
	 * @return
	 */
	public DoubleVariableListField createDoubleVariableListField(String name,
			String label) {
		DoubleVariableListField variableListField = new DoubleVariableListField(
				name);
		variableListField.setLabel(label);
		addChild(variableListField);
		return variableListField;
	}

	//#end region

	//#region FILE PATH

	/**
	 * Adds a new file path chooser
	 *
	 * @param treeViewer
	 */
	void addFilePath(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this,
				"myFilePath");
		createFilePath(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	/**
	 * Creates a new file path chooser
	 *
	 * @param name
	 * @return
	 */
	public FilePath createFilePath(String name) {
		FilePath filePath = new FilePath(name);
		addChild(filePath);
		return filePath;
	}

	/**
	 * Creates a new file path chooser
	 *
	 * @param name
	 * @param defaultPath
	 * @return
	 */
	public FilePath createFilePath(Attribute<String> wrap, String name,
			String defaultPath) {
		FilePath filePath = new FilePath(name);
		filePath.setDefaultValue(defaultPath);
		addChild(filePath);
		filePath.wrap(wrap);
		return filePath;
	}

	/**
	 * Creates a new file path chooser with given name, label and default path
	 *
	 * @param name
	 * @param label
	 * @param defaultPath
	 * @return
	 */
	public FilePath createFilePath(Attribute<String> wrap, String name,
			String label, String defaultPath) {
		FilePath filePath = createFilePath(wrap, name, defaultPath);
		filePath.setLabel(label);
		return filePath;
	}

	/**
	 * Creates a new file path chooser with given name, label, default path and
	 * validation flag
	 *
	 * @param name
	 * @param label
	 * @param defaultPath
	 * @param validatePath
	 * @return
	 */
	public FilePath createFilePath(Attribute<String> wrap, String name,
			String label, String defaultPath, Boolean validatePath) {
		FilePath filePath = createFilePath(wrap, name, defaultPath);
		filePath.setLabel(label);
		filePath.setValidatePath(validatePath);
		return filePath;
	}

	//#end region

	//#region FILE PATH LIST

	/**
	 * Adds a new file path list
	 *
	 * @param treeViewer
	 */
	void addFilePathList(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this,
				"myFilePathList");
		createFilePathList(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	/**
	 * Creates a new file path list
	 *
	 * @param name
	 * @return
	 */
	public FilePathList createFilePathList(String name) {
		FilePathList filePathList = new FilePathList(name);
		addChild(filePathList);
		return filePathList;
	}

	//#end region

	//#region DIRECTORY PATH

	/**
	 * Adds a new directory path chooser
	 *
	 * @param treeViewer
	 */
	void addDirectoryPath(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this,
				"myDirectoryPath");
		createDirectoryPath(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	//#end region

	//#region FILE OR DIRECTORY PATH

	/**
	 * Creates a new file or directory path chooser
	 *
	 * @param name
	 * @return
	 */
	public FileOrDirectoryPath createFileOrDirectoryPath(String name) {
		FileOrDirectoryPath fileOrDirectoryPath = new FileOrDirectoryPath(name);
		addChild(fileOrDirectoryPath);
		return fileOrDirectoryPath;
	}

	/**
	 * Creates a new file or directory path chooser
	 *
	 * @param name
	 * @param defaultPath
	 * @return
	 */
	public FileOrDirectoryPath createFileOrDirectoryPath(Attribute<String> wrap,
			String name, String defaultPath) {
		FileOrDirectoryPath fileOrDirectoryPath = new FileOrDirectoryPath(name);
		fileOrDirectoryPath.setDefaultValue(defaultPath);
		addChild(fileOrDirectoryPath);
		fileOrDirectoryPath.wrap(wrap);
		return fileOrDirectoryPath;
	}

	/**
	 * Creates a new file or directory path chooser with given name, label and
	 * default path
	 *
	 * @param name
	 * @param label
	 * @param defaultPath
	 * @return
	 */
	public FileOrDirectoryPath createFileOrDirectoryPath(Attribute<String> wrap,
			String name, String label, String defaultPath) {
		FileOrDirectoryPath fileOrDirectoryPath = createFileOrDirectoryPath(
				wrap, name, defaultPath);
		fileOrDirectoryPath.setLabel(label);
		return fileOrDirectoryPath;
	}

	/**
	 * Creates a new file or directory path chooser with given name, label,
	 * default path and validation flag
	 *
	 * @param name
	 * @param label
	 * @param defaultPath
	 * @param validatePath
	 * @return
	 */
	public FileOrDirectoryPath createFileOrDirectoryPath(Attribute<String> wrap,
			String name, String label, String defaultPath,
			Boolean validatePath) {
		FileOrDirectoryPath fileOrDirectoryPath = createFileOrDirectoryPath(
				wrap, name, defaultPath);
		fileOrDirectoryPath.setLabel(label);
		fileOrDirectoryPath.setValidatePath(validatePath);
		return fileOrDirectoryPath;
	}

	//#end region

	//#region DIRECTORY PATH LIST

	/**
	 * Adds a new directory path list
	 *
	 * @param treeViewer
	 */
	void addDirectoryPathList(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this,
				"myDirectoryPathList");
		createDirectoryPathList(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	/**
	 * Creates a new directory path list
	 *
	 * @param name
	 * @return
	 */
	public DirectoryPathList createDirectoryPathList(String name) {
		DirectoryPathList directoryPathList = new DirectoryPathList(name);
		addChild(directoryPathList);
		return directoryPathList;
	}

	//#end region

	/**
	 * Creates an error bar style
	 *
	 * @param name
	 * @param label
	 * @return
	 */
	public AbstractAttributeAtom<String> createErrorBarStyle(
			Attribute<String> wrap, String name, String label) {
		ErrorBarStyle errorBarStyle = new ErrorBarStyle(name);
		errorBarStyle.setLabel(label);
		addChild(errorBarStyle);
		errorBarStyle.wrap(wrap);
		return errorBarStyle;
	}

	/**
	 * Creates a new directory path chooser
	 *
	 * @param name
	 * @return
	 */
	public DirectoryPath createDirectoryPath(String name) {
		DirectoryPath directoryPath = new DirectoryPath(name);
		addChild(directoryPath);
		return directoryPath;
	}

	/**
	 * Creates a new directory path chooser
	 *
	 * @param name
	 * @param defaultPath
	 * @return
	 */
	public DirectoryPath createDirectoryPath(Attribute<String> wrap,
			String name, String defaultPath) {
		DirectoryPath directoryPath = new DirectoryPath(name);
		directoryPath.setDefaultValue(defaultPath);
		addChild(directoryPath);
		directoryPath.wrap(wrap);
		return directoryPath;
	}

	/**
	 * Creates a new directory path chooser with given name, label and default
	 * path
	 *
	 * @param name
	 * @param label
	 * @param defaultPath
	 * @return
	 */
	public DirectoryPath createDirectoryPath(Attribute<String> wrap,
			String name, String label, String defaultPath) {
		DirectoryPath directoryPath = createDirectoryPath(wrap, name,
				defaultPath);
		directoryPath.setLabel(label);
		return directoryPath;
	}

	/**
	 * Creates a fill style
	 *
	 * @param name
	 * @return
	 */
	public AbstractAttributeAtom<String> createFillStyle(Attribute<String> wrap,
			String name, String label) {
		FillStyle fillStyle = new FillStyle(name, label);
		addChild(fillStyle);
		fillStyle.wrap(wrap);
		return fillStyle;
	}

	/**
	 * Creates a new font
	 *
	 * @param name
	 * @return
	 */
	public AbstractAttributeAtom<String> createFont(Attribute<String> wrap,
			String name) {
		Font font = new Font(name);
		font.set("Arial");
		addChild(font);
		font.wrap(wrap);
		return font;
	}

	/**
	 * Adds a new spacer
	 *
	 * @param treeViewer
	 */
	void addSpacer(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this,
				"mySpacer");
		createSpacer(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	/**
	 * Creates a new spacer
	 *
	 * @param name
	 * @return
	 */
	public Spacer createSpacer(String name) {
		Spacer spacer = new Spacer(name);
		addChild(spacer);
		return spacer;
	}

	/**
	 * Creates a symbol type selection combo box
	 *
	 * @param name
	 * @return
	 */
	public AbstractAttributeAtom<String> createSymbolType(
			Attribute<String> wrap, String name, String label,
			String defaultValue) {
		SymbolType symbolType = new SymbolType(name, label, defaultValue);
		addChild(symbolType);
		symbolType.wrap(wrap);
		return symbolType;
	}

	/**
	 * Adds a new action
	 *
	 * @param treeViewer
	 * @param description
	 * @param runnable
	 */
	void addSectionAction(TreeViewerRefreshable treeViewer, String description,
			Runnable runnable) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this,
				"mySectionAction");
		createSectionAction(name, description, runnable);
		createTreeNodeAdaption().expand(treeViewer);
	}

	/**
	 * Creates a new action
	 *
	 * @param name
	 * @param description
	 * @param runnable
	 * @return
	 */
	public SectionAction createSectionAction(String name, String description,
			Runnable runnable) {
		SectionAction action = new SectionAction(name, description, runnable);
		addChild(action);
		return action;
	}

	/**
	 * Creates a new action with custom image
	 *
	 * @param name
	 * @param description
	 * @param runnable
	 * @param image
	 * @return
	 */
	public SectionAction createSectionAction(String name, String description,
			Runnable runnable, Image image) {
		SectionAction action = new SectionAction(name, description, runnable,
				image);
		addChild(action);
		return action;
	}

	/**
	 * Creates a size atom
	 *
	 * @param name
	 * @return
	 */
	public AbstractAttributeAtom<String> createSize(Attribute<String> wrap,
			String name) {
		Size size = new Size(name);
		addChild(size);
		size.wrap(wrap);
		return size;
	}

	/**
	 * Creates a size atom
	 *
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public AbstractAttributeAtom<String> createSize(Attribute<String> wrap,
			String name, String defaultValue) {
		Size size = new Size(name);
		size.setDefaultValue(defaultValue);
		addChild(size);
		size.wrap(wrap);
		return size;
	}

	/**
	 * Creates a size atom
	 *
	 * @param name
	 * @param label
	 * @param defaultValue
	 * @return
	 */
	public AbstractAttributeAtom<String> createSize(Attribute<String> wrap,
			String name, String label, String defaultValue) {
		Size size = new Size(name);
		size.setDefaultValue(defaultValue);
		size.setLabel(label);
		addChild(size);
		size.wrap(wrap);
		return size;
	}

	//#end region

	//#region VARIABLE LIST

	/**
	 * Creates a variable list
	 *
	 * @param wrap
	 * @param name
	 * @param label
	 * @return
	 */
	public VariableList createVariableList(Attribute<List<VariableField>> wrap,
			String name, String label) {

		VariableList variableList = new VariableList(name, null);
		variableList.setLabel(label);
		addChild(variableList);
		variableList.wrap(wrap);
		return variableList;

	}

	//#end region

	//#end region

	//#region ACCESSORS

	/**
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return
	 */
	public String getLayout() {
		return layout;
	}

	/**
	 * @param layout
	 */
	public void setLayout(String layout) {
		this.layout = layout;
	}

	/**
	 * @return
	 */
	public boolean getExpanded() {
		return expanded;
	}

	/**
	 * @param expanded
	 */
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	/**
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	//#end region

}
