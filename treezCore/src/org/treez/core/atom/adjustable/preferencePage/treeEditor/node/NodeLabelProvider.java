package org.treez.core.atom.adjustable.preferencePage.treeEditor.node;

import java.util.Objects;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.adaptable.TreeNodeAdaption;

/**
 * Provides label and image for the tree nodes of the TreeEditor
 */
public class NodeLabelProvider extends ColumnLabelProvider {

	//#region METHODS

	@Override
	public String getText(Object object) {
		if (object instanceof Adaptable) {
			String label = toTreeNode(object).getLabel();
			return label;
		} else {
			//throw new IllegalArgumentException("object class:" + object.getClass().getName());
			return "error";
		}
	}

	@Override
	public Image getImage(Object object) {
		if (object instanceof Adaptable) {
			Image image = toTreeNode(object).getImage();
			return image;
		} else {
			return null;
		}
	}

	/**
	 * Transforms an adaptable to a TreeNodeAdaption (is used by the helper classes in this package)
	 *
	 * @param object
	 * @return
	 */
	public static TreeNodeAdaption toTreeNode(Object object) {
		Objects.requireNonNull(object, "Object must not be null.");
		if (object instanceof Adaptable) {
			TreeNodeAdaption treeNodeAdaption = ((Adaptable) object).createTreeNodeAdaption();
			return treeNodeAdaption;
		} else {
			throw new IllegalArgumentException("Input object must implement adaptable");
		}
	}

	//#end region
}
