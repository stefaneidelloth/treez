package org.treez.results.atom.data;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.base.EmptyControlAdaption;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.AddChildAtomTreeViewerAction;
import org.treez.data.output.OutputAtom;
import org.treez.data.table.nebula.Table;
import org.treez.results.Activator;
import org.treez.results.atom.probe.AbstractProbe;
import org.treez.results.atom.probe.PickingProbe;
import org.treez.results.atom.probe.ProbabilityProbe;
import org.treez.results.atom.probe.SensitivityProbe;
import org.treez.results.atom.probe.SweepProbe;

/**
 * Represents a data atom: parent atom for tables, columns ect.
 */
public class Data extends AdjustableAtom {

	//#region ATTRIBUTES

	//#region CONSTRUCTORS

	public Data(String name) {
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
	public AbstractControlAdaption createControlAdaption(
			Composite parent,
			FocusChangingRefreshable treeViewRefreshable) {
		this.treeViewRefreshable = treeViewRefreshable;
		return new EmptyControlAdaption(parent, this, "This atom represents data.");
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("data.png");
	}

	/**
	 * Creates the context menu actions for this atom
	 */
	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {

		//table
		Action addTable = new AddChildAtomTreeViewerAction(
				Table.class,
				"table",
				org.treez.data.Activator.getImage("table.png"),
				this,
				treeViewer);
		actions.add(addTable);

		//sweep probe
		Image sweepProbeBaseImage = Activator.getImage("sweep.png");
		Image sweepProbeImage = org.treez.core.Activator.getOverlayImageStatic(sweepProbeBaseImage, "probe.png");

		Action addSweepProbe = new AddChildAtomTreeViewerAction(
				SweepProbe.class,
				"sweepProbe",
				sweepProbeImage,
				this,
				treeViewer);
		actions.add(addSweepProbe);

		//picking probe
		Image pickingProbeBaseImage = Activator.getImage("picking.png");
		Image pickingProbeImage = org.treez.core.Activator.getOverlayImageStatic(pickingProbeBaseImage, "probe.png");

		Action addPickingProbe = new AddChildAtomTreeViewerAction(
				PickingProbe.class,
				"pickingProbe",
				pickingProbeImage,
				this,
				treeViewer);
		actions.add(addPickingProbe);

		//sensitivity probe
		Image sensitivityProbeBaseImage = Activator.getImage("sensitivity.png");
		Image sensitivityProbeImage = org.treez.core.Activator.getOverlayImageStatic(sensitivityProbeBaseImage,
				"probe.png");

		Action addSensitivityProbe = new AddChildAtomTreeViewerAction(
				SensitivityProbe.class,
				"sensitivityProbe",
				sensitivityProbeImage,
				this,
				treeViewer);
		actions.add(addSensitivityProbe);

		//probability probe
		Image probabilityProbeBaseImage = Activator.getImage("probability.png");
		Image probabilityProbeImage = org.treez.core.Activator.getOverlayImageStatic(probabilityProbeBaseImage,
				"probe.png");

		Action addProbabilityProbe = new AddChildAtomTreeViewerAction(
				ProbabilityProbe.class,
				"probabilityProbe",
				probabilityProbeImage,
				this,
				treeViewer);
		actions.add(addProbabilityProbe);

		return actions;
	}

	@Override
	public void execute(FocusChangingRefreshable treeViewerRefreshable) {
		treeViewRefreshable = treeViewerRefreshable;
		executeChildren(AbstractProbe.class, treeViewRefreshable);
	}

	//#region CREATE CHILD ATOMS

	/**
	 * Creates a Table child
	 */
	public Table createTable(String name) {
		Table child = new Table(name);
		addChild(child);
		return child;
	}

	/**
	 * Creates an OutputAtom child
	 *
	 * @param name
	 * @return
	 */
	public OutputAtom createOutputAtom(String name) {
		OutputAtom output = new OutputAtom(name);
		addChild(output);
		return output;
	}

	/**
	 * Creates a SweepProbe child
	 *
	 * @param name
	 * @return
	 */
	public SweepProbe createSweepProbe(String name) {
		SweepProbe sweepProbe = new SweepProbe(name);
		addChild(sweepProbe);
		return sweepProbe;
	}

	/**
	 * Creates a PickingProbe child
	 *
	 * @param name
	 * @return
	 */
	public PickingProbe createPickingProbe(String name) {
		PickingProbe pickingProbe = new PickingProbe(name);
		addChild(pickingProbe);
		return pickingProbe;
	}

	/**
	 * Creates a SensitivityProbe child
	 *
	 * @param name
	 * @return
	 */
	public SensitivityProbe createSensitivityProbe(String name) {
		SensitivityProbe sensitivityProbe = new SensitivityProbe(name);
		addChild(sensitivityProbe);
		return sensitivityProbe;
	}

	/**
	 * Creates a ProbabilityProbe child
	 *
	 * @param name
	 * @return
	 */
	public ProbabilityProbe createProbabilityProbe(String name) {
		ProbabilityProbe probabilityProbe = new ProbabilityProbe(name);
		addChild(probabilityProbe);
		return probabilityProbe;
	}

	//#end region

	//#end region

}
