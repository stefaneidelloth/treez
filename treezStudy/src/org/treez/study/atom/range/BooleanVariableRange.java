package org.treez.study.atom.range;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.ComboBox;
import org.treez.core.atom.attribute.ModelPathSelectionType;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.variablefield.BooleanVariableField;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.study.Activator;

/**
 * Represents a variable range for boolean values (true & false or false & true). The parent must by a Study (e.g.
 * Sweep)
 */
public class BooleanVariableRange extends AbstractVariableRange<Boolean> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings({ "hiding", "unused" })
	private static Logger sysLog = Logger.getLogger(BooleanVariableRange.class);

	// #region ATTRIBUTES

	/**
	 * Used to select the value range
	 */
	private Attribute<String> range = new Wrap<>();

	// #end region

	// #region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public BooleanVariableRange(String name) {
		super(name);
	}

	// #end region

	// #region METHODS

	/**
	 * Creates the underlying model
	 */
	@Override
	protected void createVariableRangeModel() {
		// root, page and section
		AttributeRoot root = new AttributeRoot("root");
		Page dataPage = root.createPage("data", "   Data   ");

		data = dataPage.createSection("data");

		// source variable
		String defaultValue = "";
		ModelPathSelectionType selectionType = ModelPathSelectionType.FLAT;
		AbstractAtom modelEntryPoint = this;
		boolean hasToBeEnabled = true;

		data.createModelPath(sourceVariableModelPath, "sourceVariableModelPath", "Boolean variable", defaultValue,
				BooleanVariableField.class, selectionType, modelEntryPoint, hasToBeEnabled);

		boolean assignRelativeRoot = sourceModelModelPath != null && !sourceModelModelPath.isEmpty();
		if (assignRelativeRoot) {
			assignRealtiveRootToSourceVariablePath();
		}

		// range
		String items = "true & false,false & true";
		String defaultItem = "true & false";
		ComboBox rangeCombo = data.createComboBox(range, "range", items, defaultItem);
		rangeCombo.setLabel("Range");

		//enabled check box
		createEnabledCheckBox();

		setModel(root);
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		Image baseImage = Activator.getImage("booleanVariableRange.png");
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
	public List<Boolean> getRange() {
		List<Boolean> booleanRange = new ArrayList<>();
		booleanRange.add(true);
		booleanRange.add(false);
		String rangeString = range.get();
		boolean reverse = rangeString.equals("false & true");
		if (reverse) {
			Collections.reverse(booleanRange);
		}
		return booleanRange;
	}

	/**
	 * Sets the given valueString as valueString of the range
	 *
	 * @param valueString
	 */
	public void setRangeValueString(String valueString) {
		Objects.requireNonNull(valueString, "ValueString must not be null");

		boolean valueIsOk = valueString.equals("true & false") || valueString.equals("false & true");
		if (!valueIsOk) {
			String message = "The value '" + valueString + "' is not allowed.";
			throw new IllegalArgumentException(message);
		}
		range.set(valueString);
	}

	//#end region

	//#region TYPE

	@Override
	public Class<Boolean> getType() {
		return Boolean.class;
	}

	//#end region

	//#end region

}
