package org.treez.results.javafxchart;

import java.util.Objects;

import javafx.geometry.Bounds;
import javafx.scene.Group;

public class GroupToSvgConverter extends AbstractNodeToSvgConverter<Group> {

	//#region ATTRIBUTES

	//#end region

	//#region CONSTRUCTORS

	//#end region

	//#region METHODS

	@Override
	public String extendCode(String initialSvgString, Group group) {

		addDataFromGroup(group);

		//create svg string from svg node properties
		String svgString = initialSvgString + createSvgString();

		//add svg text for child nodes and add the "g-end tag" if the node has children
		svgString = extendWithChildSvgCodeAndEndTag(svgString, group);

		return svgString;
	}

	/**
	 * Extracts svg properties directly from the given Group and applies them.
	 *
	 * @param nodeProperties
	 * @param node
	 */
	private void addDataFromGroup(Group group) {

		//isGroup
		svgNodeProperties.setIsGroup(true);

		//x & y
		Bounds bounds = group.getBoundsInParent();

		Double x = bounds.getMinX();
		Double xTranslate = group.getTranslateX();

		svgNodeProperties.setX(xTranslate);

		Double y = bounds.getMinY();
		Double yTranslate = group.getTranslateY();
		svgNodeProperties.setY(yTranslate);

		//a group does not have an own shape
		svgNodeProperties.setIsDefinedByRect(false);

	}

	@Override
	protected String createTagStartString(String idString, String styleString, String transformString) {

		Objects.requireNonNull(svgNodeProperties, "svg node propeties must not be null.");

		boolean hasChildren = svgNodeProperties.hasChildren();

		String startString = "";
		if (hasChildren) {
			//add a group tag as prefix and include the id, style and transform into that group tag
			startString = startString + indentation + "<g" + idString + styleString + transformString + ">\n";
			increaseIndentation();

		} else {
			//create individual tag and directly include id, style and transform
			startString = startString + indentation + "<g" + idString + styleString + transformString;
		}

		return startString;
	}

	/**
	 * Creates the geometry string (empty for a group).
	 *
	 * @param svgNodeProperties
	 * @return
	 */
	@Override
	protected String createGeometryString() {
		return "";
	}

	//#end region

	//#region ACCESSORS

	//#end region

}
