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
import org.treez.javafxd3.d3.arrays.Array;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.scales.LinearScale;
import org.treez.javafxd3.d3.scales.Scale;

/**
 * Represents the minor tick lines
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class MinorTicks implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	/**
	 * Number of minor ticks aimed for
	 */
	public final Attribute<String> number = new Wrap<>();

	public final Attribute<String> color = new Wrap<>();

	public final Attribute<String> width = new Wrap<>();

	public final Attribute<String> length = new Wrap<>();

	public final Attribute<String> style = new Wrap<>();

	public final Attribute<String> transparency = new Wrap<>();

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

		number.addModificationConsumerAndRun("replotMinorTicks", () -> {
			replotMinorTicks(axisSelection, parent);
		});
		return axisSelection;
	}

	private void replotMinorTicks(Selection axisSelection, GraphicsAtom parent) {

		Axis axis = (Axis) parent;
		Scale<?> scale = axis.getScale();
		boolean isHorizontal = axis.data.direction.get().equals("horizontal");
		boolean isLog = axis.data.log.get();

		PrimaryAndSecondarySelection minorTickLineSelections;
		if (isLog) {
			minorTickLineSelections = createMinorTickLinesForLogScale(axisSelection, isHorizontal);
		} else {
			minorTickLineSelections = createMinorTickLinesForLinearScale(axisSelection, scale, isHorizontal);
		}

		//bind tick properties
		length.addModificationConsumerAndRun("length", () -> {
			Axis parentAxis = (Axis) parent;
			boolean axisIsHorizontal = parentAxis.data.direction.get().equals("horizontal");
			if (axisIsHorizontal) {
				minorTickLineSelections.getPrimary().attr("y2", "-" + length.get());
				minorTickLineSelections.getSecondary().attr("y2", length.get());
			} else {
				minorTickLineSelections.getPrimary().attr("x2", length.get());
				minorTickLineSelections.getSecondary().attr("x2", "-" + length.get());
			}
		});

		Selection allMinorTickLines = axisSelection //
				.selectAll("g").selectAll(".minor").selectAll("line");

		GraphicsAtom.bindStringAttribute(allMinorTickLines, "stroke", color);
		GraphicsAtom.bindStringAttribute(allMinorTickLines, "stroke-width", width);
		GraphicsAtom.bindLineStyle(allMinorTickLines, style);
		GraphicsAtom.bindLineTransparency(allMinorTickLines, transparency);
		GraphicsAtom.bindLineTransparencyToBooleanAttribute(allMinorTickLines, hide, transparency);
	}

	private PrimaryAndSecondarySelection createMinorTickLinesForLogScale(
			Selection axisSelection,
			boolean isHorizontal) {

		//note: for log scales no additional minor ticks are created;
		//only the already existing ticks are used (and modified in their length)

		PrimaryAndSecondarySelection minorTickLineSelections = new PrimaryAndSecondarySelection();

		axisSelection //
				.selectAll("g").selectAll(".tick:not(.major)").classed("minor", true);

		Selection primaryMinorTickLines = axisSelection //
				.selectAll(".primary") //
				.selectAll(".minor") //
				.selectAll("line");
		minorTickLineSelections.setPrimary(primaryMinorTickLines);

		Selection secondaryMinorTickLines = axisSelection //
				.selectAll(".secondary") //
				.selectAll(".minor") //
				.selectAll("line");
		minorTickLineSelections.setSecondary(secondaryMinorTickLines);
		if (isHorizontal) {
			primaryMinorTickLines.attr("y2", "-" + length.get());
			secondaryMinorTickLines.attr("y2", length.get());
		} else {
			primaryMinorTickLines.attr("x2", length.get());
			secondaryMinorTickLines.attr("x2", "-" + length.get());
		}

		return minorTickLineSelections;
	}

	private PrimaryAndSecondarySelection createMinorTickLinesForLinearScale(
			Selection axisSelection,
			Scale<?> scale,
			boolean isHorizontal) {
		PrimaryAndSecondarySelection minorTickLineSelections = new PrimaryAndSecondarySelection();

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

		Selection primaryMinorTickLines = primaryMinorTicks //
				.append("line") //
				.attr("stroke", "black");
		minorTickLineSelections.setPrimary(primaryMinorTickLines);

		Selection secondaryMinorTicks = axisSelection //
				.selectAll(".secondary") //
				.selectAll("tick") //
				.dataExpression(tickData, "function(d) { return d; }") //
				.enter() //
				.insert("g", ".domain") //insert instead of append to ensure that tick lines are not on top in "z-order"
				.classed("minor", true);

		Selection secondaryMinorTickLines = secondaryMinorTicks //
				.append("line") //
				.attr("stroke", "black");
		minorTickLineSelections.setSecondary(secondaryMinorTickLines);

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

		return minorTickLineSelections;
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

	//#end region

}
