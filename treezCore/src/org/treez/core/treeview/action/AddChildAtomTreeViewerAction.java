package org.treez.core.treeview.action;

import org.eclipse.swt.graphics.Image;
import org.treez.core.Activator;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.utils.Utils;

/**
 * This action adds a new child atom of the given class
 */
public class AddChildAtomTreeViewerAction extends TreeViewerAction {

	//#region ATTRIBUTES

	/**
	 * The name prefix for the new child atoms
	 */
	private String namePrefix;

	/**
	 * The parent atom of the new child atom(s)
	 */
	private AbstractAtom parentAtom;

	/**
	 * The image for the new child atom (that will be decorated)
	 */
	private Image baseImage;

	/**
	 * The class of the new child(s) to add.
	 */
	private Class<? extends AbstractAtom> atomClass;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param namePrefix
	 * @param image
	 * @param parentAtom
	 * @param treeViewer
	 * @param atomClass
	 */
	public AddChildAtomTreeViewerAction(
			Class<? extends AbstractAtom> atomClass,
			String namePrefix,
			Image image,
			AbstractAtom parentAtom,
			TreeViewerRefreshable treeViewer) {

		super(createLabel(namePrefix), image, treeViewer, null);
		this.namePrefix = namePrefix;
		this.atomClass = atomClass;
		this.parentAtom = parentAtom;
		this.baseImage = image;
		createRunnable();
		createDecoratedImage();
	}

	/**
	 * Decorates the passed image with an add symbol
	 */
	private void createDecoratedImage() {
		Image decoratedImage = Activator.getOverlayImageStatic(baseImage, "add_decoration.png");
		setImage(decoratedImage);
	}

	//#end region

	//#region METHODS

	private void createRunnable() {
		runnable = () -> {
			parentAtom.createChildAtom(atomClass, namePrefix);
			parentAtom.createTreeNodeAdaption().expand(treeViewerRefreshable);
		};
	}

	/**
	 * Creates the action label with the given name prefix
	 *
	 * @param namePrefix
	 * @return
	 */
	private static String createLabel(String namePrefix) {
		String label = "Add " + Utils.firstToUpperCase(namePrefix);
		return label;
	}

	//#end region
}
