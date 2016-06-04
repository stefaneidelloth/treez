package org.treez.core.treeview.action;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.treez.core.Activator;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.variablefield.VariableField;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.utils.Utils;

/**
 * This action adds a new child atom of the given class. The class has to extend AbstractAtom and implement
 * VariableField to be able to set the background color.
 */
public class AddColoredChildAtomTreeViewerAction extends TreeViewerAction {

	//#region ATTRIBUTES

	private static final Color BACKGROUND_COLOR = new Color(null, 240, 245, 249);

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

	public AddColoredChildAtomTreeViewerAction(
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
			AbstractAtom newChild = parentAtom.createChildAtom(atomClass, namePrefix);
			boolean isVariableField = newChild instanceof VariableField<?>;
			if (isVariableField) {
				VariableField<?> fieldAtom = (VariableField<?>) newChild;
				fieldAtom.setBackgroundColor(BACKGROUND_COLOR);
			} else {
				throw new IllegalStateException("This class must only be used for VariableFields");
			}

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
