package org.treez.data.output;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.Activator;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.base.EmptyControlAdaption;
import org.treez.core.atom.base.AtomCodeAdaption;
import org.treez.core.atom.uisynchronizing.AbstractUiSynchronizingAtom;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;

/**
 * Contains the output of a Model or Study
 */
public class OutputAtom extends AbstractUiSynchronizingAtom {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(OutputAtom.class);

	//#region ATTRIBUTES

	/**
	 * The image of the source model whose output is represented by this root output.
	 */
	private Image baseImage;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public OutputAtom(String name) {
		super(name);
		expandedNodes = new ArrayList<String>();
		baseImage = provideDefaultBaseImage();
	}

	/**
	 * Constructor
	 *
	 * @param name
	 * @param baseImage
	 */
	public OutputAtom(String name, Image baseImage) {
		super(name);
		expandedNodes = new ArrayList<String>();
		this.baseImage = baseImage;
	}

	/**
	 * Copy constructor
	 *
	 * @param rootOutputToCopy
	 */
	private OutputAtom(OutputAtom rootOutputToCopy) {
		super(rootOutputToCopy);
		expandedNodes = rootOutputToCopy.expandedNodes;
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public OutputAtom copy() {
		return new OutputAtom(this);
	}

	//#end region

	/**
	 * Returns the control adaption for this atom
	 */
	@Override
	public AbstractControlAdaption createControlAdaption(Composite parent, Refreshable treeViewRefreshable) {
		this.treeViewRefreshable = treeViewRefreshable;
		//sysLog.debug("get root control");
		return new EmptyControlAdaption(parent, this, "");
	}

	@Override
	public CodeAdaption createCodeAdaption(ScriptType scriptType) {
		return new AtomCodeAdaption(this);
	}

	/**
	 * Creates the context menu actions
	 */
	@Override
	protected ArrayList<Object> createContextMenuActions(final TreeViewerRefreshable treeViewer) {

		ArrayList<Object> actions = new ArrayList<>();
		return actions;
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		Image image = Activator.getOverlayImageStatic(baseImage, "modelOutput.png");
		return image;
	}

	/**
	 * Provides an image to represent this atom
	 */

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
	 *
	 * @param name
	 * @return
	 */
	public org.treez.data.table.Table createTable(String name) {
		org.treez.data.table.Table table = new org.treez.data.table.Table(name);
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
