package org.treez.results.atom.tornado;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.atom.graphics.length.Length;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Consumer;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.functions.AxisScaleFirstDatumFunction;
import org.treez.javafxd3.d3.functions.AxisScaleSecondDatumFunction;
import org.treez.javafxd3.d3.functions.AxisScaleThirdDatumAsSizeFunction;
import org.treez.javafxd3.d3.scales.QuantitativeScale;
import org.treez.results.atom.axis.Direction;
import org.treez.results.atom.graph.Graph;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Fill implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	private static final double GRAPHICS_TO_BAR_RATIO_FOR_SINGLE_BAR = 3;

	public final Attribute<String> leftColor = new Wrap<>();

	public final Attribute<Double> leftTransparency = new Wrap<>();

	public final Attribute<Boolean> leftHide = new Wrap<>();

	public final Attribute<String> rightColor = new Wrap<>();

	public final Attribute<Double> rightTransparency = new Wrap<>();

	public final Attribute<Boolean> rightHide = new Wrap<>();

	private Selection rectsLeftSelection;

	private Selection rectsRightSelection;

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page symbolPage = root.createPage("fill", "   Fill   ");

		Section left = symbolPage.createSection("left");

		left.createColorChooser(leftColor, "color", "grey");

		left.createDoubleVariableField(leftTransparency, this, 0.0);

		left.createCheckBox(leftHide, "hide");

		Section right = symbolPage.createSection("right");

		right.createColorChooser(rightColor, "color", "green");

		right.createDoubleVariableField(rightTransparency, this, 0.0);

		right.createCheckBox(rightHide, "hide");

	}

	@Override
	public Selection plotWithD3(D3 d3, Selection barSelection, Selection rectSelection, GraphicsAtom parent) {

		String parentName = parent.getName();

		String clipPathId = "bar-rects-" + parentName + "-clip-path";

		//remove old groups if they already exist
		barSelection //
				.select(".bar-rects-left") //
				.remove();

		barSelection //
				.select(".bar-rects-right") //
				.remove();

		//create new groups
		rectsLeftSelection = barSelection //
				.append("g") //
				.attr("id", "bar-rects-left") //
				.attr("class", "bar-rects-left") //
				.attr("clip-path", "url(#" + clipPathId);

		rectsRightSelection = barSelection //
				.append("g") //
				.attr("id", "bar-rects-right") //
				.attr("class", "bar-rects-right") //
				.attr("clip-path", "url(#" + clipPathId);

		//create clipping path that ensures that the bars are only
		//shown within the bounds of the graph
		Graph graph = getGraph(parent);

		double width = Length.toPx(graph.data.width.get());
		double height = Length.toPx(graph.data.width.get());
		barSelection.append("clipPath") //
				.attr("id", clipPathId) //
				.append("rect") //
				.attr("x", 0) //
				.attr("y", 0) //
				.attr("width", width) //
				.attr("height", height);

		//bind attributes
		GraphicsAtom.bindDisplayToBooleanAttribute("hideLeftRects", rectsLeftSelection, leftHide);
		GraphicsAtom.bindDisplayToBooleanAttribute("hideRightRects", rectsRightSelection, rightHide);

		Consumer replotRects = () -> {
			rePlotRects(parent);
		};

		//initially plot rects
		replotRects.consume();

		return barSelection;
	}

	private static Graph getGraph(GraphicsAtom parent) {
		AbstractAtom grandParent = parent.getParentAtom();
		Graph graph;
		boolean isGraph = Graph.class.isAssignableFrom(grandParent.getClass());
		if (isGraph) {
			graph = (Graph) grandParent;
		} else {
			AbstractAtom greatGrandParent = grandParent.getParentAtom();
			graph = (Graph) greatGrandParent;
		}
		return graph;
	}

	private void rePlotRects(GraphicsAtom parent) {
		removeOldRects();
		plotNewRects(parent);

	}

	private void removeOldRects() {
		rectsLeftSelection.selectAll("rect") //
				.remove();
		rectsRightSelection.selectAll("rect") //
				.remove();
	}

	private void plotNewRects(GraphicsAtom parent) {

		Tornado tornado = (Tornado) parent;
		Graph graph = getGraph(parent);
		double graphHeight = Length.toPx(graph.data.height.get());
		double graphWidth = Length.toPx(graph.data.width.get());

		QuantitativeScale<?> xScale = tornado.getXScale();
		QuantitativeScale<?> yScale = tornado.getYScale();

		String direction = tornado.data.barDirection.get();
		boolean isVertical = direction.equals(Direction.VERTICAL.toString());

		Double barFillRatio = tornado.data.barFillRatio.get();
		if (barFillRatio == null) {
			barFillRatio = 1.0;
		}

		String leftDataString = tornado.getLeftBarDataString();
		String rightDataString = tornado.getRightBarDataString();
		int numberOfBars = tornado.getDataSize();

		if (isVertical) {
			double barWidth = determineBarWidth(graphWidth, xScale, numberOfBars, barFillRatio);

			rectsLeftSelection.selectAll("rect") //
					.data(leftDataString) //
					.enter() //
					.append("rect")
					.attr("x", new AxisScaleFirstDatumFunction(xScale))
					.attr("y", new AxisScaleSecondDatumFunction(yScale))
					.attr("width", barWidth)
					.attr("transform", "translate(-" + barWidth / 2 + ",0)")
					.attr("height", new AxisScaleThirdDatumAsSizeFunction(yScale));

			rectsRightSelection.selectAll("rect") //
					.data(rightDataString) //
					.enter() //
					.append("rect")
					.attr("x", new AxisScaleFirstDatumFunction(xScale))
					.attr("y", new AxisScaleSecondDatumFunction(yScale))
					.attr("width", barWidth)
					.attr("transform", "translate(-" + barWidth / 2 + ",0)")
					.attr("height", new AxisScaleThirdDatumAsSizeFunction(yScale));

		} else {
			double barHeight = determineBarHeight(graphHeight, yScale, numberOfBars, barFillRatio);

			rectsLeftSelection.selectAll("rect") //
					.data(leftDataString) //
					.enter() //
					.append("rect")
					.attr("x", new AxisScaleSecondDatumFunction(xScale))
					.attr("y", new AxisScaleFirstDatumFunction(yScale))
					.attr("height", barHeight)
					.attr("transform", "translate(0,-" + barHeight / 2 + ")")
					.attr("width", new AxisScaleThirdDatumAsSizeFunction(xScale));

			rectsRightSelection.selectAll("rect") //
					.data(rightDataString) //
					.enter() //
					.append("rect")
					.attr("x", new AxisScaleSecondDatumFunction(xScale))
					.attr("y", new AxisScaleFirstDatumFunction(yScale))
					.attr("height", barHeight)
					.attr("transform", "translate(0,-" + barHeight / 2 + ")")
					.attr("width", new AxisScaleThirdDatumAsSizeFunction(xScale));
		}

		//bind attributes
		GraphicsAtom.bindStringAttribute(rectsLeftSelection, "fill", leftColor);
		GraphicsAtom.bindTransparency(rectsLeftSelection, leftTransparency);
		GraphicsAtom.bindTransparencyToBooleanAttribute(rectsLeftSelection, leftHide, leftTransparency);

		GraphicsAtom.bindStringAttribute(rectsRightSelection, "fill", rightColor);
		GraphicsAtom.bindTransparency(rectsRightSelection, rightTransparency);
		GraphicsAtom.bindTransparencyToBooleanAttribute(rectsRightSelection, rightHide, rightTransparency);

	}

	private static double determineBarWidth(
			double graphWidth,
			QuantitativeScale<?> xScale,
			int positionSize,
			double barFillRatio) {

		double defaultBarWidth;
		if (positionSize > 1) {

			defaultBarWidth = xScale.apply(1).asDouble();
		} else {
			defaultBarWidth = graphWidth / GRAPHICS_TO_BAR_RATIO_FOR_SINGLE_BAR;
		}
		double barWidth = defaultBarWidth * barFillRatio;
		return barWidth;
	}

	private static double determineBarHeight(
			double graphHeight,
			QuantitativeScale<?> yScale,
			int dataSize,
			double barFillRatio) {

		double defaultBarHeight;
		if (dataSize > 1) {

			defaultBarHeight = graphHeight - yScale.apply(1).asDouble();
		} else {
			defaultBarHeight = graphHeight / GRAPHICS_TO_BAR_RATIO_FOR_SINGLE_BAR;
		}
		double barHeight = defaultBarHeight * barFillRatio;
		return barHeight;
	}

	public Selection formatLegendSymbol(Selection symbolSelection, int symbolSize) {

		symbolSelection.attr("width", symbolSize);
		symbolSelection.attr("height", "10");

		GraphicsAtom.bindStringAttribute(symbolSelection, "fill", leftColor);
		GraphicsAtom.bindTransparency(symbolSelection, leftTransparency);
		GraphicsAtom.bindTransparencyToBooleanAttribute(symbolSelection, leftHide, leftTransparency);

		//refreshable.refresh();

		return symbolSelection;

	}

	//#end region

}
