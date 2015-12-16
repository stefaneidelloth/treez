package org.treez.results.atom.xy;

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
 * XY error bar settings
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class ErrorBar implements GraphicsPageModel {

	//#region ATTRIBUTES

	/**
	 *
	 */
	public final Attribute<String> color = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> width = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> style = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> transparency = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<Boolean> hide = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> endSize = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<Boolean> hideHorizontal = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<Boolean> hideVertical = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page errorBarPage = root.createPage("errorBar", "   Error Bar  ");

		Section errorBarLine = errorBarPage.createSection("errorBarLine", "Error bar line");

		errorBarLine.createColorChooser(color, "color", "black");

		errorBarLine.createSize(width, "width", "0.5pt");

		errorBarLine.createLineStyle(style, "style");

		errorBarLine.createTextField(transparency, "transparency", "0");

		errorBarLine.createCheckBox(hide, "hide");

		errorBarLine.createTextField(endSize, "endSize", "End size", "1");

		errorBarLine.createCheckBox(hideHorizontal, "hideHorizontal", "Hide horz.");

		errorBarLine.createCheckBox(hideVertical, "hideVertical", "Hide vert.");
	}

	@Override
	public Selection plotWithD3(Selection graphSelection, Selection rectSelection, GraphicsAtom parent) {

		//parent.bindStringAttribute(selection, "x", leftMargin);

		return graphSelection;
	}

	@Override
	public String createVeuszText(AbstractAtom parent) {

		String veuszString = "";

		return veuszString;
	}

	//#end region

}
