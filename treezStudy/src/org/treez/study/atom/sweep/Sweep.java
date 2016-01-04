package org.treez.study.atom.sweep;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.FilePath;
import org.treez.core.atom.attribute.ModelPathSelectionType;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.variablefield.VariableField;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.AddChildAtomTreeViewerAction;
import org.treez.core.utils.Utils;
import org.treez.data.output.OutputAtom;
import org.treez.model.input.HashMapModelInput;
import org.treez.model.input.ModelInput;
import org.treez.model.interfaces.Model;
import org.treez.model.output.ModelOutput;
import org.treez.study.Activator;
import org.treez.study.atom.AbstractParameterVariation;
import org.treez.study.atom.range.AbstractVariableRange;
import org.treez.study.atom.range.BooleanVariableRange;
import org.treez.study.atom.range.DirectoryPathVariableRange;
import org.treez.study.atom.range.DoubleVariableRange;
import org.treez.study.atom.range.FilePathVariableRange;
import org.treez.study.atom.range.QuantityVariableRange;
import org.treez.study.atom.range.StringItemVariableRange;
import org.treez.study.atom.range.StringVariableRange;

/**
 * Represents a parameter sweep with a maximum of two parameters
 */
@SuppressWarnings({ "checkstyle:visibilitymodifier", "checkstyle:classfanoutcomplexity" })
public class Sweep extends AbstractParameterVariation {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(Sweep.class);

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public Sweep(String name) {
		super(name);
		createSweepModel();
	}

	//#end region

	//#region METHODS

	/**
	 * Creates the underlying model
	 */
	private void createSweepModel() {
		// root, page and section
		AttributeRoot root = new AttributeRoot("root");
		Page dataPage = root.createPage("data", "   Data   ");

		String relativeHelpContextId = "sweep";
		String absoluteHelpContextId = Activator.getInstance().getAbsoluteHelpContextId(relativeHelpContextId);

		Section sweepSection = dataPage.createSection("sweep", "sweep", absoluteHelpContextId);
		sweepSection.createSectionAction("action", "Run sweep", () -> execute(treeViewRefreshable));

		//choose selection type and entry atom
		ModelPathSelectionType selectionType = ModelPathSelectionType.FLAT;
		AbstractAtom modelEntryPoint = this;

		//model to run
		String modelToRunDefaultValue = "";
		sweepSection.createModelPath(modelToRunModelPath, "modelToRunModelPath", "Model to run", modelToRunDefaultValue,
				Model.class, selectionType, modelEntryPoint, false);

		//source model
		String sourceModelDefaultValue = "";
		sweepSection.createModelPath(sourceModelPath, "sourceModelPath", "Variable source model (provides variables)",
				sourceModelDefaultValue, Model.class, selectionType, modelEntryPoint, false);

		//export sweep info check box
		sweepSection.createCheckBox(exportStudyInfo, "exportStudyInfo", "Export study information", true);

		//export sweep info path
		FilePath filePath = sweepSection.createFilePath(exportStudyInfoPath, "exportStudyInfoPath",
				"Target file path for study information", "");
		filePath.setValidatePath(false);
		filePath.addModifyListener("updateEnabledState", new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				boolean exportSweepInfoEnabled = exportStudyInfo.get();
				filePath.setEnabled(exportSweepInfoEnabled);
			}

		});

		setModel(root);
	}

	/**
	 * Executes the sweep
	 */
	@Override
	public void execute(Refreshable refreshable) {
		String jobTitle = "Sweep '" + getName() + "'";
		runNonUiJob(jobTitle, (monitor) -> {
			runStudy(refreshable, monitor);
		});

	}

	/**
	 * Runs the study
	 */
	@Override
	public void runStudy(Refreshable refreshable, IProgressMonitor monitor) {
		Objects.requireNonNull(monitor, "You need to pass a valid IProgressMonitor that is not null.");
		this.treeViewRefreshable = refreshable;

		String startMessage = "Executing sweep '" + getName() + "'";
		sysLog.info(startMessage);

		//create ModelInput generator
		String sweepModelPath = Sweep.this.createTreeNodeAdaption().getTreePath();
		SweepModelInputGenerator inputGenerator = new SweepModelInputGenerator(sweepModelPath);

		//get variable ranges
		List<AbstractVariableRange<?>> variableRanges = inputGenerator.getActiveVariableRanges(this);
		sysLog.info("Number of variable ranges: " + variableRanges.size());

		//check if all variable ranges reference enabled variables
		boolean allReferencedVariablesAreActive = checkIfAllREferencedVariablesAreActive(variableRanges);
		if (allReferencedVariablesAreActive) {
			doRunStudy(refreshable, monitor, inputGenerator, variableRanges);
		}

	}

	private void doRunStudy(
			Refreshable refreshable,
			IProgressMonitor monitor,
			SweepModelInputGenerator inputGenerator,
			List<AbstractVariableRange<?>> variableRanges) {
		//get total number of simulations
		int numberOfSimulations = inputGenerator.getNumberOfSimulations(variableRanges);
		sysLog.info("Number of total simulations: " + numberOfSimulations);

		//initialize progress monitor
		monitor.beginTask("", numberOfSimulations);

		//reset study index to 1
		HashMapModelInput.resetIdCounter();

		//create model inputs
		List<ModelInput> modelInputs = inputGenerator.createModelInputs(variableRanges);

		//export sweep info to text file if the corresponding option is enabled
		if (exportStudyInfo.get()) {
			exportSweepInfo(variableRanges, numberOfSimulations);
		}

		//prepare result structure
		prepareResultStructure();
		refresh();

		//get sweep output atom
		String sweepOutputAtomPath = getStudyOutputAtomPath();
		AbstractAtom sweepOutputAtom = this.getChildFromRoot(sweepOutputAtomPath);

		//remove all old children if they exist
		sweepOutputAtom.removeAllChildren();

		//execute target model for all model inputs
		executeTargetModel(refreshable, monitor, numberOfSimulations, modelInputs, sweepOutputAtom);

		//inform progress monitor to be done
		monitor.setTaskName("=>Finished!");

		//show end message
		logAndShowSweepEndMessage();
		sysLog.info("The sweep outout is located at " + sweepOutputAtomPath);
		monitor.done();
	}

	private void executeTargetModel(
			Refreshable refreshable,
			IProgressMonitor monitor,
			int numberOfSimulations,
			List<ModelInput> modelInputs,
			AbstractAtom sweepOutputAtom) {
		int counter = 1;
		Model model = getModelToRun();
		long startTime = System.currentTimeMillis();
		for (ModelInput modelInput : modelInputs) {

			//allows to cancel the sweep if a user clicks the cancel button at the progress monitor window
			if (!monitor.isCanceled()) {
				logModelStartMessage(counter, startTime, numberOfSimulations);

				//create subtask and sub monitor for progress monitor
				monitor.setTaskName("=>Simulation #" + counter);
				SubProgressMonitor subMonitor = new SubProgressMonitor(
						monitor,
						1,
						SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);

				//execute model
				ModelOutput modelOutput = model.runModel(modelInput, refreshable, subMonitor);

				//post process model output
				AbstractAtom modelOutputAtom = modelOutput.getOutputAtom();
				String modelOutputName = getName() + "OutputId" + modelInput.getId();
				modelOutputAtom.setName(modelOutputName);
				sweepOutputAtom.addChild(modelOutputAtom);
				refresh();
				counter++;

			}

		}
	}

	/**
	 * Checks if the variables that are references by the given variable ranges are active. If not an error message is
	 * shown to the user;
	 *
	 * @param variableRanges
	 * @return
	 */
	private boolean checkIfAllREferencedVariablesAreActive(List<AbstractVariableRange<?>> variableRanges) {
		List<String> inactiveVariables = new ArrayList<>();
		for (AbstractVariableRange<?> variableRange : variableRanges) {
			String variableModelPath = variableRange.getSourceVariableModelPath();
			AbstractAtom variableAtom;
			try {
				variableAtom = this.getChildFromRoot(variableModelPath);
			} catch (IllegalArgumentException exception) {
				String message = "Could not find atom '" + variableModelPath + "'.";
				Utils.showErrorMessage(message);
				return false;
			}

			VariableField variableField;
			try {
				variableField = (VariableField) variableAtom;
			} catch (ClassCastException exception) {
				String message = "Could not cast atom '" + variableAtom.createTreeNodeAdaption().getTreePath()
						+ "' to a VariableField.";
				Utils.showErrorMessage(message);
				return false;
			}
			boolean isEnabled = variableField.isEnabled();
			if (!isEnabled) {
				inactiveVariables.add(variableModelPath);
			}
		}

		if (inactiveVariables.isEmpty()) {
			return true;
		} else {
			String message = "Found disabled variable(s):\n" + String.join("\n", inactiveVariables)
					+ "Please enable the variable(s) or disable the corresponding range(s).";
			Utils.showErrorMessage(message);
			return false;
		}

	}

	/**
	 * Creates a text file with some information about the sweep and saves it at the exportSweepInfoPath
	 *
	 * @param variableRanges
	 * @param numberOfSimulations
	 */
	private void exportSweepInfo(List<AbstractVariableRange<?>> variableRanges, int numberOfSimulations) {
		String sweepInfo = "---------- SweepInfo ----------\r\n\r\n" + "Total number of simulations:\r\n"
				+ numberOfSimulations + "\r\n\r\n" + "Variable model paths and values:\r\n\r\n";

		for (AbstractVariableRange<?> range : variableRanges) {
			String variablePath = range.getSourceVariableModelPath();
			sweepInfo += variablePath + "\r\n";
			List<?> rangeValues = range.getRange();
			for (Object value : rangeValues) {
				sweepInfo += value.toString() + "\r\n";
			}
			sweepInfo += "\r\n";
		}

		String filePath = exportStudyInfoPath.get();
		File file = new File(filePath);

		try {
			FileUtils.writeStringToFile(file, sweepInfo);
		} catch (IOException exception) {
			String message = "The specified exportSweepInfoPath '" + filePath
					+ "' is not valid. Export of sweep info is skipped.";
			sysLog.error(message);
		}

	}

	/**
	 * Creates the result structure if it does not yet exist to have a place in the tree where the sweep result can be
	 * put. The sweep results will not be a child of the Sweep put a child of for example root.results.data.sweepOutput
	 */
	private void prepareResultStructure() {
		createResultsAtomIfNotExists();
		createDataAtomIfNotExists();
		createSweepOutputAtomIfNotExists();
		this.refresh();
	}

	/**
	 * Creates the sweep output atom if it does not yet exist
	 */
	private void createSweepOutputAtomIfNotExists() {
		String dataAtomPath = createOutputDataAtomPath();
		String sweepOutputAtomName = createStudyOutputAtomName();
		String sweepPutputAtomPath = getStudyOutputAtomPath();
		boolean sweepOutputAtomExists = this.rootHasChild(sweepPutputAtomPath);
		if (!sweepOutputAtomExists) {
			OutputAtom sweepOutputAtom = new OutputAtom(sweepOutputAtomName, provideImage());
			AbstractAtom data = this.getChildFromRoot(dataAtomPath);
			data.addChild(sweepOutputAtom);
			sysLog.info("Created " + sweepPutputAtomPath + " for sweep output.");
		}

	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("sweep.png");
	}

	/**
	 * Creates the context menu actions for this atom
	 */
	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {

		Action addQuantityRange = new AddChildAtomTreeViewerAction(
				QuantityVariableRange.class,
				"quantityRange",
				Activator.getImage("quantityVariableRange.png"),
				this,
				treeViewer);
		actions.add(addQuantityRange);

		Action addDoubleRange = new AddChildAtomTreeViewerAction(
				DoubleVariableRange.class,
				"doubleRange",
				Activator.getImage("doubleVariableRange.png"),
				this,
				treeViewer);
		actions.add(addDoubleRange);

		Action addBooleanRange = new AddChildAtomTreeViewerAction(
				BooleanVariableRange.class,
				"booleanRange",
				Activator.getImage("booleanVariableRange.png"),
				this,
				treeViewer);
		actions.add(addBooleanRange);

		Action addStringRange = new AddChildAtomTreeViewerAction(
				StringVariableRange.class,
				"stringRange",
				Activator.getImage("stringVariableRange.png"),
				this,
				treeViewer);
		actions.add(addStringRange);

		Action addStringItemRange = new AddChildAtomTreeViewerAction(
				StringItemVariableRange.class,
				"stringItemRange",
				Activator.getImage("stringItemVariableRange.png"),
				this,
				treeViewer);
		actions.add(addStringItemRange);

		Action addFilePathRange = new AddChildAtomTreeViewerAction(
				FilePathVariableRange.class,
				"filePathRange",
				Activator.getImage("filePathVariableRange.png"),
				this,
				treeViewer);
		actions.add(addFilePathRange);

		Action addDirectoryPathRange = new AddChildAtomTreeViewerAction(
				DirectoryPathVariableRange.class,
				"directoryPathRange",
				Activator.getImage("directoryPathVariableRange.png"),
				this,
				treeViewer);
		actions.add(addDirectoryPathRange);

		return actions;
	}

	//#region CREATE CHILD ATOMS

	/**
	 * Creates a DoubleVariableRange child
	 *
	 * @param name
	 * @return
	 */
	public DoubleVariableRange createDoubleVariableRange(String name) {
		DoubleVariableRange child = new DoubleVariableRange(name);
		addChild(child);
		return child;
	}

	/**
	 * Creates a QuantityVariableRange child
	 *
	 * @param name
	 * @return
	 */
	public QuantityVariableRange createQuantityVariableRange(String name) {
		QuantityVariableRange child = new QuantityVariableRange(name);
		addChild(child);
		return child;
	}

	/**
	 * Creates a BooleanVariableRange child
	 *
	 * @param name
	 * @return
	 */
	public BooleanVariableRange createBooleanVariableRange(String name) {
		BooleanVariableRange child = new BooleanVariableRange(name);
		addChild(child);
		return child;
	}

	/**
	 * Creates a StringVariableRange child
	 *
	 * @param name
	 * @return
	 */
	public StringVariableRange createStringVariableRange(String name) {
		StringVariableRange child = new StringVariableRange(name);
		addChild(child);
		return child;
	}

	/**
	 * Creates a StringItemVariableRange child
	 *
	 * @param name
	 * @return
	 */
	public StringItemVariableRange createStringItemVariableRange(String name) {
		StringItemVariableRange child = new StringItemVariableRange(name);
		addChild(child);
		return child;
	}

	/**
	 * Creates a FilePathVariableRange child
	 *
	 * @param name
	 * @return
	 */
	public FilePathVariableRange createFilePathVariableRange(String name) {
		FilePathVariableRange child = new FilePathVariableRange(name);
		addChild(child);
		return child;
	}

	/**
	 * Creates a DirectoryPathVariableRange child
	 *
	 * @param name
	 * @return
	 */
	public DirectoryPathVariableRange createDirectoryPathVariableRange(String name) {
		DirectoryPathVariableRange child = new DirectoryPathVariableRange(name);
		addChild(child);
		return child;
	}

	//#end region

	//#end region

}
