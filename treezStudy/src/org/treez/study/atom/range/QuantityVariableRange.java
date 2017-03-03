package org.treez.study.atom.range;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.attribute.attributeContainer.Page;
import org.treez.core.atom.attribute.modelPath.ModelPathSelectionType;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.variablefield.QuantityVariableField;
import org.treez.core.atom.variablelist.QuantityVariableListField;
import org.treez.core.quantity.Quantity;
import org.treez.study.Activator;

/**
 * Represents a variable range of Quantities, might consist of one or several values. The parent must by a Study (e.g.
 * Sweep)
 */
public class QuantityVariableRange extends AbstractVariableRange<Quantity> {

	//#region ATTRIBUTES

	/**
	 * Used to enter the value range and the unit, will give a list of Quantities
	 */
	private QuantityVariableListField range; //VariableListField extends AttributeAtom<List<Quantity>>

	//#end region

	//#region CONSTRUCTORS

	public QuantityVariableRange(String name) {
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
				.createModelPath(sourceVariableModelPath, this, defaultValue, QuantityVariableField.class,
						selectionType, modelEntryPoint, hasToBeEnabled)
				.setLabel("Quantity variable");
		boolean assignRelativeRoot = sourceModelModelPath != null && !sourceModelModelPath.isEmpty();
		if (assignRelativeRoot) {
			assignRealtiveRootToSourceVariablePath();
		}

		//range
		range = data.createQuantityVariableListField("range", "Range");

		//enabled check box
		createEnabledCheckBox();

		setModel(root);
	}

	@Override
	public Image provideImage() {
		Image baseImage = Activator.getImage("quantityVariableRange.png");
		Image image = decorateImageWidthEnabledState(baseImage);
		return image;
	}

	//#end region

	//#region ACCESSORS

	//#region RANGE VALUES

	/**
	 * Returns the range as a list of quantities
	 *
	 * @return
	 */
	@Override
	public List<Quantity> getRange() {
		return range.get();
	}

	/**
	 * Returns the numeric values of the range as Double list
	 *
	 * @return
	 */
	public List<Double> getNumericRange() {
		return range.getDoubleValue();
	}

	/**
	 * Returns the unit
	 *
	 * @return
	 */
	public String getUnitString() {
		return range.getUnitString();
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

	/**
	 * Sets the given unitString as unitString of the range
	 *
	 * @param unitString
	 */
	public void setRangeUnitString(String unitString) {
		range.setUnitString(unitString);
	}

	@Override
	public void setRange(Quantity... rangeValues) {
		range.set(Arrays.asList(rangeValues));
	}

	//#end region

	//#region TYPE

	@Override
	public Class<Quantity> getType() {
		return Quantity.class;
	}

	//#end region

	//#end region

}
