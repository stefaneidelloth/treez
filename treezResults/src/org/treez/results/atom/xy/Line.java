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
import org.treez.results.atom.graphicspage.GraphicsPropertiesPageModel;

/**
 * XY line settings
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Line implements GraphicsPropertiesPageModel {

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
		//see method replotWithD3
		return xySelection;
	}

	/**
	 * @param d3
	 * @param xySelection
	 * @param parent
	 * @param xyDataString
	 * @param xScale
	 * @param yScale
	 */
	public void replotWithD3(
			D3 d3,
			Selection xySelection,
			GraphicsAtom parent,
			String xyDataString,
			QuantitativeScale<?> xScale,
			QuantitativeScale<?> yScale) {

		Xy xy = (Xy) parent;
		interpolation.addModificationConsumer("replot", (data) -> {
			xy.area.replotWithD3(d3, xySelection, parent, xyDataString, xScale, yScale);
			doReplotWithD3(d3, xySelection, parent, xyDataString, xScale, yScale);
		});

		doReplotWithD3(d3, xySelection, parent, xyDataString, xScale, yScale);

	}

	private void doReplotWithD3(
			D3 d3,
			Selection xySelection,
			GraphicsAtom parent,
			String xyDataString,
			QuantitativeScale<?> xScale,
			QuantitativeScale<?> yScale) {

		replotLinesWithD3(d3, xySelection, xyDataString, xScale, yScale);

		Xy xy = (Xy) parent;
		xy.symbol.replotWithD3(d3, xySelection, xyDataString, xScale, yScale, parent);
	}

	private void replotLinesWithD3(
			D3 d3,
			Selection xySelection,
			String xyDataString,
			QuantitativeScale<?> xScale,
			QuantitativeScale<?> yScale) {
		xySelection //
				.selectAll("#lines") //
				.remove();

		Selection linesSelection = xySelection //
				.append("g") //
				.attr("id", "lines") //
				.attr("class", "lines");

		String modeString = interpolation.get();
		org.treez.javafxd3.d3.svg.InterpolationMode mode = org.treez.javafxd3.d3.svg.InterpolationMode
				.fromValue(modeString);

		org.treez.javafxd3.d3.svg.Line linePathGenerator = d3 //
				.svg()//
				.line()
				.x(new AxisScaleFirstDatumFunction(xScale))
				.y(new AxisScaleSecondDatumFunction(yScale))//
				.interpolate(mode);

		//plot new lines
		Selection lines = linesSelection //
				.append("path") //
				.attr("d", linePathGenerator.generate(xyDataString))
				.attr("fill", "none");

		GraphicsAtom.bindDisplayToBooleanAttribute("hideLine", lines, hide);
		GraphicsAtom.bindStringAttribute(lines, "stroke", color);
		GraphicsAtom.bindStringAttribute(lines, "stroke-width", width);
		GraphicsAtom.bindLineTransparency(lines, transparency);
		GraphicsAtom.bindLineStyle(lines, style);
	}

	//#end region

}
