package org.treez.core.treeview;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.utils.Utils;

/**
 * The content provider is responsible for providing objects to the tree view.
 */
public class TreeViewContentProvider implements ITreeContentProvider {

	private static final Logger LOG = Logger
			.getLogger(TreeViewContentProvider.class);

	//#region ATTRIBUTES

	/**
	 * The invisible root
	 */
	private Adaptable invisibleRoot = null;

	/**
	 * The root
	 */
	private Adaptable root = null;

	/**
	 * The class name(s) of the objects that are shown in the tree. This can be
	 * used to filter the elements in a model to only show nodes of a particular
	 * class (and their parent nodes). Set targetClassName to null if you want
	 * to show all classes. Use comma separated class names if you want to show
	 * several classes.
	 */
	private String wantedTypeNames;

	//#end region

	//#region CONSTRUCTORS

	public TreeViewContentProvider(String targetClassNames) {
		this.invisibleRoot = new AttributeRoot("invisibleRoot");
		this.wantedTypeNames = targetClassNames;
	}

	//#end region

	//#region METHODS

	@Override
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		//LOG.debug("input changed");
	}

	@Override
	public void dispose() {
		//do nothing
	}

	@Override
	public Object[] getElements(Object parent) {
		if (parent instanceof Adaptable) {
			return getChildren(parent);
		} else {
			Object[] rootElements = new Object[1];
			rootElements[0] = invisibleRoot;
			return rootElements;
		}
	}

	@Override
	public Object getParent(Object child) {
		if (child instanceof Adaptable) {
			TreeNodeAdaption childNode = toTreeNode(child);
			TreeNodeAdaption parentNode = childNode.getParent();
			if (parentNode != null) {
				Adaptable parent = parentNode.getAdaptable();
				return parent;
			} else {
				return null;
			}
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object parent) {
		if (parent instanceof Adaptable) {

			TreeNodeAdaption parentNode = toTreeNode(parent);
			List<TreeNodeAdaption> children = parentNode.getChildren();

			//get list of children to show
			List<Object> childrenToShow = new ArrayList<>();

			for (int k = 0; k < children.size(); k++) {
				TreeNodeAdaption childNode = children.get(k);
				Adaptable childAdaptable = childNode.getAdaptable();

				try {
					AbstractAtom atom = (AbstractAtom) childAdaptable;
					boolean showChild = checkIfAdaptableShouldBeShown(atom);
					if (showChild) {
						childrenToShow.add(childAdaptable);
					}
				} catch (ClassCastException e) {
					LOG.warn(
							"Could not display a node of following type because it is not known as AbstractAtom: "
									+ childAdaptable.getClass()
											.getSimpleName());
				}

			}

			//transform list to array
			Object[] childObjectsToDisplay = childrenToShow.toArray();

			return childObjectsToDisplay;

		} else {
			return new Object[0];
		}
	}

	/**
	 * Checks if a given AbstractAtom is of the type that should be shown in the
	 * tree or if its children contain any child that has the wanted type
	 *
	 * @param atom
	 * @return
	 */
	private boolean checkIfAdaptableShouldBeShown(AbstractAtom atom) {

		boolean showChild = false;

		if (wantedTypeNames != null) {

			String[] wantedTypeNameArray = wantedTypeNames.split(",");
			for (String wantedTypeName : wantedTypeNameArray) {
				//check if child has the wanted type
				boolean hasWantedType = Utils.checkIfHasWantedType(atom,
						wantedTypeName);
				if (hasWantedType) {
					showChild = true;
				}

				//check if the child includes sub children with the wanted type
				boolean containsWantedType = atom
						.containsChildOfType(wantedTypeName);
				if (containsWantedType) {
					showChild = true;
				}
			}

			return showChild;
		} else {
			return true;
		}
	}

	@Override
	public boolean hasChildren(Object parent) {
		if (parent instanceof Adaptable) {

			boolean childrenExist = toTreeNode(parent).hasChildren();
			if (childrenExist) {
				//check if one of the children has the wanted type
				List<TreeNodeAdaption> children = toTreeNode(parent)
						.getChildren();
				for (TreeNodeAdaption treeNodeAdaption : children) {
					Adaptable childAdaptable = treeNodeAdaption.getAdaptable();

					try {
						AbstractAtom atom = (AbstractAtom) childAdaptable;
						boolean showChild = checkIfAdaptableShouldBeShown(atom);
						if (showChild) {
							return true;
						}

					} catch (ClassCastException e) {
						throw new IllegalStateException(
								"Could not check if node has children to show because of unknown type"
										+ childAdaptable.getClass()
												.getSimpleName());
					}
				}
			}
		}
		return false;
	}

	/**
	 * Transforms an adaptable to a TreeNodeAdaption
	 *
	 * @param object
	 * @return
	 */
	public static TreeNodeAdaption toTreeNode(Object object) {
		Objects.requireNonNull(object, "Object must not be null.");
		if (object instanceof Adaptable) {
			TreeNodeAdaption treeNodeAdaption = ((Adaptable) object)
					.createTreeNodeAdaption();
			return treeNodeAdaption;
		} else {
			throw new IllegalArgumentException(
					"Input object must implement adaptable");
		}
	}

	//#end region

	//#region ACCESSORS

	public Adaptable getRoot() {
		return root;
	}

	/**
	 * Returns the invisible root of the content provider
	 *
	 * @return
	 */
	public Adaptable getInvisibleRoot() {
		return invisibleRoot;
	}

	//#end region

}
