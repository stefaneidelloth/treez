package org.treez.results.atom.axis;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.results.atom.graphicspage.GraphicsPropertiesPageFactory;

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

	/**
	 * Line type
	 */
	public final Attribute<String> color = new Wrap<>();

	/**
	 * Line width
	 */
	public final Attribute<String> width = new Wrap<>();

	/**
	 * Tick length
	 */
	public final Attribute<String> length = new Wrap<>();

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

		Page majorTicksPage = root.createPage("majorTicks", "   Major ticks   ");

		Section ticksSection = majorTicksPage.createSection("ticks", "Ticks");

		ticksSection.createTextField(number, "number", "6");

		ticksSection.createColorChooser(color, "color", "black");

		ticksSection.createTextField(width, "width", "2");

		ticksSection.createTextField(length, "length", "10");

		ticksSection.createLineStyle(style, "style", "solid");

		ticksSection.createTextField(transparency, "transparency", "0");

		ticksSection.createCheckBox(hide, "hide");

	}

	@Override
	public Selection plotWithD3(D3 d3, Selection axisSelection, Selection rectSelection, GraphicsAtom parent) {

		//Hint: The major tick lines already have been created with the axis (see Data).
		//Here only the properties of the ticks need to be applied.

		Axis axis = (Axis) parent;
		boolean isLog = axis.data.log.get();

		Selection primary = axisSelection //
				.selectAll(".primary");

		Selection secondary = axisSelection //
				.selectAll(".secondary");

		markMajorTicksWithCssClass(isLog, primary, secondary);

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

		number.addModificationConsumer("replotAxis", (data) -> axis.updatePlotWithD3(d3));

		length.addModificationConsumerAndRun("length", (data) -> {
			boolean isHorizontal = axis.data.direction.get().equals("horizontal");
			if (isHorizontal) {
				primaryMajorTickLines.attr("y2", "-" + length.get());
				secondaryMajorTickLines.attr("y2", "" + length.get());
			} else {
				primaryMajorTickLines.attr("x2", length.get());
				secondaryMajorTickLines.attr("x2", "-" + length.get());
			}
		});

		GraphicsAtom.bindStringAttribute(majorTickLines, "stroke", color);
		GraphicsAtom.bindStringAttribute(majorTickLines, "stroke-width", width);
		GraphicsAtom.bindLineStyle(majorTickLines, style);
		GraphicsAtom.bindLineTransparency(majorTickLines, transparency);
		GraphicsAtom.bindLineTransparencyToBooleanAttribute(majorTickLines, hide, transparency);

		return axisSelection;
	}

	private static void markMajorTicksWithCssClass(boolean isLog, Selection primary, Selection secondary) {
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

		} else {
			primary //
					.selectAll(".tick") //
					.classed("major", true);

			secondary //
					.selectAll(".tick") //
					.classed("major", true);
		}
	}

	//#end region

}
