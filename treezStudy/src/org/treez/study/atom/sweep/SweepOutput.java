package org.treez.study.atom.sweep;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.atom.base.AtomCodeAdaption;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.data.output.OutputAtom;
import org.treez.study.Activator;

/**
 * The root atom for model outputs
 */
public class SweepOutput extends OutputAtom {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(SweepOutput.class);

	//#region ATTRIBUTES

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public SweepOutput(String name) {
		super(name);

	}

	//#end region

	//#region METHODS

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
		Image image = Activator.getImage("sweep.png");
		return image;
	}

	//#end region

	//#region ACCESSORS

	//#end region

}
