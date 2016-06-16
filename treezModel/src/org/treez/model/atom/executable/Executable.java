package org.treez.model.atom.executable;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.adjustable.AdjustableAtomCodeAdaption;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.CheckBox;
import org.treez.core.atom.attribute.FileOrDirectoryPath;
import org.treez.core.atom.attribute.FilePath;
import org.treez.core.atom.attribute.InfoText;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.attribute.TextField;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.AddChildAtomTreeViewerAction;
import org.treez.core.utils.Utils;
import org.treez.model.Activator;
import org.treez.model.atom.AbstractModel;
import org.treez.model.output.ModelOutput;

/**
 * Represents an external executable that can be executed with additional command line arguments and file paths
 */
@SuppressWarnings({ "checkstyle:visibilitymodifier", "checkstyle:classfanoutcomplexity" })
public class Executable extends AbstractModel implements FilePathProvider {

	private static final Logger LOG = Logger.getLogger(Executable.class);

	//#region ATTRIBUTES

	public final Attribute<String> executablePath = new Wrap<>();

	public final Attribute<String> inputArguments = new Wrap<>();

	public final Attribute<String> inputPath = new Wrap<>();

	public final Attribute<String> outputArguments = new Wrap<>();

	public final Attribute<String> outputPath = new Wrap<>();

	private String modifiedOutputPath;

	public final Attribute<Boolean> copyInputFile = new Wrap<>();

	public final Attribute<Boolean> includeDateInFile = new Wrap<>();

	public final Attribute<Boolean> includeDateInFolder = new Wrap<>();

	public final Attribute<Boolean> includeDateInSubFolder = new Wrap<>();

	public final Attribute<Boolean> includeStudyIndexInFile = new Wrap<>();

	public final Attribute<Boolean> includeStudyIndexInFolder = new Wrap<>();

	public final Attribute<Boolean> includeStudyIndexInSubFolder = new Wrap<>();

	public final Attribute<String> logArguments = new Wrap<>();

	public final Attribute<String> logFilePath = new Wrap<>();

	public final Attribute<String> commandInfo = new Wrap<>();

	public final Attribute<String> executionStatusInfo = new Wrap<>();

	public final Attribute<String> studyIndexInfo = new Wrap<>();

	//#end region

	//#region CONSTRUCTORS

	public Executable(String name) {
		super(name);
		setRunnable();

		AttributeRoot root = new AttributeRoot("root");
		Page dataPage = root.createPage("data", "   Data   ");

		//update status listener
		ModifyListener updateStatusListener = (ModifyEvent e) -> refreshStatus();

		//create sections
		String executableRelativeHelpContextId = "executable";
		String executableHelpContextId = Activator.getAbsoluteHelpContextIdStatic(executableRelativeHelpContextId);
		createExecutableSection(dataPage, updateStatusListener, executableHelpContextId);

		String inputRelativeHelpContextId = "executableInput";
		String inputHelpContextId = Activator.getAbsoluteHelpContextIdStatic(inputRelativeHelpContextId);
		createInputSection(dataPage, updateStatusListener, inputHelpContextId);

		String outputRelativeHelpContextId = "executableOutput";
		String outputHelpContextId = Activator.getAbsoluteHelpContextIdStatic(outputRelativeHelpContextId);
		createOutputSection(dataPage, updateStatusListener, outputHelpContextId);

		String outputModificationRelativeHelpContextId = "executableOutputModification";
		String outputModificationHelpContextId = Activator
				.getAbsoluteHelpContextIdStatic(outputModificationRelativeHelpContextId);
		createOutputModificationSection(dataPage, updateStatusListener, outputModificationHelpContextId);

		String loggingRelativeHelpContextId = "executableLogging";
		String loggingHelpContextId = Activator.getAbsoluteHelpContextIdStatic(loggingRelativeHelpContextId);
		createLoggingSection(dataPage, updateStatusListener, loggingHelpContextId);

		String statusRelativeHelpContextId = "statusLogging";
		String statusHelpContextId = Activator.getAbsoluteHelpContextIdStatic(statusRelativeHelpContextId);
		createStatusSection(dataPage, statusHelpContextId);

		//set model
		setModel(root);

	}

	//#end region

	//#region METHODS

	@Override
	public Executable getThis() {
		return this;
	}

	private void createExecutableSection(
			Page dataPage,
			ModifyListener updateStatusListener,
			String executableHelpContextId) {
		Section executable = dataPage.createSection("executable", executableHelpContextId);
		Image resetImage = Activator.getImage("resetStudyIndex.png");
		executable.createSectionAction("resetStudyIndex", "Reset the study index to 1", () -> resetStudyIndex(),
				resetImage);
		executable.createSectionAction("action", "Run external executable", () -> execute(treeViewRefreshable));

		FilePath filePath = executable.createFilePath(executablePath, this, "Executable", "notepad.exe");
		filePath.addModifyListener("updateStatus", updateStatusListener);

	}

	private void createInputSection(
			Page dataPage,
			ModifyListener updateStatusListener,
			String executableHelpContextId) {
		Section input = dataPage.createSection("input", executableHelpContextId);

		TextField argumentTextField = input.createTextField(inputArguments, this, "");
		argumentTextField.setLabel("Input arguments");
		argumentTextField.addModifyListener("updateStatus", updateStatusListener);
		argumentTextField.setHelpId("org.eclipse.ui.ide.executable");

		FileOrDirectoryPath inputPathChooser = input.createFileOrDirectoryPath(inputPath, this, "Input file or folder",
				"");
		inputPathChooser.addModifyListener("updateStatus", updateStatusListener);
	}

	private void createOutputSection(
			Page dataPage,
			ModifyListener updateStatusListener,
			String executableHelpContextId) {
		Section output = dataPage.createSection("output", executableHelpContextId);

		TextField outputArgs = output.createTextField(outputArguments, this, "");
		outputArgs.setLabel("Output arguments");
		outputArgs.addModifyListener("updateStatus", updateStatusListener);

		FileOrDirectoryPath outputPathChooser = output.createFileOrDirectoryPath(outputPath, this,
				"Output file or folder", "", false);
		outputPathChooser.addModifyListener("updateStatus", updateStatusListener);

		CheckBox copyInputField = output.createCheckBox(copyInputFile, this, true);
		copyInputField.setLabel("Copy input file");
	}

	private void createOutputModificationSection(
			Page dataPage,
			ModifyListener updateStatusListener,
			String executableHelpContextId) {
		Section outputModification = dataPage.createSection("outputModification", executableHelpContextId);
		outputModification.setLabel("Output modification");
		outputModification.setExpanded(false);

		outputModification.createLabel("includeDate", "Include date in:");

		CheckBox dateInFolderCheck = outputModification.createCheckBox(includeDateInFolder, this, false);
		dateInFolderCheck.setLabel("Folder name");
		dateInFolderCheck.addModifyListener("updateStatus", updateStatusListener);

		CheckBox dateInSubFolderCheck = outputModification.createCheckBox(includeDateInSubFolder, this, false);
		dateInFolderCheck.setLabel("Extra folder");
		dateInSubFolderCheck.addModifyListener("updateStatus", updateStatusListener);

		CheckBox dateInFileCheck = outputModification.createCheckBox(includeDateInFile, this, false);
		dateInFileCheck.setLabel("File name");
		dateInFileCheck.addModifyListener("updateStatus", updateStatusListener);

		@SuppressWarnings("unused")
		org.treez.core.atom.attribute.Label studyIndexLabel = outputModification.createLabel("studyIndexLabel",
				"Include study index in:");

		CheckBox studyIndexInFolderCheck = outputModification.createCheckBox(includeStudyIndexInFolder, this, false);
		studyIndexInFolderCheck.setLabel("Folder name");
		studyIndexInFolderCheck.addModifyListener("updateStatus", updateStatusListener);

		CheckBox studyIndexInSubFolderCheck = outputModification.createCheckBox(includeStudyIndexInSubFolder, this,
				false);
		studyIndexInSubFolderCheck.setLabel("Extra folder");
		studyIndexInSubFolderCheck.addModifyListener("updateStatus", updateStatusListener);

		CheckBox studyIndexInFileCheck = outputModification.createCheckBox(includeStudyIndexInFile, this, false);
		studyIndexInFileCheck.setLabel("File name");
		studyIndexInFileCheck.addModifyListener("updateStatus", updateStatusListener);
	}

	private void createLoggingSection(
			Page dataPage,
			ModifyListener updateStatusListener,
			String executableHelpContextId) {
		Section logging = dataPage.createSection("logging", executableHelpContextId);
		logging.setExpanded(false);

		TextField logArgumentsText = logging.createTextField(logArguments, this, "");
		logArgumentsText.setLabel("Log arguments");
		logArgumentsText.addModifyListener("updateStatus", updateStatusListener);

		FilePath logFilePathChooser = logging.createFilePath(logFilePath, this, "Log file", "", false);
		logFilePathChooser.addModifyListener("updateStatus", updateStatusListener);
	}

	private void createStatusSection(Page dataPage, String executableHelpContextId) {
		Section status = dataPage.createSection("status", executableHelpContextId);
		status.setExpanded(false);

		//resulting command
		status.createInfoText(commandInfo, this, "Resulting command", "");

		//execution status
		status.createInfoText(executionStatusInfo, "executionStatusInfo", "Execution status", "Not yet executed.");

		//study index
		status.createInfoText(studyIndexInfo, "studyIndexInfo", "Next study index", "1");
	}

	@Override
	protected void afterCreateControlAdaptionHook() {
		//update info text
		refreshStatus();
	}

	/**
	 * Updates the status text labels with data from other attribute atoms
	 */
	private void refreshStatus() {
		this.runUiJobNonBlocking(() -> {
			String infoTextMessage = buildCommand();
			//LOG.debug("Updating info text: " + infoTextMessage);
			commandInfo.set(infoTextMessage);

			Wrap<String> infoTextWrap = (Wrap<String>) executionStatusInfo;
			InfoText executionStatusInfoText = (InfoText) infoTextWrap.getAttribute();
			executionStatusInfoText.resetError();
			executionStatusInfoText.set("Not yet executed");

			studyIndexInfo.set("" + getStudyId());
		});

	}

	@Override
	public ModelOutput runModel(FocusChangingRefreshable refreshable, IProgressMonitor monitor) {

		String startMessage = "Running " + this.getClass().getSimpleName() + " '" + getName() + "'.";
		LOG.info(startMessage);

		//initialize progress monitor
		final int totalWork = 3;
		monitor.beginTask(startMessage, totalWork);

		//delete old output file and old log file if they exist
		delteOldOutputAndLogFiles();

		//update progress monitor
		monitor.subTask("=>Running InputFileGenerator children if exist.");

		//execute input file generator child(s) if exist
		executeInputFileGenerator(refreshable);

		//update progress monitor
		monitor.worked(1);

		//update progress monitor
		monitor.subTask("=>Executiong system command.");

		//create command
		String command = buildCommand();
		LOG.info("Executing " + command);

		//execute command
		ExecutableExecutor executor = new ExecutableExecutor(this);
		executor.executeCommand(command);

		//update progress monitor
		monitor.worked(1);

		//create model output
		monitor.subTask("=>Post processing model output.");
		ModelOutput modelOutput = createEmptyModelOutput();
		//if (successful) {
		//execute data import child(s) if exist
		ModelOutput dataImportOutput = runDataImport(refreshable, monitor);
		modelOutput.addChildOutput(dataImportOutput);
		//}

		//copy input file to output folder (modifies input file name)
		if (copyInputFile.get()) {
			copyInputFileToOutputFolder();
		}

		//increase study index
		increaseStudyIndex();

		//inform progress monitor to be done
		monitor.done();

		return modelOutput;
	}

	private void increaseStudyIndex() {
		int currentIndex = 0;
		try {
			String studyId = getStudyId();
			currentIndex = Integer.parseInt(studyId);
		} catch (NumberFormatException exception) {
			LOG.warn("Could not interpret last studyId as Integer. "
					+ "Starting with 1 for the next study index of the executable.");
		}
		int newIndex = currentIndex + 1;
		setStudyId("" + newIndex);
		refreshStatus();
	}

	/**
	 * Resets the error state of the status info text
	 */
	public void resetError() {
		if (executionStatusInfo != null) {
			Wrap<String> infoTextWrap = (Wrap<String>) executionStatusInfo;
			InfoText infoText = (InfoText) infoTextWrap.getAttribute();
			infoText.resetError();
		}

	}

	/**
	 * Shows the info text in error state
	 */
	public void highlightError() {
		if (executionStatusInfo != null) {
			Wrap<String> infoTextWrap = (Wrap<String>) executionStatusInfo;
			InfoText infoText = (InfoText) infoTextWrap.getAttribute();
			infoText.highlightError();
		}

	}

	/**
	 * Copies input file to output folder and modifies the file name
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private void copyInputFileToOutputFolder() {
		String inputFilePath = inputPath.get();
		File inputFile = new File(inputFilePath);
		if (inputFile.exists()) {
			String destinationPath = null;
			try {
				destinationPath = getOutputPathToCopyInputFile();
			} catch (Exception exception) {
				LOG.warn("Input file is not copied to output folder since output folder is not known.");
			}
			if (destinationPath != null) {
				copyInputFileToOutputFolder(inputFile, destinationPath);
			}
		}

	}

	/**
	 * Copies the given inputFile to the given destination path
	 *
	 * @param inputFile
	 * @param destinationPath
	 */
	private static void copyInputFileToOutputFolder(File inputFile, String destinationPath) {
		File destinationFile = new File(destinationPath);

		try {
			FileUtils.copyFile(inputFile, destinationFile);
		} catch (IOException exception) {
			String message = "Could not copy input file to output folder";
			LOG.error(message, exception);

		}
	}

	/**
	 * Returns the destination folder for the input file
	 *
	 * @return
	 */
	private String getOutputPathToCopyInputFile() {

		String outputPathString = modifiedOutputPath;

		//split path with point to determine file extension if one exists

		boolean isFilePath = Utils.isFilePath(outputPathString);
		String folderPath = modifiedOutputPath;
		if (isFilePath) {
			folderPath = Utils.extractParentFolder(outputPathString);
		}

		String inputPathString = inputPath.get();
		boolean inputPathIsFilePath = Utils.isFilePath(inputPathString);
		if (inputPathIsFilePath) {
			String inputFileName = Utils.extractFileName(inputPathString);
			String newInputFileName = Utils.includeNumberInFileName(inputFileName, "#" + getStudyId());
			String destinationPath = folderPath + "/" + newInputFileName;
			return destinationPath;
		} else {
			return null;
		}

	}

	/**
	 * Executes all children that are of type InputFileGenerator
	 */
	private void executeInputFileGenerator(FocusChangingRefreshable refreshable) {
		executeChildren(InputFileGenerator.class, refreshable);

	}

	/**
	 * Executes all children that are of type DataImport
	 */
	private ModelOutput runDataImport(FocusChangingRefreshable refreshable, IProgressMonitor monitor) {
		boolean hasDataImportChild = hasChildModel(TableImport.class);
		if (hasDataImportChild) {
			ModelOutput modelOutput = runChildModel(TableImport.class, refreshable, monitor);
			return modelOutput;
		} else {
			LOG.info("No data has been imported since there is no DataImport child.");
			ModelOutput emptyModelOutput = createEmptyModelOutput();
			return emptyModelOutput;
		}
	}

	/**
	 * Deletes the old output and log files if some exist
	 */
	private void delteOldOutputAndLogFiles() {
		File outputFile = new File(outputPath.get());
		if (outputFile.exists()) {
			outputFile.delete();
		}
		File logFile = new File(logFilePath.get());
		if (logFile.exists()) {
			logFile.delete();
		}
	}

	/**
	 * Builds the execution command from the individual paths and arguments
	 *
	 * @return
	 */
	private String buildCommand() {
		String command = "\"" + executablePath.get() + "\"";
		boolean inputArgsIsEmpty = inputArguments.get().isEmpty();
		if (!inputArgsIsEmpty) {
			command += " " + inputArguments;
		}

		boolean inputPathIsEmpty = inputPath.get().isEmpty();
		if (!inputPathIsEmpty) {
			command += " " + inputPath;
		}

		boolean outputArgsIsEmpty = outputArguments.get().isEmpty();
		if (!outputArgsIsEmpty) {
			command += " " + outputArguments;
		}

		boolean outputPathIsEmpty = outputPath.get().isEmpty();
		if (!outputPathIsEmpty) {
			modifiedOutputPath = provideFilePath();
			command += " " + modifiedOutputPath;
		}

		boolean logArgsIsEmpty = logArguments.get().isEmpty();
		if (!logArgsIsEmpty) {
			command += " " + logArguments;
		}

		boolean logFilePathIsEmpty = logFilePath.get().isEmpty();
		if (!logFilePathIsEmpty) {
			command += " " + logFilePath;
		}

		return command;
	}

	/**
	 * Resets the study index
	 */
	public void resetStudyIndex() {
		setStudyId("1");
		refreshStatus();
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("run.png");
	}

	/**
	 * Creates the context menu actions for this atom
	 */
	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {

		Action addInputFileGenerator = new AddChildAtomTreeViewerAction(
				InputFileGenerator.class,
				"inputFileGenerator",
				Activator.getImage("inputFile.png"),
				this,
				treeViewer);
		actions.add(addInputFileGenerator);

		Action addDataImport = new AddChildAtomTreeViewerAction(
				TableImport.class,
				"tableImport",
				Activator.getImage("tableImport.png"),
				this,
				treeViewer);
		actions.add(addDataImport);

		return actions;
	}

	/**
	 * Returns the code adaption
	 */
	@Override
	public CodeAdaption createCodeAdaption(ScriptType scriptType) {
		return new AdjustableAtomCodeAdaption(this);
	}

	//#region CREATE CHILD ATOMS

	/**
	 * Creates a InputFileGenerator child
	 *
	 * @param name
	 * @return
	 */
	public InputFileGenerator createInputFileGenerator(String name) {
		InputFileGenerator child = new InputFileGenerator(name);
		addChild(child);
		return child;
	}

	/**
	 * Creates a TableImport child
	 *
	 * @param name
	 * @return
	 */
	public TableImport createTableImport(String name) {
		TableImport child = new TableImport(name);
		addChild(child);
		return child;
	}

	//#end region

	//#region FILE PATH PROVIDER

	@Override
	public String provideFilePath() {
		ExecutableOutputPathModifier outputPathModifier = new ExecutableOutputPathModifier(this);
		String filePath = outputPathModifier.getModifiedOutputPath(outputPath.get());
		return filePath;
	}

	//#end region

	//#end region

}
