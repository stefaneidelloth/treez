package org.treez.model.atom.inputFileGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.adjustable.AdjustableAtomCodeAdaption;
import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.attribute.attributeContainer.Page;
import org.treez.core.atom.attribute.attributeContainer.section.Section;
import org.treez.core.atom.attribute.checkBox.CheckBox;
import org.treez.core.atom.attribute.modelPath.ModelPath;
import org.treez.core.atom.attribute.modelPath.ModelPathSelectionType;
import org.treez.core.atom.attribute.text.TextField;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.uisynchronizing.AbstractUiSynchronizingAtom;
import org.treez.core.atom.variablefield.QuantityVariableField;
import org.treez.core.atom.variablefield.VariableField;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Consumer;
import org.treez.core.attribute.Wrap;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.model.Activator;
import org.treez.model.atom.executable.Executable;
import org.treez.model.atom.executable.InputPathModifier;
import org.treez.model.atom.executable.InputPathProvider;
import org.treez.model.atom.genericInput.GenericInputModel;

/**
 * The purpose of this atom is to generate an input text file that can be used as input for other atoms, e.g. the
 * Executable. It reads a template text file and replaces "tags"/"place holders" with Quantities. The filled template is
 * then saved as new input file at the wanted input file path.
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class InputFileGenerator extends AdjustableAtom implements InputPathProvider {

	private static final Logger LOG = LogManager.getLogger(InputFileGenerator.class);

	//#region ATTRIBUTES

	private static final String NAME_TAG = "<name>";

	private static final String LABEL_TAG = "<label>";

	private static final String VALUE_TAG = "<value>";

	private static final String UNIT_TAG = "<unit>";

	public final Attribute<String> templateFilePath = new Wrap<>();

	public final Attribute<String> sourceModel = new Wrap<>();

	public final Attribute<String> nameExpression = new Wrap<>();

	public final Attribute<String> valueExpression = new Wrap<>();

	public final Attribute<String> inputFilePath = new Wrap<>();

	public final Attribute<Boolean> deleteUnassignedRows = new Wrap<>();

	public final Attribute<Boolean> includeDateInInputFile = new Wrap<>();

	public final Attribute<Boolean> includeDateInInputFolder = new Wrap<>();

	public final Attribute<Boolean> includeDateInInputSubFolder = new Wrap<>();

	public final Attribute<Boolean> includeJobIndexInInputFile = new Wrap<>();

	public final Attribute<Boolean> includeJobIndexInInputFolder = new Wrap<>();

	public final Attribute<Boolean> includeJobIndexInInputSubFolder = new Wrap<>();

	public final Attribute<String> inputPathInfo = new Wrap<>();

	//#end region

	//#region CONSTRUCTORS

	public InputFileGenerator(String name) {
		super(name);
		setRunnable();
		createInputFileGeneratorModel();
	}

	public InputFileGenerator(InputFileGenerator atomToCopy) {
		super(atomToCopy);
		copyTreezAttributes(atomToCopy, this);
	}

	//#end region

	//#region METHODS

	@Override
	public InputFileGenerator copy() {
		return new InputFileGenerator(this);
	}

	/**
	 * Creates the model for this atom
	 */
	private void createInputFileGeneratorModel() {

		AttributeRoot root = new AttributeRoot("root");
		Page dataPage = root.createPage("data", "   Data   ");

		String relativeHelpContextId = "inputFileGenerator";
		String helpContextId = Activator.getAbsoluteHelpContextIdStatic(relativeHelpContextId);

		Section data = dataPage.createSection("data", helpContextId);
		data.setLabel("");
		data.createSectionAction("action", "Generate input file", () -> execute(treeViewRefreshable));
		//template
		data.createFilePath(templateFilePath, this, "Template for input file (contains variable place holders)",
				"C:/template.txt");

		//variable source model
		String defaultValue = "root.models.genericModel";
		ModelPathSelectionType selectionType = ModelPathSelectionType.FLAT;
		ModelPath sourceModelPath = data.createModelPath(sourceModel, this, defaultValue, GenericInputModel.class,
				selectionType, this, false);
		sourceModelPath.setLabel("Variable source model (provides variables)");

		//label width
		final int prefferedLabelWidth = 180;

		//name expression
		TextField nameExpressionTextField = data.createTextField(nameExpression, this, "{$" + LABEL_TAG + "$}");
		nameExpressionTextField.setLabel("Style for variable place holder");
		nameExpressionTextField.setPrefferedLabelWidth(prefferedLabelWidth);

		//value & unit expression
		String defaultValueExpression = "" + VALUE_TAG + " [" + UNIT_TAG + "]";
		TextField valueExpressionTextField = data.createTextField(valueExpression, this, defaultValueExpression);
		valueExpressionTextField.setLabel("Style for value and unit injection");
		valueExpressionTextField.setPrefferedLabelWidth(prefferedLabelWidth);

		//path to input file (=the output of this atom)
		data.createFilePath(inputFilePath, this, "Input file to generate", "C:/generated_input_file.txt", false);

		//enable deletion of template rows with unassigned variable place holders
		CheckBox deleteUnassigned = data.createCheckBox(deleteUnassignedRows, this, true);
		deleteUnassigned.setLabel("Delete template rows with unassigned variable place holders.");

		String inputModificationRelativeHelpContextId = "executableInputModification";
		String inputModificationHelpContextId = Activator
				.getAbsoluteHelpContextIdStatic(inputModificationRelativeHelpContextId);

		Consumer updateStatus = () -> refreshStatus();
		createInputModificationSection(dataPage, updateStatus, inputModificationHelpContextId);

		String statusRelativeHelpContextId = "statusLogging";
		String statusHelpContextId = Activator.getAbsoluteHelpContextIdStatic(statusRelativeHelpContextId);
		createStatusSection(dataPage, statusHelpContextId);

		refreshStatus();

		setModel(root);
	}

	private void createInputModificationSection(Page dataPage, Consumer updateStatusListener, String helpContextId) {

		Section inputModification = dataPage.createSection("inputModification", helpContextId);
		inputModification.setLabel("Input modification");
		inputModification.setExpanded(false);

		inputModification.createLabel("includeDate", "Include date in:");

		CheckBox dateInFolderCheck = inputModification.createCheckBox(includeDateInInputFolder, this, false);
		dateInFolderCheck.setLabel("Folder name");
		dateInFolderCheck.addModificationConsumer("updateStatus", updateStatusListener);

		CheckBox dateInSubFolderCheck = inputModification.createCheckBox(includeDateInInputSubFolder, this, false);
		dateInSubFolderCheck.setLabel("Extra folder");
		dateInSubFolderCheck.addModificationConsumer("updateStatus", updateStatusListener);

		CheckBox dateInFileCheck = inputModification.createCheckBox(includeDateInInputFile, this, false);
		dateInFileCheck.setLabel("File name");
		dateInFileCheck.addModificationConsumer("updateStatus", updateStatusListener);

		@SuppressWarnings("unused")
		org.treez.core.atom.attribute.text.Label jobIndexLabel = inputModification.createLabel("jobIndexLabel",
				"Include job index in:");

		CheckBox jobIndexInFolderCheck = inputModification.createCheckBox(includeJobIndexInInputFolder, this, false);
		jobIndexInFolderCheck.setLabel("Folder name");
		jobIndexInFolderCheck.addModificationConsumer("updateStatus", updateStatusListener);

		CheckBox jobIndexInSubFolderCheck = inputModification.createCheckBox(includeJobIndexInInputSubFolder, this,
				false);
		jobIndexInSubFolderCheck.setLabel("Extra folder");
		jobIndexInSubFolderCheck.addModificationConsumer("updateStatus", updateStatusListener);

		CheckBox jobIndexInFileCheck = inputModification.createCheckBox(includeJobIndexInInputFile, this, false);
		jobIndexInFileCheck.setLabel("File name");
		jobIndexInFileCheck.addModificationConsumer("updateStatus", updateStatusListener);
	}

	protected void createStatusSection(Page dataPage, String executableHelpContextId) {
		Section status = dataPage.createSection("status", executableHelpContextId);
		status.setExpanded(false);

		//resulting command
		status.createInfoText(inputPathInfo, this, "Resulting input file path", "");

	}

	protected void refreshStatus() {
		AbstractUiSynchronizingAtom.runUiTaskNonBlocking(() -> {

			String modifiedInputPath = getModifiedInputFilePath();
			inputPathInfo.set(modifiedInputPath);
		});
	}

	@Override
	public void execute(FocusChangingRefreshable refreshable) {

		LOG.info("Executing " + this.getClass().getSimpleName() + " '" + getName() + "'");

		String modifiedInputFilePath = getModifiedInputFilePath();

		//delete old input file (=the output of this atom) if it exists
		File inputFile = new File(modifiedInputFilePath);
		if (inputFile.exists()) {
			inputFile.delete();
		}

		//read template file
		String templateString = readTemplateFile(templateFilePath.get());

		//replace variable place holders with variable values
		GenericInputModel sourceModelAtom = getChildFromRoot(this.sourceModel.get());
		String inputFileString = applyTemplateToSourceModel(templateString, sourceModelAtom, nameExpression.get(),
				valueExpression.get(), deleteUnassignedRows.get());

		if (inputFileString.isEmpty()) {
			String message = "The input file '" + modifiedInputFilePath
					+ "' is empty. Please check the place holder and the source variables.";
			LOG.warn(message);
		}

		//save result as new input file
		saveResult(inputFileString, modifiedInputFilePath);

	}

	/**
	 * Applies the template to the variable model. This means that the variable place holders in the template string are
	 * replaced by the variable values for all variables that are provided by the variable source model.
	 */
	private static String applyTemplateToSourceModel(
			String templateString,
			GenericInputModel sourceModel,
			String nameExpression,
			String valueExpression,
			Boolean deleteUnassignedRows) {

		String resultString = templateString;

		List<VariableField<?, ?>> variableFields = sourceModel.getEnabledVariableFields();
		for (VariableField<?, ?> variableField : variableFields) {
			String variableName = variableField.getName();
			String variableLabel = variableField.getLabel();
			String valueString = variableField.getValueString(); //e.g. "1"

			String unitString = "";
			boolean isQuantityVariableField = variableField instanceof QuantityVariableField;
			if (isQuantityVariableField) {
				QuantityVariableField quantityField = (QuantityVariableField) variableField;
				unitString = quantityField.getUnitString(); //e.g. "m"
			}

			String placeholderExpression = createPlaceHolderExpression(nameExpression, variableName, variableLabel);

			String injectedExpression = createExpressionToInject(valueExpression, variableName, valueString,
					unitString);

			//inject expression into template
			LOG.info("Template placeholder to replace: '" + placeholderExpression + "'");
			LOG.info("Expression to inject: '" + injectedExpression + "'");
			resultString = resultString.replace(placeholderExpression, injectedExpression);

		}

		if (deleteUnassignedRows) {
			resultString = deleteRowsWithUnassignedPlaceHolders(nameExpression, resultString);
		}

		return resultString;
	}

	private static String createExpressionToInject(
			String valueExpression,
			String variableName,
			String valueString,
			String unitString) {

		String correctedValueString = valueString;
		if (valueString == null) {
			String message = "Value for variable '" + variableName + "' is null.";
			LOG.warn(message);
			correctedValueString = "null";
		}

		String injectedExpression;
		injectedExpression = valueExpression.replace(VALUE_TAG, correctedValueString);
		if (unitString != null) {
			injectedExpression = injectedExpression.replace(UNIT_TAG, unitString);
		} else {
			//remove unit tag
			injectedExpression = injectedExpression.replace(UNIT_TAG, "");
		}
		return injectedExpression;
	}

	private static
			String
			createPlaceHolderExpression(String nameExpression, String variableName, String variableLabel) {
		String placeholderExpression;
		boolean containsName = nameExpression.contains(NAME_TAG);
		if (containsName) {
			placeholderExpression = nameExpression.replace(NAME_TAG, variableName);
		} else {
			boolean containsLabel = nameExpression.contains(LABEL_TAG);
			if (containsLabel) {
				placeholderExpression = nameExpression.replace(LABEL_TAG, variableLabel);
			} else {
				String message = "The placeholder must contain either a " + NAME_TAG + " or a " + LABEL_TAG + " tag.";
				throw new IllegalStateException(message);
			}
		}
		return placeholderExpression;
	}

	private static String deleteRowsWithUnassignedPlaceHolders(String nameExpression, String resultString) {
		String generalPlaceHolderExpression = nameExpression.replace("{", "\\{");
		generalPlaceHolderExpression = generalPlaceHolderExpression.replace("}", "\\}");
		generalPlaceHolderExpression = generalPlaceHolderExpression.replace("$", "\\$");
		generalPlaceHolderExpression = generalPlaceHolderExpression.replace("<name>", ".*");
		generalPlaceHolderExpression = generalPlaceHolderExpression.replace("<label>", ".*");

		if (generalPlaceHolderExpression.equals(".*")) {
			String message = "The deletion of rows with unassigned place holders is not yet implemented for place holders"
					+ "of the type '" + nameExpression
					+ "'. Please adapt the name expression or disable the deletion of template rows"
					+ "with unassigned varaible place holders.";
			LOG.warn(message);
			return resultString;
		}

		String[] lines = resultString.split("\n");
		List<String> removedLines = new ArrayList<>();
		List<String> newLines = new ArrayList<>();

		Pattern pattern = Pattern.compile(generalPlaceHolderExpression);

		for (String line : lines) {
			Matcher matcher = pattern.matcher(line);
			boolean containsUnassignedPlaceHolder = matcher.find();
			if (containsUnassignedPlaceHolder) {
				removedLines.add(line);
			} else {
				newLines.add(line);
			}
		}
		String newResultString = String.join("\n", newLines);
		if (!removedLines.isEmpty()) {
			String message = "Some rows with unassigned variable place holders have been removed from the input file:\n"
					+ String.join("\n", removedLines);
			LOG.info(message);
		}
		return newResultString;
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("inputFile.png");
	}

	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {

		//no actions available right now

		return actions;
	}

	@Override
	public CodeAdaption createCodeAdaption(ScriptType scriptType) {
		return new AdjustableAtomCodeAdaption(this);
	}

	/**
	 * Reads the template file
	 */
	private static String readTemplateFile(String templatePath) {

		Path path = Paths.get(templatePath);
		Charset charSet = Charset.forName("UTF-8");
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(path);
			String text = new String(encoded, charSet);
			return text;
		} catch (IOException exception) {
			String message = "Could not read file '" + templatePath + "'.";
			throw new IllegalStateException(message, exception);
		}
	}

	/**
	 * Saves the given text as text file with the given file path
	 */
	private static void saveResult(String text, String filePath) {
		File file = new File(filePath);
		try {
			FileUtils.writeStringToFile(file, text);
		} catch (IOException exception) {
			String message = "Could not write text to file '" + filePath + "'.";
			throw new IllegalStateException(message, exception);
		}

	}

	public String getModifiedInputFilePath() {
		InputPathModifier inputPathModifier = new InputPathModifier(this);

		String modifiedInputPath = inputPathModifier.getModifiedInputPath(inputFilePath.get());
		return modifiedInputPath;
	}

	//#region INPUT FILE PROVIDER

	@Override
	public boolean getIncludeDateInInputFolder() {
		return includeDateInInputFolder.get();
	}

	@Override
	public boolean getIncludeDateInInputSubFolder() {
		return includeDateInInputSubFolder.get();
	}

	@Override
	public boolean getIncludeDateInInputFile() {
		return includeDateInInputFile.get();
	}

	@Override
	public boolean getIncludeJobIndexInInputFile() {
		return includeJobIndexInInputFile.get();
	}

	@Override
	public boolean getIncludeJobIndexInInputFolder() {
		return includeJobIndexInInputFolder.get();
	}

	@Override
	public boolean getIncludeJobIndexInInputSubFolder() {
		return includeJobIndexInInputSubFolder.get();
	}

	@Override
	public String getJobName() {

		AbstractAtom<?> parent = this.getParentAtom();
		boolean parentIsExecutable = parent instanceof Executable;
		if (parentIsExecutable) {
			Executable executable = (Executable) parent;
			return executable.getJobName();
		}

		return "{unknownjobName}";
	}

	//#end region

	//#end region

}
