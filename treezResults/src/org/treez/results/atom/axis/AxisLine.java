package org.treez.results.atom.axis;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;

/**
 * Represents the line of an axis
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class AxisLine implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	public final Attribute<String> color = new Wrap<>();

	public final Attribute<String> width = new Wrap<>();

	public final Attribute<String> style = new Wrap<>();

	public final Attribute<String> transparency = new Wrap<>();

	public final Attribute<Boolean> hide = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page axisLinePage = root.createPage("axisLine", "   Axis line   ");

		Section axisLineSection = axisLinePage.createSection("axisLine", "Axis line");

		axisLineSection.createColorChooser(color, "color", "black");

		axisLineSection.createTextField(width, "width", "2");

		axisLineSection.createLineStyle(style, "style", "solid");

		axisLineSection.createTextField(transparency, "transparency", "0");

		axisLineSection.createCheckBox(hide, "hide");

	}

	@Override
	public Selection plotWithD3(D3 d3, Selection axisSelection, Selection rectSelection, GraphicsAtom parent) {

		Selection axisDomainLine = axisSelection //
				.selectAll(".domain") //
				.style("fill", "none") //
				.style("stroke-linecap", "square") //
				.style("shape-rendering", "geometricPrecision");

		GraphicsAtom.bindStringAttribute(axisDomainLine, "stroke", color);
		GraphicsAtom.bindStringAttribute(axisDomainLine, "stroke-width", width);
		GraphicsAtom.bindLineStyle(axisDomainLine, style);
		GraphicsAtom.bindLineTransparency(axisDomainLine, transparency);
		GraphicsAtom.bindLineTransparencyToBooleanAttribute(axisDomainLine, hide, transparency);

		return axisSelection;
	}

	//#end region

}
