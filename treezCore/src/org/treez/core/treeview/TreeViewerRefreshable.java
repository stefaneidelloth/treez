package org.treez.core.treeview;

import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.base.AbstractAtom;

/**
 * Custom TreeView that implements Refreshable
 */
public class TreeViewerRefreshable extends TreeViewer implements FocusChangingRefreshable {

	//#region ATTRIBUTES

	/**
	 * A refreshable that is used to refresh dependent Refreshables, e.g. to update the Property view
	 */
	private FocusChangingRefreshable actionRefreshable;

	//#end region

	//#region CONSTRUCTORS

	public TreeViewerRefreshable(Composite parent, FocusChangingRefreshable actionRefreshable) {
		super(parent);
		this.actionRefreshable = actionRefreshable;
	}

	public TreeViewerRefreshable(Composite parent, FocusChangingRefreshable actionRefreshable, int style) {
		super(parent, style);
		this.actionRefreshable = actionRefreshable;

	}

	//#end region

	//#region METHODS

	@Override
	public synchronized void refresh() {
		//LOG.info("Refreshing tree view");

		super.refresh(true);
		if (actionRefreshable != null) {
			actionRefreshable.refresh();
		}

	}

	@Override
	public void setFocus(AbstractAtom<?> atomToFocus) {

		ITreeSelection oldSelection = this.getStructuredSelection();
		Object selectedElement = oldSelection.getFirstElement();

		boolean selectionChanged = !selectedElement.equals(atomToFocus);
		if (selectionChanged) {
			StructuredSelection selection = new StructuredSelection(atomToFocus);
			this.setSelection(selection);
		}

	}

	@Override
	public void setExpandedState(Object elementOrTreePath, boolean expanded) {
		super.setExpandedState(elementOrTreePath, expanded);
	}

	@Override
	public void handleTreeExpand(TreeEvent event) {

		Object element = event.item.getData();
		boolean isAdaptable = element instanceof Adaptable;
		if (isAdaptable) {
			Adaptable adaptable = (Adaptable) element;
			TreeNodeAdaption treeNodeAdaption = adaptable.createTreeNodeAdaption();
			treeNodeAdaption.preExpand();
		}

		super.handleTreeExpand(event);

	}

	//#end region

}
