package org.treez.core.atom.attribute.attributeContainer;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.Activator;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.EmptyControlAdaption;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.base.AtomTreeNodeAdaption;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.TreeViewerAction;

/**
 * The root AttributeAtom of the model tree of the adjustable atom
 */
public class AttributeRoot extends AbstractAtom<AttributeRoot> {

	//#region CONSTRUCTORS

	public AttributeRoot(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 */
	private AttributeRoot(AttributeRoot rootToCopy) {
		super(rootToCopy);
	}

	//#end region

	//#region METHODS

	@Override
	protected AttributeRoot getThis() {
		return this;
	}

	//#region COPY

	@Override
	public AttributeRoot copy() {
		return new AttributeRoot(this);
	}

	//#end region

	/**
	 * Returns the control adaption for this atom
	 */
	@Override
	public AbstractControlAdaption createControlAdaption(
			Composite parent,
			FocusChangingRefreshable treeViewRefreshable) {
		//LOG.debug("get root control");
		return new EmptyControlAdaption(parent, this, "");
	}

	/**
	 * Creates the context menu actions
	 */
	@Override
	protected ArrayList<Object> createContextMenuActions(final TreeViewerRefreshable treeViewerRefreshable) {

		ArrayList<Object> actions = new ArrayList<>();

		actions.add(new TreeViewerAction(
				"Add Page",
				Activator.getImage("Page.png"),
				treeViewerRefreshable,
				() -> addPage(treeViewerRefreshable)));

		return actions;
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		Image image = Activator.getImage("root.png");
		return image;
	}

	/**
	 * Adds a new form page
	 *
	 * @param treeViewer
	 */
	void addPage(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this, "myPage");
		createPage(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	/**
	 * Creates a new form page
	 *
	 * @param name
	 * @return
	 */
	public Page createPage(String name) {
		Page page = new Page(name);
		addChild(page);
		return page;
	}

	/**
	 * Creates a page with given title
	 *
	 * @param name
	 * @param title
	 * @return
	 */
	public Page createPage(String name, String title) {
		Page page = createPage(name);
		page.setTitle(title);
		return page;
	}

	//#end region

}
