package org.treez.core.treeview.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.treez.core.treeview.TreeViewerRefreshable;

/**
 * Represents an Action for a context menu. It executes the given Runnable and
 * refreshes the given TreeViewerRefreshable, e.g. a TreeViewer.
 */
public class TreeViewerAction extends Action {

	//#region ATTRIBUTES

	/**
	 * The TreeViewerRefreshable this action belongs to
	 */
	protected TreeViewerRefreshable treeViewerRefreshable;

	protected Runnable runnable;

	//#end region

	//#region CONSTRUCTORS

	public TreeViewerAction(String label, Image image,
			TreeViewerRefreshable treeViewerRefreshable, Runnable runnable) {
		this.treeViewerRefreshable = treeViewerRefreshable;
		this.runnable = runnable;
		this.setText(label);
		this.setToolTipText(label);
		this.setId("org.treez.core.items");
		setImage(image);
	}

	//#end region

	//#region METHODS

	/**
	 * Sets the image for this action
	 *
	 * @param image
	 */
	protected void setImage(Image image) {
		ImageDescriptor imageDescriptor = ImageDescriptor
				.createFromImage(image);
		setImageDescriptor(imageDescriptor);
	}

	/**
	 * Calls the performActiom method and updates the tree viewer afterwards.
	 */
	@Override
	public void run() {
		if (runnable != null) {
			runnable.run();
			treeViewerRefreshable.refresh();
		}
	}

	//#end region

}
