package org.treez.results.atom.graph;

import java.util.List;
import java.util.Objects;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.AddChildAtomTreeViewerAction;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.results.Activator;
import org.treez.results.atom.axis.Axis;
import org.treez.results.atom.bar.Bar;
import org.treez.results.atom.contour.Contour;
import org.treez.results.atom.graphicspage.Background;
import org.treez.results.atom.graphicspage.Border;
import org.treez.results.atom.graphicspage.GraphicsPropertiesPage;
import org.treez.results.atom.legend.Legend;
import org.treez.results.atom.xy.Xy;
import org.treez.results.atom.xyseries.XySeries;

/**
 * Represents a graph on a page. A graph may contain several xy plots
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Graph extends GraphicsPropertiesPage {

	//#region ATTRIBUTES

	public Data data;

	public Background background;

	public Border border;

	private Selection graphGroupSelection;

	private Selection rectSelection;

	//#end region

	//#region CONSTRUCTORS

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

	@Override
	public Image provideImage() {
		return Activator.getImage("graph.png");
	}

	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {

		Action addData = new AddChildAtomTreeViewerAction(
				Axis.class,
				"axis",
				Activator.getImage("axis.png"),
				this,
				treeViewer);
		actions.add(addData);

		Action addXySeries = new AddChildAtomTreeViewerAction(
				XySeries.class,
				"xySeries",
				Activator.getImage("xySeries.png"),
				this,
				treeViewer);
		actions.add(addXySeries);

		Action addXy = new AddChildAtomTreeViewerAction(Xy.class, "xy", Activator.getImage("xy.png"), this, treeViewer);
		actions.add(addXy);

		Action addBar = new AddChildAtomTreeViewerAction(
				Bar.class,
				"bar",
				Activator.getImage("bar.png"),
				this,
				treeViewer);
		actions.add(addBar);

		Action addContour = new AddChildAtomTreeViewerAction(
				Contour.class,
				"contour",
				Activator.getImage("contour.png"),
				this,
				treeViewer);
		actions.add(addContour);

		Action addLegend = new AddChildAtomTreeViewerAction(
				Legend.class,
				"legend",
				Activator.getImage("legend.png"),
				this,
				treeViewer);
		actions.add(addLegend);

		return actions;
	}

	@Override
	public void execute(FocusChangingRefreshable refreshable) {

		executeChildren(XySeries.class, treeViewRefreshable);

		//org.treez.results.atom.page.Page page = (org.treez.results.atom.page.Page) createTreeNodeAdaption()
		//		.getParent()
		//		.getAdaptable();
		//page.execute(refreshable);
	}

	//#region D3

	/**
	 * @param d3
	 * @param pageSelection
	 * @return
	 */
	@Override
	public Selection plotWithD3(
			D3 d3,
			Selection pageSelection,
			Selection pageRectSelection,
			FocusChangingRefreshable refreshable) {

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

	@Override
	public void updatePlotWithD3(D3 d3) {
		plotPageModels(d3);
		plotChildren(d3);
	}

	private void plotPageModels(D3 d3) {
		for (GraphicsPropertiesPageFactory pageModel : propertyPageFactories) {
			graphGroupSelection = pageModel.plotWithD3(d3, graphGroupSelection, rectSelection, this);
		}
	}

	private void plotChildren(D3 d3) {
		plotAxis(d3);
		plotContour(d3);
		plotXySeries(d3);
		plotXy(d3);
		plotBar(d3);
		plotLegend(d3);
	}

	private void plotAxis(D3 d3) {
		for (Adaptable child : children) {
			Boolean isAxis = child.getClass().equals(Axis.class);
			if (isAxis) {
				Axis axis = (Axis) child;
				axis.plotWithD3(d3, graphGroupSelection, rectSelection, this.treeViewRefreshable);
			}
		}
	}

	private void plotXySeries(D3 d3) {
		for (Adaptable child : children) {
			Boolean isXySeries = child.getClass().equals(XySeries.class);
			if (isXySeries) {
				XySeries xySeries = (XySeries) child;
				xySeries.plotWithD3(d3, graphGroupSelection, this.treeViewRefreshable);
			}
		}
	}

	private void plotXy(D3 d3) {
		for (Adaptable child : children) {
			Boolean isXy = child.getClass().equals(Xy.class);
			if (isXy) {
				Xy xy = (Xy) child;
				xy.plotWithD3(d3, graphGroupSelection, rectSelection, this.treeViewRefreshable);
			}
		}
	}

	private void plotBar(D3 d3) {
		for (Adaptable child : children) {
			Boolean isBar = child.getClass().equals(Bar.class);
			if (isBar) {
				Bar bar = (Bar) child;
				bar.plotWithD3(d3, graphGroupSelection, rectSelection, this.treeViewRefreshable);
			}
		}
	}

	private void plotContour(D3 d3) {
		for (Adaptable child : children) {
			Boolean isContour = child.getClass().equals(Contour.class);
			if (isContour) {
				Contour contour = (Contour) child;
				contour.plotWithD3(d3, graphGroupSelection, rectSelection, this.treeViewRefreshable);
			}
		}
	}

	private void plotLegend(D3 d3) {
		for (Adaptable child : children) {
			Boolean isLegend = child.getClass().equals(Legend.class);
			if (isLegend) {
				Legend legend = (Legend) child;
				legend.plotWithD3(d3, graphGroupSelection, rectSelection, this.treeViewRefreshable);
			}
		}
	}

	//#end region

	//#region CREATE CHILD ATOMS

	public Axis createAxis(String name) {
		Axis child = new Axis(name);
		addChild(child);
		return child;
	}

	public XySeries createXySeries(String name) {
		XySeries child = new XySeries(name);
		addChild(child);
		return child;
	}

	public Xy createXy(String name) {
		Xy child = new Xy(name);
		addChild(child);
		return child;
	}

	public Bar createBar(String name) {
		Bar child = new Bar(name);
		addChild(child);
		return child;
	}

	public Legend createLegend(String name) {
		Legend child = new Legend(name);
		addChild(child);
		return child;
	}

	public Contour createContour(String name) {
		Contour child = new Contour(name);
		addChild(child);
		return child;
	}

	//#end region

	//#end region

}
