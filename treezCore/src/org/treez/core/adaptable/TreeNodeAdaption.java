package org.treez.core.adaptable;

import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Image;
import org.treez.core.treeview.TreeViewerRefreshable;

/**
 * The tree node adaption for the adaptable
 */
public interface TreeNodeAdaption extends Adaption {

	//#region METHODS

	/**
	 * Deletes this TreeNodeAdaption from its parent TreeNodeAdaption (including the corresponding adaptable).
	 */
	void delete();

	/**
	 * Removes a child TreeNodeAdaption from this TreeNodeAdaption. (A method for adding a child TreeNodeAdaption is not
	 * included in this general interface. Implementing classes might provide such a method.)
	 *
	 * @param child
	 */
	void removeChild(TreeNodeAdaption child);

	/**
	 * Expands the corresponding tree node in the given tree viewer. (This will be used to restore the expansion state
	 * of the tree.)
	 *
	 * @param treeViewer
	 */
	void expand(TreeViewerRefreshable treeViewer);

	/**
	 * This method is called by the tree viewer before the tree node is expanded
	 */
	void preExpand();

	/**
	 * Fills the context menu of the TreeNodeAdaption with for the given TreeViewer with the given IMenuManager.
	 *
	 * @param treeViewer
	 * @param manager
	 */
	void fillContextMenu(TreeViewerRefreshable treeViewer, IMenuManager manager);

	//#end region

	//#region ACCESORS

	//Basic Attributes

	/**
	 * Returns the name of this TreeNodeAdaption.
	 *
	 * @return
	 */
	String getName();

	/**
	 * Sets the name of this TreeNodeAdaption. In order to to be able to identify a TreeNodeAdaption by its name the
	 * name should only be used once for all children of the parent TreeNodeAdaption.
	 *
	 * @param name
	 */
	void setName(String name);

	/**
	 * Returns the label for showing this TreeNodeAdaption in a tree view. Depending on a specific implementation, the
	 * label might for example contain the name of this TreeNodeAdaption.)
	 *
	 * @return
	 */
	String getLabel();

	/**
	 * Returns the icon for showing this TreeNodeAdaption in a tree view
	 *
	 * @return
	 */
	Image getImage();

	/**
	 * Returns the absolute path of this TreeNodeAdaption in the containing tree.
	 *
	 * @return
	 */
	String getTreePath();

	//Parent

	/**
	 * Returns true if the parent TreeNodeAdaption is not null.
	 *
	 * @return
	 */
	boolean hasParent();

	/**
	 * Returns the parent TreeNodeAdaption. Returns null if no parent TreeNodeAdaption is specified.
	 *
	 * @return
	 */
	TreeNodeAdaption getParent();

	/**
	 * Sets the parent TreeNodeAdaption.
	 *
	 * @param parent
	 */
	void setParent(TreeNodeAdaption parent);

	//Children

	/**
	 * Returns true if this TreeNodeAdaption has child TreeNodeAdaptions.
	 *
	 * @return
	 */
	boolean hasChildren();

	/**
	 * Returns the child tree node adaptions. If no child TreeNodeAdaption exists an empty list is returned.
	 *
	 * @return
	 */
	List<TreeNodeAdaption> getChildren();

	//#end region

}
