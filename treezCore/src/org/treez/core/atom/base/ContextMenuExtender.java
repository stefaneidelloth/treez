package org.treez.core.atom.base;

import java.util.List;

import org.treez.core.treeview.TreeViewerRefreshable;

public interface ContextMenuExtender {

	/**
	 * Please note that the additional entries for the context menu have to implement IAction or IContributionItem
	 */
	List<Object> extendContextMenu(
			List<Object> existingContextMenuEntries,
			AbstractAtom<?> atom,
			TreeViewerRefreshable treeViewer);

}
