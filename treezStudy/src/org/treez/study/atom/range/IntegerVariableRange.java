package org.treez.study.atom.range;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.attribute.attributeContainer.Page;
import org.treez.core.atom.attribute.modelPath.ModelPathSelectionType;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.variablefield.IntegerVariableField;
import org.treez.core.atom.variablelist.IntegerVariableListField;
import org.treez.study.Activator;

/**
 * Represents a variable range of Integer values, might consist of one or several values. The parent must by a Study
 * (e.g. Sweep)
 */
public class IntegerVariableRange extends AbstractVariableRange<Integer> {

	//#region ATTRIBUTES

	private IntegerVariableListField range;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public IntegerVariableRange(String name) {
		super(name);
	}

	//#end region

	//#region METHODS

	@Override
	protected void createVariableRangeModel() {
		// root, page and section
		AttributeRoot root = new AttributeRoot("root");
		Page dataPage = root.createPage("data", "   Data   ");
		data = dataPage.createSection("data");

		// source variable
		String defaultValue = "";
		ModelPathSelectionType selectionType = ModelPathSelectionType.FLAT;
		AbstractAtom<?> modelEntryPoint = this;
		boolean hasToBeEnabled = true;
		data
				.createModelPath(sourceVariableModelPath, this, defaultValue, IntegerVariableField.class, selectionType,
						modelEntryPoint, hasToBeEnabled)
				.setLabel("Integer variable");
		boolean assignRelativeRoot = sourceModelModelPath != null && !sourceModelModelPath.isEmpty();
		if (assignRelativeRoot) {
			assignRealtiveRootToSourceVariablePath();
		}

		//range
		range = data.createIntegerVariableListField("range", "Range");

		//enabled check box
		createEnabledCheckBox();

		setModel(root);
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		Image baseImage = Activator.getImage("integerVariableRange.png");
		Image image = decorateImageWidthEnabledState(baseImage);
		return image;
	}

	//#end region

	//#region ACCESSORS

	//#region RANGE VALUES

	/**
	 * Returns the range as a list of Integers
	 *
	 * @return
	 */
	@Override
	public List<Integer> getRange() {
		return range.get();
	}

	/**
	 * Sets the given valueString as valueString of the range
	 *
	 * @param valueString
	 */
	@Override
	public void setRangeValueString(String valueString) {
		range.setValueString(valueString);
	}

	@Override
	public void setRange(Integer... rangeValues) {
		range.set(Arrays.asList(rangeValues));
	}

	//#end region

	//#region TYPE

	@Override
	public Class<Integer> getType() {
		return Integer.class;
	}

	//#end region

	//#end region

}
