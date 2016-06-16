package org.treez.study.atom.probability;

import java.util.Objects;

import org.eclipse.swt.graphics.Image;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.CheckBox;
import org.treez.core.atom.attribute.ModelPath;
import org.treez.core.atom.attribute.ModelPathSelectionType;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.variablefield.DoubleVariableField;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.AttributeWrapper;
import org.treez.core.attribute.Wrap;
import org.treez.study.Activator;

/**
 * Represents an assumption with a normal probability distribution.
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class AbstractAssumption extends AdjustableAtom {

	//#region ATTRIBUTES

	/**
	 * The section
	 */
	protected Section data;

	/**
	 * The enabled state check box
	 */
	public Attribute<Boolean> enabled = new Wrap<>(new CheckBox("enabled", true));

	/**
	 * The absolute model path to the source variable (the atom control might only display the relative path in respect
	 * to the source model)
	 */
	public Attribute<String> sourceVariableModelPath = new Wrap<>();

	/**
	 * The full model path to the source model (is updated when setting the parent Study atom)
	 */
	protected String sourceModelModelPath = null;

	//#end region

	//#region CONSTRUCTORS

	public AbstractAssumption(String name) {
		super(name);
		createAbstractAssumptionModel();
	}

	//#end region

	//#region METHODS

	/**
	 * Creates the underlying model
	 */
	private void createAbstractAssumptionModel() {
		// root, page and section
		AttributeRoot root = new AttributeRoot("root");
		Page dataPage = root.createPage("data", "   Data   ");

		String relativeHelpContextId = "normal";
		String absoluteHelpContextId = Activator.getInstance().getAbsoluteHelpContextId(relativeHelpContextId);

		data = dataPage.createSection("normal", absoluteHelpContextId);

		//source variable
		String defaultValue = "";
		ModelPathSelectionType selectionType = ModelPathSelectionType.FLAT;
		AbstractAtom<?> modelEntryPoint = this;
		boolean hasToBeEnabled = true;
		data
				.createModelPath(sourceVariableModelPath, this, defaultValue, DoubleVariableField.class, selectionType,
						modelEntryPoint, hasToBeEnabled)
				.setLabel("Double variable");

		boolean assignRelativeRoot = sourceModelModelPath != null && !sourceModelModelPath.isEmpty();
		if (assignRelativeRoot) {
			assignRealtiveRootToSourceVariablePath();
		}

		setModel(root);
	}

	/**
	 * Changes the model path selection for the source variable to use the source model as relative root
	 */
	protected void assignRealtiveRootToSourceVariablePath() {
		Objects.requireNonNull(sourceModelModelPath, "Source model path must not be null when calling this function.");
		data.setLabel("Data for " + sourceModelModelPath);
		AbstractAtom<?> relativeRootAtom = this.getChildFromRoot(sourceModelModelPath);
		AttributeWrapper<String> pathWrapper = (AttributeWrapper<String>) sourceVariableModelPath;
		ModelPath modelPath = (ModelPath) pathWrapper.getAttribute();
		modelPath.setModelRelativeRoot(relativeRootAtom);
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("normalAssumption.png");
	}

	//#end region

}
