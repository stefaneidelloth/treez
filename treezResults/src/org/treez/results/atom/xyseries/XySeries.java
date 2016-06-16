package org.treez.results.atom.xyseries;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.AbstractGraphicsAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.color.ColorBrewer;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.data.column.Column;
import org.treez.data.column.Columns;
import org.treez.data.table.Table;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.results.Activator;
import org.treez.results.atom.axis.Axis;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.legend.LegendContributor;
import org.treez.results.atom.legend.LegendContributorProvider;
import org.treez.results.atom.xy.Xy;

/**
 * Represents a series of xy plots that references a table
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class XySeries extends AbstractGraphicsAtom implements LegendContributorProvider {

	private static final Logger LOG = Logger.getLogger(XySeries.class);

	//#region ATTRIBUTES

	public final Attribute<String> sourceTable = new Wrap<>();

	public final Attribute<String> domainLabel = new Wrap<>();

	public final Attribute<String> rangeLabel = new Wrap<>();

	public final Attribute<String> colorMap = new Wrap<>();

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
		section.setLabel("XySeries");

		Runnable runAction = () -> execute(treeViewRefreshable);
		section.createSectionAction("action", "Build XySeries", runAction);

		section.createModelPath(sourceTable, this, "", Table.class, this) //
				.setLabel("Source table");

		section.createTextField(domainLabel, this).setLabel("Domain label");

		section.createTextField(rangeLabel, this).setLabel("Range label");

		section.createColorMap(colorMap, this);

		section.createCheckBox(hide, this);

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
	public void addLegendContributors(List<LegendContributor> legendContributors) {
		List<AbstractAtom<?>> children = getChildAtoms();
		for (AbstractAtom<?> child : children) {
			boolean isLegendContributorProvider = child instanceof LegendContributorProvider;
			if (isLegendContributorProvider) {
				LegendContributorProvider provider = (LegendContributorProvider) child;
				provider.addLegendContributors(legendContributors);
			}
		}
	}

	@Override
	public void execute(FocusChangingRefreshable refreshable) {
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

		final int smallMapSize = 10;
		final int largeMapSize = 20;

		int colorMapSize = smallMapSize;
		if (numberOfPlots > smallMapSize) {
			colorMapSize = largeMapSize;
		}
		if (numberOfPlots > largeMapSize) {
			String message = "XySeries only supports up to " + largeMapSize + " plots. It can't create " + numberOfPlots
					+ " plots.";
			throw new IllegalStateException(message);
		}

		String[] seriesColors = ColorBrewer.Category.get(colorMapSize);

		Columns columns = foundSourceTable.getColumns();
		String columnsName = columns.getName();
		List<String> columnHeaders = columns.getHeaders();
		String domainColumnName = columnHeaders.get(0);
		for (int rangeColumnIndex = 1; rangeColumnIndex <= numberOfPlots; rangeColumnIndex++) {
			String rangeColumnName = columnHeaders.get(rangeColumnIndex);
			Column rangeColumn = columns.getColumn(rangeColumnName);
			String rangeLegend = rangeColumn.header.get();
			String color = seriesColors[rangeColumnIndex - 1];
			createNewXyChild(sourceTablePath, columnsName, domainAxis, domainColumnName, rangeAxis, rangeColumnName,
					rangeLegend, color);
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

			domainAxis.data.label.set(domainLabel.get());

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

			rangeAxis.data.label.set(rangeLabel.get());
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

	@SuppressWarnings("checkstyle:parameternumber")
	private void createNewXyChild(
			String sourceTablePath,
			String columnsName,
			Axis domainAxis,
			String domainColumnName,
			Axis rangeAxis,
			String rangeColumnName,
			String rangeLegend,
			String color) {

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

		xy.line.color.set(color);
		xy.symbol.fillColor.set(color);
		xy.symbol.hideLine.set(true);

		this.addChild(xy);

	}

	public Selection plotWithD3(D3 d3, Selection graphSelection, FocusChangingRefreshable refreshable) {
		this.treeViewRefreshable = refreshable;

		//remove old series group if it already exists
		graphSelection //
				.select("#" + name).remove();

		//create new series group
		seriesGroupSelection = graphSelection //
				.append("g") //
				.onMouseClick(this);
		bindNameToId(seriesGroupSelection);

		AbstractGraphicsAtom.bindDisplayToBooleanAttribute("hidePage", seriesGroupSelection, hide);

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
