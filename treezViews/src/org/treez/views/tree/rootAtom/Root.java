package org.treez.views.tree.rootAtom;

import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.attribute.base.EmptyControlAdaption;
import org.treez.core.atom.base.RegionsAtomCodeAdaption;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.AddChildAtomTreeViewerAction;
import org.treez.model.atom.Models;
import org.treez.results.atom.results.Results;
import org.treez.study.atom.Studies;
import org.treez.views.Activator;

/**
 * The root item for the tree view
 */
public class Root extends AdjustableAtom {

	//#region CONSTRUCTORS

	public Root(String name) {
		super(name);
		createEmptyModel();
	}

	/**
	 * Copy constructor
	 */
	private Root(Root rootToCopy) {
		super(rootToCopy);
		createRootModel();
	}

	//#end region

	//#region METHODS

	@Override
	public Root copy() {
		return new Root(this);
	}

	/**
	 * Creates the underlying model
	 */
	public void createRootModel() {
		AttributeRoot root = new AttributeRoot("root");
		org.treez.core.atom.attribute.Page dataPage = root.createPage("");
		String relativeHelpContextId = "root";
		String absoluteHelpContextId = Activator.getInstance().getAbsoluteHelpContextId(relativeHelpContextId);
		Section studies = dataPage.createSection("root", absoluteHelpContextId);
		studies.setTitle("");
		studies.createLabel("label",
				"This atom represents the root of the tree.\nClick the help button for more information.");
		setModel(root);
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("root.png");
	}

	/**
	 * Provides a control to represent this atom
	 */
	@Override
	public AbstractControlAdaption createControlAdaption(Composite parent, FocusChangingRefreshable treeViewRefreshable) {
		return new EmptyControlAdaption(parent, this, "");
	}

	@Override
	public CodeAdaption createCodeAdaption(ScriptType scriptType) {
		return new RegionsAtomCodeAdaption(this);
	}

	/**
	 * Creates the context menu actions
	 */
	@Override
	protected ArrayList<Object> createContextMenuActions(final TreeViewerRefreshable treeViewer) {

		ArrayList<Object> actions = new ArrayList<>();

		Action addModel = new AddChildAtomTreeViewerAction(
				Models.class,
				"models",
				Activator.getImage("models.png"),
				this,
				treeViewer);
		actions.add(addModel);

		Action addStudies = new AddChildAtomTreeViewerAction(
				Studies.class,
				"studies",
				Activator.getImage("studies.png"),
				this,
				treeViewer);
		actions.add(addStudies);

		Action addResults = new AddChildAtomTreeViewerAction(
				org.treez.results.atom.results.Results.class,
				"results",
				Activator.getImage("results.png"),
				this,
				treeViewer);
		actions.add(addResults);

		return actions;
	}

	//#region CREATE CHILD ATOMS

	public Models createModels(String name) {
		Models models = new Models(name);
		addChild(models);
		return models;
	}

	public Studies createStudies(String name) {
		Studies studies = new Studies(name);
		addChild(studies);
		return studies;
	}

	public Results createResults(String name) {
		Results results = new Results(name);
		addChild(results);
		return results;
	}

	//#end region

	//#end region
}
