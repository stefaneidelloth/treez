package org.treez.study.atom.picking;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.variablefield.VariableField;
import org.treez.study.Activator;

/**
 * Represents a picking parameter variation. The variation does does not walk through a whole definition space. Instead,
 * a few parameter tuples are "picked". The picked parameter tuples do not have to be located on a rectangular grid in
 * the definition space.
 */
public class Sample extends AdjustableAtom {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(Sample.class);

	//#region ATTRIBUTES

	/**
	 * Contains all data of the sample, maps from VariableField name to VariableField atom
	 */
	private Map<String, VariableField> variableData;

	/**
	 * Same as variableData, but for temporary data during update
	 */
	private Map<String, VariableField> tempVariableData;

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public Sample(String name) {
		super(name);
	}

	//#end region

	//#region METHODS

	@Override
	public AbstractControlAdaption createControlAdaption(Composite parent, Refreshable treeViewRefreshable) {
		updateSampleModel();
		AbstractControlAdaption controlAdaption = super.createControlAdaption(parent, treeViewRefreshable);
		return controlAdaption;
	}

	//#region CREATE & UPDATE MODEL

	private void updateSampleModel() {

		// root, page and section
		AttributeRoot root = new AttributeRoot("root");
		Page dataPage = root.createPage("data", "   Data   ");

		String relativeHelpContextId = "pickingSample";
		String absoluteHelpContextId = Activator.getInstance().getAbsoluteHelpContextId(relativeHelpContextId);

		Section pickingSection = dataPage.createSection("sample", absoluteHelpContextId);

		//create / update variable fields
		createOrUpdateVariableFields(pickingSection);

		setModel(root);

	}

	private void createOrUpdateVariableFields(Section pickingSection) {

		List<VariableField> variableFields = getVariableFieldsFromParent();

		if (variableData == null) {
			variableData = new LinkedHashMap<>();
		}

		//transfer data from variableData to tempVariableData if they
		//still exist and create or update new variable fields
		tempVariableData = new LinkedHashMap<>();
		for (VariableField variableField : variableFields) {
			if (variableField != null) {
				createOrUpdateVariableField(variableField);
			}
		}

		//update map and model with adapted variable data
		variableData = new LinkedHashMap<>();
		for (String variableName : tempVariableData.keySet()) {
			VariableField variableField = tempVariableData.get(variableName);
			variableData.put(variableName, variableField);
			AbstractAtom variableFieldAtom = (AbstractAtom) variableField;
			pickingSection.addChild(variableFieldAtom);
		}

	}

	private void createOrUpdateVariableField(VariableField variableFieldAtom) {
		String variableFieldName = variableFieldAtom.getName();
		boolean alreadyExists = variableData.containsKey(variableFieldName);
		if (alreadyExists) {
			updateVariableField(variableFieldAtom);
		} else {
			createVariableField(variableFieldAtom);
		}
	}

	private void updateVariableField(VariableField variableField) {
		String name = variableField.getName();
		VariableField oldVariableField = variableData.get(name);
		String newLabel = variableField.getLabel();
		oldVariableField.setLabel(newLabel);

		AbstractAtom variableFieldAtom = (AbstractAtom) oldVariableField;
		AbstractAtom newVariableFieldAtom = variableFieldAtom.copy();
		VariableField newVariableField = (VariableField) newVariableFieldAtom;
		tempVariableData.put(name, newVariableField);
	}

	private void createVariableField(VariableField variableField) {
		AbstractAtom variableFieldAtom = (AbstractAtom) variableField;
		AbstractAtom newVariableFieldAtom = variableFieldAtom.copy();
		VariableField newVariableField = (VariableField) newVariableFieldAtom;
		String variableFieldName = newVariableFieldAtom.getName();
		tempVariableData.put(variableFieldName, newVariableField);
	}

	private List<VariableField> getVariableFieldsFromParent() {
		AbstractAtom parent = this.getParentAtom();
		checkIfParentIsPicking(parent);
		Picking picking = (Picking) parent;
		List<VariableField> variableFields = picking.getPickingVariables();
		return variableFields;
	}

	private static void checkIfParentIsPicking(AbstractAtom parent) {
		boolean parentIsPicking = parent instanceof Picking;
		if (!parentIsPicking) {
			String message = "Sample atoms must be used with a Picking parent.";
			throw new IllegalStateException(message);
		}
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("pickingSample.png");
	}

	//#end region

}
