package org.treez.study.atom.sweep;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.attribute.attributeContainer.Page;
import org.treez.core.atom.attribute.attributeContainer.section.Section;
import org.treez.core.atom.attribute.checkBox.CheckBox;
import org.treez.core.atom.attribute.modelPath.ModelPathSelectionType;
import org.treez.core.atom.attribute.text.TextField;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.uisynchronizing.AbstractUiSynchronizingAtom;
import org.treez.core.atom.variablefield.VariableField;
import org.treez.core.monitor.ObservableMonitor;
import org.treez.core.monitor.TreezMonitor;
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
import org.treez.study.atom.ModelInputGenerator;
import org.treez.study.atom.exportStudyInfo.StudyInfoExport;
import org.treez.study.atom.range.AbstractVariableRange;
import org.treez.study.atom.range.BooleanVariableRange;
import org.treez.study.atom.range.DirectoryPathVariableRange;
import org.treez.study.atom.range.DoubleVariableRange;
import org.treez.study.atom.range.FilePathVariableRange;
import org.treez.study.atom.range.IntegerVariableRange;
import org.treez.study.atom.range.QuantityVariableRange;
import org.treez.study.atom.range.StringItemVariableRange;
import org.treez.study.atom.range.StringVariableRange;

/**
 * Represents a parameter sweep with a maximum of two parameters
 */
@SuppressWarnings({ "checkstyle:visibilitymodifier", "checkstyle:classfanoutcomplexity" })
public class Sweep extends AbstractParameterVariation {

	private static final Logger LOG = Logger.getLogger(Sweep.class);

	//#region ATTRIBUTES

	private int numberOfActiveThreads = 0;

	private boolean isAlreadyCanceled = false;

	//#emd region

	//#region CONSTRUCTORS

	public Sweep(String name) {
		super(name);
		createSweepModel();
	}

	//#end region

	//#region METHODS

	private void createSweepModel() {
		// root, page and section
		AttributeRoot root = new AttributeRoot("root");
		Page dataPage = root.createPage("data", "   Data   ");

		String relativeHelpContextId = "sweep";
		String absoluteHelpContextId = Activator.getInstance().getAbsoluteHelpContextId(relativeHelpContextId);

		Section sweepSection = dataPage.createSection("sweep", absoluteHelpContextId);
		sweepSection.createSectionAction("action", "Run sweep", () -> execute(treeViewRefreshable));

		//studyName
		TextField studyNameField = sweepSection.createTextField(studyName, this, "");
		studyNameField.setLabel("Name");

		//description
		TextField descriptionField = sweepSection.createTextField(studyDescription, this);
		descriptionField.setLabel("Description");

		//choose selection type and entry atom
		ModelPathSelectionType selectionType = ModelPathSelectionType.FLAT;
		AbstractAtom<?> modelEntryPoint = this;

		//model to run
		String modelToRunDefaultValue = "";
		sweepSection
				.createModelPath(modelToRunModelPath, this, modelToRunDefaultValue, Model.class, selectionType,
						modelEntryPoint, false)
				.setLabel("Model to run");

		//source model
		String sourceModelDefaultValue = "";
		sweepSection
				.createModelPath(sourceModelPath, this, sourceModelDefaultValue, Model.class, selectionType,
						modelEntryPoint, false)
				.setLabel("Variable source model (provides variables)");

		//jobName offset
		var isUsingjobNameOffsetCheckBox = sweepSection.createCheckBox(isUsingjobNameOffset, this, false);
		isUsingjobNameOffsetCheckBox.setLabel("Use manual offset for jobName");

		var jobNameOffsetField = sweepSection.createTextField(jobNameOffset, this);
		jobNameOffsetField.setLabel("First jobName");

		isUsingjobNameOffsetCheckBox.addModificationConsumerAndRun("toggleVisibilityOfjobNameOffsetTextField", () -> {
			jobNameOffsetField.setEnabled(isUsingjobNameOffsetCheckBox.get());
		});

		//parallel execution
		CheckBox concurrentCheckBox = sweepSection.createCheckBox(isConcurrentVariation, this, true);
		concurrentCheckBox.setLabel("Parallel execution");

		setModel(root);
	}

	@Override
	public void execute(FocusChangingRefreshable refreshable) {
		String sweepTitle = "Sweep '" + getName() + "'";
		runNonUiTask(sweepTitle, (mainMonitor) -> {
			try {
				runStudy(refreshable, mainMonitor);
			} catch (Exception exception) {
				String message = "Could not run " + sweepTitle;
				LOG.error(message, exception);
				mainMonitor.done();
			}

		});
	}

	@Override
	public void runStudy(FocusChangingRefreshable refreshable, SubMonitor mainMonitor) {
		//Objects.requireNonNull(monitor, "You need to pass a valid IProgressMonitor that is not null.");
		this.treeViewRefreshable = refreshable;
		this.isAlreadyCanceled = false;

		String startMessage = "Executing sweep '" + getName() + "'";
		LOG.info(startMessage);

		//create ModelInput generator
		SweepModelInputGenerator inputGenerator = new SweepModelInputGenerator(this);

		//get variable ranges
		List<AbstractVariableRange<?>> variableRanges = inputGenerator.getEnabledVariableRanges();
		LOG.info("Number of variable ranges: " + variableRanges.size());

		//check if all variable ranges reference enabled variables
		boolean allReferencedVariablesAreActive = checkIfAllReferencedVariablesAreActive(variableRanges);
		if (allReferencedVariablesAreActive) {
			doRunStudy(refreshable, inputGenerator, mainMonitor);
		} else {
			mainMonitor.done();
		}

	}

	private void doRunStudy(
			FocusChangingRefreshable refreshable,
			SweepModelInputGenerator inputGenerator,
			SubMonitor mainMonitor) {

		String sweepTitle = "Running Sweep";
		int numberOfSimulations = inputGenerator.getNumberOfSimulations();

		TreezMonitor sweepMonitor = new TreezMonitor(sweepTitle, mainMonitor, numberOfSimulations);
		TreezMonitor.showInMonitorView(sweepMonitor);

		LOG.info("Number of total simulations: " + numberOfSimulations);

		//set initial job index

		if (isUsingjobNameOffset.get()) {
			var nextId = Integer.parseInt(jobNameOffset.get());
			HashMapModelInput.setIdCounter(nextId);
		} else {
			HashMapModelInput.resetIdCounter();
		}

		//create model inputs
		List<ModelInput> modelInputs = inputGenerator.createModelInputs();

		//prepare result structure
		prepareResultStructure();
		refresh();

		//get sweep output atom
		String sweepOutputAtomPath = getStudyOutputAtomPath();
		AbstractAtom<?> sweepOutputAtom = this.getChildFromRoot(sweepOutputAtomPath);

		//remove all old children if they exist
		sweepOutputAtom.removeAllChildren();

		//execute target model for all model inputs
		List<Integer> numberOfRemainingModelJobs = new ArrayList<>();
		numberOfRemainingModelJobs.add(numberOfSimulations);

		if (!mainMonitor.isCanceled()) {
			Runnable jobFinishedHook = () -> finishOrCancelIfDone(refreshable, sweepMonitor,
					numberOfRemainingModelJobs);

			if (isConcurrentVariation.get()) {
				executeTargetModelConcurrently(refreshable, numberOfSimulations, modelInputs, sweepOutputAtom,
						sweepMonitor, jobFinishedHook);
			} else {
				executeTargetModelOneAfterAnother(refreshable, numberOfSimulations, modelInputs, sweepOutputAtom,
						sweepMonitor, jobFinishedHook);
			}

			executeExecutableChildren(refreshable);

		}

	}

	private synchronized void finishOrCancelIfDone(
			FocusChangingRefreshable refreshable,
			TreezMonitor mainMonitor,
			List<Integer> numberOfRemainingModelJobs) {

		int remainingModelJobs = numberOfRemainingModelJobs.get(0) - 1;
		numberOfRemainingModelJobs.set(0, remainingModelJobs);

		if (mainMonitor.isChildCanceled()) {

			if (isAlreadyCanceled) {
				return;
			}
			isAlreadyCanceled = true;
			mainMonitor.markIssue();
			mainMonitor.setDescription("Canceled!");
			mainMonitor.cancel();

			logAndShowSweepCancelMessage();
			AbstractUiSynchronizingAtom.runUiTaskNonBlocking(() -> {
				try {
					refreshable.refresh();
				} catch (Exception exception) {
					LOG.error("Could not refresh.", exception);
				}
			});
		}

		if (remainingModelJobs == 0) {

			mainMonitor.setDescription("Finished!");

			if (mainMonitor.isChildCanceled()) {
				mainMonitor.cancel();
			} else {
				mainMonitor.done();
			}

			logAndShowSweepEndMessage();
			AbstractUiSynchronizingAtom.runUiTaskNonBlocking(() -> {
				try {
					refreshable.refresh();
				} catch (Exception exception) {
					LOG.error("Could not refresh.", exception);
				}
			});
		}

	}

	private void executeTargetModelConcurrently(
			FocusChangingRefreshable refreshable,
			int numberOfSimulations,
			List<ModelInput> modelInputs,
			AbstractAtom<?> sweepOutputAtom,
			TreezMonitor sweepMonitor,
			Runnable jobFinishedHook) {

		Model model = getModelToRun();

		//get current time
		Double currentTime = Double.parseDouble("" + System.currentTimeMillis());
		String currentDateString = millisToDateString(currentTime);

		//log start message
		String message = "-- " + currentDateString + " --- Starting " + numberOfSimulations + " simulations ----------";
		LOG.info(message);

		Queue<Runnable> jobQueue = new ConcurrentLinkedQueue<Runnable>();

		Runnable jobFinished = () -> {

			jobFinishedHook.run();
			numberOfActiveThreads--;
			continueToProcessQueue(jobQueue);
		};

		//fill queue with model jobs
		for (final ModelInput modelInput : modelInputs) {
			createAndEnqueueModelJob(jobQueue, refreshable, sweepOutputAtom, model, modelInput, sweepMonitor,
					jobFinished);
		}

		//process queue
		continueToProcessQueue(jobQueue);

	}

	private synchronized void continueToProcessQueue(Queue<Runnable> jobQueue) {

		int numberOfProcessors = Runtime.getRuntime().availableProcessors();

		//Reserve some processors on the server for other tasks.
		//Also set a minimum priority.
		//Otherwise it would be possible to freeze the server
		//and the UI of Eclipse might not react any more.

		if (numberOfProcessors > 6) {
			numberOfProcessors = numberOfProcessors / 2;
		} else {
			if (numberOfProcessors > 1) {
				numberOfProcessors -= 1;
			}
		}

		int numberOfFreeThreads = numberOfProcessors - numberOfActiveThreads;

		for (int index = 0; index < numberOfFreeThreads; index++) {
			Runnable modelJob = jobQueue.poll();
			if (modelJob == null) {
				return;
			}

			Thread thread = new Thread(modelJob);
			thread.setPriority(Thread.MIN_PRIORITY);
			numberOfActiveThreads++;
			thread.start();
		}

		Thread.yield();

		//LOG.info("Working on model job queue with " + numberOfActiveThreads + " threads.");

	}

	private void createAndEnqueueModelJob(
			Queue<Runnable> queue,
			FocusChangingRefreshable refreshable,
			AbstractAtom<?> sweepOutputAtom,
			Model modelToRun,
			final ModelInput modelInput,
			TreezMonitor sweepMonitor,
			Runnable jobFinishedHook) {

		String jobName = modelInput.getjobName();
		String jobTitle = "Sweep Job '" + jobName + "'";

		AbstractAtom<?> modelAtom = (AbstractAtom<?>) modelToRun;
		String pathForModelToRun = modelAtom.createTreeNodeAdaption().getTreePath();
		AbstractAtom<?> root = sweepOutputAtom.getRoot();

		//create snapshot of root as blueprint for shadow roots
		//(The actual root tree will be modified during execution and it would be a bad idea
		//to use the changing root as blueprint.)

		AbstractAtom<?> rootSnapshot = root.copy();

		Runnable modelJob = () -> {

			if (sweepMonitor.isCanceled()) {
				jobFinishedHook.run();
				return;
			}

			try {

				//create shadow tree and retrieve shadow model
				AbstractAtom<?> shadowRoot = rootSnapshot.copy();

				//run shadow model
				Model shadowModelToRun = (Model) shadowRoot.getChildFromRoot(pathForModelToRun);

				try (
						ObservableMonitor jobMonitor = sweepMonitor.createChild(jobTitle, jobName, 1)) {

					if (sweepMonitor.isCanceled()) {
						jobFinishedHook.run();
						return;
					}

					ModelOutput modelOutput = shadowModelToRun.runModel(modelInput, refreshable, jobMonitor);

					if (sweepMonitor.isCanceled()) {
						jobFinishedHook.run();
						return;
					}

					//store output in sweep output of main tree
					AbstractAtom<?> modelOutputAtom = modelOutput.getOutputAtom();
					String modelOutputName = this.getName() + "OutputId" + modelInput.getjobName();
					modelOutputAtom.setName(modelOutputName);
					sweepOutputAtom.addChild(modelOutputAtom);

				}

			} catch (Exception exception) {
				LOG.error("Could not run " + jobTitle, exception);
				sweepMonitor.cancel();
			}

			jobFinishedHook.run();

			Thread.yield();

		};

		queue.add(modelJob);

	}

	private void executeTargetModelOneAfterAnother(
			FocusChangingRefreshable refreshable,
			int numberOfSimulations,
			List<ModelInput> modelInputs,
			AbstractAtom<?> sweepOutputAtom,
			TreezMonitor monitor,
			Runnable jobFinishedHook) {

		int counter = 1;
		Model model = getModelToRun();
		long startTime = System.currentTimeMillis();

		for (ModelInput modelInput : modelInputs) {

			//allows to cancel the sweep if a user clicks the cancel button at the progress monitor window
			if (!monitor.isCanceled()) {
				logModelStartMessage(counter, startTime, numberOfSimulations);

				monitor.setDescription("=>Job #" + counter);

				String jobName = modelInput.getjobName();
				String jobTitle = "Sweep Job '" + jobName + "'";
				ObservableMonitor jobMonitor = monitor.createChild(jobTitle, jobName, 1);

				ModelOutput modelOutput = model.runModel(modelInput, refreshable, jobMonitor);

				AbstractAtom<?> modelOutputAtom = modelOutput.getOutputAtom();
				String modelOutputName = getName() + "OutputId" + modelInput.getjobName();
				modelOutputAtom.setName(modelOutputName);
				sweepOutputAtom.addChild(modelOutputAtom);

				jobFinishedHook.run();

				counter++;
			}
		}

		refresh();

	}

	/**
	 * Checks if the variables that are references by the given variable ranges are active. If not an error message is
	 * shown to the user;
	 */
	private boolean checkIfAllReferencedVariablesAreActive(List<AbstractVariableRange<?>> variableRanges) {
		List<String> inactiveVariables = new ArrayList<>();
		for (AbstractVariableRange<?> variableRange : variableRanges) {
			String variableModelPath = variableRange.getSourceVariableModelPath();
			VariableField<?, ?> variableField;
			try {
				variableField = this.getChildFromRoot(variableModelPath);
			} catch (IllegalArgumentException exception) {
				String message = "Could not find atom '" + variableModelPath + "'.";
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
			AbstractAtom<?> data = this.getChildFromRoot(dataAtomPath);
			data.addChild(sweepOutputAtom);
			LOG.info("Created " + sweepPutputAtomPath + " for sweep output.");
		}

	}

	@Override
	public ModelInputGenerator getModelInputGenerator() {
		return new SweepModelInputGenerator(this);
	}

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

		Action addIntegerRange = new AddChildAtomTreeViewerAction(
				IntegerVariableRange.class,
				"integerRange",
				Activator.getImage("integerVariableRange.png"),
				this,
				treeViewer);
		actions.add(addIntegerRange);

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

		Action addStudyInfoExport = new AddChildAtomTreeViewerAction(
				StudyInfoExport.class,
				"studyInfoExport",
				Activator.getImage("studyInfoExport.png"),
				this,
				treeViewer);
		actions.add(addStudyInfoExport);

		return actions;
	}

	//#region CREATE CHILD ATOMS

	public DoubleVariableRange createDoubleVariableRange(String name) {
		DoubleVariableRange child = new DoubleVariableRange(name);
		addChild(child);
		return child;
	}

	public IntegerVariableRange createIntegerVariableRange(String name) {
		IntegerVariableRange child = new IntegerVariableRange(name);
		addChild(child);
		return child;
	}

	public BooleanVariableRange createBooleanVariableRange(String name) {
		BooleanVariableRange child = new BooleanVariableRange(name);
		addChild(child);
		return child;
	}

	public StringVariableRange createStringVariableRange(String name) {
		StringVariableRange child = new StringVariableRange(name);
		addChild(child);
		return child;
	}

	public StringItemVariableRange createStringItemVariableRange(String name) {
		StringItemVariableRange child = new StringItemVariableRange(name);
		addChild(child);
		return child;
	}

	public FilePathVariableRange createFilePathVariableRange(String name) {
		FilePathVariableRange child = new FilePathVariableRange(name);
		addChild(child);
		return child;
	}

	public DirectoryPathVariableRange createDirectoryPathVariableRange(String name) {
		DirectoryPathVariableRange child = new DirectoryPathVariableRange(name);
		addChild(child);
		return child;
	}

	public QuantityVariableRange createQuantityVariableRange(String name) {
		QuantityVariableRange child = new QuantityVariableRange(name);
		addChild(child);
		return child;
	}

	public StudyInfoExport createStudyInfoExport(String name) {
		StudyInfoExport child = new StudyInfoExport(name);
		addChild(child);
		return child;
	}

	//#end region

	//#end region

}
