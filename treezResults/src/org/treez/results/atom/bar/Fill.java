package org.treez.results.atom.bar;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.AbstractGraphicsAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.atom.graphics.length.Length;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Consumer;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.JsEngine;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.functions.data.axis.AxisScaleFirstDataFunction;
import org.treez.javafxd3.d3.functions.data.axis.AxisScaleInversedFirstDataFunction;
import org.treez.javafxd3.d3.functions.data.axis.AxisScaleSecondDataFunction;
import org.treez.javafxd3.d3.scales.QuantitativeScale;
import org.treez.results.atom.axis.Direction;
import org.treez.results.atom.graph.Graph;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Fill implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	private static final double GRAPHICS_TO_BAR_RATIO_FOR_SINGLE_BAR = 3;

	public final Attribute<String> color = new Wrap<>();

	//public final Attribute<String> fillStyle = new Wrap<>();

	public final Attribute<Double> transparency = new Wrap<>();

	public final Attribute<Boolean> hide = new Wrap<>();

	private Selection rectsSelection;

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom<?> parent) {

		Page symbolPage = root.createPage("fill", "   Fill   ");

		//#region marker section

		Section fill = symbolPage.createSection("fill");

		fill.createColorChooser(color, this, "black").setLabel("Color");

		//fill.createFillStyle(fillStyle, "style", "Style");

		fill.createDoubleVariableField(transparency, this, 0.0).setLabel("Transparency");

		fill.createCheckBox(hide, this).setLabel("Hide");

		//#end region
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection barSelection, Selection rectSelection, AbstractGraphicsAtom parent) {

		String parentName = parent.getName();

		String clipPathId = "bar-rects-" + parentName + "-clip-path";

		//remove old group if it already exists
		barSelection //
				.select(".bar-rects") //
				.remove();

		//create new group
		rectsSelection = barSelection //
				.append("g") //
				.attr("id", "bar-rects") //
				.attr("class", "bar-rects") //
				.attr("clip-path", "url(#" + clipPathId);

		//create clipping path that ensures that the bars are only
		//shown within the bounds of the graph
		Graph graph = getGraph(parent);

		double width = Length.toPx(graph.data.width.get());
		double height = Length.toPx(graph.data.width.get());
		rectsSelection.append("clipPath") //
				.attr("id", clipPathId) //
				.append("rect") //
				.attr("x", 0) //
				.attr("y", 0) //
				.attr("width", width) //
				.attr("height", height);

		//bind attributes
		AbstractGraphicsAtom.bindDisplayToBooleanAttribute("hideRects", rectsSelection, hide);

		Consumer replotRects = () -> {
			rePlotRects(parent);
		};

		//initially plot rects
		replotRects.consume();

		return barSelection;
	}

	private static Graph getGraph(AbstractGraphicsAtom parent) {
		AbstractAtom<?> grandParent = parent.getParentAtom();
		Graph graph;
		boolean isGraph = Graph.class.isAssignableFrom(grandParent.getClass());
		if (isGraph) {
			graph = (Graph) grandParent;
		} else {
			AbstractAtom<?> greatGrandParent = grandParent.getParentAtom();
			graph = (Graph) greatGrandParent;
		}
		return graph;
	}

	private void rePlotRects(AbstractGraphicsAtom parent) {
		removeOldRects();
		plotNewRects(parent);

	}

	private void removeOldRects() {
		rectsSelection.selectAll("rect") //
				.remove();
	}

	private void plotNewRects(AbstractGraphicsAtom parent) {

		Bar bar = (Bar) parent;
		Graph graph = getGraph(parent);
		double graphHeight = Length.toPx(graph.data.height.get());
		double graphWidth = Length.toPx(graph.data.width.get());

		String dataString = bar.getBarDataString();
		QuantitativeScale<?> xScale = bar.getXScale();
		QuantitativeScale<?> yScale = bar.getYScale();

		String direction = bar.data.barDirection.get();
		boolean isVertical = direction.equals(Direction.VERTICAL.toString());

		int positionSize = bar.getPositionSize();

		Double barFillRatio = bar.data.barFillRatio.get();
		if (barFillRatio == null) {
			barFillRatio = 1.0;
		}

		JsEngine engine = rectsSelection.getJsEngine();

		if (isVertical) {
			double barWidth = determineBarWidth(bar, graphWidth, xScale, positionSize, barFillRatio);

			rectsSelection.selectAll("rect") //
					.data(dataString) //
					.enter() //
					.append("rect")
					.attr("x", new AxisScaleFirstDataFunction(engine, xScale))
					.attr("y", new AxisScaleSecondDataFunction(engine, yScale))
					.attr("width", barWidth)
					.attr("transform", "translate(-" + barWidth / 2 + ",0)")
					.attr("height", new AxisScaleInversedFirstDataFunction(engine, yScale, graphHeight));
		} else {
			double barHeight = determineBarHeight(bar, graphHeight, yScale, positionSize, barFillRatio);

			rectsSelection.selectAll("rect") //
					.data(dataString) //
					.enter() //
					.append("rect")
					.attr("x", 0)
					.attr("y", new AxisScaleFirstDataFunction(engine, yScale))
					.attr("height", barHeight)
					.attr("transform", "translate(0,-" + barHeight / 2 + ")")
					.attr("width", new AxisScaleSecondDataFunction(engine, xScale));
		}

		//bind attributes
		AbstractGraphicsAtom.bindStringAttribute(rectsSelection, "fill", color);
		AbstractGraphicsAtom.bindTransparency(rectsSelection, transparency);
		AbstractGraphicsAtom.bindTransparencyToBooleanAttribute(rectsSelection, hide, transparency);

	}

	private static double determineBarWidth(
			Bar bar,
			double graphWidth,
			QuantitativeScale<?> xScale,
			int positionSize,
			double barFillRatio) {

		double defaultBarWidth;
		if (positionSize > 1) {
			double smallestPositionDistance = bar.getSmallestPositionDistance();
			defaultBarWidth = xScale.apply(smallestPositionDistance).asDouble();
		} else {
			defaultBarWidth = graphWidth / GRAPHICS_TO_BAR_RATIO_FOR_SINGLE_BAR;
		}
		double barWidth = defaultBarWidth * barFillRatio;
		return barWidth;
	}

	private static double determineBarHeight(
			Bar bar,
			double graphHeight,
			QuantitativeScale<?> yScale,
			int positionSize,
			double barFillRatio) {

		double defaultBarHeight;
		if (positionSize > 1) {
			double smallestPositionDistance = bar.getSmallestPositionDistance();
			defaultBarHeight = graphHeight - yScale.apply(smallestPositionDistance).asDouble();
		} else {
			defaultBarHeight = graphHeight / GRAPHICS_TO_BAR_RATIO_FOR_SINGLE_BAR;
		}
		double barHeight = defaultBarHeight * barFillRatio;
		return barHeight;
	}

	public Selection formatLegendSymbol(Selection symbolSelection, int symbolSize) {

		symbolSelection.attr("width", symbolSize);
		symbolSelection.attr("height", "10");

		AbstractGraphicsAtom.bindStringAttribute(symbolSelection, "fill", color);
		AbstractGraphicsAtom.bindTransparency(symbolSelection, transparency);
		AbstractGraphicsAtom.bindTransparencyToBooleanAttribute(symbolSelection, hide, transparency);

		//refreshable.refresh();

		return symbolSelection;

	}

	//#end region

}
