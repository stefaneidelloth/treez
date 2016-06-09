package org.treez.core.atom.attribute;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.AbstractStringAttributeAtom;
import org.treez.core.atom.attribute.base.parent.AbstractAttributeContainerAtom;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.base.AtomTreeNodeAdaption;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.atom.variablefield.DoubleVariableField;
import org.treez.core.atom.variablefield.IntegerVariableField;
import org.treez.core.atom.variablefield.QuantityVariableField;
import org.treez.core.atom.variablefield.VariableField;
import org.treez.core.atom.variablelist.DoubleVariableListField;
import org.treez.core.atom.variablelist.IntegerVariableListField;
import org.treez.core.atom.variablelist.QuantityVariableListField;
import org.treez.core.atom.variablelist.VariableList;
import org.treez.core.attribute.Attribute;
import org.treez.core.data.column.ColumnType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.TreeViewerAction;
import org.treez.core.utils.Utils;
import org.treez.javafxd3.javafx.EnumValueProvider;

/**
 * An item example
 */
@SuppressWarnings({ "checkstyle:classfanoutcomplexity", "checkstyle:cyclomaticcomplexity" })
public class Section extends AbstractAttributeContainerAtom {

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "Section Title")
	private String title;

	@IsParameter(defaultValue = "")
	private String description;

	@IsParameter(defaultValue = "VERTICAL", comboItems = { "VERTICAL", "HORIZONTAL" })
	private String layout;

	@IsParameter(defaultValue = "true")
	private boolean expanded;

	//#end region

	//#region CONSTRUCTORS

	public Section(String name) {
		super(name);
		title = Utils.firstToUpperCase(name); //this default title might be overridden by explicitly setting the label
	}

	/**
	 * Copy constructor
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

	@Override
	public Section copy() {
		return new Section(this);
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("Section.png");
	}

	@Override
	protected ArrayList<Object> createContextMenuActions(final TreeViewerRefreshable treeViewer) {
		ArrayList<Object> actions = new ArrayList<>();

		//add section
		actions.add(new TreeViewerAction(
				"Add Section",
				Activator.getImage("Section.png"),
				treeViewer,
				() -> addSection(treeViewer)));

		//add text field
		actions.add(new TreeViewerAction(
				"Add TextField",
				Activator.getImage("TextField.png"),
				treeViewer,
				() -> addTextField(treeViewer)));

		//add check box
		actions.add(new TreeViewerAction(
				"Add CheckBox",
				Activator.getImage("CheckBox.png"),
				treeViewer,
				() -> addCheckBox(treeViewer)));

		//add combo box
		actions.add(new TreeViewerAction(
				"Add ComboBox",
				Activator.getImage("ComboBox.png"),
				treeViewer,
				() -> addComboBox(treeViewer)));

		//add spacer
		actions.add(new TreeViewerAction(
				"Add Spacer",
				Activator.getImage("Spacer.png"),
				treeViewer,
				() -> addSpacer(treeViewer)));

		//delete
		actions.add(new TreeViewerAction(
				"Delete",
				Activator.getImage(ISharedImages.IMG_TOOL_DELETE),
				treeViewer,
				() -> createTreeNodeAdaption().delete()));

		return actions;
	}

	//#region CONTROL

	@Override
	public void createAtomControl(Composite parent, FocusChangingRefreshable treeViewerRefreshable) {

		SectionControlProvider controlProvider = new SectionControlProvider(this, parent, treeViewerRefreshable);
		controlProvider.createAtomControl();

	}

	void addSection(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this, "mySection");
		createSection(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	public Section createSection(String name) {
		Section section = new Section(name);
		addChild(section);
		return section;
	}

	void addCheckBox(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this, "myCheckBox");
		createCheckBox(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	public CheckBox createCheckBox(String name) {
		CheckBox checkBox = new CheckBox(name);
		addChild(checkBox);
		return checkBox;
	}

	public CheckBox createCheckBox(Attribute<Boolean> wrap, String name) {
		CheckBox checkBox = new CheckBox(name);
		addChild(checkBox);
		checkBox.wrap(wrap);
		return checkBox;
	}

	public CheckBox createCheckBox(Attribute<Boolean> wrap, String name, boolean defaultValue) {
		CheckBox checkBox = createCheckBox(wrap, name);
		checkBox.setDefaultValue(defaultValue);
		checkBox.set(defaultValue);
		return checkBox;
	}

	void addComboBox(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this, "myComboBox");
		createComboBox(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	public ColorChooser createColorChooser(String name) {
		ColorChooser colorChooser = new ColorChooser(name);
		addChild(colorChooser);
		return colorChooser;
	}

	public ColorChooser createColorChooser(Attribute<String> wrap, String name) {
		ColorChooser colorChooser = new ColorChooser(name);
		addChild(colorChooser);
		colorChooser.wrap(wrap);
		return colorChooser;
	}

	public ColorChooser createColorChooser(Attribute<String> wrap, String name, String defaultValue) {
		ColorChooser colorChooser = createColorChooser(wrap, name);
		colorChooser.setDefaultValue(defaultValue);
		return colorChooser;
	}

	public AbstractStringAttributeAtom createColorMap(Attribute<String> wrap, AbstractAtom attributeParent) {

		String attributeName = getFieldName(wrap, attributeParent);
		ColorMap colorMap = new ColorMap(attributeName);
		addChild(colorMap);
		colorMap.wrap(wrap);
		return colorMap;
	}

	void addColorChooser(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this, "myColor");
		createColorChooser(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	public ComboBox createComboBox(String name) {
		ComboBox comboBox = new ComboBox(name);
		addChild(comboBox);
		return comboBox;
	}

	public ComboBox createComboBox(Attribute<String> wrap, String name, String items, String defaultValue) {
		ComboBox comboBox = new ComboBox(name);
		comboBox.setItems(items);
		comboBox.setDefaultValue(defaultValue);
		comboBox.setValue(defaultValue);
		addChild(comboBox);
		comboBox.wrap(wrap);
		return comboBox;
	}

	/**
	 * Creates a new combo box with a given enum value. The enum provides the available values and the enum value is
	 * used as default value.
	 *
	 * @param name
	 * @param defaultEnumValue
	 * @return
	 */
	public ComboBox createComboBox(Attribute<String> wrap, String name, Enum<?> defaultEnumValue) {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		ComboBox comboBox = new ComboBox(name);
		comboBox.setItems(defaultEnumValue);
		comboBox.setDefaultValue(defaultEnumValue);
		addChild(comboBox);
		comboBox.wrap(wrap);
		return comboBox;
	}

	public ComboBox createComboBox(Attribute<String> wrap, AbstractAtom attributeParent) {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		String attributeName = getFieldName(wrap, attributeParent);
		ComboBox comboBox = new ComboBox(attributeName);
		addChild(comboBox);
		comboBox.wrap(wrap);
		return comboBox;
	}

	public <T extends EnumValueProvider<?>> EnumComboBox<T> createEnumComboBox(
			Attribute<String> wrap,
			String name,
			EnumValueProvider<?> defaultEnumValue) {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		EnumComboBox<T> comboBox = new EnumComboBox(defaultEnumValue, name);
		addChild(comboBox);
		comboBox.wrap(wrap);
		return comboBox;
	}

	public <T extends EnumValueProvider<?>> EnumComboBox<T> createEnumComboBox(
			Attribute<String> wrap,
			Object attributeParent,
			EnumValueProvider<?> defaultEnumValue) {
		String attributeName = getFieldName(wrap, attributeParent);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		EnumComboBox<T> comboBox = new EnumComboBox(defaultEnumValue, attributeName);
		addChild(comboBox);
		comboBox.wrap(wrap);
		return comboBox;
	}

	public ComboBoxEnableTarget createComboBoxEnableTarget(String name, String enableValues, String targetPath) {
		ComboBoxEnableTarget comboBoxEnableTarget = new ComboBoxEnableTarget(name, enableValues, targetPath);
		addChild(comboBoxEnableTarget);
		return comboBoxEnableTarget;
	}

	public ColumnTypeComboBox createColumnTypeComboBox(Attribute<String> wrap, String name, ColumnType defaultValue) {
		ColumnTypeComboBox combo = new ColumnTypeComboBox(name);
		combo.setDefaultValue(defaultValue.getValue());
		combo.wrap(wrap);
		return combo;
	}

	public InfoText createInfoText(String name) {
		InfoText infoText = new InfoText(name);
		addChild(infoText);
		return infoText;
	}

	public InfoText createInfoText(String name, String defaultValue) {
		InfoText infoText = new InfoText(name);
		infoText.setDefaultValue(defaultValue);
		infoText.set(defaultValue);
		addChild(infoText);
		return infoText;
	}

	public InfoText createInfoText(Attribute<String> wrap, String name, String label, String defaultValue) {
		InfoText infoText = createInfoText(name, defaultValue);
		infoText.setLabel(label);
		infoText.wrap(wrap);
		return infoText;
	}

	public LineStyle createLineStyle(String name) {
		LineStyle lineStyle = new LineStyle(name);
		addChild(lineStyle);
		return lineStyle;
	}

	public LineStyle createLineStyle(Attribute<String> wrap, String name) {
		LineStyle lineStyle = new LineStyle(name);
		addChild(lineStyle);
		lineStyle.wrap(wrap);
		return lineStyle;
	}

	public LineStyle createLineStyle(Attribute<String> wrap, String name, String defaultValue) {
		LineStyle lineStyle = new LineStyle(name, defaultValue);
		addChild(lineStyle);
		lineStyle.wrap(wrap);
		return lineStyle;
	}

	//#region FUNCTION PLOTTER

	public FunctionPlotter createFunctionPlotter(Attribute<String> wrap, String name) {
		FunctionPlotter plotter = new FunctionPlotter(name);
		addChild(plotter);
		plotter.wrap(wrap);
		return plotter;
	}

	//#end region

	//#region MODEL_PATH

	public ModelPath createModelPath(
			Attribute<String> wrap,
			Object attributeParent,
			String defaultPath,
			Class<?> atomType) {
		String attributeName = getFieldName(wrap, attributeParent);
		ModelPathSelectionType selectionType = ModelPathSelectionType.FLAT;
		ModelPath modelPath = new ModelPath(attributeName, defaultPath, atomType, selectionType, null, false);
		addChild(modelPath);
		modelPath.wrap(wrap);
		return modelPath;
	}

	public ModelPath createModelPath(
			Attribute<String> wrap,
			AbstractAtom attributeParent,
			String defaultPath,
			Class<?>[] atomTypes) {
		String attributeName = getFieldName(wrap, attributeParent);
		ModelPathSelectionType selectionType = ModelPathSelectionType.FLAT;
		ModelPath modelPath = new ModelPath(
				attributeName,
				defaultPath,
				atomTypes,
				selectionType,
				attributeParent,
				false);
		addChild(modelPath);
		modelPath.wrap(wrap);
		return modelPath;
	}

	public ModelPath createModelPath(
			Attribute<String> wrap,
			AbstractAtom attributeParent,
			ModelPath parentModelPath,
			Class<?> atomType) {
		String attributeName = getFieldName(wrap, attributeParent);
		ModelPath modelPath = new ModelPath(attributeName, parentModelPath, atomType);
		addChild(modelPath);
		modelPath.wrap(wrap);
		return modelPath;
	}

	public ModelPath createModelPath(
			Attribute<String> wrap,
			AbstractAtom attributeParent,
			String defaultPath,
			Class<?> atomType,
			ModelPathSelectionType selectionType) {
		String attributeName = getFieldName(wrap, attributeParent);
		ModelPath modelPath = new ModelPath(
				attributeName,
				defaultPath,
				atomType,
				selectionType,
				attributeParent,
				false);
		addChild(modelPath);
		modelPath.wrap(wrap);
		return modelPath;
	}

	public ModelPath createModelPath(
			Attribute<String> wrap,
			Object attributeParent,
			String defaultPath,
			Class<?> atomType,
			AbstractAtom modelEntryAtom) {
		String attributeName = getFieldName(wrap, attributeParent);
		ModelPathSelectionType selectionType = ModelPathSelectionType.FLAT;
		ModelPath modelPath = new ModelPath(attributeName, defaultPath, atomType, selectionType, modelEntryAtom, false);
		addChild(modelPath);
		modelPath.wrap(wrap);
		return modelPath;
	}

	@SuppressWarnings("checkstyle:parameternumber")
	public ModelPath createModelPath(
			Attribute<String> wrap,
			Object attributeParent,
			String defaultPath,
			Class<?> atomType,
			ModelPathSelectionType selectionType,
			AbstractAtom modelEntryPoint,
			boolean hasToBeEnabled) {
		String attributeName = getFieldName(wrap, attributeParent);
		ModelPath modelPath = new ModelPath(
				attributeName,
				defaultPath,
				atomType,
				selectionType,
				modelEntryPoint,
				hasToBeEnabled);
		addChild(modelPath);
		modelPath.wrap(wrap);
		return modelPath;
	}

	//#end region

	void addLineStyle(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this, "myColor");
		createLineStyle(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	//#region TEXT FIELD

	void addTextField(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this, "myTextField");
		createTextField(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	public TextField createTextField(String name) {
		TextField textField = new TextField(name);
		addChild(textField);
		return textField;
	}

	public TextField createTextField(Attribute<String> wrap, String name) {
		TextField textField = new TextField(name);
		addChild(textField);
		textField.wrap(wrap);
		return textField;
	}

	public TextField createTextField(Attribute<String> wrap, String name, String defaultValue) {
		TextField textField = new TextField(name);
		textField.setDefaultValue(defaultValue);
		textField.set(defaultValue);
		addChild(textField);
		textField.wrap(wrap);
		return textField;
	}

	public TextField createTextField(Attribute<String> wrap, AbstractAtom attributeParent) {
		String attributeName = getFieldName(wrap, attributeParent);
		TextField textField = new TextField(attributeName);
		addChild(textField);
		textField.wrap(wrap);
		return textField;
	}

	public TextField createTextField(Attribute<String> wrap, GraphicsPropertiesPageFactory attributeParent) {
		String attributeName = getFieldName(wrap, attributeParent);
		TextField textField = new TextField(attributeName);
		addChild(textField);
		textField.wrap(wrap);
		return textField;
	}

	//#end region

	//#region LABEL

	public org.treez.core.atom.attribute.Label createLabel(String name) {
		org.treez.core.atom.attribute.Label label = new org.treez.core.atom.attribute.Label(name);
		addChild(label);
		return label;
	}

	public org.treez.core.atom.attribute.Label createLabel(String name, String labelText) {
		org.treez.core.atom.attribute.Label label = new org.treez.core.atom.attribute.Label(name);
		label.setLabel(labelText);
		addChild(label);
		return label;
	}

	//#end region

	//#region QUANTITY VARIABLE FIELD

	public QuantityVariableField createQuantityVariableField(String name) {
		QuantityVariableField variableField = new QuantityVariableField(name);
		addChild(variableField);
		return variableField;
	}

	public QuantityVariableListField createQuantityVariableListField(String name) {
		QuantityVariableListField variableListField = new QuantityVariableListField(name);
		addChild(variableListField);
		return variableListField;
	}

	public QuantityVariableListField createQuantityVariableListField(String name, String label) {
		QuantityVariableListField variableListField = new QuantityVariableListField(name);
		variableListField.setLabel(label);
		addChild(variableListField);
		return variableListField;
	}

	//#end region

	//#region DOUBLE VARIABLE FIELD

	public DoubleVariableField createDoubleVariableField(
			Attribute<Double> wrap,
			AbstractAtom attributeParent,
			Double defaultValue) {
		String attributeName = getFieldName(wrap, attributeParent);
		DoubleVariableField variableField = new DoubleVariableField(attributeName);
		variableField.setDefaultValue(defaultValue);
		variableField.set(defaultValue);
		variableField.wrap(wrap);
		addChild(variableField);
		return variableField;
	}

	public DoubleVariableField createDoubleVariableField(
			Attribute<Double> wrap,
			GraphicsPropertiesPageFactory attributeParent,
			Double defaultValue) {
		String attributeName = getFieldName(wrap, attributeParent);
		DoubleVariableField variableField = new DoubleVariableField(attributeName);
		variableField.setDefaultValue(defaultValue);
		variableField.set(defaultValue);
		variableField.wrap(wrap);
		addChild(variableField);
		return variableField;
	}

	public DoubleVariableListField createDoubleListField(String name) {
		DoubleVariableListField variableListField = new DoubleVariableListField(name);
		addChild(variableListField);
		return variableListField;
	}

	public DoubleVariableListField createDoubleVariableListField(String name, String label) {
		DoubleVariableListField variableListField = new DoubleVariableListField(name);
		variableListField.setLabel(label);
		addChild(variableListField);
		return variableListField;
	}

	public DoubleVariableListField createDoubleVariableListField(
			Attribute<List<Double>> wrap,
			AbstractAtom attributeParent,
			String label) {

		String attributeName = getFieldName(wrap, attributeParent);
		DoubleVariableListField variableListField = new DoubleVariableListField(attributeName);
		variableListField.setLabel(label);

		addChild(variableListField);
		variableListField.wrap(wrap);
		return variableListField;

	}

	//#end region

	//#region INTEGER VARIABLE FIELD

	public IntegerVariableField createIntegerVariableField(
			Attribute<Integer> wrap,
			AbstractAtom attributeParent,
			Integer defaultValue) {
		String attributeName = getFieldName(wrap, attributeParent);
		IntegerVariableField variableField = new IntegerVariableField(attributeName);
		variableField.setDefaultValue(defaultValue);
		variableField.set(defaultValue);
		variableField.wrap(wrap);
		addChild(variableField);
		return variableField;
	}

	public IntegerVariableField createIntegerVariableField(
			Attribute<Integer> wrap,
			GraphicsPropertiesPageFactory attributeParent,
			Integer defaultValue) {
		String attributeName = getFieldName(wrap, attributeParent);
		IntegerVariableField variableField = new IntegerVariableField(attributeName);
		variableField.setDefaultValue(defaultValue);
		variableField.set(defaultValue);
		variableField.wrap(wrap);
		addChild(variableField);
		return variableField;
	}

	public IntegerVariableField createIntegerVariableField(String name) {
		IntegerVariableField variableField = new IntegerVariableField(name);
		addChild(variableField);
		return variableField;
	}

	public IntegerVariableListField createIntegerListField(String name) {
		IntegerVariableListField variableListField = new IntegerVariableListField(name);
		addChild(variableListField);
		return variableListField;
	}

	public IntegerVariableListField createIntegerVariableListField(String name, String label) {
		IntegerVariableListField variableListField = new IntegerVariableListField(name);
		variableListField.setLabel(label);
		addChild(variableListField);
		return variableListField;
	}

	public IntegerVariableListField createIntegerVariableListField(
			Attribute<List<Integer>> wrap,
			AbstractAtom attributeParent,
			String label) {
		String attributeName = getFieldName(wrap, attributeParent);
		IntegerVariableListField variableListField = new IntegerVariableListField(attributeName);
		variableListField.setLabel(label);

		addChild(variableListField);
		variableListField.wrap(wrap);
		return variableListField;

	}

	//#end region

	//#region FILE PATH

	void addFilePath(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this, "myFilePath");
		createFilePath(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	public FilePath createFilePath(String name) {
		FilePath filePath = new FilePath(name);
		addChild(filePath);
		return filePath;
	}

	public FilePath createFilePath(Attribute<String> wrap, String name, String defaultPath) {
		FilePath filePath = new FilePath(name);
		filePath.setDefaultValue(defaultPath);
		addChild(filePath);
		filePath.wrap(wrap);
		return filePath;
	}

	public FilePath createFilePath(Attribute<String> wrap, String name, String label, String defaultPath) {
		FilePath filePath = createFilePath(wrap, name, defaultPath);
		filePath.setLabel(label);
		return filePath;
	}

	public FilePath createFilePath(
			Attribute<String> wrap,
			String name,
			String label,
			String defaultPath,
			Boolean validatePath) {
		FilePath filePath = createFilePath(wrap, name, defaultPath);
		filePath.setLabel(label);
		filePath.setValidatePath(validatePath);
		return filePath;
	}

	//#end region

	//#region FILE PATH LIST

	void addFilePathList(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this, "myFilePathList");
		createFilePathList(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	public FilePathList createFilePathList(String name) {
		FilePathList filePathList = new FilePathList(name);
		addChild(filePathList);
		return filePathList;
	}

	//#end region

	//#region DIRECTORY PATH

	void addDirectoryPath(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this, "myDirectoryPath");
		createDirectoryPath(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	//#end region

	//#region FILE OR DIRECTORY PATH

	public FileOrDirectoryPath createFileOrDirectoryPath(String name) {
		FileOrDirectoryPath fileOrDirectoryPath = new FileOrDirectoryPath(name);
		addChild(fileOrDirectoryPath);
		return fileOrDirectoryPath;
	}

	public FileOrDirectoryPath createFileOrDirectoryPath(Attribute<String> wrap, String name, String defaultPath) {
		FileOrDirectoryPath fileOrDirectoryPath = new FileOrDirectoryPath(name);
		fileOrDirectoryPath.setDefaultValue(defaultPath);
		addChild(fileOrDirectoryPath);
		fileOrDirectoryPath.wrap(wrap);
		return fileOrDirectoryPath;
	}

	public FileOrDirectoryPath createFileOrDirectoryPath(
			Attribute<String> wrap,
			String name,
			String label,
			String defaultPath) {
		FileOrDirectoryPath fileOrDirectoryPath = createFileOrDirectoryPath(wrap, name, defaultPath);
		fileOrDirectoryPath.setLabel(label);
		return fileOrDirectoryPath;
	}

	public FileOrDirectoryPath createFileOrDirectoryPath(
			Attribute<String> wrap,
			String name,
			String label,
			String defaultPath,
			Boolean validatePath) {
		FileOrDirectoryPath fileOrDirectoryPath = createFileOrDirectoryPath(wrap, name, defaultPath);
		fileOrDirectoryPath.setLabel(label);
		fileOrDirectoryPath.setValidatePath(validatePath);
		return fileOrDirectoryPath;
	}

	//#end region

	//#region DIRECTORY PATH LIST

	void addDirectoryPathList(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this, "myDirectoryPathList");
		createDirectoryPathList(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	public DirectoryPathList createDirectoryPathList(String name) {
		DirectoryPathList directoryPathList = new DirectoryPathList(name);
		addChild(directoryPathList);
		return directoryPathList;
	}

	//#end region

	public AbstractStringAttributeAtom createErrorBarStyle(Attribute<String> wrap, String name, String label) {
		ErrorBarStyle errorBarStyle = new ErrorBarStyle(name);
		errorBarStyle.setLabel(label);
		addChild(errorBarStyle);
		errorBarStyle.wrap(wrap);
		return errorBarStyle;
	}

	public DirectoryPath createDirectoryPath(String name) {
		DirectoryPath directoryPath = new DirectoryPath(name);
		addChild(directoryPath);
		return directoryPath;
	}

	public DirectoryPath createDirectoryPath(Attribute<String> wrap, String name, String defaultPath) {
		DirectoryPath directoryPath = new DirectoryPath(name);
		directoryPath.setDefaultValue(defaultPath);
		addChild(directoryPath);
		directoryPath.wrap(wrap);
		return directoryPath;
	}

	public DirectoryPath createDirectoryPath(Attribute<String> wrap, String name, String label, String defaultPath) {
		DirectoryPath directoryPath = createDirectoryPath(wrap, name, defaultPath);
		directoryPath.setLabel(label);
		return directoryPath;
	}

	public AbstractStringAttributeAtom createFillStyle(Attribute<String> wrap, String name, String label) {
		FillStyle fillStyle = new FillStyle(name, label);
		addChild(fillStyle);
		fillStyle.wrap(wrap);
		return fillStyle;
	}

	public AbstractStringAttributeAtom createFont(Attribute<String> wrap, String name) {
		Font font = new Font(name, "Arial");
		addChild(font);
		font.wrap(wrap);
		return font;
	}

	void addSpacer(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this, "mySpacer");
		createSpacer(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	public Spacer createSpacer(String name) {
		Spacer spacer = new Spacer(name);
		addChild(spacer);
		return spacer;
	}

	public AbstractStringAttributeAtom createSymbolType(
			Attribute<String> wrap,
			Object attributeParent,
			String label,
			String defaultValue) {
		String attributeName = getFieldName(wrap, attributeParent);
		SymbolType symbolType = new SymbolType(attributeName, label, defaultValue);
		addChild(symbolType);
		symbolType.wrap(wrap);
		return symbolType;
	}

	void addSectionAction(TreeViewerRefreshable treeViewer, String description, Runnable runnable) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this, "mySectionAction");
		createSectionAction(name, description, runnable);
		createTreeNodeAdaption().expand(treeViewer);
	}

	public SectionAction createSectionAction(String name, String description, Runnable runnable) {
		SectionAction action = new SectionAction(name, description, runnable);
		addChild(action);
		return action;
	}

	public SectionAction createSectionAction(String name, String description, Runnable runnable, Image image) {
		SectionAction action = new SectionAction(name, description, runnable, image);
		addChild(action);
		return action;
	}

	public AbstractStringAttributeAtom createSize(Attribute<String> wrap, String name) {
		Size size = new Size(name);
		addChild(size);
		size.wrap(wrap);
		return size;
	}

	public AbstractStringAttributeAtom createSize(Attribute<String> wrap, String name, String defaultValue) {
		Size size = new Size(name);
		size.setDefaultValue(defaultValue);
		addChild(size);
		size.wrap(wrap);
		return size;
	}

	public AbstractStringAttributeAtom createSize(
			Attribute<String> wrap,
			String name,
			String label,
			String defaultValue) {
		Size size = new Size(name);
		size.setDefaultValue(defaultValue);
		size.setLabel(label);
		addChild(size);
		size.wrap(wrap);
		return size;
	}

	//#end region

	//#region VARIABLE LIST

	public VariableList createVariableList(Attribute<List<VariableField<?>>> wrap, String name, String label) {

		VariableList variableList = new VariableList(name, null);
		variableList.setLabel(label);
		addChild(variableList);
		variableList.wrap(wrap);
		return variableList;
	}

	//#end region

	//#end region

	//#region ACCESSORS

	public String getTitle() {
		return title;
	}

	public void setLabel(String title) {
		this.title = title;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public boolean getExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	//#end region

}
