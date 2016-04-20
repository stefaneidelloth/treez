package org.treez.results.atom.xy;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.CheckBox;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.attribute.TextField;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.results.atom.graphicspage.GraphicsPropertiesPageFactory;

/**
 * XY error bar settings
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class ErrorBar implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	public final Attribute<String> color = new Wrap<>();

	public final Attribute<String> width = new Wrap<>();

	public final Attribute<String> style = new Wrap<>();

	public final Attribute<String> transparency = new Wrap<>();

	public final Attribute<Boolean> hide = new Wrap<>();

	public final Attribute<String> endSize = new Wrap<>();

	public final Attribute<Boolean> hideHorizontal = new Wrap<>();

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

		TextField endSizeField = errorBarLine.createTextField(endSize, "endSize", "1");
		endSizeField.setLabel("End size");

		CheckBox hideHorz = errorBarLine.createCheckBox(hideHorizontal, "hideHorizontal");
		hideHorz.setLabel("Hide horz.");
		CheckBox hideVert = errorBarLine.createCheckBox(hideVertical, "hideVertical");
		hideVert.setLabel("Hide vert.");
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection graphSelection, Selection rectSelection, GraphicsAtom parent) {

		//parent.bindStringAttribute(selection, "x", leftMargin);

		return graphSelection;
	}

	//#end region

}
