package org.treez.model.atom;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.RegionsAtomCodeAdaption;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.AddChildAtomTreeViewerAction;
import org.treez.model.Activator;
import org.treez.model.atom.executable.Executable;
import org.treez.model.atom.genericInput.GenericInputModel;

/**
 * Represents the root atom for all models. This model inherits from AbstractModel and can therefore be remotely
 * executed from a Study. This "main model" does not model anything by itself but it will have sub models that do the
 * work.
 */
public class Models extends AbstractModel {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings({ "hiding", "unused" })
	private static Logger sysLog = Logger.getLogger(Models.class);

	//#region ATTRIBUTES

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public Models(String name) {
		super(name);
		setRunnable();
		createModelsModel();
	}

	//#region METHODS

	/**
	 * Creates the control model: an empty section with a help button in the title bar
	 */
	private void createModelsModel() {

		AttributeRoot root = new AttributeRoot("root");
		Page dataPage = root.createPage("");
		String relativeHelpContextId = "models";
		String absoluteHelpContextId = Activator.getInstance().getAbsoluteHelpContextId(relativeHelpContextId);
		Section models = dataPage.createSection("models", "", absoluteHelpContextId);
		models.createLabel("label", "This atom represents models.");
		setModel(root);

	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("models.png");
	}

	/**
	 * Creates the context menu actions for this atom
	 */
	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {

		Action addGenericModel = new AddChildAtomTreeViewerAction(
				GenericInputModel.class,
				"genericInputModel",
				Activator.getImage("genericModel.png"),
				this,
				treeViewer);
		actions.add(addGenericModel);

		Action addExecutable = new AddChildAtomTreeViewerAction(
				Executable.class,
				"executable",
				Activator.getImage("run.png"),
				this,
				treeViewer);
		actions.add(addExecutable);

		return actions;
	}

	@Override
	public CodeAdaption createCodeAdaption(ScriptType scriptType) {
		return new RegionsAtomCodeAdaption(this);
	}

	//#region CREATE CHILD ATOMS

	/**
	 * Creates a GenericInputModel child
	 *
	 * @param name
	 * @return
	 */
	public GenericInputModel createGenericInputModel(String name) {
		GenericInputModel child = new GenericInputModel(name);
		addChild(child);
		return child;
	}

	/**
	 * Creates an Executable child
	 *
	 * @param name
	 * @return
	 */
	public Executable createExecutable(String name) {
		Executable child = new Executable(name);
		addChild(child);
		return child;
	}

	//#end region

	//#end region

}
