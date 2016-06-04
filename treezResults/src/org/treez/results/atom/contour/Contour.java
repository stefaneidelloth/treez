package org.treez.results.atom.contour;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.graphics.length.Length;
import org.treez.core.attribute.Consumer;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.scales.QuantitativeScale;
import org.treez.javafxd3.plotly.Configuration;
import org.treez.javafxd3.plotly.Contours;
import org.treez.javafxd3.plotly.Layout;
import org.treez.javafxd3.plotly.Line;
import org.treez.javafxd3.plotly.Plotly;
import org.treez.javafxd3.plotly.PlotlyType;
import org.treez.javafxd3.plotly.SingleData;
import org.treez.javafxd3.plotly.ZeroMargin;
import org.treez.results.Activator;
import org.treez.results.atom.axis.Axis;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.graphicspage.GraphicsPropertiesPage;

import javafx.scene.web.WebEngine;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Contour extends GraphicsPropertiesPage {

	//#region ATTRIBUTES

	public Data data;

	public Fill fill;

	public Lines lines;

	//public Label label;

	private Selection graphSelection;

	private Graph graph;

	private Plotly plotly;

	private Consumer updateConsumer;

	//#end region

	//#region CONSTRUCTORS

	public Contour(String name) {
		super(name);
	}

	//#end region

	//#region METHODS

	@Override
	protected void createPropertyPageFactories() {

		data = new Data();
		propertyPageFactories.add(data);

		fill = new Fill();
		propertyPageFactories.add(fill);

		lines = new Lines();
		propertyPageFactories.add(lines);

		//label = new Label();

	}

	@Override
	public Image provideImage() {
		return Activator.getImage("contour.png");
	}

	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {
		// no actions available right now
		return actions;
	}

	@Override
	public void execute(FocusChangingRefreshable refreshable) {
		treeViewRefreshable = refreshable;
	}

	@Override
	public Selection plotWithD3(
			D3 d3,
			Selection graphSelection,
			Selection graphRectSelection,
			FocusChangingRefreshable refreshable) {
		Objects.requireNonNull(d3);

		this.treeViewRefreshable = refreshable;
		this.graphSelection = graphSelection;
		graph = (Graph) getParentAtom();

		WebEngine webEngine = d3.getWebEngine();
		plotly = new Plotly(webEngine);

		updateConsumer = () -> updatePlotWithD3(d3);

		updatePlotWithD3(d3);

		return graphSelection;
	}

	@Override
	public void updatePlotWithD3(D3 d3) {

		Selection contourSelection = recreateContourGroup();
		Layout plotlyLayout = createPlotlyLayout();
		SingleData plotlyData = createPlotlyData();
		Configuration plotlyConfiguration = plotly.createConfiguration();
		plotly.newPlot("dummyDiv", plotlyData, plotlyLayout, plotlyConfiguration);
		movePlotlyContourFromDummyDivToContourGroup(contourSelection);
		createClipPath(contourSelection);

		Selection fillSelection = getFillSelection(contourSelection);
		bindAdditionalFillAttributes(fillSelection);

		Selection lineSelection = getLineSelection(contourSelection);
		bindAdditionalLineAttributes(lineSelection);

	}

	private static Selection getFillSelection(Selection contourSelection) {
		return contourSelection.select(".contour") //
				.selectAll(".contourfill, .contourbg") //
				.selectAll("path");
	}

	private void bindAdditionalFillAttributes(Selection fillSelection) {
		bindTransparency(fillSelection, fill.transparency);
		bindDisplayToBooleanAttribute("hide", fillSelection, fill.hide);
	}

	private static Selection getLineSelection(Selection contourSelection) {
		return contourSelection.select(".contour") //
				.selectAll(".contourlevel") //
				.selectAll("path");
	}

	private void bindAdditionalLineAttributes(Selection lineSelection) {
		bindLineStyle(lineSelection, lines.style);
		bindLineTransparency(lineSelection, lines.transparency);
		bindDisplayToBooleanAttribute("hide", lineSelection, lines.hide);
	}

	private Layout createPlotlyLayout() {
		double width = Length.toPx(graph.data.width.get());
		double height = Length.toPx(graph.data.height.get());

		Layout layout = plotly.createLayout();
		layout.setWidth(width);
		layout.setHeight(height);
		createPlotlyAxis(layout);

		ZeroMargin margin = plotly.createZeroMargin();
		layout.setMargin(margin);

		graph.data.width.addModificationConsumer("width", updateConsumer);
		graph.data.height.addModificationConsumer("height", updateConsumer);

		return layout;
	}

	private void createPlotlyAxis(Layout layout) {
		org.treez.results.atom.axis.Axis xAxisAtom = getXAxis();
		double xMin = Double.parseDouble(xAxisAtom.data.min.get()); //TODO: consider 'auto' for auto scale
		double xMax = Double.parseDouble(xAxisAtom.data.max.get());
		org.treez.results.atom.axis.Axis yAxisAtom = getYAxis();
		double yMin = Double.parseDouble(yAxisAtom.data.min.get());
		double yMax = Double.parseDouble(yAxisAtom.data.max.get());

		org.treez.javafxd3.plotly.Axis xAxisPlotly = plotly.createAxis();
		xAxisPlotly.setRange(xMin, xMax);
		xAxisPlotly.setShowTickLabels(false);
		xAxisPlotly.setTicks("");
		layout.setXAxis(xAxisPlotly);

		org.treez.javafxd3.plotly.Axis yAxisPlotly = plotly.createAxis();
		yAxisPlotly.setRange(yMin, yMax);
		yAxisPlotly.setShowTickLabels(false);
		yAxisPlotly.setTicks("");
		layout.setYAxis(yAxisPlotly);

		xAxisAtom.data.min.addModificationConsumer("min", updateConsumer);
		xAxisAtom.data.max.addModificationConsumer("max", updateConsumer);

		yAxisAtom.data.min.addModificationConsumer("min", updateConsumer);
		yAxisAtom.data.max.addModificationConsumer("max", updateConsumer);

	}

	private SingleData createPlotlyData() {

		SingleData singleData = plotly.createSingleData();
		singleData.setType(PlotlyType.CONTOUR);
		singleData.setShowScale(false);
		singleData.setVisible(true);
		singleData.setOpacity(1);

		List<Double> xData = getXData();
		singleData.setX(xData);

		List<Double> yData = getYData();
		singleData.setY(yData);

		List<Double> zData = getZData();
		singleData.setZ(zData);

		boolean autoZ = data.automaticZLimits.get();
		singleData.setZAuto(autoZ);
		if (!autoZ) {
			singleData.setZMin(data.zMin.get());
			singleData.setZMax(data.zMax.get());
		}

		boolean autoContour = data.automaticContours.get();
		singleData.setAutoContour(autoContour);
		if (autoContour) {
			singleData.setNContours(data.numberOfContours.get());
		} else {
			Contours contours = createPlotlyContours();
			singleData.setContours(contours);
		}

		singleData.setConnectGaps(data.connectGaps.get());

		singleData.setColorScale(fill.colorScale.get());
		singleData.setReverseScale(fill.reverseScale.get());

		Line line = createPlotlyLine();

		singleData.setLine(line);

		data.xData.addModificationConsumer("xData", updateConsumer);
		data.yData.addModificationConsumer("yData", updateConsumer);
		data.zData.addModificationConsumer("zData", updateConsumer);

		data.automaticZLimits.addModificationConsumer("autoZLimits", updateConsumer);
		data.zMin.addModificationConsumer("zMin", updateConsumer);
		data.zMax.addModificationConsumer("zMax", updateConsumer);

		data.automaticContours.addModificationConsumer("autoNumberOfContours", updateConsumer);
		data.numberOfContours.addModificationConsumer("numberOfContours", updateConsumer);
		data.connectGaps.addModificationConsumer("connectGaps", updateConsumer);
		fill.colorScale.addModificationConsumer("colorScale", updateConsumer);
		fill.reverseScale.addModificationConsumer("reverseScale", updateConsumer);

		return singleData;
	}

	private Contours createPlotlyContours() {
		Contours contours = plotly.createContourOptions();
		contours.setShowLines(true);
		contours.setStart(data.startLevel.get());
		contours.setEnd(data.endLevel.get());
		contours.setSize(data.levelSize.get());
		contours.setColoring(data.coloring.get());

		data.startLevel.addModificationConsumer("startLevel", updateConsumer);
		data.endLevel.addModificationConsumer("endLevel", updateConsumer);
		data.levelSize.addModificationConsumer("levelSize", updateConsumer);
		data.coloring.addModificationConsumer("coloring", updateConsumer);

		return contours;
	}

	private Line createPlotlyLine() {
		Line line = plotly.createLine();
		line.setSmoothing(lines.smoothing.get());
		line.setWidth(lines.width.get());
		line.setColor(lines.color.get());

		lines.smoothing.addModificationConsumer("smoothing", updateConsumer);
		lines.width.addModificationConsumer("width", updateConsumer);
		lines.color.addModificationConsumer("color", updateConsumer);

		return line;
	}

	private void movePlotlyContourFromDummyDivToContourGroup(Selection contourSelection) {
		String contourId = contourSelection.attr("id");
		String copyCommand = "$('.main-svg').find('.contour').appendTo($('#root').find('#" + contourId + "'));";
		plotly.eval(copyCommand);

		String clearCommand = "$('#dummyDiv').empty().removeAttr('class')";
		plotly.eval(clearCommand);

		String deleteExtraSvgCommand = "$('#js-plotly-tester').remove()";
		plotly.eval(deleteExtraSvgCommand);

	}

	private void createClipPath(Selection contourSelection) {

		double width = Length.toPx(graph.data.width.get());
		double height = Length.toPx(graph.data.height.get());

		String contourId = contourSelection.attr("id");
		String clipId = "clippath-" + contourId;
		contourSelection //
				.append("clipPath")
				.attr("id", clipId)
				.append("rect") //
				.attr("width", width)
				.attr("height", height);

		contourSelection //
				.select(".contour") //
				.attr("clip-path", "url(#" + clipId + ")");
	}

	private Selection recreateContourGroup() {
		graphSelection //
				.select("#" + name) //
				.remove();

		Selection contourSelection = graphSelection //
				.insert("g", ".axis") //
				.attr("class", "contour-group") //
				.onMouseClick(this);
		bindNameToId(contourSelection);
		return contourSelection;
	}

	private List<Double> getXData() {
		String xDataPath = data.xData.get();
		if (xDataPath.isEmpty()) {
			return new ArrayList<>();
		}
		org.treez.data.column.Column xDataColumn = getChildFromRoot(xDataPath);
		List<Double> xDataValues = xDataColumn.getDoubleValues();
		return xDataValues;
	}

	private List<Double> getYData() {
		String yDataPath = data.yData.get();
		if (yDataPath.isEmpty()) {
			return new ArrayList<>();
		}
		org.treez.data.column.Column yDataColumn = getChildFromRoot(yDataPath);
		List<Double> yDataValues = yDataColumn.getDoubleValues();
		return yDataValues;
	}

	private List<Double> getZData() {
		String zDataPath = data.zData.get();
		if (zDataPath.isEmpty()) {
			return new ArrayList<>();
		}
		org.treez.data.column.Column zDataColumn = getChildFromRoot(zDataPath);
		List<Double> zDataValues = zDataColumn.getDoubleValues();
		return zDataValues;
	}

	public QuantitativeScale<?> getXScale() {
		Axis xAxisAtom = getXAxis();
		if (xAxisAtom == null) {
			return null;
		}
		QuantitativeScale<?> scale = (QuantitativeScale<?>) xAxisAtom.getScale();
		return scale;
	}

	public QuantitativeScale<?> getYScale() {
		Axis yAxisAtom = getYAxis();
		if (yAxisAtom == null) {
			return null;
		}
		QuantitativeScale<?> scale = (QuantitativeScale<?>) yAxisAtom.getScale();
		return scale;
	}

	public Axis getXAxis() {
		String xAxisPath = data.xAxis.get();
		if (xAxisPath == null || xAxisPath.isEmpty()) {
			return null;
		}
		Axis xAxisAtom = getChildFromRoot(xAxisPath);
		return xAxisAtom;
	}

	public Axis getYAxis() {
		String yAxisPath = data.yAxis.get();
		if (yAxisPath == null || yAxisPath.isEmpty()) {
			return null;
		}
		Axis yAxisAtom = getChildFromRoot(yAxisPath);
		return yAxisAtom;
	}

	//#end region

}
