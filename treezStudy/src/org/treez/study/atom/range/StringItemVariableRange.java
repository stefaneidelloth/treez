package org.treez.study.atom.range;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.eclipse.swt.graphics.Image;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.ModelPath;
import org.treez.core.atom.attribute.ModelPathSelectionType;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.StringItemList;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.variablefield.StringItemVariableField;
import org.treez.study.Activator;

/**
 * Represents a variable range for string items (similar to enumeration values). The parent must by a Study (e.g. Sweep)
 */
public class StringItemVariableRange extends AbstractVariableRange<String> {

	//#region ATTRIBUTES

	/**
	 * Used to edit a list of Strings
	 */
	private StringItemList range;

	//#end region

	//#region CONSTRUCTORS

	public StringItemVariableRange(String name) {
		super(name);
	}

	//#end region

	//#region METHODS

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
		AbstractAtom<?> modelEntryPoint = this;
		boolean hasToBeEnabled = true;
		ModelPath variablePath = data.createModelPath(sourceVariableModelPath, this, defaultValue,
				StringItemVariableField.class, selectionType, modelEntryPoint, hasToBeEnabled);
		variablePath.setLabel("String variable");
		variablePath.addModifyListener("updateItemList", (event) -> {
			updateStringItemListWithSourceVariable();
		});

		boolean assignRelativeRoot = sourceModelModelPath != null && !sourceModelModelPath.isEmpty();
		if (assignRelativeRoot) {
			assignRealtiveRootToSourceVariablePath();
		}

		// range
		range = new StringItemList("range", "Error!!");
		range.setLabel("Range");
		data.addChild(range);

		//enabled check box
		createEnabledCheckBox();

		setModel(root);
	}

	/**
	 * Updates the string item list after a new source variable has been selected
	 */
	private void updateStringItemListWithSourceVariable() {

		//get source variable
		String sourcePath = sourceVariableModelPath.get();
		if (sourcePath != null) {
			StringItemVariableField stringItemVariable = this.getChildFromRoot(sourcePath);
			List<String> availableItems = stringItemVariable.getItemList();
			//set available items (also clears old list)
			range.setAvailableItems(availableItems);
		}

	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		Image baseImage = Activator.getImage("stringItemVariableRange.png");
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
	public List<String> getRange() {
		List<String> stringList = range.get();
		return stringList;
	}

	/**
	 * Sets the given valueString as valueString of the range
	 *
	 * @param valueString
	 */
	@Override
	public void setRangeValueString(String valueString) {
		Objects.requireNonNull(valueString, "ValueString must not be null");
		range.setValue(valueString);
	}

	@Override
	public void setRange(String... rangeValues) {
		range.set(Arrays.asList(rangeValues));
	}

	//#end region

	//#region TYPE

	@Override
	public Class<String> getType() {
		return String.class;
	}

	//#end region

	//#end region

}
