package org.treez.core.atom.attribute;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.parent.AbstractAttributeContainerAtom;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.base.AtomTreeNodeAdaption;
import org.treez.core.atom.base.annotation.IsParameter;
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
public class Section extends AbstractAttributeContainerAtom<Section> {

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "Section Title")
	private String title;

	@IsParameter(defaultValue = "")
	private String description;

	@IsParameter(defaultValue = "VERTICAL", comboItems = { "VERTICAL", "HORIZONTAL" })
	private String layout;

	@IsParameter(defaultValue = "true")
	private boolean expanded;

	private SectionControlProvider controlProvider;

	private boolean isEnabled = true;

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
	protected Section getThis() {
		return this;
	}

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
		controlProvider = new SectionControlProvider(this, parent, treeViewerRefreshable);
		controlProvider.createAtomControl();
	}

	//#end region

	//#region SECTION

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

	//#end region

	//#region SECTION ACTION

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

	//#end region

	//#region TEXT FIELD

	private void addTextField(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this, "myTextField");
		createTextField(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	private TextField createTextField(String name) {
		TextField textField = new TextField(name);
		addChild(textField);
		return textField;
	}

	public TextField createTextField(Attribute<String> wrap, Object attributeParent) {
		String attributeName = getFieldName(wrap, attributeParent);
		TextField textField = new TextField(attributeName);
		addChild(textField);
		textField.wrap(wrap);
		return textField;
	}

	public TextField createTextField(Attribute<String> wrap, Object attributeParent, String defaultValue) {
		String attributeName = getFieldName(wrap, attributeParent);
		TextField textField = new TextField(attributeName);
		textField.setDefaultValue(defaultValue);
		textField.set(defaultValue);
		addChild(textField);
		textField.wrap(wrap);
		return textField;
	}

	//#end region

	//#region LABEL

	public org.treez.core.atom.attribute.Label createLabel(String name, String labelText) {
		org.treez.core.atom.attribute.Label label = new org.treez.core.atom.attribute.Label(name);
		label.setLabel(labelText);
		addChild(label);
		return label;
	}

	//#end region

	//#region INFO TEXT

	public InfoText createInfoText(Attribute<String> wrap, Object attributeParent, String label, String defaultValue) {
		String attributeName = getFieldName(wrap, attributeParent);
		InfoText infoText = createInfoText(attributeName, defaultValue);
		infoText.setLabel(label);
		infoText.wrap(wrap);
		return infoText;
	}

	private InfoText createInfoText(String name, String defaultValue) {
		InfoText infoText = new InfoText(name);
		infoText.setDefaultValue(defaultValue);
		infoText.set(defaultValue);
		addChild(infoText);
		return infoText;
	}

	//#end region

	//#region TEXT AREA

	public TextArea createTextArea(Attribute<String> wrap, Object attributeParent) {
		String attributeName = getFieldName(wrap, attributeParent);
		TextArea textArea = new TextArea(attributeName);
		addChild(textArea);
		textArea.wrap(wrap);
		return textArea;
	}

	//#end region

	//#region CHECK BOX

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

	public CheckBox createCheckBox(Attribute<Boolean> wrap, Object attributeParent) {
		String attributeName = getFieldName(wrap, attributeParent);
		CheckBox checkBox = new CheckBox(attributeName);
		addChild(checkBox);
		checkBox.wrap(wrap);
		return checkBox;
	}

	public CheckBox createCheckBox(Attribute<Boolean> wrap, Object attributeParent, boolean defaultValue) {
		CheckBox checkBox = createCheckBox(wrap, attributeParent);
		checkBox.setDefaultValue(defaultValue);
		checkBox.set(defaultValue);
		return checkBox;
	}

	//#end region

	//#region COMBO BOX

	void addComboBox(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this, "myComboBox");
		createComboBox(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	private ComboBox createComboBox(String name) {
		ComboBox comboBox = new ComboBox(name);
		addChild(comboBox);
		return comboBox;
	}

	public ComboBox createComboBox(Attribute<String> wrap, Object attributeParent) {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		String attributeName = getFieldName(wrap, attributeParent);
		ComboBox comboBox = new ComboBox(attributeName);
		addChild(comboBox);
		comboBox.wrap(wrap);
		return comboBox;
	}

	public ComboBox createComboBox(Attribute<String> wrap, Object attributeParent, String items, String defaultValue) {
		String attributeName = getFieldName(wrap, attributeParent);
		ComboBox comboBox = new ComboBox(attributeName);
		comboBox.setItems(items);
		comboBox.setDefaultValue(defaultValue);
		comboBox.setValue(defaultValue);
		addChild(comboBox);
		comboBox.wrap(wrap);
		return comboBox;
	}

	//#end region

	//#region COMBO BOX ENABLE TARGET

	public ComboBoxEnableTarget createComboBoxEnableTarget(String name, String enableValues, String targetPath) {
		ComboBoxEnableTarget comboBoxEnableTarget = new ComboBoxEnableTarget(name, enableValues, targetPath);
		addChild(comboBoxEnableTarget);
		return comboBoxEnableTarget;
	}

	//#end region

	//#region ENUM COMBO BOX

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

	//#end region

	//#region COLUMN TYPE COMBO BOX

	public ColumnTypeComboBox createColumnTypeComboBox(
			Attribute<String> wrap,
			Object attributeParent,
			ColumnType defaultValue) {
		String attributeName = getFieldName(wrap, attributeParent);
		ColumnTypeComboBox combo = new ColumnTypeComboBox(attributeName);
		combo.setDefaultValue(defaultValue.getValue());
		combo.wrap(wrap);
		return combo;
	}

	//#end region

	//#region COLOR CHOOSER

	void addColorChooser(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this, "myColor");
		createColorChooser(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	private ColorChooser createColorChooser(String name) {
		ColorChooser colorChooser = new ColorChooser(name);
		addChild(colorChooser);
		return colorChooser;
	}

	public ColorChooser createColorChooser(Attribute<String> wrap, Object attributeParent) {
		String attributeName = getFieldName(wrap, attributeParent);
		ColorChooser colorChooser = new ColorChooser(attributeName);
		addChild(colorChooser);
		colorChooser.wrap(wrap);
		return colorChooser;
	}

	public ColorChooser createColorChooser(Attribute<String> wrap, Object attributeParent, String defaultValue) {
		ColorChooser colorChooser = createColorChooser(wrap, attributeParent);
		colorChooser.setDefaultValue(defaultValue);
		return colorChooser;
	}

	//#end region

	//#region COLOR MAP

	public ColorMap createColorMap(Attribute<String> wrap, Object attributeParent) {
		String attributeName = getFieldName(wrap, attributeParent);
		ColorMap colorMap = new ColorMap(attributeName);
		addChild(colorMap);
		colorMap.wrap(wrap);
		return colorMap;
	}

	//#end region

	//#region LINE STYLE

	public LineStyle createLineStyle(Attribute<String> wrap, Object attributeParent) {
		String attributeName = getFieldName(wrap, attributeParent);
		LineStyle lineStyle = new LineStyle(attributeName);
		addChild(lineStyle);
		lineStyle.wrap(wrap);
		return lineStyle;
	}

	public LineStyle createLineStyle(Attribute<String> wrap, Object attributeParent, String defaultValue) {
		String attributeName = getFieldName(wrap, attributeParent);
		LineStyle lineStyle = new LineStyle(attributeName, defaultValue);
		addChild(lineStyle);
		lineStyle.wrap(wrap);
		return lineStyle;
	}

	//#end region

	//#region FILL STYLE

	public FillStyle createFillStyle(Attribute<String> wrap, Object attributeParent, String label) {
		String attributeName = getFieldName(wrap, attributeParent);
		FillStyle fillStyle = new FillStyle(attributeName, label);
		addChild(fillStyle);
		fillStyle.wrap(wrap);
		return fillStyle;
	}

	//#end region

	//#region ERROR BAR STYLE

	public ErrorBarStyle createErrorBarStyle(Attribute<String> wrap, Object attributeParent, String label) {
		String attributeName = getFieldName(wrap, attributeParent);
		ErrorBarStyle errorBarStyle = new ErrorBarStyle(attributeName);
		errorBarStyle.setLabel(label);
		addChild(errorBarStyle);
		errorBarStyle.wrap(wrap);
		return errorBarStyle;
	}

	//#end region

	//#region FONT

	public Font createFont(Attribute<String> wrap, Object attributeParent) {
		String attributeName = getFieldName(wrap, attributeParent);
		Font font = new Font(attributeName, "Arial");
		addChild(font);
		font.wrap(wrap);
		return font;
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
			Object attributeParent,
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
			AbstractAtom<?> attributeParent,
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
			AbstractAtom<?> attributeParent,
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
			AbstractAtom<?> modelEntryAtom) {
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
			AbstractAtom<?> modelEntryPoint,
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

	//#region QUANTITY VARIABLE

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

	//#region DOUBLE VARIABLE

	public DoubleVariableField createDoubleVariableField(
			Attribute<Double> wrap,
			Object attributeParent,
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
			Object attributeParent,
			String label) {

		String attributeName = getFieldName(wrap, attributeParent);
		DoubleVariableListField variableListField = new DoubleVariableListField(attributeName);
		variableListField.setLabel(label);

		addChild(variableListField);
		variableListField.wrap(wrap);
		return variableListField;

	}

	//#end region

	//#region INTEGER VARIABLE

	public IntegerVariableField createIntegerVariableField(
			Attribute<Integer> wrap,
			Object attributeParent,
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
			Object attributeParent,
			String label) {
		String attributeName = getFieldName(wrap, attributeParent);
		IntegerVariableListField variableListField = new IntegerVariableListField(attributeName);
		variableListField.setLabel(label);

		addChild(variableListField);
		variableListField.wrap(wrap);
		return variableListField;

	}

	//#end region

	//#region VARIABLE LIST

	public VariableList createVariableList(
			Attribute<List<VariableField<?, ?>>> wrap,
			Object attributeParent,
			String label) {
		String attributeName = getFieldName(wrap, attributeParent);
		VariableList variableList = new VariableList(attributeName, null);
		variableList.setLabel(label);
		addChild(variableList);
		variableList.wrap(wrap);
		return variableList;
	}

	//#end region

	//#region FILE PATH

	public FilePath createFilePath(Attribute<String> wrap, Object attributeParent, String defaultPath) {
		String attributeName = getFieldName(wrap, attributeParent);
		FilePath filePath = new FilePath(attributeName);
		filePath.setDefaultValue(defaultPath);
		addChild(filePath);
		filePath.wrap(wrap);
		return filePath;
	}

	public FilePath createFilePath(Attribute<String> wrap, Object attributeParent, String label, String defaultPath) {
		FilePath filePath = createFilePath(wrap, attributeParent, defaultPath);
		filePath.setLabel(label);
		return filePath;
	}

	public FilePath createFilePath(
			Attribute<String> wrap,
			Object attributeParent,
			String label,
			String defaultPath,
			Boolean validatePath) {
		FilePath filePath = createFilePath(wrap, attributeParent, defaultPath);
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

	private FilePathList createFilePathList(String name) {
		FilePathList filePathList = new FilePathList(name);
		addChild(filePathList);
		return filePathList;
	}

	//#end region

	//#region DIRECTORY PATH

	public DirectoryPath createDirectoryPath(
			Attribute<String> wrap,
			Object attributeParent,
			String label,
			String defaultPath) {
		DirectoryPath directoryPath = createDirectoryPath(wrap, attributeParent, defaultPath);
		directoryPath.setLabel(label);
		return directoryPath;
	}

	public DirectoryPath createDirectoryPath(Attribute<String> wrap, Object attributeParent, String defaultPath) {
		String attributeName = getFieldName(wrap, attributeParent);
		DirectoryPath directoryPath = createDirectoryPath(attributeName);
		directoryPath.setDefaultValue(defaultPath);
		directoryPath.wrap(wrap);
		return directoryPath;
	}

	void addDirectoryPath(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this, "myDirectoryPath");
		createDirectoryPath(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	private DirectoryPath createDirectoryPath(String name) {
		DirectoryPath directoryPath = new DirectoryPath(name);
		addChild(directoryPath);
		return directoryPath;
	}

	//#end region

	//#region FILE OR DIRECTORY PATH

	private FileOrDirectoryPath createFileOrDirectoryPath(
			Attribute<String> wrap,
			Object attributeParent,
			String defaultPath) {
		String attributeName = getFieldName(wrap, attributeParent);
		FileOrDirectoryPath fileOrDirectoryPath = new FileOrDirectoryPath(attributeName);
		fileOrDirectoryPath.setDefaultValue(defaultPath);
		addChild(fileOrDirectoryPath);
		fileOrDirectoryPath.wrap(wrap);
		return fileOrDirectoryPath;
	}

	public FileOrDirectoryPath createFileOrDirectoryPath(
			Attribute<String> wrap,
			Object attributeParent,
			String label,
			String defaultPath) {
		FileOrDirectoryPath fileOrDirectoryPath = createFileOrDirectoryPath(wrap, attributeParent, defaultPath);
		fileOrDirectoryPath.setLabel(label);
		return fileOrDirectoryPath;
	}

	public FileOrDirectoryPath createFileOrDirectoryPath(
			Attribute<String> wrap,
			Object attributeParent,
			String label,
			String defaultPath,
			Boolean validatePath) {
		FileOrDirectoryPath fileOrDirectoryPath = createFileOrDirectoryPath(wrap, attributeParent, defaultPath);
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

	private DirectoryPathList createDirectoryPathList(String name) {
		DirectoryPathList directoryPathList = new DirectoryPathList(name);
		addChild(directoryPathList);
		return directoryPathList;
	}

	//#end region

	//#region SPACER

	private void addSpacer(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this, "mySpacer");
		createSpacer(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	private Spacer createSpacer(String name) {
		Spacer spacer = new Spacer(name);
		addChild(spacer);
		return spacer;
	}

	//#end region

	//#region SYMBOL TYPE

	public SymbolType createSymbolType(
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

	//#end region

	//#region SIZE

	public Size createSize(Attribute<String> wrap, Object attributeParent) {
		String attributeName = getFieldName(wrap, attributeParent);
		Size size = new Size(attributeName);
		addChild(size);
		size.wrap(wrap);
		return size;
	}

	public Size createSize(Attribute<String> wrap, Object attributeParent, String defaultValue) {
		String attributeName = getFieldName(wrap, attributeParent);
		Size size = new Size(attributeName);
		size.setDefaultValue(defaultValue);
		addChild(size);
		size.wrap(wrap);
		return size;
	}

	public Size createSize(Attribute<String> wrap, Object attributeParent, String label, String defaultValue) {
		String attributeName = getFieldName(wrap, attributeParent);
		Size size = new Size(attributeName);
		size.setDefaultValue(defaultValue);
		size.setLabel(label);
		addChild(size);
		size.wrap(wrap);
		return size;
	}

	//#end region

	//#region FUNCTION PLOTTER

	public FunctionPlotter createFunctionPlotter(Attribute<String> wrap, Object attributeParent) {
		String attributeName = getFieldName(wrap, attributeParent);
		FunctionPlotter plotter = new FunctionPlotter(attributeName);
		addChild(plotter);
		plotter.wrap(wrap);
		return plotter;
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

	public boolean isExpanded() {
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

	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public Section setEnabled(boolean enable) {
		this.isEnabled = enable;
		if (controlProvider != null) {
			controlProvider.setEnabled(enable);
		}
		return getThis();
	}

	//#end region

}
