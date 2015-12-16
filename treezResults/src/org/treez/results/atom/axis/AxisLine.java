package org.treez.results.atom.axis;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.results.atom.graphics.GraphicsAtom;
import org.treez.results.atom.veuszpage.GraphicsPageModel;

/**
 * Represents the line of an axis
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class AxisLine implements GraphicsPageModel {

	//#region ATTRIBUTES

	/**
	 * Line type
	 */
	public final Attribute<String> axisLine = new Wrap<>();

	/**
	 * Line width
	 */
	public final Attribute<String> width = new Wrap<>();

	/**
	 * Line style
	 */
	public final Attribute<String> style = new Wrap<>();

	/**
	 * Line transparency
	 */
	public final Attribute<String> transparency = new Wrap<>();

	/**
	 * Hide
	 */
	public final Attribute<Boolean> hide = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page axisLinePage = root.createPage("axisLine", "   Axis line   ");

		Section axisLineSection = axisLinePage.createSection("axisLine", "Axis line");

		axisLineSection.createColorChooser(axisLine, "color", "black");

		axisLineSection.createSize(width, "width", "0.5pt");

		axisLineSection.createLineStyle(style, "style");

		axisLineSection.createTextField(transparency, "transparency", "0");

		axisLineSection.createCheckBox(hide, "hide");

	}

	@Override
	public Selection plotWithD3(Selection graphSelection, Selection rectSelection, GraphicsAtom parent) {

		//parent.bindStringAttribute(selection, "x", leftMargin);

		return graphSelection;
	}

	@Override
	public String createVeuszText(AbstractAtom parent) {

		String veuszString = "\n";

		return veuszString;
	}

	//#end region

}
