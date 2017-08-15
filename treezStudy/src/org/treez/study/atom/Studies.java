package org.treez.study.atom;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.attribute.attributeContainer.section.Section;
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

	//#region ATTRIBUTES

	//#region CONSTRUCTORS

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
		org.treez.core.atom.attribute.attributeContainer.Page dataPage = root.createPage("");
		String relativeHelpContextId = "studies";
		String absoluteHelpContextId = Activator.getInstance().getAbsoluteHelpContextId(relativeHelpContextId);
		Section studies = dataPage.createSection("studies", absoluteHelpContextId);
		studies.setLabel("");
		studies.createLabel("label", "This atom represents studies.");
		setModel(root);
	}

	/**
	 * Executes all child studies
	 */
	@Override
	public void execute(FocusChangingRefreshable refreshable) {
		this.treeViewRefreshable = refreshable;
		for (AbstractAtom<?> child : getChildAtoms()) {
			boolean isStudy = child instanceof Study;
			if (isStudy) {
				//get study
				Study childStudy = (Study) child;

				//run study
				runNonUiJob("Studies:execute study", (monitor) -> {
					childStudy.runStudy(refreshable, monitor);
					monitor.done();
				});

			}
		}
		//refresh();
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

	public Sweep createSweep(String name) {
		Sweep child = new Sweep(name);
		addChild(child);
		return child;
	}

	public Picking createPicking(String name) {
		Picking child = new Picking(name);
		addChild(child);
		return child;
	}

	public Sensitivity createSensitivity(String name) {
		Sensitivity child = new Sensitivity(name);
		addChild(child);
		return child;
	}

	public Probability createProbability(String name) {
		Probability child = new Probability(name);
		addChild(child);
		return child;
	}

	//#end region

	//#end region

}
