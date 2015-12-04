package org.treez.study.atom;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.AddChildAtomTreeViewerAction;
import org.treez.study.Activator;
import org.treez.study.atom.picking.Picking;
import org.treez.study.atom.probability.Probability;
import org.treez.study.atom.sensitivity.Sensitivity;
import org.treez.study.atom.sweep.Sweep;

/**
 * Represents the root atom for all studies
 */
public class Studies extends AdjustableAtom {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(Studies.class);

	//#region ATTRIBUTES

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public Studies(String name) {
		super(name);
		setRunnable();
		createStudiesModel();
	}

	//#end region

	//#region METHODS

	/**
	 * Creates the underlying model
	 */
	public void createStudiesModel() {
		AttributeRoot root = new AttributeRoot("root");
		org.treez.core.atom.attribute.Page dataPage = root.createPage("");
		String relativeHelpContextId = "studies";
		String absoluteHelpContextId = Activator.getInstance().getAbsoluteHelpContextId(relativeHelpContextId);
		Section studies = dataPage.createSection("studies", "", absoluteHelpContextId);
		studies.createLabel("label", "This atom represents studies.");
		setModel(root);
	}

	/**
	 * Executes all child studies
	 */
	@Override
	public void execute(Refreshable refreshable) {
		this.treeViewRefreshable = refreshable;
		for (AbstractAtom child : getChildAtoms()) {
			boolean isStudy = child instanceof Study;
			if (isStudy) {
				//get study
				Study childStudy = (Study) child;

				//run study
				runNonUiJob("Sweep:execute sweep", (monitor) -> {
					childStudy.runStudy(refreshable, monitor);
				});

			}
		}
		refresh();
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("studies.png");
	}

	/**
	 * Creates the context menu actions for this atom
	 */
	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {

		Action addSweep = new AddChildAtomTreeViewerAction(
				Sweep.class,
				"sweep",
				Activator.getImage("sweep.png"),
				this,
				treeViewer);
		actions.add(addSweep);

		Action addPicking = new AddChildAtomTreeViewerAction(
				Picking.class,
				"picking",
				Activator.getImage("picking.png"),
				this,
				treeViewer);
		actions.add(addPicking);

		Action addSensitivity = new AddChildAtomTreeViewerAction(
				Sensitivity.class,
				"sensitivity",
				Activator.getImage("sensitivity.png"),
				this,
				treeViewer);
		actions.add(addSensitivity);

		Action addProbability = new AddChildAtomTreeViewerAction(
				Probability.class,
				"probability",
				Activator.getImage("probability.png"),
				this,
				treeViewer);
		actions.add(addProbability);

		return actions;
	}

	//#region CREATE CHILD ATOMS

	/**
	 * Creates a Sweep child
	 *
	 * @param name
	 * @return
	 */
	public Sweep createSweep(String name) {
		Sweep child = new Sweep(name);
		addChild(child);
		return child;
	}

	/**
	 * Creates a Picking child
	 *
	 * @param name
	 * @return
	 */
	public Picking createPicking(String name) {
		Picking child = new Picking(name);
		addChild(child);
		return child;
	}

	/**
	 * Creates a Sensitivity child
	 *
	 * @param name
	 * @return
	 */
	public Sensitivity createSensitivity(String name) {
		Sensitivity child = new Sensitivity(name);
		addChild(child);
		return child;
	}

	/**
	 * Creates a Probability child
	 *
	 * @param name
	 * @return
	 */
	public Probability createProbability(String name) {
		Probability child = new Probability(name);
		addChild(child);
		return child;
	}

	//#end region

	//#end region

}
