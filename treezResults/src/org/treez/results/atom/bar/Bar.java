package org.treez.results.atom.bar;

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
import org.treez.javafxd3.d3.scales.Scale;
import org.treez.results.Activator;
import org.treez.results.atom.axis.Axis;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.graphicsPage.GraphicsPropertiesPage;
import org.treez.results.atom.legend.LegendContributor;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Bar extends GraphicsPropertiesPage implements LegendContributor {

	//#region ATTRIBUTES

	public Data data;

	public Fill fill;

	public Line line;

	//public Label label;
	//public ErrorBar errorBar;

	private Selection barSelection;

	//#end region

	//#region CONSTRUCTORS

	public Bar(String name) {
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

		//label = new Label();
		//errorBar = new ErrorBar();

	}

	@Override
	public Image provideImage() {
		return Activator.getImage("bar.png");
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
				.onClick(this);
		bindNameToId(barSelection);

		updatePlotWithD3(d3);

		return barSelection;
	}

	@Override
	public void updatePlotWithD3(D3 d3) {
		contributeDataForAutoScale(d3);
		plotPageModels(d3);
	}

	private void contributeDataForAutoScale(D3 d3) {

		boolean horizontalScaleChanged = false;
		boolean verticalScaleChanged = false;

		boolean isVerticalBar = data.barDirection.get().isVertical();
		if (isVerticalBar) {
			horizontalScaleChanged = contributePositionValuesToHorizontalAxis();
			verticalScaleChanged = contributeLengthValuesToVerticalAxis();
		} else {
			horizontalScaleChanged = contributeLengthValuesToHorizontalAxis();
			verticalScaleChanged = contributePositionValuesToVerticalAxis();
		}

		if (horizontalScaleChanged || verticalScaleChanged) {
			Graph graph = getGraph();
			graph.updatePlotForChangedScales(d3);
		}

	}

	private boolean contributePositionValuesToHorizontalAxis() {

		Axis horizontalAxis = getHorizontalAxis();
		boolean horizontalAxisIsQuantitative = horizontalAxis.isQuantitative();
		if (horizontalAxisIsQuantitative) {

			List<Double> positionValues = getQuantitativePositions();

			Double[] oldHorizontalLimits = horizontalAxis.getQuantitativeLimits();
			horizontalAxis.includeDataForAutoScale(positionValues);
			Double[] horizontalLimits = horizontalAxis.getQuantitativeLimits();

			boolean horizontalScaleChanged = !Arrays.equals(horizontalLimits, oldHorizontalLimits);
			return horizontalScaleChanged;

		} else {

			List<String> positionValues = getOrdinalPositions();

			int oldNumberOfValues = horizontalAxis.getNumberOfValues();
			horizontalAxis.includeOrdinalValuesForAutoScale(positionValues);
			int numberOfValues = horizontalAxis.getNumberOfValues();

			boolean horizontalScaleChanged = numberOfValues != oldNumberOfValues;
			return horizontalScaleChanged;

		}

	}

	private boolean contributeLengthValuesToVerticalAxis() {

		Axis verticalAxis = getVerticalAxis();
		boolean verticalAxisIsQuantitative = verticalAxis.isQuantitative();
		if (verticalAxisIsQuantitative) {

			List<Double> lengthValues = getQuantitativeLengths();

			Double[] oldVerticalLimits = verticalAxis.getQuantitativeLimits();
			verticalAxis.includeDataForAutoScale(lengthValues);
			Double[] verticalLimits = verticalAxis.getQuantitativeLimits();

			boolean verticalScaleChanged = !Arrays.equals(verticalLimits, oldVerticalLimits);
			return verticalScaleChanged;

		} else {

			List<String> lengthValues = getOrdinalLengths();

			int oldNumberOfValues = verticalAxis.getNumberOfValues();
			verticalAxis.includeOrdinalValuesForAutoScale(lengthValues);
			int numberOfValues = verticalAxis.getNumberOfValues();

			boolean verticalScaleChanged = numberOfValues != oldNumberOfValues;
			return verticalScaleChanged;

		}

	}

	private boolean contributePositionValuesToVerticalAxis() {

		Axis verticalAxis = getVerticalAxis();
		boolean verticalAxisIsQuantitative = verticalAxis.isQuantitative();
		if (verticalAxisIsQuantitative) {

			List<Double> positionValues = getQuantitativePositions();

			Double[] oldVerticalLimits = verticalAxis.getQuantitativeLimits();
			verticalAxis.includeDataForAutoScale(positionValues);
			Double[] verticalLimits = verticalAxis.getQuantitativeLimits();

			boolean verticalScaleChanged = !Arrays.equals(verticalLimits, oldVerticalLimits);
			return verticalScaleChanged;

		} else {

			List<String> positionValues = getOrdinalPositions();

			int oldNumberOfValues = verticalAxis.getNumberOfValues();
			verticalAxis.includeOrdinalValuesForAutoScale(positionValues);
			int numberOfValues = verticalAxis.getNumberOfValues();

			boolean verticalScaleChanged = numberOfValues != oldNumberOfValues;
			return verticalScaleChanged;

		}

	}

	private boolean contributeLengthValuesToHorizontalAxis() {

		Axis horizontalAxis = getHorizontalAxis();
		boolean horizontalAxisIsQuantitative = horizontalAxis.isQuantitative();
		if (horizontalAxisIsQuantitative) {

			List<Double> lengthValues = getQuantitativeLengths();

			Double[] oldHorizontalLimits = horizontalAxis.getQuantitativeLimits();
			horizontalAxis.includeDataForAutoScale(lengthValues);
			Double[] horizontalLimits = horizontalAxis.getQuantitativeLimits();

			boolean horizontalScaleChanged = !Arrays.equals(horizontalLimits, oldHorizontalLimits);
			return horizontalScaleChanged;

		} else {

			List<String> lengthValues = getOrdinalLengths();

			int oldNumberOfValues = horizontalAxis.getNumberOfValues();
			horizontalAxis.includeOrdinalValuesForAutoScale(lengthValues);
			int numberOfValues = horizontalAxis.getNumberOfValues();

			boolean horizontalScaleChanged = numberOfValues != oldNumberOfValues;
			return horizontalScaleChanged;
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
	public
			Selection
			createLegendSymbolGroup(D3 d3, Selection parentSelection, int symbolLengthInPx, Refreshable refreshable) {
		Selection symbolSelection = parentSelection //
				.append("rect") //
				.classed("bar-legend-entry-symbol", true);

		this.fill.formatLegendSymbol(symbolSelection, symbolLengthInPx);
		this.line.formatLegendSymbolLine(symbolSelection, refreshable);

		return symbolSelection;
	}

	public String getBarDataString(boolean positionAxisIsOrdinal, boolean lengthAxisIsOrdinal) {

		List<Object> lengthDataValues = getLengthData();
		List<Object> positionDataValues = getPositionData();

		int lengthSize = lengthDataValues.size();
		int positionSize = positionDataValues.size();
		assertEqualSizes(lengthSize, positionSize);

		if (lengthSize == 0) {
			return "[]";
		}

		Object firstPosition = positionDataValues.get(0);
		boolean positionsAreOrdinal = firstPosition instanceof String;

		Object firstLength = lengthDataValues.get(0);
		boolean lengthsAreOrdinal = firstLength instanceof String;

		List<String> rowList = new java.util.ArrayList<>();
		for (int rowIndex = 0; rowIndex < lengthSize; rowIndex++) {

			Object positionDatum = positionDataValues.get(rowIndex);
			String position = positionDatum.toString();
			if (positionsAreOrdinal) {
				if (positionAxisIsOrdinal) {
					position = "'" + position + "'";
				} else {
					position = "" + (positionDataValues.indexOf(positionDatum) + 1);
				}

			}

			Object lengthDatum = lengthDataValues.get(rowIndex);
			String length = lengthDatum.toString();
			if (lengthsAreOrdinal) {
				if (lengthAxisIsOrdinal) {
					length = "'" + length + "'";
				} else {
					length = "" + (lengthDataValues.indexOf(lengthDatum) + 1);
				}

			}

			String rowString = "[" + position + "," + length + "]";
			rowList.add(rowString);
		}
		String dataString = "[" + String.join(",", rowList) + "]";
		return dataString;
	}

	private void assertEqualSizes(int lengthSize, int positionSize) {
		boolean sizesAreOk = lengthSize == positionSize;
		if (!sizesAreOk) {
			String message = "The length and position data has to be of equal size but size of length data is "
					+ lengthSize + " and size of position data is " + positionSize;
			throw new IllegalStateException(message);
		}
	}

	public int getNumberOfPositionValues() {
		List<Object> positionDataValues = getPositionData();
		return positionDataValues.size();
	}

	public double getSmallestPositionDistance() {

		double smallestDistance = Double.MAX_VALUE;

		List<Object> positionDataValues = getPositionData();
		int positionSize = positionDataValues.size();

		if (positionSize > 1) {

			Object firstPosition = positionDataValues.get(0);
			boolean isOrdinal = firstPosition instanceof String;
			if (isOrdinal) {
				return 1;
			}

			for (int positionIndex = 1; positionIndex < positionSize; positionIndex++) {
				Object leftPositionObj = positionDataValues.get(positionIndex - 1);
				Double leftPosition = Double.parseDouble(leftPositionObj.toString());

				Object rightPositionObj = positionDataValues.get(positionIndex);
				Double rightPosition = Double.parseDouble(rightPositionObj.toString());

				double distance = Math.abs(rightPosition - leftPosition);
				if (distance < smallestDistance) {
					smallestDistance = distance;
				}
			}
			return smallestDistance;
		} else {
			return 0;
		}

	}

	public Scale<?> getHorizontalScale() {
		Axis horizontalAxisAtom = getHorizontalAxis();
		if (horizontalAxisAtom == null) {
			return null;
		}
		return horizontalAxisAtom.getScale();
	}

	public Scale<?> getVerticalScale() {
		Axis verticalAxisAtom = getVerticalAxis();
		if (verticalAxisAtom == null) {
			return null;
		}
		return verticalAxisAtom.getScale();

	}

	public Axis getHorizontalAxis() {
		String horizontalAxisPath = data.horizontalAxis.get();
		if (horizontalAxisPath == null || horizontalAxisPath.isEmpty()) {
			return null;
		}
		Axis horizontalAxisAtom = getChildFromRoot(horizontalAxisPath);
		return horizontalAxisAtom;
	}

	public Axis getVerticalAxis() {
		String verticalAxisPath = data.verticalAxis.get();
		if (verticalAxisPath == null || verticalAxisPath.isEmpty()) {
			return null;
		}
		Axis verticalAxisAtom = getChildFromRoot(verticalAxisPath);
		return verticalAxisAtom;
	}

	private List<Object> getLengthData() {
		String lengthDataPath = data.barLengths.get();
		if (lengthDataPath.isEmpty()) {
			return new ArrayList<>();
		}
		org.treez.data.column.Column lengthDataColumn = getChildFromRoot(lengthDataPath);
		List<Object> lengthDataValues = lengthDataColumn.getValues();
		return lengthDataValues;
	}

	private List<Object> getPositionData() {
		String positionDataPath = data.barPositions.get();
		if (positionDataPath.isEmpty()) {
			return new ArrayList<>();
		}
		org.treez.data.column.Column positionDataColumn = getChildFromRoot(positionDataPath);
		List<Object> positionDataValues = positionDataColumn.getValues();
		return positionDataValues;
	}

	private List<Double> getQuantitativePositions() {
		String positionDataPath = data.barPositions.get();
		if (positionDataPath.isEmpty()) {
			return new ArrayList<>();
		}
		org.treez.data.column.Column positionDataColumn = getChildFromRoot(positionDataPath);

		boolean isNumericColumn = positionDataColumn.isNumeric();
		if (isNumericColumn) {
			List<Double> positionValues = positionDataColumn.getDoubleValues();
			return positionValues;
		} else {
			List<String> ordinalPositionValues = positionDataColumn.getStringValues();
			List<Double> positionValues = new ArrayList<>();

			for (Double position = 1.0; position <= ordinalPositionValues.size(); position++) {
				positionValues.add(position);
			}
			return positionValues;
		}

	}

	private List<String> getOrdinalPositions() {
		String positionDataPath = data.barPositions.get();
		if (positionDataPath.isEmpty()) {
			return new ArrayList<>();
		}
		org.treez.data.column.Column positionDataColumn = getChildFromRoot(positionDataPath);
		List<String> positionDataValues = positionDataColumn.getStringValues();
		return positionDataValues;
	}

	private List<Double> getQuantitativeLengths() {
		String lengthDataPath = data.barLengths.get();
		if (lengthDataPath.isEmpty()) {
			return new ArrayList<>();
		}

		org.treez.data.column.Column lengthDataColumn = getChildFromRoot(lengthDataPath);

		boolean isNumericColumn = lengthDataColumn.isNumeric();
		if (isNumericColumn) {
			List<Double> lengthValues = lengthDataColumn.getDoubleValues();
			return lengthValues;
		} else {
			List<String> ordinalLengthValues = lengthDataColumn.getStringValues();
			List<Double> lengthValues = new ArrayList<>();

			for (Double position = 1.0; position <= ordinalLengthValues.size(); position++) {
				lengthValues.add(position);
			}
			return lengthValues;
		}

	}

	private List<String> getOrdinalLengths() {
		String lengthDataPath = data.barLengths.get();
		if (lengthDataPath.isEmpty()) {
			return new ArrayList<>();
		}
		org.treez.data.column.Column lengthDataColumn = getChildFromRoot(lengthDataPath);
		List<String> lengthDataValues = lengthDataColumn.getStringValues();
		return lengthDataValues;
	}

	//#end region

}
