package org.treez.results.atom.tornado;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
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
import org.treez.javafxd3.d3.functions.data.attribute.AttributeStringDataFunction;
import org.treez.javafxd3.d3.functions.data.axis.AxisScaleInversedSizeDataFunction;
import org.treez.javafxd3.d3.functions.data.axis.AxisScaleInversedValueDataFunction;
import org.treez.javafxd3.d3.functions.data.axis.AxisScaleKeyDataFunction;
import org.treez.javafxd3.d3.functions.data.axis.AxisScaleSizeDataFunction;
import org.treez.javafxd3.d3.functions.data.axis.AxisScaleValueDataFunction;
import org.treez.javafxd3.d3.scales.Scale;
import org.treez.results.atom.graph.Graph;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Fill implements GraphicsPropertiesPageFactory {

	Logger LOG = LogManager.getLogger(Fill.class);

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
	public void createPage(AttributeRoot root, AbstractAtom<?> parent) {

		Page symbolPage = root.createPage("fill", "   Fill   ");

		Section leftSection = symbolPage.createSection("left");

		leftSection.createColorChooser(leftColor, this, "grey").setLabel("Color");

		leftSection.createDoubleVariableField(leftTransparency, this, 0.0);

		leftSection.createCheckBox(leftHide, this).setLabel("Hide");

		Section rightSection = symbolPage.createSection("right");

		rightSection.createColorChooser(rightColor, this, "green").setLabel("Color");

		rightSection.createDoubleVariableField(rightTransparency, this, 0.0);

		rightSection.createCheckBox(rightHide, this).setLabel("Hide");

	}

	@Override
	public Selection plotWithD3(
			D3 d3,
			Selection tornadoSelection,
			Selection rectSelection,
			AbstractGraphicsAtom parent) {

		String parentName = parent.getName();

		String clipPathId = "bar-rects-" + parentName + "-clip-path";

		//remove old groups and clip path if they already exist
		tornadoSelection //
				.select(".bar-rects-left") //
				.remove();

		tornadoSelection //
				.select(".bar-rects-right") //
				.remove();

		tornadoSelection.select(clipPathId).remove();

		//create new groups
		rectsLeftSelection = tornadoSelection //
				.append("g") //
				.attr("id", "bar-rects-left") //
				.attr("class", "bar-rects-left") //
				.attr("clip-path", "url(#" + clipPathId);

		rectsRightSelection = tornadoSelection //
				.append("g") //
				.attr("id", "bar-rects-right") //
				.attr("class", "bar-rects-right") //
				.attr("clip-path", "url(#" + clipPathId);

		//create clipping path that ensures that the bars are only
		//shown within the bounds of the graph
		Graph graph = getGraph(parent);

		double width = Length.toPx(graph.data.width.get());
		double height = Length.toPx(graph.data.width.get());
		tornadoSelection.append("clipPath") //
				.attr("id", clipPathId) //
				.append("rect") //
				.attr("x", 0) //
				.attr("y", 0) //
				.attr("width", width) //
				.attr("height", height);

		//bind attributes
		AbstractGraphicsAtom.bindDisplayToBooleanAttribute("hideLeftRects", rectsLeftSelection, leftHide);
		AbstractGraphicsAtom.bindDisplayToBooleanAttribute("hideRightRects", rectsRightSelection, rightHide);

		Consumer replotRects = () -> {
			rePlotRects(parent, d3);
		};

		//initially plot rects
		replotRects.consume();

		return tornadoSelection;
	}

	private static Graph getGraph(AbstractGraphicsAtom parent) {
		Tornado tornado = (Tornado) parent;
		return tornado.getGraph();
	}

	private void rePlotRects(AbstractGraphicsAtom parent, D3 d3) {
		removeOldRects();
		plotNewRects(parent, d3);

	}

	private void removeOldRects() {
		rectsLeftSelection.selectAll("rect") //
				.remove();
		rectsRightSelection.selectAll("rect") //
				.remove();
	}

	private void plotNewRects(AbstractGraphicsAtom parent, D3 d3) {

		Tornado tornado = (Tornado) parent;
		Graph graph = tornado.getGraph();
		double graphHeight = Length.toPx(graph.data.height.get());
		double graphWidth = Length.toPx(graph.data.width.get());

		org.treez.results.atom.axis.Axis inputAxis = tornado.data.getInputAxis();
		boolean inputAxisIsOrdinal = inputAxis.isOrdinal();
		boolean inputAxisIsHorizontal = inputAxis.isHorizontal();
		Scale<?> inputScale = tornado.data.getInputScale();

		org.treez.results.atom.axis.Axis outputAxis = tornado.data.getOutputAxis();
		boolean outputAxisIsOrdinal = outputAxis.isOrdinal();
		boolean outputAxisIsHorizontal = outputAxis.isHorizontal();
		Scale<?> outputScale = tornado.data.getOutputScale();

		Double barFillRatio = tornado.data.barFillRatio.get();

		String leftDataString = tornado.data.getLeftBarDataString();
		String rightDataString = tornado.data.getRightBarDataString();
		int numberOfBars = tornado.data.getDataSize();

		boolean inputScaleChanged = false;
		if (inputAxisIsOrdinal) {
			List<Object> labelData = tornado.data.getInputLabelData();

			List<String> labels = labelData.stream() //
					.map((labelObj) -> labelObj.toString()) //
					.collect(Collectors.toList());

			int oldNumberOfValues = inputAxis.getNumberOfValues();
			inputAxis.includeOrdinalValuesForAutoScale(labels);
			int numberOfValues = inputAxis.getNumberOfValues();
			inputScaleChanged = numberOfValues != oldNumberOfValues;

		} else {
			//TODO
			//inputAxis.includeDataForAutoScale(inputData);
		}

		List<Double> allOutputData = tornado.data.getAllBarData();
		Double[] oldOutputLimits = outputAxis.getQuantitativeLimits();
		outputAxis.includeDataForAutoScale(allOutputData);
		Double[] outputLimits = outputAxis.getQuantitativeLimits();
		boolean outputScaleChanged = !Arrays.equals(outputLimits, oldOutputLimits);

		if (inputScaleChanged || outputScaleChanged) {
			graph.updatePlotForChangedScales(d3);
		}

		JsEngine engine = rectsLeftSelection.getJsEngine();

		if (outputAxisIsHorizontal) {

			double barHeight = determineBarHeight(graphHeight, inputScale, numberOfBars, barFillRatio,
					inputAxisIsOrdinal);

			rectsLeftSelection.selectAll("rect") //
					.data(leftDataString) //
					.enter() //
					.append("rect")
					.attr("x", new AxisScaleValueDataFunction(engine, outputScale))
					.attr("y", new AxisScaleKeyDataFunction(engine, inputScale))
					.attr("height", barHeight)
					.attr("transform", "translate(0,-" + barHeight / 2 + ")")
					.attr("width", new AxisScaleSizeDataFunction(engine, outputScale));

			rectsLeftSelection.selectAll("text") //
					.data(leftDataString) //
					.enter() //
					.append("text")
					.attr("x", new AxisScaleValueDataFunction(engine, outputScale))
					.attr("y", new AxisScaleKeyDataFunction(engine, inputScale))
					.style("fill", "black")
					.text(new AttributeStringDataFunction(engine, "input"));

			rectsRightSelection.selectAll("rect") //
					.data(rightDataString) //
					.enter() //
					.append("rect")
					.attr("x", new AxisScaleValueDataFunction(engine, outputScale))
					.attr("y", new AxisScaleKeyDataFunction(engine, inputScale))
					.attr("height", barHeight)
					.attr("transform", "translate(0,-" + barHeight / 2 + ")")
					.attr("width", new AxisScaleSizeDataFunction(engine, outputScale));
		} else {

			double barWidth = determineBarWidth(graphWidth, inputScale, numberOfBars, barFillRatio, inputAxisIsOrdinal);

			rectsLeftSelection.selectAll("rect") //
					.data(leftDataString) //
					.enter() //
					.append("rect")
					.attr("x", new AxisScaleKeyDataFunction(engine, inputScale))
					.attr("y", new AxisScaleInversedValueDataFunction(engine, outputScale, graphHeight))
					.attr("width", barWidth)
					.attr("transform", "translate(-" + barWidth / 2 + ",0)")
					.attr("height", new AxisScaleInversedSizeDataFunction(engine, outputScale));

			rectsRightSelection.selectAll("rect") //
					.data(rightDataString) //
					.enter() //
					.append("rect")
					.attr("x", new AxisScaleKeyDataFunction(engine, inputScale))
					.attr("y", new AxisScaleInversedValueDataFunction(engine, outputScale, graphHeight))
					.attr("width", barWidth)
					.attr("transform", "translate(-" + barWidth / 2 + ",0)")
					.attr("height", new AxisScaleInversedSizeDataFunction(engine, outputScale));

		}

		//bind attributes
		AbstractGraphicsAtom.bindStringAttribute(rectsLeftSelection, "fill", leftColor);
		AbstractGraphicsAtom.bindTransparency(rectsLeftSelection, leftTransparency);
		AbstractGraphicsAtom.bindTransparencyToBooleanAttribute(rectsLeftSelection, leftHide, leftTransparency);

		AbstractGraphicsAtom.bindStringAttribute(rectsRightSelection, "fill", rightColor);
		AbstractGraphicsAtom.bindTransparency(rectsRightSelection, rightTransparency);
		AbstractGraphicsAtom.bindTransparencyToBooleanAttribute(rectsRightSelection, rightHide, rightTransparency);

	}

	private static double determineBarWidth(
			double graphWidth,
			Scale<?> xScale,
			int dataSize,
			double barFillRatio,
			boolean axisIsOrdinal) {

		double defaultBarWidth;
		if (dataSize > 1) {
			if (axisIsOrdinal) {
				defaultBarWidth = graphWidth / dataSize;
			} else {
				defaultBarWidth = xScale.apply(1).asDouble();
			}
		} else {
			defaultBarWidth = graphWidth / GRAPHICS_TO_BAR_RATIO_FOR_SINGLE_BAR;
		}
		double barWidth = defaultBarWidth * barFillRatio;
		return barWidth;
	}

	private static double determineBarHeight(
			double graphHeight,
			Scale<?> yScale,
			int dataSize,
			double barFillRatio,
			boolean axisIsOrdinal) {

		double defaultBarHeight;
		if (dataSize > 1) {
			if (axisIsOrdinal) {
				defaultBarHeight = graphHeight / dataSize;
			} else {
				defaultBarHeight = graphHeight - yScale.apply(1).asDouble();
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

		AbstractGraphicsAtom.bindStringAttribute(symbolSelection, "fill", leftColor);
		AbstractGraphicsAtom.bindTransparency(symbolSelection, leftTransparency);
		AbstractGraphicsAtom.bindTransparencyToBooleanAttribute(symbolSelection, leftHide, leftTransparency);

		//refreshable.refresh();

		return symbolSelection;

	}

	//#end region

}
