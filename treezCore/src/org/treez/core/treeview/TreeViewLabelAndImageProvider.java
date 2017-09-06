package org.treez.core.treeview;

import java.util.Objects;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.adaptable.TreeNodeAdaption;

/**
 * Provides a label and an image for the tree node (it just delegates the work to the tree node) *
 */
public class TreeViewLabelAndImageProvider extends LabelProvider {

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
	 * Transforms an adaptable to a TreeNodeAdaption
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
}
