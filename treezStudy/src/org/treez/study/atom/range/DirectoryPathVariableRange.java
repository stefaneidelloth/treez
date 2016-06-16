package org.treez.study.atom.range;

import java.util.List;
import java.util.Objects;

import org.eclipse.swt.graphics.Image;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.DirectoryPathList;
import org.treez.core.atom.attribute.ModelPathSelectionType;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.variablefield.DirectoryPathVariableField;
import org.treez.study.Activator;

/**
 * Represents a variable range for directories. The parent must by a Study (e.g. Sweep)
 */
public class DirectoryPathVariableRange extends AbstractVariableRange<String> {

	//#region ATTRIBUTES

	/**
	 * Used to edit a list of Strings
	 */
	private DirectoryPathList range;

	//#end region

	//#region CONSTRUCTORS

	public DirectoryPathVariableRange(String name) {
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
				.createModelPath(sourceVariableModelPath, this, defaultValue, DirectoryPathVariableField.class,
						selectionType, modelEntryPoint, hasToBeEnabled)
				.setLabel("Directory path variable");
		boolean assignRelativeRoot = sourceModelModelPath != null && !sourceModelModelPath.isEmpty();
		if (assignRelativeRoot) {
			assignRealtiveRootToSourceVariablePath();
		}

		// range
		range = new DirectoryPathList("range");
		range.setLabel("Directory paths");
		data.addChild(range);

		//enabled check box
		createEnabledCheckBox();

		setModel(root);
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		Image baseImage = Activator.getImage("directoryPathVariableRange.png");
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
	public void setRangeValueString(String valueString) {
		Objects.requireNonNull(valueString, "ValueString must not be null");
		range.setValue(valueString);
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
