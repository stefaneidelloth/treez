package org.treez.core.atom.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.treeview.TreeViewerRefreshable;

/**
 * TreeNodeAdaption for the AbstractAtom
 */
public class AtomTreeNodeAdaption implements TreeNodeAdaption {

	private static final Logger LOG = Logger.getLogger(AtomTreeNodeAdaption.class);

	//#region ATTRIBUTES

	/**
	 * The adaptable that is adapted
	 */
	protected AbstractAtom<?> atomAdaptable;

	//#end region

	//#region CONSTRUCTORS

	public AtomTreeNodeAdaption(AbstractAtom<?> correspondingAtomAdaptable) {
		this.atomAdaptable = correspondingAtomAdaptable;
	}

	//#end region

	//#region METHODS

	/**
	 * Returns the tree path for this AtomTreeNodeAdaption
	 */
	@Override
	public String getTreePath() {
		String path = getName();

		TreeNodeAdaption parentNode = getParentNode();
		if (parentNode != null) {
			Adaptable parent = parentNode.getAdaptable();
			String parentName = parent.createTreeNodeAdaption().getName();
			if (!parentName.equals("invisibleRoot")) {
				path = parent.createTreeNodeAdaption().getTreePath() + "." + path;
			}
		}
		return path;
	}

	/**
	 * Delete this abstract atom and all its children
	 */
	@Override
	public void delete() {

		//delete children
		for (TreeNodeAdaption childNode : getChildren()) {
			childNode.delete();
		}

		//get parent
		AbstractAtom<?> parent = (AbstractAtom<?>) getParent().getAdaptable();

		//delete self
		parent.createTreeNodeAdaption().removeChild(this);
	}

	/**
	 * Remove a child node and its corresponding atom
	 *
	 * @param childNode
	 */
	@Override
	public void removeChild(TreeNodeAdaption childNode) {
		childNode.setParent(null);
		atomAdaptable.children.remove(childNode.getAdaptable());
	}

	/**
	 * Implements the method TreeNodeAdaption.fillContextMenu(...) using the method createContextMenuActions(...) of the
	 * corresponding AbstractAtom
	 */
	@Override
	public void fillContextMenu(TreeViewerRefreshable treeViewerRefreshable, IMenuManager manager) {
		List<Object> items = atomAdaptable.createContextMenuActions(treeViewerRefreshable);
		for (Object item : items) {

			//try to add item as IAction
			boolean continueTrials = true;
			try {
				IAction action = (IAction) item;
				manager.add(action);
				continueTrials = false;
			} catch (ClassCastException exception) {
				//do nothing
				//LOG.info("", exception);
			}

			if (continueTrials) {
				//try to add item as contribution (e.g. separator)
				try {
					IContributionItem contribution = (IContributionItem) item;
					manager.add(contribution);
					continueTrials = false;
				} catch (ClassCastException exception) {
					//do nothing
					//LOG.info("", exception);
				}

				if (continueTrials) {
					//throw exception if it did not work
					String message = "The menu entry has to be IAction or IContributionItem but is "
							+ item.getClass().getSimpleName();
					LOG.error(message);
					throw new IllegalArgumentException(message);
				}

			}

		}
	}

	/**
	 * Expands the tree node in the tree viewer
	 *
	 * @param treeViewer
	 */
	@Override
	public void expand(TreeViewerRefreshable treeViewer) {

		treeViewer.setExpandedState(getAdaptable(), true);

		/*
		TreeNodeAdaption treeNode = getAdaptable().createTreeNodeAdaption();
		String itemName = treeNode.getName();
		List<TreeNodeAdaption> childNodes = treeNode.getChildren();
		LOG.info("Expanding " + itemName
				+ " with following children ---------------------:");
		for (TreeNodeAdaption childNode : childNodes) {
			LOG.info(childNode.getName());
		}
		LOG.info(
				"-------------------------------------------------------------------------");
		*/

	}

	@Override
	public void preExpand() {
		//This default implementation does nothing
	}

	/**
	 * Get the parent tree node adaption
	 *
	 * @return
	 */
	private TreeNodeAdaption getParentNode() {
		AbstractAtom<?> parentAtom = atomAdaptable.getParentAtom();
		if (parentAtom != null) {
			return parentAtom.createTreeNodeAdaption();
		} else {
			return null;
		}
	}

	/**
	 * Gets a new name for the next child to be created
	 *
	 * @param atom
	 * @param defaultName
	 * @return
	 */
	public static String createChildNameStartingWith(AbstractAtom<?> atom, String defaultName) {

		NameAndNumber currentNameAndNumber = new NameAndNumber(defaultName, 0);
		NameAndNumber nextNameAndNumber;
		boolean goOn = true;
		while (goOn) {
			nextNameAndNumber = getNextDummyChildName(atom, currentNameAndNumber);
			boolean currentNameIsOk = nextNameAndNumber.equals(currentNameAndNumber);
			if (currentNameIsOk) {
				goOn = false;
			}
			currentNameAndNumber = nextNameAndNumber;
		}

		String fullName = currentNameAndNumber.getFullName();
		return fullName;
	}

	/**
	 * Checks if a child atom with a name that corresponds to the given NameAndNumber already exists and returns a new
	 * NameAndNumber
	 *
	 * @param atom
	 * @param start
	 * @return
	 */
	private static NameAndNumber getNextDummyChildName(AbstractAtom<?> atom, NameAndNumber start) {
		NameAndNumber next = start.copy();
		boolean childWithSameNameAlreadyExists = false;

		String currentName = start.getFullName();
		List<TreeNodeAdaption> existingNodes = atom.createTreeNodeAdaption().getChildren();
		for (TreeNodeAdaption treeNodeAdaption : existingNodes) {
			String childName = treeNodeAdaption.getName();
			boolean namesAreEqual = currentName.equals(childName);
			if (namesAreEqual) {
				childWithSameNameAlreadyExists = true;
				break;
			}
		}

		if (childWithSameNameAlreadyExists) {
			//increase number to generate a new name
			next.increaseNumber();
		}
		return next;
	}

	//#end region

	//#region ACCESSORS

	//#region basic attributes

	/**
	 * Implements TreeNodeAdaption.getName() by returning the name of the corresponding AbstractAtom
	 */
	@Override
	public String getName() {
		return atomAdaptable.getName();
	}

	/**
	 * Implements TreeNodeAdaption.setName() by setting the name of the corresponding AbstractAtom. (In order to be able
	 * to identify an AbstractAtom<?> by its tree path, this name should only be used once for all children of the
	 * parent AbstractAtom.)
	 */
	@Override
	public void setName(String name) {
		atomAdaptable.setName(name);
	}

	/**
	 * Returns the corresponding AbstractAtom<?> for this AtomTreeNodeAdaption
	 */
	@Override
	public Adaptable getAdaptable() {
		return atomAdaptable;
	}

	@Override
	public String getLabel() {
		return atomAdaptable.getName();
	}

	@Override
	public Image getImage() {
		return atomAdaptable.provideImage();
	}

	//#end region

	//#region parent

	@Override
	public boolean hasParent() {
		TreeNodeAdaption parentNode = getParentNode();
		if (parentNode == null) {
			return false;
		}
		String parentName = parentNode.getName();
		if (parentName != null) {
			if (parentName.equals("invisibleRoot")) {
				return false;
			}
		}

		return true;
	}

	/**
	 * returns the tree note adaption of the parent atom
	 */
	@Override
	public TreeNodeAdaption getParent() {
		return getParentNode();
	}

	/**
	 * Sets the parent atom with the given parent tree node adaption
	 */
	@Override
	public void setParent(TreeNodeAdaption parent) {
		if (parent != null) {
			atomAdaptable.parentAtom = (AbstractAtom<?>) parent.getAdaptable();
		}
	}

	/**
	 * Sets the parent adaptable with the given atom
	 *
	 * @param parentAtom
	 */
	public void setParentByAtom(AbstractAtom<?> parentAtom) {
		if (parentAtom != null) {
			atomAdaptable.parentAtom = parentAtom;
		}
	}

	//#end region

	//#region children

	@Override
	public boolean hasChildren() {
		Integer size;
		size = atomAdaptable.children.size();
		boolean hasChilds = (size > 0);
		return hasChilds;
	}

	@Override
	public ArrayList<TreeNodeAdaption> getChildren() {
		ArrayList<TreeNodeAdaption> treeNodeArray = new ArrayList<>();
		TreeNodeAdaption treeNodeAdaption;
		for (Adaptable child : atomAdaptable.children) {
			treeNodeAdaption = child.createTreeNodeAdaption();
			treeNodeArray.add(treeNodeAdaption);
		}
		return treeNodeArray;
	}

	//#end region

	//#end region

}
