package org.treez.results.atom.xy;

import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.scales.QuantitativeScale;
import org.treez.results.Activator;
import org.treez.results.atom.axis.Axis;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.graphicspage.GraphicsPropertiesPage;
import org.treez.results.atom.graphicspage.GraphicsPropertiesPageFactory;

/**
 * Represents an xy scatter plot
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Xy extends GraphicsPropertiesPage {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(Xy.class);

	//#region ATTRIBUTES

	/**
	 * The data properties of the xy plot
	 */
	public Data data;

	/**
	 * The marker properties of the xy plot
	 */
	public Symbol symbol;

	/**
	 * The line properties of the xy plot
	 */
	public Line line;

	/**
	 * The error bar properties of the xy plot
	 */
	//public ErrorBar errorBar;

	/**
	 * The area properties of the xy plot
	 */
	public Area area;

	/**
	 * The label properties of the xy plot
	 */
	//public Label label;

	private Selection xySelection;

	//#end region

	// #region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public Xy(String name) {
		super(name);
		setRunnable();
	}

	// #end region

	// #region METHODS

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

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("xy.png");
	}

	/**
	 * Creates the context menu actions
	 */
	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {
		// no actions available right now
		return actions;
	}

	@Override
	public void execute(Refreshable refreshable) {
		Graph graph = (Graph) createTreeNodeAdaption().getParent().getAdaptable();
		graph.execute(refreshable);
	}

	/**
	 * @param d3
	 * @param graphSelection
	 */
	@Override
	public Selection plotWithD3(
			D3 d3,
			Selection graphSelection,
			Selection graphRectSelection,
			Refreshable refreshable) {
		Objects.requireNonNull(d3);
		this.treeViewRefreshable = refreshable;

		//remove old xy group if it already exists
		graphSelection //
				.select("#" + name) //
				.remove();

		//create new axis group
		xySelection = graphSelection //
				.insert("g", ".axis") //
				.attr("class", "xy") //
				.onMouseClick(this);
		bindNameToId(xySelection);

		updatePlotWithD3(d3);

		return graphSelection;
	}

	@Override
	public void updatePlotWithD3(D3 d3) {
		plotPageModels(d3);
	}

	/**
	 * Plots the page models for the Graph (e.g. Border)
	 *
	 * @param d3
	 */
	private void plotPageModels(D3 d3) {
		for (GraphicsPropertiesPageFactory pageModel : propertyPageFactories) {
			xySelection = pageModel.plotWithD3(d3, xySelection, null, this);
		}
	}

	/**
	 * @return
	 */
	public String getXyDataString() {

		List<Object> xDataValues = getXData();
		List<Object> yDataValues = getYData();

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
			Object xDatum = xDataValues.get(rowIndex);
			Double x = Double.parseDouble(xDatum.toString());

			Object yDatum = yDataValues.get(rowIndex);
			Double y = Double.parseDouble(yDatum.toString());

			String rowString = "[" + x + "," + y + "]";
			rowList.add(rowString);
		}
		String xyDataString = "[" + String.join(",", rowList) + "]";
		return xyDataString;
	}

	/**
	 * @return
	 */
	public QuantitativeScale<?> getXScale() {
		Axis xAxisAtom = getXAxis();
		QuantitativeScale<?> scale = (QuantitativeScale<?>) xAxisAtom.getScale();
		return scale;
	}

	/**
	 * @return
	 */
	public QuantitativeScale<?> getYScale() {
		Axis yAxisAtom = getYAxis();
		QuantitativeScale<?> scale = (QuantitativeScale<?>) yAxisAtom.getScale();
		return scale;
	}

	private Axis getXAxis() {
		String xAxisPath = data.xAxis.get();
		Axis xAxisAtom = (Axis) getChildFromRoot(xAxisPath);
		return xAxisAtom;
	}

	private Axis getYAxis() {
		String yAxisPath = data.yAxis.get();
		Axis yAxisAtom = (Axis) getChildFromRoot(yAxisPath);
		return yAxisAtom;
	}

	private List<Object> getXData() {
		String xDataPath = data.xData.get();
		org.treez.data.column.Column xDataColumn = (org.treez.data.column.Column) getChildFromRoot(xDataPath);
		List<Object> xDataValues = xDataColumn.getValues();
		return xDataValues;
	}

	private List<Object> getYData() {
		String yDataPath = data.yData.get();
		org.treez.data.column.Column yDataColumn = (org.treez.data.column.Column) getChildFromRoot(yDataPath);
		List<Object> yDataValues = yDataColumn.getValues();
		return yDataValues;
	}

	// #end region

}
