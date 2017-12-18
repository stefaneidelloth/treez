package org.treez.study.atom.sensitivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.attribute.attributeContainer.Page;
import org.treez.core.atom.attribute.attributeContainer.section.Section;
import org.treez.core.atom.attribute.checkBox.CheckBox;
import org.treez.core.atom.attribute.comboBox.enumeration.EnumComboBox;
import org.treez.core.atom.attribute.fileSystem.FilePath;
import org.treez.core.atom.attribute.modelPath.ModelPath;
import org.treez.core.atom.attribute.modelPath.ModelPathSelectionType;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.variablefield.DoubleVariableField;
import org.treez.core.atom.variablefield.IntegerVariableField;
import org.treez.core.atom.variablefield.VariableField;
import org.treez.core.atom.variablelist.DoubleVariableListField;
import org.treez.core.atom.variablelist.VariableList;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.monitor.TreezMonitor;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.TreeViewerAction;
import org.treez.core.utils.Utils;
import org.treez.data.output.OutputAtom;
import org.treez.model.atom.AbstractModel;
import org.treez.model.input.HashMapModelInput;
import org.treez.model.input.ModelInput;
import org.treez.model.interfaces.Model;
import org.treez.model.output.ModelOutput;
import org.treez.study.Activator;
import org.treez.study.atom.AbstractParameterVariation;
import org.treez.study.atom.picking.PickingModelInputGenerator;
import org.treez.study.atom.picking.Sample;
import org.treez.study.atom.sweep.ExportStudyInfoType;

/**
 * Represents a sensitivity parameter variation where the varied values can be specified differently for each variables
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class CustomSensitivity extends AbstractParameterVariation {

	private static final Logger LOG = LogManager.getLogger(CustomSensitivity.class);

	//#region ATTRIBUTES

	/**
	 * Type of the sensitivity variation (RELATIVE_DISTANCE, ABSOLUTE_POSITION, ...)
	 */
	public final Attribute<SensitivityType> sensitivityType = new Wrap<>();

	/**
	 * If the sensitivity type uses relative numbers, there are several ways to do so. The relationType specifies how
	 * exactly relative numbers are entered.
	 */
	public final Attribute<RelationType> relationType = new Wrap<>();

	/**
	 * If true, the variation values for a variable are specified as range (with number of values and step size). If
	 * false, the variation is specified with individual values.
	 */
	public final Attribute<Boolean> usesRanges = new Wrap<>();

	public final Attribute<Integer> rangeSize = new Wrap<>();

	public final Attribute<Double> rangeStepSize = new Wrap<>();

	public final Attribute<List<Double>> individualValues = new Wrap<>();

	/**
	 * If true, the variation values are automatically mirrored around the working point value. If there is for example
	 * a working point value of {10} and the variation values are specified as 11, 12 ... then we will get the values 8,
	 * 9, {10}, 11, 12. The values 8, 9 are added by the auto mirror functionality to provide the wanted symmetry.
	 */
	public final Attribute<Boolean> autoMirror = new Wrap<>();

	public final Attribute<Boolean> generalizedVariation = new Wrap<>();

	/**
	 * The variables for which values are varied. The variable types that can be used depends on the type of the
	 * sensitivity study.
	 */
	public final Attribute<List<VariableField<?, ?>>> variables = new Wrap<>();

	/**
	 * A handle to the variable list (that is wrapped in the Attribute 'variables')
	 */
	private VariableList variableList;

	private EnumComboBox<RelationType> relationTypeCombo;

	private CheckBox usesRangesCheckBox;

	private CheckBox autoMirrorCheckBox;

	private CheckBox generalizedVariationCheckBox;

	private IntegerVariableField rangeSizeField;

	private DoubleVariableField rangeStepSizeField;

	private DoubleVariableListField individualValuesField;

	//#end region

	//#region CONSTRUCTORS

	public CustomSensitivity(String name) {
		super(name);
		createSensitivityModel();
	}

	//#end region

	//#region METHODS

	@Override
	public
			AbstractControlAdaption
			createControlAdaption(Composite parent, FocusChangingRefreshable treeViewRefreshable) {

		updateAvailableVariablesForVariableList();
		return super.createControlAdaption(parent, treeViewRefreshable);

	}

	private void createSensitivityModel() {
		// root, page and section
		AttributeRoot root = new AttributeRoot("root");
		Page dataPage = root.createPage("data", "   Data   ");

		String relativeHelpContextId = "sensitivity";
		String absoluteHelpContextId = Activator.getInstance().getAbsoluteHelpContextId(relativeHelpContextId);

		Section sensitivitySection = dataPage.createSection("sensitivity", absoluteHelpContextId);
		sensitivitySection.createSectionAction("action", "Run sensitivity", () -> execute(treeViewRefreshable));

		//choose selection type and entry atom
		ModelPathSelectionType selectionType = ModelPathSelectionType.FLAT;
		AbstractAtom<?> modelEntryPoint = this;

		//model to run
		String modelToRunDefaultValue = "";
		sensitivitySection
				.createModelPath(modelToRunModelPath, this, modelToRunDefaultValue, Model.class, selectionType,
						modelEntryPoint, false)
				.setLabel("Model to run");

		//variable source model
		String sourceModelDefaultValue = "";
		ModelPath modelPath = sensitivitySection.createModelPath(sourceModelPath, this, sourceModelDefaultValue,
				Model.class, selectionType, modelEntryPoint, false);
		modelPath.setLabel("Variable source model (provides variables)");

		//sensitivity type
		sensitivitySection
				.createEnumComboBox(sensitivityType, this, SensitivityType.RELATIVE_DISTANCE) //
				.setLabel("Sensitivity type") //
				.addModificationConsumer("sensitivityTypeChanged", () -> sensitivityTypeChanged());

		//relation type
		relationTypeCombo = sensitivitySection
				.createEnumComboBox(relationType, this, RelationType.PERCENTAGE) //
				.setLabel("Relation type");

		usesRangesCheckBox = sensitivitySection
				.createCheckBox(usesRanges, this, true) //
				.setLabel("Use ranges") //
				.addModificationConsumer("usesRangesChanged", () -> usesRangesChanged());

		autoMirrorCheckBox = sensitivitySection
				.createCheckBox(autoMirror, this, false) //
				.setLabel("Mirror sample values");

		generalizedVariationCheckBox = sensitivitySection
				.createCheckBox(generalizedVariation, this, true) //
				.setLabel("Use same variation for all variables");

		Section valuesSection = dataPage.createSection("values", absoluteHelpContextId);

		rangeSizeField = valuesSection
				.createIntegerVariableField(rangeSize, this, 2) //
				.setLabel("Number of sample values");

		rangeStepSizeField = valuesSection
				.createDoubleVariableField(rangeStepSize, this, 10.0) //
				.setLabel("Step size");

		individualValuesField = valuesSection
				.createDoubleVariableListField(individualValues, this, "Sample values") //
				.set(Arrays.asList(new Double[] { -10.0, 10.0 }));

		//variable list
		Section variableSection = dataPage.createSection("variables", absoluteHelpContextId);
		variableList = variableSection.createVariableList(variables, this, "Picking variables");

		//add listener to update variable list for new source model path and do initial update
		modelPath.addModificationConsumer("updateVariableList", () -> updateAvailableVariablesForVariableList());

		//study info
		Section studyInfoSection = dataPage.createSection("studyInfo", absoluteHelpContextId);
		studyInfoSection.setLabel("Export study info");

		//export study info combo box
		EnumComboBox<ExportStudyInfoType> exportStudy = studyInfoSection.createEnumComboBox(exportStudyInfoType, this,
				ExportStudyInfoType.DISABLED);
		exportStudy.setLabel("Export study information");

		//export sweep info path
		FilePath filePath = studyInfoSection.createFilePath(exportStudyInfoPath, this,
				"Target file path for study information", "");
		filePath.setValidatePath(false);

		filePath.addModificationConsumer("updateEnabledState", () -> {

			ExportStudyInfoType exportType = exportStudyInfoType.get();
			switch (exportType) {
			case DISABLED:
				filePath.setEnabled(false);
				break;
			case TEXT_FILE:
				filePath.setEnabled(true);
				break;
			case SQLITE:
				filePath.setEnabled(true);
				break;
			case MYSQL:
				filePath.setEnabled(false);
				break;
			default:
				throw new IllegalStateException("The export type '" + exportType + "' has not yet been implemented.");
			}

		});

		setModel(root);
	}

	private void sensitivityTypeChanged() {
		SensitivityType type = sensitivityType.get();
		relationTypeCombo.setVisible(type.isRelative());

	}

	private void usesRangesChanged() {
		Boolean isUsingRanges = usesRanges.get();
		if (isUsingRanges) {
			rangeSizeField.setEnabled(true);
			rangeStepSizeField.setEnabled(true);
			individualValuesField.setEnabled(false);
		} else {
			rangeSizeField.setEnabled(false);
			rangeStepSizeField.setEnabled(false);
			individualValuesField.setEnabled(true);
		}
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
		return Activator.getImage("sensitivity.png");
	}

	/**
	 * Creates the context menu actions for this atom
	 */
	@Override
	protected List<Object> createContextMenuActions(TreeViewerRefreshable treeViewer) {

		List<Object> actions = new ArrayList<>();

		actions.add(new TreeViewerAction(
				"Add sample",
				Activator.getImage("add.png"),
				treeViewer,
				() -> LOG.debug("add sample")));

		return actions;
	}

	@Override
	public void runStudy(FocusChangingRefreshable refreshable, SubMonitor monitor) {

		/*
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
		*/

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
			SubMonitor monitor,
			PickingModelInputGenerator inputGenerator,
			List<Sample> samples) {

		//get total number of simulations
		int numberOfSimulations = samples.size();
		LOG.info("Number of total simulations: " + numberOfSimulations);

		//initialize progress monitor
		monitor.beginTask("", numberOfSimulations);

		//reset job index to 1
		HashMapModelInput.resetIdCounter();

		//create model inputs
		List<ModelInput> modelInputs = inputGenerator.createModelInputs(studyId.get(), studyDescription.get(), samples);

		//export study info to text file if the corresponding option is enabled
		if (exportStudyInfoType.get() != ExportStudyInfoType.DISABLED) {
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
		LOG.info("The picking output is located at " + studyOutputAtomPath);
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
			SubMonitor monitor,
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
				TreezMonitor subMonitor = new TreezMonitor("Executing target", monitor, 1);

				//execute model
				ModelOutput modelOutput = model.runModel(modelInput, refreshable, subMonitor);

				//post process model output
				AbstractAtom<?> modelOutputAtom = modelOutput.getOutputAtom();
				String modelOutputName = getName() + "OutputId" + modelInput.getJobId();
				modelOutputAtom.setName(modelOutputName);
				pickingOutputAtom.addChild(modelOutputAtom);
				refresh();
				counter++;

			}

		}
	}

	//#end region

	//#region ACCESSORS

	public void setIndividualValuesValueString(String valueString) {

	}

	@Override
	public String getId() {
		return studyId.get();
	}

	@Override
	public String getDescription() {
		return studyDescription.get();
	}

	//#end region

}
