package org.treez.views.tree.rootAtom;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.Refreshable;
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

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(Root.class);

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public Root(String name) {
		super(name);
		createEmptyModel();
	}

	/**
	 * Copy constructor
	 *
	 * @param rootToCopy
	 */
	private Root(Root rootToCopy) {
		super(rootToCopy);
		createRootModel();
	}

	//#end region

	//#region METHODS

	//#region COPY

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
		Section studies = dataPage.createSection("root", "", absoluteHelpContextId);
		studies.createLabel("label",
				"This atom represents the root of the tree.\nClick the help button for more information.");
		setModel(root);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("root.png");
	}

	/**
	 * Provides a control to represent this atom
	 */
	@Override
	public AbstractControlAdaption createControlAdaption(Composite parent, Refreshable treeViewRefreshable) {
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

	/**
	 * Creates a models child
	 *
	 * @param name
	 * @return
	 */
	public Models createModels(String name) {
		Models models = new Models(name);
		addChild(models);
		return models;
	}

	/**
	 * Creates a studies child
	 *
	 * @param name
	 * @return
	 */
	public Studies createStudies(String name) {
		Studies studies = new Studies(name);
		addChild(studies);
		return studies;
	}

	/**
	 * Creates a results child
	 *
	 * @param name
	 * @return
	 */
	public Results createResults(String name) {
		Results results = new Results(name);
		addChild(results);
		return results;
	}

	//#end region

	//#end region
}
