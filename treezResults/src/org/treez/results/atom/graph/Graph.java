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
import org.treez.javafxd3.d3.functions.MouseClickFunction;
import org.treez.results.Activator;
import org.treez.results.atom.axis.Axis;
import org.treez.results.atom.veuszpage.GraphicsPageModel;
import org.treez.results.atom.veuszpage.GraphicsPropertiesPage;
import org.treez.results.atom.xy.Xy;

/**
 * Represents a veusz graph
 */
public class Graph extends GraphicsPropertiesPage {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(Graph.class);

	//#region ATTRIBUTES

	private Selection graphSelection;

	Selection rectSelection;

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
		MouseClickFunction dummy;
	}

	//#end region

	//#region METHODS

	@Override
	protected void fillVeuszPageModels() {
		veuszPageModels.add(new Main());
		veuszPageModels.add(new Background());
		veuszPageModels.add(new Border());
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
	public Selection plotWidthD3(D3 d3, Selection pageSelection) {
		Objects.requireNonNull(d3);

		plotPageWithD3AndCreateGraphSelection(d3, pageSelection);

		//initialize axis scales at the beginning, so that they can be used
		//by other plot components
		for (Adaptable child : children) {
			Boolean isAxis = child.getClass().equals(Axis.class);
			if (isAxis) {
				Axis axis = (Axis) child;
				axis.initializeScalesWithD3(d3, rectSelection);
			}
		}

		for (Adaptable child : children) {
			Boolean isXY = child.getClass().equals(Xy.class);
			if (isXY) {
				Xy xy = (Xy) child;
				xy.plotWithD3(d3, graphSelection, rectSelection);
			}
		}

		//plot axis at the end to show them on top
		for (Adaptable child : children) {
			Boolean isAxis = child.getClass().equals(Axis.class);
			if (isAxis) {
				Axis axis = (Axis) child;
				axis.plotWithD3(d3, graphSelection, rectSelection);
			}
		}

		return graphSelection;

	}

	private void plotPageWithD3AndCreateGraphSelection(D3 d3, Selection pageSelection) {

		graphSelection = pageSelection //
				.append("g") //
				.attr("id", "" + name);

		rectSelection = graphSelection //
				.append("rect") //
				.attr("fill", "lightblue");

		for (GraphicsPageModel pageModel : veuszPageModels) {
			graphSelection = pageModel.plotWithD3(d3, graphSelection, rectSelection, this);
		}

		//handle mouse click
		rectSelection.onMouseClick(this);
	}

	//#end region

	//#region VEUSZ TEXT

	/**
	 * Creates start of veusz text
	 *
	 * @return
	 */
	@Override
	public String createVeuszStartText() {

		String veuszString = "\n";
		veuszString = veuszString + "Add('graph', name='" + name + "', autoadd=False)\n";
		veuszString = veuszString + "To('" + name + "')\n";

		return veuszString;
	}

	/**
	 * Creates end of veusz text
	 *
	 * @return
	 */
	@Override
	public String createVeuszEndText() {

		String veuszString = "";

		//add veusz text of children
		for (Adaptable child : children) {

			Boolean isAxis = child.getClass().getSimpleName().equals("Axis");
			if (isAxis) {
				String axisText = ((Axis) child).getVeuszText();
				veuszString = veuszString + axisText;
				Objects.requireNonNull(axisText);
				veuszString = veuszString + "To('..')\n";
				veuszString = veuszString + "\n";
			}

			Boolean isXY = child.getClass().getSimpleName().equals("XY");
			if (isXY) {
				String xyText = ((Xy) child).getVeuszText();
				Objects.requireNonNull(xyText);
				veuszString = veuszString + xyText;
				veuszString = veuszString + "To('..')\n";
				veuszString = veuszString + "\n";
			}

		}

		return veuszString;
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
