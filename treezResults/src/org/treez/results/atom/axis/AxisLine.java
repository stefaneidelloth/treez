package org.treez.results.atom.axis;

import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.attribute.attributeContainer.Page;
import org.treez.core.atom.attribute.attributeContainer.section.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.AbstractGraphicsAtom;
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

	public final Attribute<Double> transparency = new Wrap<>();

	public final Attribute<Boolean> hide = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom<?> parent) {

		Page axisLinePage = root.createPage("axisLine", "   Axis line   ");

		Section axisLineSection = axisLinePage.createSection("axisLine", "Axis line");

		axisLineSection.createColorChooser(color, this, "black");

		axisLineSection.createTextField(width, this, "2");

		axisLineSection.createLineStyle(style, this, "solid");

		axisLineSection.createDoubleVariableField(transparency, this, 0.0);

		axisLineSection.createCheckBox(hide, this);

	}

	@Override
	public Selection plotWithD3(D3 d3, Selection axisSelection, Selection rectSelection, AbstractGraphicsAtom parent) {

		Selection axisDomainLine = axisSelection //
				.selectAll(".domain") //
				.style("fill", "none") //
				.style("stroke-linecap", "square") //
				.style("shape-rendering", "geometricPrecision");

		AbstractGraphicsAtom.bindStringAttribute(axisDomainLine, "stroke", color);
		AbstractGraphicsAtom.bindStringAttribute(axisDomainLine, "stroke-width", width);
		AbstractGraphicsAtom.bindLineStyle(axisDomainLine, style);
		AbstractGraphicsAtom.bindLineTransparency(axisDomainLine, transparency);
		AbstractGraphicsAtom.bindLineTransparencyToBooleanAttribute(axisDomainLine, hide, transparency);

		return axisSelection;
	}

	//#end region

}
