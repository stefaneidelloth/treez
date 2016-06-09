package org.treez.results.atom.tornado;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.scales.QuantitativeScale;
import org.treez.results.Activator;
import org.treez.results.atom.axis.Axis;
import org.treez.results.atom.graphicspage.GraphicsPropertiesPage;
import org.treez.results.atom.legend.LegendContributor;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Tornado extends GraphicsPropertiesPage implements LegendContributor {

	//#region ATTRIBUTES

	public Data data;

	public Fill fill;

	public Line line;

	private Selection barSelection;

	//#end region

	//#region CONSTRUCTORS

	public Tornado(String name) {
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

		line = new Line();
		propertyPageFactories.add(line);

	}

	@Override
	public Image provideImage() {
		return Activator.getImage("tornado.png");
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
			Selection graphOrBarSeriesSelection,
			Selection graphRectSelection,
			FocusChangingRefreshable refreshable) {
		Objects.requireNonNull(d3);
		this.treeViewRefreshable = refreshable;

		//remove old bar group if it already exists
		graphOrBarSeriesSelection //
				.select("#" + name) //
				.remove();

		//create new axis group
		barSelection = graphOrBarSeriesSelection //
				.insert("g", ".axis") //
				.attr("class", "bar") //
				.onMouseClick(this);
		bindNameToId(barSelection);

		updatePlotWithD3(d3);

		return barSelection;
	}

	@Override
	public void updatePlotWithD3(D3 d3) {
		plotPageModels(d3);
	}

	private void plotPageModels(D3 d3) {
		for (GraphicsPropertiesPageFactory pageModel : propertyPageFactories) {
			barSelection = pageModel.plotWithD3(d3, barSelection, null, this);
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
				.append("rect") //
				.classed("bar-legend-entry-symbol", true);

		this.fill.formatLegendSymbol(symbolSelection, symbolLengthInPx);
		this.line.formatLegendSymbolLine(symbolSelection, refreshable);

		return symbolSelection;
	}

	public String getLeftBarDataString() {
		List<Object> rangeBaseData = getRangeBaseData();
		List<Object> rangeLeftData = getRangeLeftData();
		int dataSize = rangeBaseData.size();

		List<String> rowList = new java.util.ArrayList<>();
		for (int rowIndex = 0; rowIndex < dataSize; rowIndex++) {
			String rangeBaseString = rangeBaseData.get(rowIndex).toString();
			String rangeLeftString = rangeLeftData.get(rowIndex).toString();
			Double rangeBase = Double.parseDouble(rangeBaseString);
			Double rangeLeft = Double.parseDouble(rangeLeftString);
			Double difference = rangeBase - rangeLeft;

			Double position = rangeLeft;
			Double size = difference;
			if (difference < 0) {
				position = rangeBase;
				size = -difference;
			}

			String rowString = "[" + (rowIndex + 1) + "," + position + "," + size + "]";
			rowList.add(rowString);
		}
		String dataString = "[" + String.join(",", rowList) + "]";
		return dataString;
	}

	public String getRightBarDataString() {
		List<Object> rangeBaseData = getRangeBaseData();
		List<Object> rangeRightData = getRangeRightData();
		int dataSize = rangeBaseData.size();

		List<String> rowList = new java.util.ArrayList<>();
		for (int rowIndex = 0; rowIndex < dataSize; rowIndex++) {
			String rangeBaseString = rangeBaseData.get(rowIndex).toString();
			String rangeRightString = rangeRightData.get(rowIndex).toString();
			Double rangeBase = Double.parseDouble(rangeBaseString);
			Double rangeRight = Double.parseDouble(rangeRightString);
			Double difference = rangeRight - rangeBase;
			Double position = rangeBase;
			Double size = difference;
			if (difference < 0) {
				position = rangeRight;
				size = -difference;
			}

			String rowString = "[" + (rowIndex + 1) + "," + position + "," + size + "]";
			rowList.add(rowString);
		}
		String dataString = "[" + String.join(",", rowList) + "]";
		return dataString;
	}

	public int getDataSize() {
		List<Object> domainBaseData = getDomainBaseData();
		return domainBaseData.size();
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

	private Axis getXAxis() {
		String xAxisPath = data.domainAxis.get();
		if (xAxisPath == null || xAxisPath.isEmpty()) {
			return null;
		}
		Axis xAxisAtom = getChildFromRoot(xAxisPath);
		return xAxisAtom;
	}

	private Axis getYAxis() {
		String yAxisPath = data.rangeAxis.get();
		if (yAxisPath == null || yAxisPath.isEmpty()) {
			return null;
		}
		Axis yAxisAtom = getChildFromRoot(yAxisPath);
		return yAxisAtom;
	}

	private List<Object> getRangeBaseData() {
		String dataPath = data.rangeBase.get();
		return getValuesWithColumnPath(dataPath);
	}

	private List<Object> getRangeLeftData() {
		String dataPath = data.rangeLeft.get();
		return getValuesWithColumnPath(dataPath);
	}

	private List<Object> getRangeRightData() {
		String dataPath = data.rangeRight.get();
		return getValuesWithColumnPath(dataPath);
	}

	private List<Object> getDomainBaseData() {
		String dataPath = data.domainBase.get();
		return getValuesWithColumnPath(dataPath);
	}

	private List<Object> getDomainLeftData() {
		String dataPath = data.domainLeft.get();
		return getValuesWithColumnPath(dataPath);
	}

	private List<Object> getDomainRightData() {
		String dataPath = data.domainRight.get();
		return getValuesWithColumnPath(dataPath);
	}

	private List<Object> getValuesWithColumnPath(String dataPath) {
		if (dataPath.isEmpty()) {
			return new ArrayList<>();
		}
		org.treez.data.column.Column dataColumn = getChildFromRoot(dataPath);
		List<Object> dataValues = dataColumn.getValues();
		return dataValues;
	}

	//#end region

}
