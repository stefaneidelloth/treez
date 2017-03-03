package org.treez.results.atom.xy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.scales.QuantitativeScale;
import org.treez.results.Activator;
import org.treez.results.atom.axis.Axis;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.graphicsPage.GraphicsPropertiesPage;
import org.treez.results.atom.legend.LegendContributor;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Xy extends GraphicsPropertiesPage implements LegendContributor {

	//#region ATTRIBUTES

	public Data data;

	public Symbol symbol;

	public Line line;

	//public ErrorBar errorBar;

	public Area area;

	//public Label label;

	private Selection xySelection;

	//#end region

	//#region CONSTRUCTORS

	public Xy(String name) {
		super(name);
	}

	//#end region

	//#region METHODS

	@Override
	protected void createPropertyPageFactories() {

		data = new Data();
		propertyPageFactories.add(data);

		area = new Area();
		propertyPageFactories.add(area);

		line = new Line();
		propertyPageFactories.add(line);

		symbol = new Symbol();
		propertyPageFactories.add(symbol);

		//errorBar = new ErrorBar();
		//label = new Label();
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("xy.png");
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
			Selection graphOrXySeriesSelection,
			Selection graphRectSelection,
			FocusChangingRefreshable refreshable) {

		Objects.requireNonNull(d3);
		this.treeViewRefreshable = refreshable;

		//remove old xy group if it already exists
		graphOrXySeriesSelection //
				.select("#" + name) //
				.remove();

		//create new axis group
		xySelection = graphOrXySeriesSelection //
				.insert("g", ".axis") //
				.attr("class", "xy") //
				.onMouseClick(this);
		bindNameToId(xySelection);

		updatePlotWithD3(d3);

		return xySelection;
	}

	@Override
	public void updatePlotWithD3(D3 d3) {
		contributeDataForAutoScale(d3);
		plotPageModels(d3);
	}

	private void plotPageModels(D3 d3) {
		for (GraphicsPropertiesPageFactory pageModel : propertyPageFactories) {
			xySelection = pageModel.plotWithD3(d3, xySelection, null, this);
		}
	}

	private void contributeDataForAutoScale(D3 d3) {
		List<Double> xDataValues = getXDataAsDoubles();
		Axis xAxis = getXAxis();
		Double[] oldXLimits = xAxis.getQuantitativeLimits();
		xAxis.includeDataForAutoScale(xDataValues);
		Double[] xLimits = xAxis.getQuantitativeLimits();
		boolean xScaleChanged = !Arrays.equals(xLimits, oldXLimits);

		List<Double> yDataValues = getYDataAsDoubles();
		Axis yAxis = getYAxis();
		Double[] oldYLimits = yAxis.getQuantitativeLimits();
		yAxis.includeDataForAutoScale(yDataValues);
		Double[] yLimits = yAxis.getQuantitativeLimits();
		boolean yScaleChanged = !Arrays.equals(yLimits, oldYLimits);

		if (xScaleChanged || yScaleChanged) {
			Graph graph = getGraph();
			graph.updatePlotForChangedScales(d3);
		}

	}

	private Graph getGraph() {
		AbstractAtom<?> parent = getParentAtom();
		boolean parentIsGraph = Graph.class.isAssignableFrom(parent.getClass());
		if (parentIsGraph) {
			return (Graph) parent;
		} else {
			AbstractAtom<?> grandParent = parent.getParentAtom();
			return (Graph) grandParent;
		}
	}

	@Override
	public void addLegendContributors(List<LegendContributor> legendContributors) {
		if (providesLegendEntry()) {
			legendContributors.add(this);
		}
	}

	@Override
	public boolean providesLegendEntry() {
		return !getLegendText().isEmpty();
	}

	@Override
	public String getLegendText() {
		return data.legendText.get();
	}

	@Override
	public Selection createLegendSymbolGroup(
			D3 d3,
			Selection parentSelection,
			int symbolLengthInPx,
			Refreshable refreshable) {
		Selection symbolSelection = parentSelection //
				.append("g") //
				.classed("xy-legend-entry-symbol", true);

		this.line.plotLegendLineWithD3(d3, symbolSelection, symbolLengthInPx);
		this.symbol.plotLegendSymbolWithD3(d3, symbolSelection, symbolLengthInPx / 2, refreshable);

		return symbolSelection;
	}

	public String createXyDataString(List<Double> xDataValues, List<Double> yDataValues) {

		int xLength = xDataValues.size();
		int yLength = yDataValues.size();
		boolean lengthsAreOk = xLength == yLength;
		if (!lengthsAreOk) {
			String message = "The x and y data has to be of equal size but size of x data is " + xLength
					+ " and size of y data is " + yLength;
			throw new IllegalStateException(message);
		}

		List<String> rowList = new java.util.ArrayList<>();
		for (int rowIndex = 0; rowIndex < xLength; rowIndex++) {
			Double x = xDataValues.get(rowIndex);
			Double y = yDataValues.get(rowIndex);
			String rowString = "[" + x + "," + y + "]";
			rowList.add(rowString);
		}
		String xyDataString = "[" + String.join(",", rowList) + "]";
		return xyDataString;
	}

	public QuantitativeScale<?> getXScale() {
		Axis xAxisAtom = getXAxis();
		QuantitativeScale<?> scale = (QuantitativeScale<?>) xAxisAtom.getScale();
		return scale;
	}

	public QuantitativeScale<?> getYScale() {
		Axis yAxisAtom = getYAxis();
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

	public List<Double> getXDataAsDoubles() {
		String xDataPath = data.xData.get();
		if (xDataPath.isEmpty()) {
			return new ArrayList<>();
		}
		org.treez.data.column.Column xDataColumn = getChildFromRoot(xDataPath);
		List<Double> xDataValues = xDataColumn.getDoubleValues();
		return xDataValues;
	}

	public List<Double> getYDataAsDoubles() {
		String yDataPath = data.yData.get();
		if (yDataPath.isEmpty()) {
			return new ArrayList<>();
		}
		org.treez.data.column.Column yDataColumn = getChildFromRoot(yDataPath);
		List<Double> yDataValues = yDataColumn.getDoubleValues();
		return yDataValues;
	}

	//#end region

}
