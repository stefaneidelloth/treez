package org.treez.data.output;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.Activator;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.EmptyControlAdaption;
import org.treez.core.atom.base.AtomCodeAdaption;
import org.treez.core.atom.uisynchronizing.AbstractUiSynchronizingAtom;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.data.table.nebula.Table;

/**
 * Contains the output of a Model or Study
 */
public class OutputAtom extends AbstractUiSynchronizingAtom<OutputAtom> {

	//#region ATTRIBUTES

	/**
	 * The image of the source model whose output is represented by this root output.
	 */
	private Image baseImage;

	//#end region

	//#region CONSTRUCTORS

	public OutputAtom(String name) {
		super(name);
		expandedNodes = new ArrayList<String>();
		baseImage = provideDefaultBaseImage();
	}

	public OutputAtom(String name, Image baseImage) {
		super(name);
		expandedNodes = new ArrayList<String>();
		this.baseImage = baseImage;
	}

	/**
	 * Copy constructor
	 */
	private OutputAtom(OutputAtom rootOutputToCopy) {
		super(rootOutputToCopy);
		expandedNodes = rootOutputToCopy.expandedNodes;
	}

	//#end region

	//#region METHODS

	@Override
	public OutputAtom getThis() {
		return this;
	}

	@Override
	public OutputAtom copy() {
		return new OutputAtom(this);
	}

	@Override
	public AbstractControlAdaption createControlAdaption(
			Composite parent,
			FocusChangingRefreshable treeViewRefreshable) {
		this.treeViewRefreshable = treeViewRefreshable;
		//LOG.debug("get root control");
		return new EmptyControlAdaption(parent, this, "");
	}

	@Override
	public CodeAdaption createCodeAdaption(ScriptType scriptType) {
		return new AtomCodeAdaption(this);
	}

	@Override
	protected ArrayList<Object> createContextMenuActions(final TreeViewerRefreshable treeViewer) {

		ArrayList<Object> actions = new ArrayList<>();
		return actions;
	}

	@Override
	public Image provideImage() {
		Image image = Activator.getOverlayImageStatic(baseImage, "modelOutput.png");
		return image;
	}

	protected Image provideDefaultBaseImage() {
		Image image = Activator.getImage("modelOutput.png");
		return image;
	}

	//#region CREATE CHILD ATOMS

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
	 * Creates a Table child
	 */
	public Table createTable(String name) {
		Table table = new Table(name);
		addChild(table);
		return table;
	}

	//#end region

	//#end region

	//#region ACCESSORS

	/**
	 * Gets the expanded nodes that were saved in this root.
	 *
	 * @return
	 */
	@Override
	public ArrayList<String> getExpandedNodes() {
		return expandedNodes;
	}

	/**
	 * Sets the expanded nodes
	 *
	 * @param expandedNodesString
	 */
	@Override
	public void setExpandedNodes(String expandedNodesString) {
		ArrayList<String> expandedNodes = new ArrayList<>();
		String[] nodes = expandedNodesString.split(",");
		for (String node : nodes) {
			expandedNodes.add(node);
		}
		this.expandedNodes = expandedNodes;
	}

	//#end region

}
