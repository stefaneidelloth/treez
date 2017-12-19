package org.treez.model.atom.executable;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.adjustable.AdjustableAtomCodeAdaption;
import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.attribute.attributeContainer.Page;
import org.treez.core.atom.attribute.attributeContainer.section.Section;
import org.treez.core.atom.attribute.checkBox.CheckBox;
import org.treez.core.atom.attribute.fileSystem.FileOrDirectoryPath;
import org.treez.core.atom.attribute.fileSystem.FilePath;
import org.treez.core.atom.attribute.text.InfoText;
import org.treez.core.atom.attribute.text.TextArea;
import org.treez.core.atom.attribute.text.TextField;
import org.treez.core.atom.uisynchronizing.AbstractUiSynchronizingAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Consumer;
import org.treez.core.attribute.Wrap;
import org.treez.core.monitor.ObservableMonitor;
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
public class Executable extends AbstractModel implements FilePathProvider, InputPathProvider {

	static final Logger LOG = Logger.getLogger(Executable.class);

	//#region ATTRIBUTES

	public final Attribute<String> executablePath = new Wrap<>();

	public final Attribute<String> inputArguments = new Wrap<>();

	public final Attribute<String> inputPath = new Wrap<>();

	public final Attribute<Boolean> copyInputFile = new Wrap<>();

	public final Attribute<Boolean> includeDateInInputFile = new Wrap<>();

	public final Attribute<Boolean> includeDateInInputFolder = new Wrap<>();

	public final Attribute<Boolean> includeDateInInputSubFolder = new Wrap<>();

	public final Attribute<Boolean> includeJobIndexInInputFile = new Wrap<>();

	public final Attribute<Boolean> includeJobIndexInInputFolder = new Wrap<>();

	public final Attribute<Boolean> includeJobIndexInInputSubFolder = new Wrap<>();

	public final Attribute<String> outputArguments = new Wrap<>();

	public final Attribute<String> outputPath = new Wrap<>();

	public final Attribute<Boolean> includeDateInOutputFile = new Wrap<>();

	public final Attribute<Boolean> includeDateInOutputFolder = new Wrap<>();

	public final Attribute<Boolean> includeDateInOutputSubFolder = new Wrap<>();

	public final Attribute<Boolean> includeJobIndexInOutputFile = new Wrap<>();

	public final Attribute<Boolean> includeJobIndexInOutputFolder = new Wrap<>();

	public final Attribute<Boolean> includeJobIndexInOutputSubFolder = new Wrap<>();

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

	public Executable(Executable atomToCopy, boolean skipAttributeCopy) {
		super(atomToCopy);
		if (!skipAttributeCopy) {
			copyTreezAttributes(atomToCopy, this);
		}
	}

	//#end region

	//#region METHODS

	@Override
	public Executable getThis() {
		return this;
	}

	@Override
	public Executable copy() {
		return new Executable(this, false);
	}

	protected void createModel() {
		AttributeRoot root = new AttributeRoot("root");
		Page dataPage = root.createPage("data", "   Data   ");

		//update status listener
		Consumer updateStatus = () -> refreshStatus();

		//create sections

		String executableRelativeHelpContextId = "executable";
		String executableHelpContextId = Activator.getAbsoluteHelpContextIdStatic(executableRelativeHelpContextId);
		createExecutableSection(dataPage, updateStatus, executableHelpContextId);

		String inputRelativeHelpContextId = "executableInput";
		String inputHelpContextId = Activator.getAbsoluteHelpContextIdStatic(inputRelativeHelpContextId);
		createInputSection(dataPage, updateStatus, inputHelpContextId);

		String inputModificationRelativeHelpContextId = "executableInputModification";
		String inputModificationHelpContextId = Activator
				.getAbsoluteHelpContextIdStatic(inputModificationRelativeHelpContextId);
		createInputModificationSection(dataPage, updateStatus, inputModificationHelpContextId);

		String outputRelativeHelpContextId = "executableOutput";
		String outputHelpContextId = Activator.getAbsoluteHelpContextIdStatic(outputRelativeHelpContextId);
		createOutputSection(dataPage, updateStatus, outputHelpContextId);

		String outputModificationRelativeHelpContextId = "executableOutputModification";
		String outputModificationHelpContextId = Activator
				.getAbsoluteHelpContextIdStatic(outputModificationRelativeHelpContextId);
		createOutputModificationSection(dataPage, updateStatus, outputModificationHelpContextId);

		String loggingRelativeHelpContextId = "executableLogging";
		String loggingHelpContextId = Activator.getAbsoluteHelpContextIdStatic(loggingRelativeHelpContextId);
		createLoggingSection(dataPage, updateStatus, loggingHelpContextId);

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

	protected
			void
			createExecutableSection(Page dataPage, Consumer updateStatusListener, String executableHelpContextId) {
		Section executable = dataPage.createSection("executable", executableHelpContextId);
		Image resetImage = Activator.getImage("resetJobIndex.png");
		executable.createSectionAction("resetJobIndex", "Reset the job index to 1", () -> resetJobIndex(), resetImage);
		executable.createSectionAction("action", "Run external executable", () -> execute(treeViewRefreshable));

		FilePath filePath = executable.createFilePath(executablePath, this, "Executable", "notepad.exe");
		filePath.addModificationConsumer("updateStatus", updateStatusListener);

	}

	private void createInputSection(Page dataPage, Consumer updateStatusListener, String executableHelpContextId) {
		Section input = dataPage.createSection("input", executableHelpContextId);

		TextArea argumentTextField = input.createTextArea(inputArguments, this);
		argumentTextField.setLabel("Input arguments");
		argumentTextField.addModificationConsumer("updateStatus", updateStatusListener);
		argumentTextField.setHelpId("org.eclipse.ui.ide.executable");

		FileOrDirectoryPath inputPathChooser = input.createFileOrDirectoryPath(inputPath, this, "Input file or folder",
				"");
		inputPathChooser.addModificationConsumer("updateStatus", updateStatusListener);
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

	private void createOutputSection(Page dataPage, Consumer updateStatusListener, String executableHelpContextId) {
		Section output = dataPage.createSection("output", executableHelpContextId);

		TextField outputArgs = output.createTextField(outputArguments, this, "");
		outputArgs.setLabel("Output arguments");
		outputArgs.addModificationConsumer("updateStatus", updateStatusListener);

		FileOrDirectoryPath outputPathChooser = output.createFileOrDirectoryPath(outputPath, this,
				"Output file or folder", "", false);
		outputPathChooser.addModificationConsumer("updateStatus", updateStatusListener);

		CheckBox copyInputField = output.createCheckBox(copyInputFile, this, true);
		copyInputField.setLabel("Copy input file");
	}

	private void createOutputModificationSection(
			Page dataPage,
			Consumer updateStatusListener,
			String executableHelpContextId) {
		Section outputModification = dataPage.createSection("outputModification", executableHelpContextId);
		outputModification.setLabel("Output modification");
		outputModification.setExpanded(false);

		outputModification.createLabel("includeDate", "Include date in:");

		CheckBox dateInFolderCheck = outputModification.createCheckBox(includeDateInOutputFolder, this, false);
		dateInFolderCheck.setLabel("Folder name");
		dateInFolderCheck.addModificationConsumer("updateStatus", updateStatusListener);

		CheckBox dateInSubFolderCheck = outputModification.createCheckBox(includeDateInOutputSubFolder, this, false);
		dateInSubFolderCheck.setLabel("Extra folder");
		dateInSubFolderCheck.addModificationConsumer("updateStatus", updateStatusListener);

		CheckBox dateInFileCheck = outputModification.createCheckBox(includeDateInOutputFile, this, false);
		dateInFileCheck.setLabel("File name");
		dateInFileCheck.addModificationConsumer("updateStatus", updateStatusListener);

		@SuppressWarnings("unused")
		org.treez.core.atom.attribute.text.Label jobIndexLabel = outputModification.createLabel("jobIndexLabel",
				"Include job index in:");

		CheckBox jobIndexInFolderCheck = outputModification.createCheckBox(includeJobIndexInOutputFolder, this, false);
		jobIndexInFolderCheck.setLabel("Folder name");
		jobIndexInFolderCheck.addModificationConsumer("updateStatus", updateStatusListener);

		CheckBox jobIndexInSubFolderCheck = outputModification.createCheckBox(includeJobIndexInOutputSubFolder, this,
				false);
		jobIndexInSubFolderCheck.setLabel("Extra folder");
		jobIndexInSubFolderCheck.addModificationConsumer("updateStatus", updateStatusListener);

		CheckBox jobIndexInFileCheck = outputModification.createCheckBox(includeJobIndexInOutputFile, this, false);
		jobIndexInFileCheck.setLabel("File name");
		jobIndexInFileCheck.addModificationConsumer("updateStatus", updateStatusListener);
	}

	private void createLoggingSection(Page dataPage, Consumer updateStatusListener, String executableHelpContextId) {
		Section logging = dataPage.createSection("logging", executableHelpContextId);
		logging.setExpanded(false);

		TextField logArgumentsText = logging.createTextField(logArguments, this, "");
		logArgumentsText.setLabel("Log arguments");
		logArgumentsText.addModificationConsumer("updateStatus", updateStatusListener);

		FilePath logFilePathChooser = logging.createFilePath(logFilePath, this, "Log file", "", false);
		logFilePathChooser.addModificationConsumer("updateStatus", updateStatusListener);
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
		AbstractUiSynchronizingAtom.runUiTaskNonBlocking(() -> {
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
	public ModelOutput runModel(FocusChangingRefreshable refreshable, ObservableMonitor executableMonitor) {

		String startMessage = "Running " + this.getClass().getSimpleName() + " '" + getName() + "'.";
		LOG.info(startMessage);

		//initialize progress monitor
		final int totalWork = 3;
		executableMonitor.setTotalWork(totalWork);

		//delete old output file and old log file if they exist
		delteOldOutputAndLogFiles();

		//update progress monitor
		executableMonitor.setDescription("Running InputFileGenerator children if exist.");

		//execute input file generator child(s) if exist
		try {
			executeInputFileGenerator(refreshable);
		} catch (Exception exception) {
			LOG.error("Could not execute input file generator for executable " + getName(), exception);
			executableMonitor.cancel();
			return createEmptyModelOutput();
		}

		//update progress monitor
		executableMonitor.worked(1);

		//update progress monitor
		executableMonitor.setDescription("Executing system command.");

		//create command
		String command = buildCommand();
		LOG.info("Executing " + command);

		//execute command
		ExecutableExecutor executor = new ExecutableExecutor(this);

		try {
			boolean success = executor.executeCommand(command, executableMonitor);
			if (!success) {
				String message = "Executing system command failed.";
				executableMonitor.setDescription(message);
				LOG.error(message);
				executableMonitor.cancel();
				return createEmptyModelOutput();
			}
		} catch (Exception exception) {
			LOG.error("Could not execute " + getName(), exception);
			executableMonitor.cancel();
			return createEmptyModelOutput();
		}

		//update progress monitor
		executableMonitor.worked(1);

		//create model output
		executableMonitor.setDescription("=>Post processing model output.");
		ModelOutput modelOutput = createEmptyModelOutput();

		//execute data import child(s) if exist
		try {
			ModelOutput dataImportOutput = runDataImport(refreshable, executableMonitor);
			modelOutput.addChildOutput(dataImportOutput);
		} catch (Exception exception) {
			LOG.error("Could not import results of " + getName(), exception);
			executableMonitor.cancel();
			return modelOutput;
		}

		//copy input file to output folder (modifies input file name)
		try {
			if (copyInputFile.get()) {
				copyInputFileToOutputFolder();
			}
		} catch (Exception exception) {
			LOG.error("Could not copy input file for " + getName(), exception);
			executableMonitor.cancel();
			return modelOutput;
		}

		//increase job index
		increasejobIndex();

		//inform progress monitor to be done
		executableMonitor.setDescription("finished\n");
		executableMonitor.done();

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
		String inputFilePath = getModifiedInputPath();
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
	 */
	private String getOutputPathToCopyInputFile() {

		String outputPathString = provideFilePath();

		//split path with point to determine file extension if one exists

		boolean isFilePath = Utils.isFilePath(outputPathString);
		String folderPath = outputPathString;
		if (isFilePath) {
			folderPath = Utils.extractParentFolder(outputPathString);
		}

		String inputPathString = getModifiedInputPath();
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
	private ModelOutput runDataImport(FocusChangingRefreshable refreshable, ObservableMonitor monitor) {
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
	 */
	protected String buildCommand() {
		String command = "\"" + executablePath.get() + "\"";
		command = addInputArguments(command);
		command = addOutputArguments(command);
		command = addLoggingArguments(command);
		return command;
	}

	protected String addInputArguments(String commandToExtend) {
		String command = commandToExtend;
		boolean inputArgsIsEmpty = inputArguments.get().isEmpty();
		if (!inputArgsIsEmpty) {
			String modifiedInputArguments = injectStudyAndJobInfo(inputArguments);
			command += " " + modifiedInputArguments;
		}

		boolean inputPathIsEmpty = inputPath.get().isEmpty();
		if (!inputPathIsEmpty) {
			command += " " + getModifiedInputPath();
		}
		return command;
	}

	protected String addOutputArguments(String commandToExtend) {
		String command = commandToExtend;
		boolean outputArgsIsEmpty = outputArguments.get().isEmpty();
		if (!outputArgsIsEmpty) {
			command += " " + outputArguments;
		}

		boolean outputPathIsEmpty = outputPath.get().isEmpty();
		if (!outputPathIsEmpty) {
			command += " " + provideFilePath();
		}
		return command;
	}

	protected String addLoggingArguments(String commandToExtend) {
		String command = commandToExtend;
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

	public void resetJobIndex() {
		setJobId("1");
		refreshStatus();
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("run.png");
	}

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

	@Override
	public CodeAdaption createCodeAdaption(ScriptType scriptType) {
		return new AdjustableAtomCodeAdaption(this);
	}

	public String getModifiedInputPath() {
		InputPathModifier inputPathModifier = new InputPathModifier(this);
		String filePath = inputPathModifier.getModifiedInputPath(inputPath.get());
		return filePath;
	}

	//#region CREATE CHILD ATOMS

	/**
	 * Creates a InputFileGenerator child
	 */
	public InputFileGenerator createInputFileGenerator(String name) {
		InputFileGenerator child = new InputFileGenerator(name);
		addChild(child);
		return child;
	}

	/**
	 * Creates a TableImport child
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
		OutputPathModifier outputPathModifier = new OutputPathModifier(this);
		String filePath = outputPathModifier.getModifiedOutputPath(outputPath.get());
		return filePath;
	}

	//#end region

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

	//#end region

	//#end region

}
