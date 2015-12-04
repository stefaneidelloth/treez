package org.treez.results.atom.results;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Section;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.AddChildAtomTreeViewerAction;
import org.treez.results.Activator;
import org.treez.results.atom.data.Data;
import org.treez.results.atom.page.Page;

/**
 * Represents the root atom for all results, like plots, reports ect.
 */
public class Results extends AdjustableAtom {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(Results.class);

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public Results(String name) {
		super(name);
		setRunnable();
		createResultsModel();
	}

	//#end region

	//#region METHODS

	/**
	 * Creates the underlying model
	 */
	public void createResultsModel() {
		AttributeRoot root = new AttributeRoot("root");
		org.treez.core.atom.attribute.Page dataPage = root.createPage("");
		String relativeHelpContextId = "results";
		String absoluteHelpContextId = Activator.getInstance().getAbsoluteHelpContextId(relativeHelpContextId);
		Section results = dataPage.createSection("results", "", absoluteHelpContextId);
		results.createLabel("label", "This atom represents results.");
		setModel(root);
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("results.png");
	}

	/**
	 * Creates the context menu actions for this atom
	 */
	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {

		Action addData = new AddChildAtomTreeViewerAction(
				Data.class,
				"data",
				org.treez.results.Activator.getImage("data.png"),
				this,
				treeViewer);
		actions.add(addData);

		Action addVeuszPage = new AddChildAtomTreeViewerAction(
				Page.class,
				"page",
				Activator.getImage("page.png"),
				this,
				treeViewer);
		actions.add(addVeuszPage);

		return actions;
	}

	//#region CREATE CHILD ATOMS

	/**
	 * Creates a Data child
	 *
	 * @param name
	 * @return
	 */
	public Data createData(String name) {
		Data child = new Data(name);
		addChild(child);
		return child;
	}

	/**
	 * Creates a Page child
	 *
	 * @param name
	 * @return
	 */
	public Page createPage(String name) {
		Page child = new Page(name);
		addChild(child);
		return child;
	}

	//#end region

	//#end region

}
