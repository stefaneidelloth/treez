package org.treez.core.treeview;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.base.AbstractAtom;

/**
 * Custom TreeView that implements Refreshable
 */
public class TreeViewerRefreshable extends TreeViewer implements Refreshable {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(TreeViewer.class);

	//#region ATTRIBUTES

	/**
	 * A refreshable that is used to refresh dependent Refreshables, e.g. to
	 * update the Property view
	 */
	private Refreshable actionRefreshable;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param parent
	 */
	public TreeViewerRefreshable(Composite parent,
			Refreshable actionRefreshable) {
		super(parent);
		this.actionRefreshable = actionRefreshable;
	}

	/**
	 * Constructor
	 *
	 * @param parent
	 * @param style
	 */
	public TreeViewerRefreshable(Composite parent,
			Refreshable actionRefreshable, int style) {
		super(parent, style);
		this.actionRefreshable = actionRefreshable;
	}

	//#end region

	//#region METHODS

	@Override
	public synchronized void refresh() {
		//sysLog.info("Refreshing tree view");

		//		Display display = Display.getCurrent();
		//		if (display == null || display.isDisposed()) {
		//			return;
		//		}
		//
		//		display.syncExec(() -> super.refresh(true));
		super.refresh(true);
		if (actionRefreshable != null) {
			actionRefreshable.refresh();
		}

	}

	@Override
	public void setFocus(AbstractAtom atomToFocus) {
		StructuredSelection selection = new StructuredSelection(atomToFocus);
		this.setSelection(selection);
	}

	//#end region

}
