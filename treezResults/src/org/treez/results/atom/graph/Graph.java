package org.treez.results.atom.graph;

import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.AddChildAtomTreeViewerAction;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.results.Activator;
import org.treez.results.atom.axis.Axis;
import org.treez.results.atom.graphicspage.GraphicsPropertiesPage;
import org.treez.results.atom.graphicspage.GraphicsPropertiesPageFactory;
import org.treez.results.atom.xy.Xy;

/**
 * Represents a graph on a page. A graph may contain several xy plots
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Graph extends GraphicsPropertiesPage {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(Graph.class);

	//#region ATTRIBUTES

	/**
	 * Main properties, e.g. width & height
	 */
	public Data data;

	/**
	 * The properties of the background
	 */
	public Background background;

	/**
	 * The properties of the border
	 */
	public Border border;

	private Selection graphGroupSelection;

	private Selection rectSelection;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public Graph(String name) {
		super(name);
		setRunnable();
	}

	//#end region

	//#region METHODS

	@Override
	protected void createPropertyPageFactories() {
		data = new Data();
		propertyPageFactories.add(data);

		background = new Background();
		propertyPageFactories.add(background);

		border = new Border();
		propertyPageFactories.add(border);
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("graph.png");
	}

	/**
	 * Creates the context menu actions
	 */
	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {

		Action addData = new AddChildAtomTreeViewerAction(
				Axis.class,
				"axis",
				Activator.getImage("axis.png"),
				this,
				treeViewer);
		actions.add(addData);

		Action addXY = new AddChildAtomTreeViewerAction(Xy.class, "xy", Activator.getImage("xy.png"), this, treeViewer);
		actions.add(addXY);

		return actions;
	}

	@Override
	public void execute(Refreshable refreshable) {
		org.treez.results.atom.page.Page page = (org.treez.results.atom.page.Page) createTreeNodeAdaption()
				.getParent()
				.getAdaptable();
		page.execute(refreshable);
	}

	//#region D3

	/**
	 * @param d3
	 * @param pageSelection
	 * @return
	 */
	@Override
	public Selection plotWithD3(D3 d3, Selection pageSelection, Selection pageRectSelection, Refreshable refreshable) {
		Objects.requireNonNull(d3);
		this.treeViewRefreshable = refreshable;

		//remove old graph group if it already exists
		pageSelection //
				.select("#" + name) //
				.remove();

		//create new graph group
		graphGroupSelection = pageSelection //
				.append("g");
		bindNameToId(graphGroupSelection);

		//create rect
		rectSelection = graphGroupSelection //
				.append("rect") //
				.onMouseClick(this);

		updatePlotWithD3(d3);
		return graphGroupSelection;
	}

	/**
	 * Updates the Graph
	 */
	@Override
	public void updatePlotWithD3(D3 d3) {
		plotPageModels(d3);
		plotChildren(d3);
	}

	/**
	 * Plots the page models for the Graph (e.g. Border)
	 *
	 * @param d3
	 */
	private void plotPageModels(D3 d3) {
		for (GraphicsPropertiesPageFactory pageModel : propertyPageFactories) {
			graphGroupSelection = pageModel.plotWithD3(d3, graphGroupSelection, rectSelection, this);
		}
	}

	/**
	 * Plots the children (e.g. Axis, Xy) of the graph
	 *
	 * @param d3
	 */
	private void plotChildren(D3 d3) {
		plotAxis(d3);
		plotXy(d3);
	}

	/**
	 * Plots all child Xy
	 *
	 * @param d3
	 */
	private void plotXy(D3 d3) {
		for (Adaptable child : children) {
			Boolean isXY = child.getClass().equals(Xy.class);
			if (isXY) {
				Xy xy = (Xy) child;
				xy.plotWithD3(d3, graphGroupSelection, rectSelection, this.treeViewRefreshable);
			}
		}
	}

	/**
	 * Plots all child Axis
	 *
	 * @param d3
	 */
	private void plotAxis(D3 d3) {
		for (Adaptable child : children) {
			Boolean isAxis = child.getClass().equals(Axis.class);
			if (isAxis) {
				Axis axis = (Axis) child;
				axis.plotWithD3(d3, graphGroupSelection, rectSelection, this.treeViewRefreshable);
			}
		}
	}

	//#end region

	//#region CREATE CHILD ATOMS

	/**
	 * Creates an Axis child
	 *
	 * @param name
	 * @return
	 */
	public Axis createAxis(String name) {
		Axis child = new Axis(name);
		addChild(child);
		return child;
	}

	/**
	 * Creates an XY child
	 *
	 * @param name
	 * @return
	 */
	public Xy createXy(String name) {
		Xy child = new Xy(name);
		addChild(child);
		return child;
	}

	//#end region

	//#end region

}
