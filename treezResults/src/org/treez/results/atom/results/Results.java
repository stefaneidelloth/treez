package org.treez.results.atom.results;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.FocusChangingRefreshable;
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

	//#region CONSTRUCTORS

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
		Section results = dataPage.createSection("results", absoluteHelpContextId);
		results.setLabel("");
		results.createLabel("label", "This atom represents results.");
		setModel(root);
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("results.png");
	}

	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {

		Action addData = new AddChildAtomTreeViewerAction(
				Data.class,
				"data",
				org.treez.results.Activator.getImage("data.png"),
				this,
				treeViewer);
		actions.add(addData);

		Action addPage = new AddChildAtomTreeViewerAction(
				Page.class,
				"page",
				Activator.getImage("page.png"),
				this,
				treeViewer);
		actions.add(addPage);

		return actions;
	}

	@Override
	public void execute(FocusChangingRefreshable treeViewerRefreshable) {
		treeViewRefreshable = treeViewerRefreshable;
		executeChildren(Data.class, treeViewRefreshable);
		executeChildren(Page.class, treeViewRefreshable);
	}

	//#region CREATE CHILD ATOMS

	public Data createData(String name) {
		Data child = new Data(name);
		addChild(child);
		return child;
	}

	public Page createPage(String name) {
		Page child = new Page(name);
		addChild(child);
		return child;
	}

	//#end region

	//#end region

}
