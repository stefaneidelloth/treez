package org.treez.results.atom.axis;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.arrays.Array;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.scales.LinearScale;
import org.treez.javafxd3.d3.scales.Scale;
import org.treez.results.atom.veuszpage.GraphicsPageModel;

/**
 * Represents the minor tick lines
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class MinorTicks implements GraphicsPageModel {

	//#region ATTRIBUTES

	/**
	 * Number of minor ticks aimed for
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

		Page minorTicksPage = root.createPage("minorTicks", "   Minor ticks   ");

		Section ticksSection = minorTicksPage.createSection("ticks", "Ticks");

		ticksSection.createTextField(number, "number", "40");

		ticksSection.createColorChooser(color, "color", "black");

		ticksSection.createTextField(width, "width", "2");

		ticksSection.createTextField(length, "length", "5");

		ticksSection.createLineStyle(style, "style", "solid");

		ticksSection.createTextField(transparency, "transparency", "0");

		ticksSection.createCheckBox(hide, "hide");

	}

	@Override
	public Selection plotWithD3(D3 d3, Selection axisSelection, Selection rectSelection, GraphicsAtom parent) {

		number.addModificationConsumerAndRun("replotMinorTicks", (data) -> {
			replotMinorTicks(axisSelection, parent);
		});
		return axisSelection;
	}

	private void replotMinorTicks(Selection axisSelection, GraphicsAtom parent) {

		Axis axis = (Axis) parent;
		Scale<?> scale = axis.getScale();

		boolean isHorizontal = axis.data.direction.get().equals("horizontal");

		boolean isLog = axis.data.log.get();

		Selection primaryMinorTickLines;
		Selection secondaryMinorTickLines;
		if (isLog) {

			axisSelection //
					.selectAll("g")
					.selectAll(".tick:not(.major)")
					.classed("minor", true);

			primaryMinorTickLines = axisSelection //
					.selectAll(".primary") //
					.selectAll(".minor") //
					.selectAll("line");

			secondaryMinorTickLines = axisSelection //
					.selectAll(".secondary") //
					.selectAll(".minor") //
					.selectAll("line");

			if (isHorizontal) {
				primaryMinorTickLines.attr("y2", "-" + length.get());
				secondaryMinorTickLines.attr("y2", length.get());
			} else {
				primaryMinorTickLines.attr("x2", length.get());
				secondaryMinorTickLines.attr("x2", "-" + length.get());
			}

		} else {

			//remove old minor ticks
			axisSelection.selectAll(".minor").remove();

			//recreate minor ticks
			int numberOfTicksAimedFor = getNumberOfTicksAimedFor();
			LinearScale linearScale = ((LinearScale) scale);
			Array<Double> tickData = linearScale.ticks(numberOfTicksAimedFor);

			Selection primaryMinorTicks = axisSelection //
					.selectAll(".primary") //
					.selectAll(".tick") //
					.dataExpression(tickData, "function(d) { return d; }") //
					.enter() //
					.insert("g", ".domain") //insert instead of append to ensure that tick lines are not on top in "z-order"
					.classed("minor", true);

			primaryMinorTickLines = primaryMinorTicks.append("line").attr("stroke", "black");

			Selection secondaryMinorTicks = axisSelection //
					.selectAll(".secondary") //
					.selectAll("tick") //
					.dataExpression(tickData, "function(d) { return d; }") //
					.enter() //
					.insert("g", ".domain") //insert instead of append to ensure that tick lines are not on top in "z-order"
					.classed("minor", true);

			secondaryMinorTickLines = secondaryMinorTicks.append("line").attr("stroke", "black");

			//set tick line geometry
			if (isHorizontal) {
				primaryMinorTickLines //
						.attr("x1", linearScale) //
						.attr("x2", linearScale)
						.attr("y1", "0") //
						.attr("y2", "-" + length.get()); //
				secondaryMinorTickLines //
						.attr("x1", linearScale) //
						.attr("x2", linearScale) //
						.attr("y1", "0") //
						.attr("y2", length.get());

			} else {
				primaryMinorTickLines //
						.attr("x1", "0") //
						.attr("x2", length.get()) //
						.attr("y1", linearScale) //
						.attr("y2", linearScale);
				secondaryMinorTickLines //
						.attr("x1", "0") //
						.attr("x2", "-" + length.get()) //
						.attr("y1", linearScale) //
						.attr("y2", linearScale);
			}
		}

		//bind tick properties
		length.addModificationConsumerAndRun("length", (data) -> {
			Axis parentAxis = (Axis) parent;
			boolean axisIsHorizontal = parentAxis.data.direction.get().equals("horizontal");
			if (axisIsHorizontal) {
				primaryMinorTickLines.attr("y2", "-" + length.get());
				secondaryMinorTickLines.attr("y2", length.get());
			} else {
				primaryMinorTickLines.attr("x2", length.get());
				secondaryMinorTickLines.attr("x2", "-" + length.get());
			}
		});

		Selection minorTickLines = axisSelection //
				.selectAll("g")
				.selectAll(".minor")
				.selectAll("line");

		GraphicsAtom.bindStringAttribute(minorTickLines, "stroke", color);
		GraphicsAtom.bindStringAttribute(minorTickLines, "stroke-width", width);
		GraphicsAtom.bindLineStyle(minorTickLines, style);
		GraphicsAtom.bindLineTransparency(minorTickLines, transparency);
		GraphicsAtom.bindLineTransparencyToBooleanAttribute(minorTickLines, hide, transparency);
	}

	private int getNumberOfTicksAimedFor() {
		int numberOfTicks = 0;
		try {
			numberOfTicks = Integer.parseInt(number.get());
		} catch (NumberFormatException exception) {
			//do nothing
		}
		return numberOfTicks;
	}

	@Override
	public String createVeuszText(AbstractAtom parent) {
		String veuszString = "\n";
		return veuszString;
	}

	//#end region

}
