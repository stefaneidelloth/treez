package org.treez.study.atom.picking;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.CheckBox;
import org.treez.core.atom.attribute.FilePath;
import org.treez.core.atom.attribute.ModelPath;
import org.treez.core.atom.attribute.ModelPathSelectionType;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.variablefield.VariableField;
import org.treez.core.atom.variablelist.VariableList;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.AddChildAtomTreeViewerAction;
import org.treez.model.interfaces.Model;
import org.treez.study.Activator;
import org.treez.study.atom.AbstractParameterVariation;

/**
 * Represents a picking parameter variation. The variation does does not walk through a whole definition space. Instead,
 * a few parameter tuples are "picked". The picked parameter tuples do not have to be located on a rectangular grid in
 * the definition space.
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Picking extends AbstractParameterVariation {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(Picking.class);

	//#region ATTRIBUTES

	/**
	 * The variables for which values are picked
	 */
	public final Attribute<List<VariableField>> variables = new Wrap<>();

	/**
	 * A handle to the variable list (that is wrapped in the Attribute 'variables')
	 */
	private VariableList variableList;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public Picking(String name) {
		super(name);
		createPickingModel();
	}

	//#end region

	//#region METHODS

	@Override
	public AbstractControlAdaption createControlAdaption(Composite parent, Refreshable treeViewRefreshable) {
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
		AbstractAtom modelEntryPoint = this;

		//model to run
		String modelToRunDefaultValue = "";
		pickingSection
				.createModelPath(modelToRunModelPath, "modelToRunModelPath", modelToRunDefaultValue, Model.class,
						selectionType, modelEntryPoint, false)
				.setLabel("Model to run");

		//variable source model
		String sourceModelDefaultValue = "";
		ModelPath modelPath = pickingSection.createModelPath(sourceModelPath, "sourceModelPath",
				sourceModelDefaultValue, Model.class, selectionType, modelEntryPoint, false);
		modelPath.setLabel("Variable source model (provides variables)");

		//variable list
		variableList = pickingSection.createVariableList(variables, "variables", "Picking variables");

		//add listener to update variable list for new source model path and do initial update
		modelPath.addModifyListener("updateVariableList", (modifyEvent) -> updateAvailableVariablesForVariableList());

		//export study info check box
		CheckBox export = pickingSection.createCheckBox(exportStudyInfo, "exportStudyInfo", true);
		export.setLabel("Export study information");

		//export study info path
		FilePath filePath = pickingSection.createFilePath(exportStudyInfoPath, "exportStudyInfoPath",
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
	 * Determines the available variables with the variable source model path and updates the available variables of the
	 * variable list.
	 */
	private void updateAvailableVariablesForVariableList() {

		AbstractAtom parent = this.getParentAtom();
		if (parent != null) {
			List<VariableField> availableVariables = new ArrayList<>();
			AbstractAtom sourceModel = getSourceModelAtom();
			List<AbstractAtom> children = sourceModel.getChildAtoms();
			for (AbstractAtom child : children) {
				boolean isVariableField = child instanceof VariableField;
				if (isVariableField) {
					VariableField variableField = (VariableField) child;
					availableVariables.add(variableField);
				}
			}
			variableList.setAvailableVariables(availableVariables);
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
	 * Creates the context menu actions for this atom
	 */
	@Override
	protected List<Object> createContextMenuActions(TreeViewerRefreshable treeViewer) {

		List<Object> actions = new ArrayList<>();

		//sample
		Action addSample = new AddChildAtomTreeViewerAction(
				Sample.class,
				"sample",
				Activator.getImage("pickingSample.png"),
				this,
				treeViewer);
		actions.add(addSample);

		return actions;
	}

	@Override
	public void runStudy(Refreshable refreshable, IProgressMonitor monitor) {
		//not yet implemented
	}

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
	public List<VariableField> getPickingVariables() {
		List<VariableField> selectedVariables = variableList.get();
		return selectedVariables;
	}

	//#end region

}
