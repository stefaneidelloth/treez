package org.treez.views.tree;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.TreeItem;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.treeview.TreeViewerRefreshable;

/**
 * A MenuManager for the context menu of the tree nodes. It only allows own contributions to the context menu, where the
 * id starts with org.treez. Contributions from other plugins that do not start with de.treez are filtered out.
 */
public class TreeNodeMenuManager extends MenuManager {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(TreeNodeMenuManager.class);

	//#region ATTRIBUTES

	/**
	 * The TreeViewer this MenuManager belongs to
	 */
	private TreeViewerRefreshable treeViewer;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param treeViewer
	 */
	public TreeNodeMenuManager(TreeViewerRefreshable treeViewer) {
		super();
		this.treeViewer = treeViewer;
		this.setRemoveAllWhenShown(true);
		IMenuListener menuListener = createMenuListener();
		this.addMenuListener(menuListener);
	}

	//#end region

	//#region METHODS

	/**
	 * Creates the menu listener
	 *
	 * @return
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private IMenuListener createMenuListener() {
		IMenuListener menuListener = new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				//get current adaptable
				TreeItem[] treeItems = treeViewer.getTree().getSelection();
				TreeItem treeItem = treeItems[0];
				Adaptable adaptable = (Adaptable) treeItem.getData();

				//delete the old content of the context menu
				manager.removeAll();

				//let the tree node of the adaptable fill the context menu
				TreeNodeAdaption treeNode = adaptable.createTreeNodeAdaption();
				try {
					treeNode.fillContextMenu(treeViewer, manager);
				} catch (Exception exception) {
					String message = "Could not create context menu for " + treeNode.getTreePath();
					sysLog.error(message, exception);
				}
			}
		};
		return menuListener;
	}

	/**
	 * Only allows own contributions to the context menu, where the id starts with org.treez.
	 *
	 * @see org.eclipse.jface.action.ContributionManager#getItems()
	 */
	@Override
	public IContributionItem[] getItems() {

		IContributionItem[] items = super.getItems();
		List<IContributionItem> filteredItems = new ArrayList<IContributionItem>();
		for (IContributionItem item : items) {

			//sysLog.debug("context menu item:" + item.getId());

			if (item != null && item.getId() != null && item.getId().startsWith("org.treez")) {

				filteredItems.add(item);
			}
		}

		items = new IContributionItem[filteredItems.size()];
		return filteredItems.toArray(items);
	}

	//#end region

}
