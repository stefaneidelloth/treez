package org.treez.study.atom.sensitivity;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.base.EmptyControlAdaption;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.TreeViewerAction;
import org.treez.study.Activator;
import org.treez.study.atom.Study;

/**
 * Represents a sensitivity parameter variation
 */
public class Sensitivity extends AdjustableAtom implements Study {

	private static final Logger LOG = Logger.getLogger(Sensitivity.class);

	//#region ATTRIBUTES

	//#region CONSTRUCTORS

	public Sensitivity(String name) {
		super(name);
		AttributeRoot emptyModel = new AttributeRoot("root");
		setModel(emptyModel);
	}

	//#end region

	//#region METHODS

	/**
	 * Provides a control to represent this atom
	 */
	@Override
	public AbstractControlAdaption createControlAdaption(Composite parent, Refreshable treeViewRefreshable) {

		String relativeHelpContextId = "sensitivity";
		String absoluteHelpContextId = Activator.getInstance().getAbsoluteHelpContextId(relativeHelpContextId);

		this.treeViewRefreshable = treeViewRefreshable;
		return new EmptyControlAdaption(parent, this, "This atom represents a sensitivity parameter variation.");
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("sensitivity.png");
	}

	/**
	 * Creates the context menu actions for this atom
	 */
	@Override
	protected List<Object> createContextMenuActions(TreeViewerRefreshable treeViewer) {

		List<Object> actions = new ArrayList<>();

		actions.add(new TreeViewerAction(
				"Add page",
				Activator.getImage("add.png"),
				treeViewer,
				() -> LOG.debug("add page")));

		return actions;
	}

	@Override
	public void runStudy(Refreshable refreshable, IProgressMonitor monitor) {
		//not yet implemented
	}

	//#end region

	//#region ACCESSORS

	@Override
	public String getSourceModelPath() {
		//return sourceModelPath.getValue();
		return "not implemented";
	}

	@Override
	public String getModelToRunModelPath() {
		// TODO Auto-generated method stub
		return "not implemented";
	}

	//#end region

}
