package org.treez.study.atom.picking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.CheckBox;
import org.treez.core.atom.attribute.FilePath;
import org.treez.core.atom.attribute.ModelPath;
import org.treez.core.atom.attribute.ModelPathSelectionType;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.variablefield.DoubleVariableField;
import org.treez.core.atom.variablefield.IntegerVariableField;
import org.treez.core.atom.variablefield.VariableField;
import org.treez.core.atom.variablelist.DoubleVariableListField;
import org.treez.core.atom.variablelist.IntegerVariableListField;
import org.treez.core.atom.variablelist.NumberRangeProvider;
import org.treez.core.atom.variablelist.VariableList;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.AddChildAtomTreeViewerAction;
import org.treez.core.utils.Utils;
import org.treez.data.output.OutputAtom;
import org.treez.model.atom.AbstractModel;
import org.treez.model.input.HashMapModelInput;
import org.treez.model.input.ModelInput;
import org.treez.model.interfaces.Model;
import org.treez.model.output.ModelOutput;
import org.treez.study.Activator;
import org.treez.study.atom.AbstractParameterVariation;

/**
 * Represents a picking parameter variation. The variation does does not walk through a whole definition space. Instead,
 * a few parameter tuples are "picked". The picked parameter tuples do not have to be located on a rectangular grid in
 * the definition space.
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Picking extends AbstractParameterVariation implements NumberRangeProvider {

	private static final Logger LOG = Logger.getLogger(Picking.class);

	//#region ATTRIBUTES

	/**
	 * True if the variable values depends on a "time parameter". Each picking variable will then be specified with an
	 * array of values.
	 */
	public final Attribute<Boolean> isTimeDependent = new Wrap<>();

	/**
	 * The model path of the variable that represents the time. The variable values may be of type Integer or Double.
	 */
	public final Attribute<String> timeVariableModelPath = new Wrap<>();

	/**
	 * The range for the time variable. This also determines the length of the arrays that have to be specified for the
	 * individual picking variables. Only used if type of time variable corresponds to Integer values.
	 */
	private final Attribute<List<Integer>> integerTimeRange = new Wrap<>();

	/**
	 * The range for the time variable. This also determines the length of the arrays that have to be specified for the
	 * individual picking variables. Only used if type of time variable corresponds to Double values.
	 */
	private final Attribute<List<Double>> doubleTimeRange = new Wrap<>();

	/**
	 * The atom that represents the timeRange
	 */
	private AbstractAttributeAtom<?, ?> timeRangeAtom;

	/**
	 * The variables for which values are picked
	 */
	public final Attribute<List<VariableField<?, ?>>> variables = new Wrap<>();

	/**
	 * A handle to the variable list (that is wrapped in the Attribute 'variables')
	 */
	private VariableList variableList;

	//#end region

	//#region CONSTRUCTORS

	public Picking(String name) {
		super(name);
		createPickingModel();
	}

	//#end region

	//#region METHODS

	@Override
	public AbstractControlAdaption createControlAdaption(
			Composite parent,
			FocusChangingRefreshable treeViewRefreshable) {
		updateAvailableVariablesForVariableList();
		AbstractControlAdaption controlAdaption = super.createControlAdaption(parent, treeViewRefreshable);
		return controlAdaption;
	}

	/**
	 * Creates the underlying model
	 */
	private void createPickingModel() {
		// root, page and section
		AttributeRoot root = new AttributeRoot("root");
		Page dataPage = root.createPage("data", "   Data   ");

		String relativeHelpContextId = "picking";
		String absoluteHelpContextId = Activator.getInstance().getAbsoluteHelpContextId(relativeHelpContextId);

		Section pickingSection = dataPage.createSection("picking", absoluteHelpContextId);
		pickingSection.createSectionAction("action", "Run picking", () -> execute(treeViewRefreshable));

		//choose selection type and entry atom
		ModelPathSelectionType selectionType = ModelPathSelectionType.FLAT;
		AbstractAtom<?> modelEntryPoint = this;

		//model to run
		String modelToRunDefaultValue = "";
		pickingSection
				.createModelPath(modelToRunModelPath, this, modelToRunDefaultValue, Model.class, selectionType,
						modelEntryPoint, false)
				.setLabel("Model to run");

		//variable source model
		String sourceModelDefaultValue = "";
		ModelPath modelPath = pickingSection.createModelPath(sourceModelPath, this, sourceModelDefaultValue,
				Model.class, selectionType, modelEntryPoint, false);
		modelPath.setLabel("Variable source model (provides variables)");

		//time dependent picking
		Section timeDependentSection = dataPage.createSection("timeDependent", absoluteHelpContextId);
		timeDependentSection.setLabel("Time dependent picking");
		timeDependentSection.setExpanded(false);
		CheckBox isTimeDependentCheckBox = timeDependentSection.createCheckBox(isTimeDependent, this);
		isTimeDependentCheckBox.setLabel("Use time series");
		isTimeDependentCheckBox.set(false);

		Class<?>[] supportedVariableClasses = { IntegerVariableField.class, DoubleVariableField.class };
		ModelPath timeVariablePath = timeDependentSection.createModelPath(timeVariableModelPath, this, "",
				supportedVariableClasses);
		timeVariablePath.setLabel("Time variable");
		timeVariablePath.setEnabled(false);

		timeVariablePath.addModificationConsumer("recreateTimeRangeAtom", () -> {
			String variablePath = timeVariablePath.get();
			recreateTimeRangeAtom(timeDependentSection, variablePath);
		});

		//recreateTimeRangeAtom(timeDependentSection, timeVariablePath.get());

		isTimeDependentCheckBox.addModificationConsumer("showOrHideDependentAttributes", () -> {
			boolean isSelected = isTimeDependentCheckBox.get();
			timeVariablePath.setEnabled(isSelected);
			if (timeRangeAtom != null) {
				timeRangeAtom.setEnabled(isSelected);
			}
		});

		//variable list
		Section variableSection = dataPage.createSection("variables", absoluteHelpContextId);
		variableList = variableSection.createVariableList(variables, this, "Picking variables");

		//add listener to update variable list for new source model path and do initial update
		modelPath.addModifyListener("updateVariableList", (modifyEvent) -> updateAvailableVariablesForVariableList());

		//study info
		Section studyInfoSection = dataPage.createSection("studyInfo", absoluteHelpContextId);
		studyInfoSection.setLabel("Export study info");

		//export study info check box
		CheckBox export = studyInfoSection.createCheckBox(exportStudyInfo, this, true);
		export.setLabel("Export study information");

		//export study info path
		FilePath filePath = studyInfoSection.createFilePath(exportStudyInfoPath, this,
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
	 * Creates a range atom that fits to the type of the time variable
	 *
	 * @param variablePath
	 */
	private void recreateTimeRangeAtom(Section section, String variablePath) {

		//remove old time range if it exists
		String timeRangeAtomName = getFieldName(integerTimeRange, this);
		section.removeChildIfExists(timeRangeAtomName);

		String doubleRangeAtomName = getFieldName(doubleTimeRange, this);
		section.removeChildIfExists(doubleRangeAtomName);

		if (variablePath == null || "".equals(variablePath)) {
			timeRangeAtom = null;
			if (treeViewRefreshable != null) {
				treeViewRefreshable.refresh();
			}
			return;
		}

		AbstractAtom<?> variableAtom = this.getChildFromRoot(variablePath);
		Class<?> atomClass = variableAtom.getClass();
		boolean isDoubleVariable = DoubleVariableField.class.isAssignableFrom(atomClass);
		if (isDoubleVariable) {
			timeRangeAtom = section.createDoubleVariableListField(doubleTimeRange, this, "Time range (Double)");
			if (treeViewRefreshable != null) {
				treeViewRefreshable.refresh();
			}
			return;
		}

		boolean isIntegerVariable = IntegerVariableField.class.isAssignableFrom(atomClass);
		if (isIntegerVariable) {
			timeRangeAtom = section.createIntegerVariableListField(integerTimeRange, this, "Time range (Integer)");
			if (treeViewRefreshable != null) {
				treeViewRefreshable.refresh();
			}
			return;
		}

		String message = "Could not create range atom for variable of type '" + atomClass.getSimpleName() + "'";
		throw new IllegalStateException(message);

	}

	/**
	 * Determines the available variables with the variable source model path and updates the available variables of the
	 * variable list.
	 */
	private void updateAvailableVariablesForVariableList() {

		AbstractAtom<?> parent = this.getParentAtom();
		if (parent != null) {
			List<VariableField<?, ?>> availableVariables = new ArrayList<>();
			AbstractModel sourceModel = getSourceModelAtom();
			if (sourceModel != null) {
				List<AbstractAtom<?>> children = sourceModel.getChildAtoms();
				for (AbstractAtom<?> child : children) {
					boolean isVariableField = child instanceof VariableField;
					if (isVariableField) {
						VariableField<?, ?> variableField = (VariableField<?, ?>) child;
						availableVariables.add(variableField);
					}
				}
				variableList.setAvailableVariables(availableVariables);
			}
		}
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("picking.png");
	}

	/**
	 * Extends the context menu actions for this atom
	 */
	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {

		//create sample
		Action addSample = new AddChildAtomTreeViewerAction(
				Sample.class,
				"sample",
				Activator.getImage("pickingSample.png"),
				this,
				treeViewer);
		actions.add(addSample);

		return actions;
	}

	//#region EXECUTE

	@Override
	public void execute(FocusChangingRefreshable refreshable) {
		String jobTitle = "Picking '" + getName() + "'";
		runNonUiJob(jobTitle, (monitor) -> {
			runStudy(refreshable, monitor);
		});

	}

	@Override
	public void runStudy(FocusChangingRefreshable refreshable, IProgressMonitor monitor) {
		Objects.requireNonNull(monitor, "You need to pass a valid IProgressMonitor that is not null.");
		this.treeViewRefreshable = refreshable;

		String startMessage = "Executing picking '" + getName() + "'";
		LOG.info(startMessage);

		//create ModelInput generator
		PickingModelInputGenerator inputGenerator = new PickingModelInputGenerator(this);

		//get samples
		List<Sample> samples = inputGenerator.getEnabledSamples();
		int numberOfSamples = samples.size();
		LOG.info("Number of samples: " + numberOfSamples);

		boolean isTimeDependentPicking = this.isTimeDependent.get();
		if (isTimeDependentPicking) {
			int numberOfTimeSteps = inputGenerator.getNumberOfTimeSteps();
			LOG.info("Number of time steps: " + numberOfTimeSteps);
		}

		if (numberOfSamples > 0) {
			Sample firstSample = samples.get(0);

			//check if the picking variables reference enabled variables
			boolean allReferencedVariablesAreActive = checkIfAllReferencedVariablesAreActive(firstSample);
			if (allReferencedVariablesAreActive) {
				doRunStudy(refreshable, monitor, inputGenerator, samples);
			}
		}

	}

	/**
	 * Checks if the variables that are references by the given sample are active. If not an error message is shown to
	 * the user;
	 *
	 * @param variableRanges
	 * @return
	 */
	private boolean checkIfAllReferencedVariablesAreActive(Sample sample) {

		Map<String, VariableField<?, ?>> variableData = sample.getVariableData();

		List<String> inactiveVariables = new ArrayList<>();
		for (String variableName : variableData.keySet()) {
			String sourceModelPath = this.sourceModelPath.get();
			String variableModelPath = sourceModelPath + "." + variableName;
			VariableField<?, ?> variableField;
			try {
				variableField = this.getChildFromRoot(variableModelPath);
			} catch (IllegalArgumentException exception) {
				String message = "Could not find variable atom '" + variableModelPath + "'.";
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

	private void doRunStudy(
			FocusChangingRefreshable refreshable,
			IProgressMonitor monitor,
			PickingModelInputGenerator inputGenerator,
			List<Sample> samples) {

		//get total number of simulations
		int numberOfSimulations = samples.size();
		LOG.info("Number of total simulations: " + numberOfSimulations);

		//initialize progress monitor
		monitor.beginTask("", numberOfSimulations);

		//reset study index to 1
		HashMapModelInput.resetIdCounter();

		//create model inputs
		List<ModelInput> modelInputs = inputGenerator.createModelInputs(samples);

		//export study info to text file if the corresponding option is enabled
		if (exportStudyInfo.get()) {
			exportStudyInfo(samples, numberOfSimulations);
		}

		//prepare result structure
		prepareResultStructure();
		refresh();

		//get sweep output atom
		String studyOutputAtomPath = getStudyOutputAtomPath();
		AbstractAtom<?> studyOutputAtom = this.getChildFromRoot(studyOutputAtomPath);

		//remove all old children if they exist
		studyOutputAtom.removeAllChildren();

		//execute target model for all model inputs
		executeTargetModel(refreshable, monitor, numberOfSimulations, modelInputs, studyOutputAtom);

		//inform progress monitor to be done
		monitor.setTaskName("=>Finished!");

		//show end message
		logAndShowSweepEndMessage();
		LOG.info("The picking outout is located at " + studyOutputAtomPath);
		monitor.done();
	}

	/**
	 * Creates a text file with some information about the sweep and saves it at the exportSweepInfoPath
	 *
	 * @param variableRanges
	 * @param numberOfSimulations
	 */
	private void exportStudyInfo(List<Sample> samples, int numberOfSimulations) {
		String studyInfo = "---------- PickingInfo ----------\r\n\r\n" + //
				"Total number of simulations:\r\n" + numberOfSimulations + "\r\n\r\n" + //
				"Source model path:\r\n" + sourceModelPath.get() + "\r\n\r\n" + //
				"Variable names and values:\r\n\r\n";

		for (Sample sample : samples) {
			studyInfo += "== Sample '" + sample.getName() + "' ===\r\n";

			Map<String, VariableField<?, ?>> variableData = sample.getVariableData();
			for (String variableName : variableData.keySet()) {
				VariableField<?, ?> variableField = variableData.get(variableName);
				String valueString = variableField.getValueString();
				studyInfo += variableName + ": " + valueString + "\r\n";
			}
			studyInfo += "\r\n";
		}

		String filePath = exportStudyInfoPath.get();
		File file = new File(filePath);

		try {
			FileUtils.writeStringToFile(file, studyInfo);
		} catch (IOException exception) {
			String message = "The specified exportStudyInfoPath '" + filePath
					+ "' is not valid. Export of study info is skipped.";
			LOG.error(message);
		}

	}

	/**
	 * Creates the result structure if it does not yet exist to have a place in the tree where the sweep result can be
	 * put. The sweep results will not be a child of the Sweep put a child of for example root.results.data.sweepOutput
	 */
	private void prepareResultStructure() {
		createResultsAtomIfNotExists();
		createDataAtomIfNotExists();
		createPickingOutputAtomIfNotExists();
		this.refresh();
	}

	/**
	 * Creates the Picking output atom if it does not yet exist
	 */
	private void createPickingOutputAtomIfNotExists() {
		String dataAtomPath = createOutputDataAtomPath();
		String pickingOutputAtomName = createStudyOutputAtomName();
		String pickingPutputAtomPath = getStudyOutputAtomPath();
		boolean pickingOutputAtomExists = this.rootHasChild(pickingPutputAtomPath);
		if (!pickingOutputAtomExists) {
			OutputAtom pickingOutputAtom = new OutputAtom(pickingOutputAtomName, provideImage());
			AbstractAtom<?> data = this.getChildFromRoot(dataAtomPath);
			data.addChild(pickingOutputAtom);
			LOG.info("Created " + pickingPutputAtomPath + " for picking output.");
		}
	}

	private void executeTargetModel(
			FocusChangingRefreshable refreshable,
			IProgressMonitor monitor,
			int numberOfSimulations,
			List<ModelInput> modelInputs,
			AbstractAtom<?> pickingOutputAtom) {
		int counter = 1;
		Model model = getModelToRun();
		long startTime = System.currentTimeMillis();
		for (ModelInput modelInput : modelInputs) {

			//allows to cancel the Picking if a user clicks the cancel button at the progress monitor window
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
				AbstractAtom<?> modelOutputAtom = modelOutput.getOutputAtom();
				String modelOutputName = getName() + "OutputId" + modelInput.getId();
				modelOutputAtom.setName(modelOutputName);
				pickingOutputAtom.addChild(modelOutputAtom);
				refresh();
				counter++;

			}

		}
	}

	//#end region

	//#region ADD VARIABLES

	/**
	 * Adds a variable to the picking probe so that values can be specified for it in the children of the picking atom
	 *
	 * @param variableName
	 */
	public void addVariable(String variableName) {
		String sourceModel = sourceModelPath.get();
		if (sourceModel != null) {
			String variablePath = sourceModel + "." + variableName;
			VariableField<?, ?> variableAtom = this.getChildFromRoot(variablePath);
			if (variableAtom != null) {
				variableList.addVariable(variableAtom);
			}
		}

	}

	//#end region

	//#region CREATE CHILD ATOMS

	/**
	 * Creates a new picking sample
	 *
	 * @param name
	 * @return
	 */
	public Sample createSample(String name) {
		Sample sample = new Sample(name);
		this.addChild(sample);
		return sample;
	}

	//#end region

	//#end region

	//#region ACCESSORS

	/**
	 * Returns a list of the variables that is used for the picking samples
	 *
	 * @return
	 */
	public List<VariableField<?, ?>> getPickingVariables() {
		List<VariableField<?, ?>> selectedVariables = variableList.get();
		return selectedVariables;
	}

	@Override
	public List<Number> getRange() {
		return getTimeRange();
	}

	@Override
	public Class<? extends Number> getRangeType() {
		if (isTimeDependent.get()) {
			Class<?> rangeType = timeRangeAtom.getClass();
			boolean isIntegerRange = IntegerVariableListField.class.isAssignableFrom(rangeType);
			if (isIntegerRange) {
				return Integer.class;
			}
			boolean isDoubleRange = DoubleVariableListField.class.isAssignableFrom(rangeType);
			if (isDoubleRange) {
				return Double.class;
			}

			String message = "Could not get value type for time range atom of type " + rangeType.getSimpleName();
			throw new IllegalStateException(message);
		} else {
			return null;
		}
	}

	/**
	 * Returns the time range. If the picking is not time dependent, null will be returned
	 *
	 * @return
	 */
	public List<Number> getTimeRange() {

		if (isTimeDependent.get()) {
			Class<?> rangeType = timeRangeAtom.getClass();
			boolean isIntegerRange = IntegerVariableListField.class.isAssignableFrom(rangeType);
			if (isIntegerRange) {
				IntegerVariableListField listField = (IntegerVariableListField) timeRangeAtom;
				List<Integer> timeRange = listField.get();
				List<Number> numberTimeRange = new ArrayList<>();
				numberTimeRange.addAll(timeRange);
				return numberTimeRange;
			}
			boolean isDoubleRange = DoubleVariableListField.class.isAssignableFrom(rangeType);
			if (isDoubleRange) {
				DoubleVariableListField listField = (DoubleVariableListField) timeRangeAtom;
				List<Double> timeRange = listField.get();
				List<Number> numberTimeRange = new ArrayList<>();
				numberTimeRange.addAll(timeRange);
				return numberTimeRange;
			}

			String message = "Could not get time range from time range atom of type " + rangeType.getSimpleName();
			throw new IllegalStateException(message);
		} else {
			return null;
		}

	}

	//#end region

}
