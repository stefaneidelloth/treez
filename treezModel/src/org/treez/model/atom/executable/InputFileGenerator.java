package org.treez.model.atom.executable;

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
import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.adjustable.AdjustableAtomCodeAdaption;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.ModelPathSelectionType;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.attribute.TextField;
import org.treez.core.atom.variablefield.QuantityVariableField;
import org.treez.core.atom.variablefield.VariableField;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.model.Activator;
import org.treez.model.atom.genericInput.GenericInputModel;

/**
 * The purpose of this atom is to generate an input text file that can be used as input for other atoms, e.g. the
 * Executable. It reads a template text file and replaces "tags"/"place holders" with Quantities. The filled template is
 * then saved as new input file at the wanted input file path.
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class InputFileGenerator extends AdjustableAtom {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(InputFileGenerator.class);

	//#region ATTRIBUTES

	private static final String NAME_TAG = "<name>";

	private static final String LABEL_TAG = "<label>";

	private static final String VALUE_TAG = "<value>";

	private static final String UNIT_TAG = "<unit>";

	/**
	 *
	 */
	public final Attribute<String> templateFilePath = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> sourceModel = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> nameExpression = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> valueExpression = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> inputFilePath = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<Boolean> deleteUnassignedRows = new Wrap<>();

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public InputFileGenerator(String name) {
		super(name);
		setRunnable();
		createInputFileGeneratorModel();
	}

	//#end region

	//#region METHODS

	/**
	 * Creates the model for this atom
	 */
	private void createInputFileGeneratorModel() {

		AttributeRoot root = new AttributeRoot("root");
		Page dataPage = root.createPage("data", "   Data   ");

		String relativeHelpContextId = "inputFileGenerator";
		String helpContextId = Activator.getAbsoluteHelpContextIdStatic(relativeHelpContextId);

		Section data = dataPage.createSection("data", "", helpContextId);
		data.createSectionAction("action", "Generate input file", () -> execute(treeViewRefreshable));
		//template
		data.createFilePath(templateFilePath, "templateFilePath",
				"Template for input file (contains variable place holders)", "C:/template.txt");

		//variable source model
		String defaultValue = "root.models.genericModel";
		ModelPathSelectionType selectionType = ModelPathSelectionType.FLAT;
		String label = "Variable source model (provides variables)";
		data.createModelPath(sourceModel, "sourceModel", label, defaultValue, GenericInputModel.class, selectionType,
				this, false);

		//label width
		final int prefferedLabelWidth = 180;

		//name expression
		TextField nameExpressionTextField = data.createTextField(nameExpression, "nameExpression",
				"Style for variable place holder", "{$" + LABEL_TAG + "$}");
		nameExpressionTextField.setPrefferedLabelWidth(prefferedLabelWidth);

		//value & unit expression
		String defaultValueExpression = "" + VALUE_TAG + " [" + UNIT_TAG + "]";
		TextField valueExpressionTextField = data.createTextField(valueExpression, "valueExpression",
				"Style for value and unit injection", defaultValueExpression);
		valueExpressionTextField.setPrefferedLabelWidth(prefferedLabelWidth);

		//path to input file (=the output of this atom)
		data.createFilePath(inputFilePath, "inputFilePath", "Input file to generate", "C:/generated_input_file.txt",
				false);

		//enable deletion of template rows with unassigned variable place holders
		data.createCheckBox(deleteUnassignedRows, "deleteUnassignedRows",
				"Delete template rows with unassigned variable place holders.", true);

		setModel(root);
	}

	@Override
	public void execute(Refreshable refreshable) {

		sysLog.info("Executing " + this.getClass().getSimpleName() + " '" + getName() + "'");

		//delete old input file (=the output of this atom) if it exists
		File inputFile = new File(inputFilePath.get());
		if (inputFile.exists()) {
			inputFile.delete();
		}

		//read template file
		String templateString = readTemplateFile(templateFilePath.get());

		//replace variable place holders with variable values
		GenericInputModel sourceModelAtom = (GenericInputModel) getChildFromRoot(this.sourceModel.get());
		String inputFileString = applyTemplateToSourceModel(templateString, sourceModelAtom, nameExpression.get(),
				valueExpression.get(), deleteUnassignedRows.get());

		//save result as new input file
		saveResult(inputFileString, inputFilePath.get());

	}

	/**
	 * Applies the template to the variable model. This means that the variable place holders in the template string are
	 * replaced by the variable values for all variables that are provided by the variable source model.
	 *
	 * @param templateString
	 * @param sourceModel
	 * @return
	 */
	private static String applyTemplateToSourceModel(
			String templateString,
			GenericInputModel sourceModel,
			String nameExpression,
			String valueExpression,
			Boolean deleteUnassignedRows) {

		String resultString = templateString;

		List<VariableField> variableFields = sourceModel.getEnabledVariableFields();
		for (VariableField variableField : variableFields) {
			String variableName = variableField.getName();
			String variableLabel = variableField.getLabel();
			String valueString = variableField.getValueString(); //e.g. "1"

			String unitString = "";
			boolean isQuantityVariableField = variableField instanceof QuantityVariableField;
			if (isQuantityVariableField) {
				QuantityVariableField quantityField = (QuantityVariableField) variableField;
				unitString = quantityField.getUnitString(); //e.g. "m"
			}

			//get regular expression to replace
			String placeholderExpression = nameExpression.replace(NAME_TAG, variableName);
			placeholderExpression = placeholderExpression.replace(LABEL_TAG, variableLabel);

			//get expression to inject
			if (valueString == null) {
				String message = "Value for variable '" + variableName + "' is null.";
				sysLog.warn(message);
				valueString = "null";
			}

			String injectedExpression;
			injectedExpression = valueExpression.replace(VALUE_TAG, valueString);
			if (unitString != null) {
				injectedExpression = injectedExpression.replace(UNIT_TAG, unitString);
			} else {
				//remove unit tag
				injectedExpression = injectedExpression.replace(UNIT_TAG, "");
			}

			//inject expression into template
			sysLog.info("Template placeholder to replace: '" + placeholderExpression + "'");
			sysLog.info("Expression to inject: '" + injectedExpression + "'");
			resultString = resultString.replace(placeholderExpression, injectedExpression);

		}

		if (deleteUnassignedRows) {
			resultString = deleteRowsWithUnassignedPlaceHolders(nameExpression, resultString);
		}

		return resultString;
	}

	private static String deleteRowsWithUnassignedPlaceHolders(String nameExpression, String resultString) {
		String generalPlaceHolderExpression = nameExpression.replace(NAME_TAG, ".*?");
		generalPlaceHolderExpression = generalPlaceHolderExpression.replace(LABEL_TAG, ".*?");
		generalPlaceHolderExpression = generalPlaceHolderExpression.replace("{", "\\{");
		generalPlaceHolderExpression = generalPlaceHolderExpression.replace("}", "\\}");
		generalPlaceHolderExpression = generalPlaceHolderExpression.replace("$", "\\$");

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
			sysLog.info(message);
		}
		return newResultString;
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("inputFile.png");
	}

	/**
	 * Creates the context menu actions
	 */
	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {

		//no actions available right now

		return actions;
	}

	/**
	 * Returns the code adaption
	 */
	@Override
	public CodeAdaption createCodeAdaption(ScriptType scriptType) {
		return new AdjustableAtomCodeAdaption(this);
	}

	/**
	 * Reads the template file
	 *
	 * @param templatePath
	 * @return
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
	 *
	 * @param text
	 * @param inputPath
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

	//#end region

}
