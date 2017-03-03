package org.treez.results.atom.bar;

import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.attribute.attributeContainer.Page;
import org.treez.core.atom.attribute.attributeContainer.section.Section;
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
import org.treez.javafxd3.d3.functions.data.axis.AxisScaleInversedSecondDataFunction;
import org.treez.javafxd3.d3.functions.data.axis.AxisScaleSecondDataFunction;
import org.treez.javafxd3.d3.scales.Scale;
import org.treez.results.atom.axis.Axis;
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

		Axis horizontalAxis = bar.getHorizontalAxis();
		boolean horizontalAxisIsOrdinal = horizontalAxis.isOrdinal();
		Scale<?> horizontalScale = bar.getHorizontalScale();

		Axis verticalAxis = bar.getVerticalAxis();
		boolean verticalAxisIsOrdinal = verticalAxis.isOrdinal();
		Scale<?> verticalScale = bar.getVerticalScale();

		Direction direction = bar.data.barDirection.get();
		boolean isVertical = direction.isVertical();

		Double barFillRatio = bar.data.barFillRatio.get();
		if (barFillRatio == null) {
			barFillRatio = 1.0;
		}

		JsEngine engine = rectsSelection.getJsEngine();

		int numberOfPositionValues = bar.getNumberOfPositionValues();

		if (isVertical) {

			double barWidth = determineBarWidth(bar, graphWidth, horizontalAxis, numberOfPositionValues, barFillRatio);

			String dataString = bar.getBarDataString(horizontalAxisIsOrdinal, verticalAxisIsOrdinal);

			rectsSelection.selectAll("rect") //
					.data(dataString) //
					.enter() //
					.append("rect")
					.attr("x", new AxisScaleFirstDataFunction(engine, horizontalScale))
					.attr("y", new AxisScaleSecondDataFunction(engine, verticalScale))
					.attr("width", barWidth)
					.attr("transform", "translate(-" + barWidth / 2 + ",0)")
					.attr("height", new AxisScaleInversedSecondDataFunction(engine, verticalScale, graphHeight));
		} else {
			double barHeight = determineBarHeight(bar, graphHeight, verticalAxis, numberOfPositionValues, barFillRatio);

			String dataString = bar.getBarDataString(verticalAxisIsOrdinal, horizontalAxisIsOrdinal);

			rectsSelection.selectAll("rect") //
					.data(dataString) //
					.enter() //
					.append("rect")
					.attr("x", 0)
					.attr("y", new AxisScaleFirstDataFunction(engine, verticalScale))
					.attr("height", barHeight)
					.attr("transform", "translate(0,-" + barHeight / 2 + ")")
					.attr("width", new AxisScaleSecondDataFunction(engine, horizontalScale));
		}

		//bind attributes
		AbstractGraphicsAtom.bindStringAttribute(rectsSelection, "fill", color);
		AbstractGraphicsAtom.bindTransparency(rectsSelection, transparency);
		AbstractGraphicsAtom.bindTransparencyToBooleanAttribute(rectsSelection, hide, transparency);

	}

	private static double determineBarWidth(
			Bar bar,
			double graphWidth,
			Axis horizontalAxis,
			int numberOfPositionValues,
			double barFillRatio) {

		double defaultBarWidth;
		if (numberOfPositionValues > 1) {

			boolean horizontalAxisIsOrdinal = horizontalAxis.isOrdinal();
			if (horizontalAxisIsOrdinal) {
				defaultBarWidth = graphWidth / numberOfPositionValues;
			} else {
				double smallestPositionDistance = bar.getSmallestPositionDistance();
				Scale<?> horizontalScale = horizontalAxis.getScale();
				Double[] limits = horizontalAxis.getQuantitativeLimits();
				Double minValue = limits[0];
				Double offset = horizontalScale.apply(minValue).asDouble();
				Double scaledWidth = horizontalScale.apply(minValue + smallestPositionDistance).asDouble();
				defaultBarWidth = scaledWidth - offset;
			}

		} else {
			defaultBarWidth = graphWidth / GRAPHICS_TO_BAR_RATIO_FOR_SINGLE_BAR;
		}
		double barWidth = defaultBarWidth * barFillRatio;
		return barWidth;
	}

	private static double determineBarHeight(
			Bar bar,
			double graphHeight,
			Axis verticalAxis,
			int numberOfPositionValues,
			double barFillRatio) {

		double defaultBarHeight;
		if (numberOfPositionValues > 1) {

			boolean verticalAxisIsOrdinal = verticalAxis.isOrdinal();
			if (verticalAxisIsOrdinal) {
				defaultBarHeight = graphHeight / numberOfPositionValues;
			} else {
				double smallestPositionDistance = bar.getSmallestPositionDistance();
				Scale<?> verticalScale = verticalAxis.getScale();
				Double minValue = verticalAxis.getQuantitativeLimits()[0];
				Double offset = graphHeight - verticalScale.apply(minValue).asDouble();
				Double scaledHeight = graphHeight - verticalScale.apply(minValue + smallestPositionDistance).asDouble();
				defaultBarHeight = scaledHeight - offset;
			}

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
