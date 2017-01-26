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
import org.treez.core.atom.attribute.TextArea;
import org.treez.core.atom.attribute.TextField;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.AddChildAtomTreeViewerAction;
import org.treez.core.utils.Utils;
import org.treez.model.Activator;
import org.treez.model.atom.AbstractModel;
import org.treez.model.atom.inputFileGenerator.InputFileGenerator;
import org.treez.model.atom.tableImport.TableImport;
import org.treez.model.output.ModelOutput;

/**
 * Represents an external executable that can be executed with additional command line arguments and file paths
 */
@SuppressWarnings({ "checkstyle:visibilitymodifier", "checkstyle:classfanoutcomplexity" })
public class Executable extends AbstractModel implements FilePathProvider {

	static final Logger LOG = Logger.getLogger(Executable.class);

	//#region ATTRIBUTES

	public final Attribute<String> executablePath = new Wrap<>();

	public final Attribute<String> inputArguments = new Wrap<>();

	public final Attribute<String> inputPath = new Wrap<>();

	public final Attribute<String> outputArguments = new Wrap<>();

	public final Attribute<String> outputPath = new Wrap<>();

	public String modifiedOutputPath;

	public final Attribute<Boolean> copyInputFile = new Wrap<>();

	public final Attribute<Boolean> includeDateInFile = new Wrap<>();

	public final Attribute<Boolean> includeDateInFolder = new Wrap<>();

	public final Attribute<Boolean> includeDateInSubFolder = new Wrap<>();

	public final Attribute<Boolean> includeJobIndexInFile = new Wrap<>();

	public final Attribute<Boolean> includeJobIndexInFolder = new Wrap<>();

	public final Attribute<Boolean> includeJobIndexInSubFolder = new Wrap<>();

	public final Attribute<String> logArguments = new Wrap<>();

	public final Attribute<String> logFilePath = new Wrap<>();

	public final Attribute<String> commandInfo = new Wrap<>();

	public final Attribute<String> executionStatusInfo = new Wrap<>();

	public final Attribute<String> jobIndexInfo = new Wrap<>();

	//#end region

	//#region CONSTRUCTORS

	public Executable(String name) {
		super(name);
		setRunnable();
		createModel();
	}

	protected void createModel() {
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

	/**
	 * Dummy method that can be overridden by inheriting classes
	 */
	protected void createExtraSections(
			@SuppressWarnings("unused") Page dataPage,
			@SuppressWarnings("unused") ModifyListener updateStatusListener,
			@SuppressWarnings("unused") String executableHelpContextId) {
		//this default implementation does nothing
	}

	//#end region

	//#region METHODS

	@Override
	public Executable getThis() {
		return this;
	}

	protected void createExecutableSection(
			Page dataPage,
			ModifyListener updateStatusListener,
			String executableHelpContextId) {
		Section executable = dataPage.createSection("executable", executableHelpContextId);
		Image resetImage = Activator.getImage("resetJobIndex.png");
		executable.createSectionAction("resetJobIndex", "Reset the job index to 1", () -> resetJobIndex(), resetImage);
		executable.createSectionAction("action", "Run external executable", () -> execute(treeViewRefreshable));

		FilePath filePath = executable.createFilePath(executablePath, this, "Executable", "notepad.exe");
		filePath.addModifyListener("updateStatus", updateStatusListener);

	}

	private void createInputSection(
			Page dataPage,
			ModifyListener updateStatusListener,
			String executableHelpContextId) {
		Section input = dataPage.createSection("input", executableHelpContextId);

		TextArea argumentTextField = input.createTextArea(inputArguments, this);
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
		org.treez.core.atom.attribute.Label jobIndexLabel = outputModification.createLabel("jobIndexLabel",
				"Include job index in:");

		CheckBox jobIndexInFolderCheck = outputModification.createCheckBox(includeJobIndexInFolder, this, false);
		jobIndexInFolderCheck.setLabel("Folder name");
		jobIndexInFolderCheck.addModifyListener("updateStatus", updateStatusListener);

		CheckBox jobIndexInSubFolderCheck = outputModification.createCheckBox(includeJobIndexInSubFolder, this, false);
		jobIndexInSubFolderCheck.setLabel("Extra folder");
		jobIndexInSubFolderCheck.addModifyListener("updateStatus", updateStatusListener);

		CheckBox jobIndexInFileCheck = outputModification.createCheckBox(includeJobIndexInFile, this, false);
		jobIndexInFileCheck.setLabel("File name");
		jobIndexInFileCheck.addModifyListener("updateStatus", updateStatusListener);
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

	protected void createStatusSection(Page dataPage, String executableHelpContextId) {
		Section status = dataPage.createSection("status", executableHelpContextId);
		status.setExpanded(false);

		//resulting command
		status.createInfoText(commandInfo, this, "Resulting command", "");

		//execution status
		status.createInfoText(executionStatusInfo, this, "Execution status", "Not yet executed.");

		//job index
		status.createInfoText(jobIndexInfo, this, "Next job index", "1");
	}

	@Override
	protected void afterCreateControlAdaptionHook() {
		//update info text
		refreshStatus();
	}

	/**
	 * Updates the status text labels with data from other attribute atoms
	 */
	protected void refreshStatus() {
		this.runUiJobNonBlocking(() -> {
			String infoTextMessage = buildCommand();
			//LOG.debug("Updating info text: " + infoTextMessage);
			commandInfo.set(infoTextMessage);

			Wrap<String> infoTextWrap = (Wrap<String>) executionStatusInfo;
			InfoText executionStatusInfoText = (InfoText) infoTextWrap.getAttribute();
			executionStatusInfoText.resetError();
			executionStatusInfoText.set("Not yet executed");

			jobIndexInfo.set("" + getJobId());
		});

	}

	@Override
	public ModelOutput runModel(FocusChangingRefreshable refreshable, IProgressMonitor progressMonitor) {

		String startMessage = "Running " + this.getClass().getSimpleName() + " '" + getName() + "'.";
		LOG.info(startMessage);

		//initialize progress monitor
		final int totalWork = 3;
		progressMonitor.beginTask(startMessage, totalWork);

		//delete old output file and old log file if they exist
		delteOldOutputAndLogFiles();

		//update progress monitor
		progressMonitor.subTask("=>Running InputFileGenerator children if exist.");

		//execute input file generator child(s) if exist
		executeInputFileGenerator(refreshable);

		//update progress monitor
		progressMonitor.worked(1);

		//update progress monitor
		progressMonitor.subTask("=>Executiong system command.");

		//create command
		String command = buildCommand();
		LOG.info("Executing " + command);

		//execute command
		ExecutableExecutor executor = new ExecutableExecutor(this);
		executor.executeCommand(command);

		//update progress monitor
		progressMonitor.worked(1);

		//create model output
		progressMonitor.subTask("=>Post processing model output.");
		ModelOutput modelOutput = createEmptyModelOutput();
		//if (successful) {
		//execute data import child(s) if exist
		ModelOutput dataImportOutput = runDataImport(refreshable, progressMonitor);
		modelOutput.addChildOutput(dataImportOutput);
		//}

		//copy input file to output folder (modifies input file name)
		if (copyInputFile.get()) {
			copyInputFileToOutputFolder();
		}

		//increase job index
		increasejobIndex();

		//inform progress monitor to be done
		progressMonitor.done();

		return modelOutput;
	}

	private void increasejobIndex() {
		int currentIndex = 0;
		try {
			String jobId = getJobId();
			currentIndex = Integer.parseInt(jobId);
		} catch (NumberFormatException exception) {
			LOG.warn("Could not interpret last jobId as Integer. "
					+ "Starting with 1 for the next job index of the executable.");
		}
		int newIndex = currentIndex + 1;
		setJobId("" + newIndex);
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
			String newInputFileName = Utils.includeNumberInFileName(inputFileName, "#" + getJobId());
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
	protected String buildCommand() {
		String command = "\"" + executablePath.get() + "\"";
		boolean inputArgsIsEmpty = inputArguments.get().isEmpty();
		if (!inputArgsIsEmpty) {
			String modifiedInputArguments = injectStudyAndJobInfo(inputArguments);
			command += " " + modifiedInputArguments;
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
	 * If the input arguments contain place holders, those place holders are replaced by the actual studyId,
	 * studyDescription and jobId.
	 *
	 * @param input
	 * @return
	 */

	protected String injectStudyAndJobInfo(Attribute<String> input) {
		String studyIdKey = "{$studyId$}";
		String studyDescriptionKey = "{$studyDescription$}";
		String jobIdKey = "{$jobId$}";

		String currentInputArguments = input.get();

		if (currentInputArguments.contains(studyIdKey)) {
			String studyName = getStudyId();
			if (studyName == null) {
				currentInputArguments = currentInputArguments.replace(studyIdKey, "");
			} else {
				currentInputArguments = currentInputArguments.replace(studyIdKey, studyName);
			}

		}

		if (currentInputArguments.contains(studyDescriptionKey)) {
			currentInputArguments = currentInputArguments.replace(studyDescriptionKey, getStudyDescription());
		}

		if (currentInputArguments.contains(jobIdKey)) {
			currentInputArguments = currentInputArguments.replace(jobIdKey, getJobId());
		}
		return currentInputArguments;
	}

	/**
	 * Resets the job index
	 */
	public void resetJobIndex() {
		setJobId("1");
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
