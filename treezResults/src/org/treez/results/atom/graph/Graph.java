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
import org.treez.results.Activator;
import org.treez.results.atom.axis.Axis;
import org.treez.results.atom.veuszpage.VeuszPropertiesPage;
import org.treez.results.atom.xy.XY;

/**
 * Represents a veusz graph
 */
public class Graph extends VeuszPropertiesPage {

	//#region ATTRIBUTES

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(Graph.class);

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

		Action addXY = new AddChildAtomTreeViewerAction(XY.class, "xy", Activator.getImage("xy.png"), this, treeViewer);
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
				String xyText = ((XY) child).getVeuszText();
				Objects.requireNonNull(xyText);
				veuszString = veuszString + xyText;
				veuszString = veuszString + "To('..')\n";
				veuszString = veuszString + "\n";
			}

		}

		return veuszString;
	}

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
	public XY createXY(String name) {
		XY child = new XY(name);
		addChild(child);
		return child;
	}

	//#end region

	//#end region

}
