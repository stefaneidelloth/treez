package org.treez.results.atom.xy;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.functions.AxisScaleFirstDatumFunction;
import org.treez.javafxd3.d3.functions.AxisScaleSecondDatumFunction;
import org.treez.javafxd3.d3.scales.QuantitativeScale;
import org.treez.results.atom.graphicspage.GraphicsPropertiesPageFactory;

/**
 * XY line settings
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Line implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	/**
	 * Interpolation mode
	 */
	public final Attribute<String> interpolation = new Wrap<>();

	/**
	 *
	 */
	//public final Attribute<Boolean> bezierJoin = new Wrap<>();

	/**
	 * Color
	 */
	public final Attribute<String> color = new Wrap<>();

	/**
	 * Width
	 */
	public final Attribute<String> width = new Wrap<>();

	/**
	 * Style
	 */
	public final Attribute<String> style = new Wrap<>();

	/**
	 * Transparency
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

		Page linePage = root.createPage("line", "   Line    ");

		Section line = linePage.createSection("line", "Line");

		line.createEnumComboBox(interpolation, "interpolation", InterpolationMode.LINEAR);

		//line.createCheckBox(bezierJoin, "bezierJoin", "Bezier join");

		line.createColorChooser(color, "color", "black");

		line.createTextField(width, "width", "3");

		line.createLineStyle(style, "style", "solid");

		line.createTextField(transparency, "transparency", "0");

		line.createCheckBox(hide, "hide");
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection xySelection, Selection rectSelection, GraphicsAtom parent) {

		String parentName = parent.getName();
		String id = "lines_" + parentName;

		//remove old line group if it already exists
		xySelection //
				.selectAll("#" + id) //
				.remove();

		//create new line group
		Selection linesSelection = xySelection //
				.append("g") //
				.attr("id", id) //
				.attr("class", "lines");

		//get xy parent
		Xy xy = (Xy) parent;

		//get interpolation mode
		String modeString = interpolation.get();
		org.treez.javafxd3.d3.svg.InterpolationMode mode = org.treez.javafxd3.d3.svg.InterpolationMode
				.fromValue(modeString);

		//line path generator
		QuantitativeScale<?> xScale = xy.getXScale();
		QuantitativeScale<?> yScale = xy.getYScale();

		org.treez.javafxd3.d3.svg.Line linePathGenerator = d3 //
				.svg()//
				.line()
				.x(new AxisScaleFirstDatumFunction(xScale))
				.y(new AxisScaleSecondDatumFunction(yScale))//
				.interpolate(mode);

		//plot new lines
		String xyDataString = xy.getXyDataString();
		Selection lines = linesSelection //
				.append("path") //
				.attr("d", linePathGenerator.generate(xyDataString))
				.attr("fill", "none");

		//bind attributes
		GraphicsAtom.bindDisplayToBooleanAttribute("hideLine", lines, hide);
		GraphicsAtom.bindStringAttribute(lines, "stroke", color);
		GraphicsAtom.bindStringAttribute(lines, "stroke-width", width);
		GraphicsAtom.bindLineTransparency(lines, transparency);
		GraphicsAtom.bindLineStyle(lines, style);

		interpolation.addModificationConsumer("replot", (data) -> {
			//if the line interpolation changes other stuff like the area
			//has to be updated as well
			//=> update the whole xy
			xy.updatePlotWithD3(d3);
		});

		return xySelection;
	}

	//#end region

}
