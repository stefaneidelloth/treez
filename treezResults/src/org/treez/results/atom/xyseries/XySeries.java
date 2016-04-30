package org.treez.results.atom.xyseries;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.data.column.Column;
import org.treez.data.column.Columns;
import org.treez.data.table.Table;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.results.Activator;
import org.treez.results.atom.axis.Axis;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.xy.Xy;

/**
 * Represents a series of xy plots that references a table
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class XySeries extends GraphicsAtom {

	private static final Logger LOG = Logger.getLogger(XySeries.class);

	//#region ATTRIBUTES

	public final Attribute<String> sourceTable = new Wrap<>();

	public final Attribute<Boolean> hide = new Wrap<>();

	private Selection seriesGroupSelection;

	//#end region

	//#region CONSTRUCTORS

	public XySeries(String name) {
		super(name);
		setRunnable();
		createModel();
	}

	//#end region

	//#region METHODS

	private void createModel() {

		//root
		AttributeRoot root = new AttributeRoot("root");

		//page
		org.treez.core.atom.attribute.Page page = root.createPage("page");

		//section
		Section section = page.createSection("section");
		section.setTitle("XySeries");

		Runnable runAction = () -> execute(treeViewRefreshable);
		section.createSectionAction("action", "Build XySeries", runAction);

		section
				.createModelPath(sourceTable, this, "", Table.class, this) //
				.setLabel("Source table");

		section.createCheckBox(hide, "hide");

		setModel(root);

	}

	@Override
	public Image provideImage() {
		return Activator.getImage("xySeries.png");
	}

	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {

		return actions;
	}

	@Override
	public void execute(Refreshable refreshable) {
		treeViewRefreshable = refreshable;

		String sourceTablePath = sourceTable.get();
		boolean sourceIsSpecified = sourceTablePath != null && !"".equals(sourceTablePath);
		if (sourceIsSpecified) {
			Table foundSourceTable = this.getChildFromRoot(sourceTablePath);

			Axis domainAxis = getOrCreateDomainAxis(foundSourceTable);
			Axis rangeAxis = getOrCreateRangeAxis(foundSourceTable);
			removeAllChildren();
			createNewXyChildren(sourceTablePath, domainAxis, rangeAxis);
		} else {
			LOG.warn("The xy series '" + this.name + "' has no source table.");
		}
	}

	private void createNewXyChildren(String sourceTablePath, Axis domainAxis, Axis rangeAxis) {
		Table foundSourceTable = this.getChildFromRoot(sourceTablePath);
		int numberOfColumns = foundSourceTable.getNumberOfColumns();
		int numberOfPlots = numberOfColumns - 1;
		Columns columns = foundSourceTable.getColumns();
		String columnsName = columns.getName();
		List<String> columnHeaders = columns.getHeaders();
		String domainColumnName = columnHeaders.get(0);
		for (int rangeColumnIndex = 1; rangeColumnIndex <= numberOfPlots; rangeColumnIndex++) {
			String rangeColumnName = columnHeaders.get(rangeColumnIndex);
			Column rangeColumn = columns.getColumn(rangeColumnName);
			String rangeLegend = rangeColumn.description.get();
			createNewXyChild(sourceTablePath, columnsName, domainAxis, domainColumnName, rangeAxis, rangeColumnName,
					rangeLegend);
		}
	}

	private Axis getOrCreateDomainAxis(Table sourceTable) {
		List<Axis> axisList = getAllAxisFromParentGraph();
		if (axisList.size() > 0) {
			Axis domainAxis = axisList.get(0);
			return domainAxis;
		} else {
			Graph graph = (Graph) this.getParentAtom();
			Axis domainAxis = graph.createAxis("xAxis");
			double[] domainAxisLimits = getDomainLimits(sourceTable);
			domainAxis.data.min.set("" + domainAxisLimits[0]);
			domainAxis.data.max.set("" + domainAxisLimits[1]);
			return domainAxis;
		}
	}

	private static double[] getDomainLimits(Table sourceTable) {
		Columns columns = sourceTable.getColumns();
		int numberOfColumns = columns.getNumberOfColumns();
		if (numberOfColumns > 0) {
			Column domainColumn = sourceTable.getColumns().getColumnByIndex(0);
			List<Double> domainValues = domainColumn.getDoubleValues();
			return getLimits(domainValues, Double.MAX_VALUE, Double.MIN_VALUE);
		} else {
			double[] limits = { 0, 1 };
			return limits;
		}

	}

	private static double[] getLimits(List<Double> domainValues, double initialMin, double initialMax) {

		if (domainValues.isEmpty()) {
			double[] limits = { 0, 1 };
			return limits;
		}

		double min = initialMin;
		double max = initialMax;
		for (Double value : domainValues) {
			if (value < min) {
				min = value;
			}

			if (value > max) {
				max = value;
			}
		}

		double[] limits = { min, max };
		return limits;
	}

	private Axis getOrCreateRangeAxis(Table sourceTable) {
		List<Axis> axisList = getAllAxisFromParentGraph();
		if (axisList.size() > 1) {
			Axis rangeAxis = axisList.get(1);
			return rangeAxis;
		} else {
			Graph graph = (Graph) this.getParentAtom();
			Axis rangeAxis = graph.createAxis("yAxis");
			rangeAxis.data.direction.set("vertical");
			double[] rangeAxisLimits = getRangeLimits(sourceTable);
			rangeAxis.data.min.set("" + rangeAxisLimits[0]);
			rangeAxis.data.max.set("" + rangeAxisLimits[1]);
			return rangeAxis;
		}
	}

	private static double[] getRangeLimits(Table sourceTable) {

		Columns columns = sourceTable.getColumns();
		int numberOfColumns = columns.getNumberOfColumns();
		if (numberOfColumns > 1) {
			double[] limits = { Double.MAX_VALUE, Double.MIN_VALUE };
			for (int columnIndex = 1; columnIndex < numberOfColumns; columnIndex++) {
				Column rangeColumn = columns.getColumnByIndex(columnIndex);
				List<Double> rangeValues = rangeColumn.getDoubleValues();
				limits = getLimits(rangeValues, limits[0], limits[1]);
			}
			return limits;
		} else {
			double[] limits = { 0, 1 };
			return limits;
		}
	}

	private List<Axis> getAllAxisFromParentGraph() {
		Graph graph = (Graph) this.getParentAtom();
		List<Axis> childAxis = graph.getChildrenByClass(Axis.class);
		return childAxis;
	}

	private void createNewXyChild(
			String sourceTablePath,
			String columnsName,
			Axis domainAxis,
			String domainColumnName,
			Axis rangeAxis,
			String rangeColumnName,
			String rangeLegend) {

		Xy xy = new Xy(rangeColumnName);

		String xAxisPath = domainAxis.createTreeNodeAdaption().getTreePath();
		xy.data.xAxis.set(xAxisPath);

		String xValuePath = sourceTablePath + "." + columnsName + "." + domainColumnName;
		xy.data.xData.set(xValuePath);

		String yAxisPath = rangeAxis.createTreeNodeAdaption().getTreePath();
		xy.data.yAxis.set(yAxisPath);

		String yValuePath = sourceTablePath + "." + columnsName + "." + rangeColumnName;
		xy.data.yData.set(yValuePath);

		xy.data.legendText.set(rangeLegend);

		this.addChild(xy);

	}

	public Selection plotWithD3(D3 d3, Selection graphSelection, Refreshable refreshable) {
		this.treeViewRefreshable = refreshable;

		//remove old series group if it already exists
		graphSelection //
				.select("#" + name)
				.remove();

		//create new series group
		seriesGroupSelection = graphSelection //
				.append("g") //
				.onMouseClick(this);
		bindNameToId(seriesGroupSelection);

		GraphicsAtom.bindDisplayToBooleanAttribute("hidePage", seriesGroupSelection, hide);

		updatePlotWithD3(d3);

		return graphSelection;
	}

	public void updatePlotWithD3(D3 d3) {

		for (Adaptable child : children) {
			Boolean isXy = child.getClass().equals(Xy.class);
			if (isXy) {
				Xy xy = (Xy) child;
				xy.plotWithD3(d3, seriesGroupSelection, null, this.treeViewRefreshable);

			}
		}
	}

	//#end region

}
