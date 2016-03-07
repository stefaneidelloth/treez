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

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(XySeries.class);

	//#region ATTRIBUTES

	/**
	 * Source table
	 */
	public final Attribute<String> sourceTable = new Wrap<>();

	/**
	 * If this is true the xy series is hidden.
	 */
	public final Attribute<Boolean> hide = new Wrap<>();

	private Selection seriesGroupSelection;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public XySeries(String name) {
		super(name);
		setRunnable();
		createModel();
	}

	//#end region

	//#region METHODS

	/**
	 * Creates the treez model for this atom
	 */
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

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("xySeries.png");
	}

	/**
	 * Creates the context menu actions for this atom
	 */
	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {

		return actions;
	}

	@Override
	public void execute(Refreshable refreshable) {
		treeViewRefreshable = refreshable;

		String sourceTablePath = sourceTable.get();
		Axis domainAxis = getOrCreateDomainAxis();
		Axis rangeAxis = getOrCreateRangeAxis();
		boolean sourceIsSpecified = sourceTablePath != null && !"".equals(sourceTablePath);
		if (sourceIsSpecified) {
			removeAllChildren();
			createNewXyChildren(sourceTablePath, domainAxis, rangeAxis);
		} else {
			sysLog.warn("The xy series '" + this.name + "' has no source table.");
		}
	}

	private void createNewXyChildren(String sourceTablePath, Axis domainAxis, Axis rangeAxis) {
		Table foundSourceTable = (Table) this.getChildFromRoot(sourceTablePath);
		int numberOfColumns = foundSourceTable.getNumberOfColumns();
		int numberOfPlots = numberOfColumns - 1;
		Columns columns = foundSourceTable.getColumns();
		List<String> columnHeaders = columns.getHeaders();
		String domainColumnName = columnHeaders.get(0);
		for (int rangeColumnIndex = 1; rangeColumnIndex <= numberOfPlots; rangeColumnIndex++) {
			String rangeColumnName = columnHeaders.get(rangeColumnIndex);
			Column rangeColumn = columns.getColumn(rangeColumnName);
			String rangeLegend = rangeColumn.description.get();
			createXyChild(sourceTablePath, domainAxis, domainColumnName, rangeAxis, rangeColumnName, rangeLegend);
		}
	}

	private Axis getOrCreateDomainAxis() {
		List<Axis> axisList = getAllAxisFromParentGraph();
		if (axisList.size() > 0) {
			Axis domainAxis = axisList.get(0);
			return domainAxis;
		} else {
			Graph graph = (Graph) this.getParentAtom();
			Axis domainAxis = graph.createAxis("xAxis");
			return domainAxis;
		}
	}

	private Axis getOrCreateRangeAxis() {
		List<Axis> axisList = getAllAxisFromParentGraph();
		if (axisList.size() > 1) {
			Axis domainAxis = axisList.get(1);
			return domainAxis;
		} else {
			Graph graph = (Graph) this.getParentAtom();
			Axis domainAxis = graph.createAxis("yAxis");
			return domainAxis;
		}
	}

	private List<Axis> getAllAxisFromParentGraph() {
		Graph graph = (Graph) this.getParentAtom();
		List<Axis> childAxis = graph.getChildrenByClass(Axis.class);
		return childAxis;
	}

	private void createXyChild(
			String sourceTablePath,
			Axis domainAxis,
			String domainColumnName,
			Axis rangeAxis,
			String rangeColumnName,
			String rangeLegend) {

		Xy xy = new Xy(rangeColumnName);

		String xAxisPath = domainAxis.createTreeNodeAdaption().getTreePath();
		xy.data.xAxis.set(xAxisPath);

		String xValuePath = sourceTablePath + ".columns." + domainColumnName;
		xy.data.xData.set(xValuePath);

		String yAxisPath = rangeAxis.createTreeNodeAdaption().getTreePath();
		xy.data.yAxis.set(yAxisPath);

		String yValuePath = sourceTablePath + ".columns." + rangeColumnName;
		xy.data.yData.set(yValuePath);

		xy.data.legendText.set(rangeLegend);

		this.addChild(xy);

	}

	/**
	 * @param d3
	 * @param graphSelection
	 * @param refreshable
	 * @return
	 */
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

	/**
	 * @param d3
	 */
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
