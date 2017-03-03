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
 * Represents the major tick lines
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class MajorTicks implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	/**
	 * Number of major ticks aimed for
	 */
	public final Attribute<String> number = new Wrap<>();

	public final Attribute<String> color = new Wrap<>();

	public final Attribute<String> width = new Wrap<>();

	public final Attribute<String> length = new Wrap<>();

	public final Attribute<String> style = new Wrap<>();

	public final Attribute<Double> transparency = new Wrap<>();

	public final Attribute<Boolean> hide = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom<?> parent) {

		Page majorTicksPage = root.createPage("majorTicks", "   Major ticks   ");

		Section ticksSection = majorTicksPage.createSection("ticks");

		ticksSection.createTextField(number, this, "6");

		ticksSection.createColorChooser(color, this, "black");

		ticksSection.createTextField(width, this, "2");

		ticksSection.createTextField(length, this, "10");

		ticksSection.createLineStyle(style, this, "solid");

		ticksSection.createDoubleVariableField(transparency, this, 0.0);

		ticksSection.createCheckBox(hide, this);

	}

	@Override
	public Selection plotWithD3(D3 d3, Selection axisSelection, Selection rectSelection, AbstractGraphicsAtom parent) {

		//Hint: The major tick lines already have been created with the axis (see Data).
		//Here only the properties of the ticks need to be applied.

		Axis axis = (Axis) parent;

		Selection primary = axisSelection //
				.selectAll(".primary");

		Selection secondary = axisSelection //
				.selectAll(".secondary");

		markMajorTicksWithCssClass(axis, primary, secondary);

		Selection primaryMajorTickLines = primary //
				.selectAll(".major") //
				.selectAll("line");

		Selection secondaryMajorTickLines = secondary //
				.selectAll(".major") //
				.selectAll("line")
				.style("fill", "none") //
				.style("shape-rendering", "geometricPrecision");

		Selection majorTickLines = axisSelection //
				.selectAll("g") //
				.selectAll(".major") //
				.selectAll("line");

		number.addModificationConsumer("replotAxis", () -> axis.updatePlotWithD3(d3));

		length.addModificationConsumerAndRun("length", () -> {
			boolean isHorizontal = axis.data.direction.get().isHorizontal();
			if (isHorizontal) {
				primaryMajorTickLines.attr("y2", "-" + length.get());
				secondaryMajorTickLines.attr("y2", "" + length.get());
			} else {
				primaryMajorTickLines.attr("x2", length.get());
				secondaryMajorTickLines.attr("x2", "-" + length.get());
			}
		});

		AbstractGraphicsAtom.bindStringAttribute(majorTickLines, "stroke", color);
		AbstractGraphicsAtom.bindStringAttribute(majorTickLines, "stroke-width", width);
		AbstractGraphicsAtom.bindLineStyle(majorTickLines, style);
		AbstractGraphicsAtom.bindLineTransparency(majorTickLines, transparency);
		AbstractGraphicsAtom.bindLineTransparencyToBooleanAttribute(majorTickLines, hide, transparency);

		return axisSelection;
	}

	private static void markMajorTicksWithCssClass(Axis axis, Selection primary, Selection secondary) {

		if (axis.data.isQuantitative()) {
			boolean isLog = axis.data.log.get();
			if (isLog) {

				primary //
						.selectAll(".tick:nth-child(1)") //
						.classed("major", true);

				primary //
						.selectAll(".tick:nth-child(9n+1)") //
						.classed("major", true);

				secondary //
						.selectAll(".tick:nth-child(1)") //
						.classed("major", true);

				secondary //
						.selectAll(".tick:nth-child(9n+1)") //
						.classed("major", true);
				return;

			}
		}

		primary //
				.selectAll(".tick") //
				.classed("major", true);

		secondary //
				.selectAll(".tick") //
				.classed("major", true);

	}

	//#end region

}
