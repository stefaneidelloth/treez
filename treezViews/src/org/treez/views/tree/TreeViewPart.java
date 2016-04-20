package org.treez.views.tree;

import java.util.Objects;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.ViewPart;
import org.treez.core.treeview.TreeViewCodeConverter;
import org.treez.core.treeview.TreeViewProvider;
import org.treez.core.treeview.TreezView;
import org.treez.core.treeview.action.ActionProviderRefreshable;

/**
 * This plugin provides a Tree View for the UGUI
 */
public class TreeViewPart extends ViewPart implements TreezView {

	private static final Logger LOG = Logger.getLogger(TreeViewPart.class);

	//#region ATTRIBUTES

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.treez.views.tree";

	/**
	 * Will provide the tree view for this view part
	 */
	private TreeViewProvider treeViewProvider;

	/**
	 * A composite for the content of this part view
	 */
	private Composite contentComposite;

	//#end region

	//#region CONSTRUCTORS

	public TreeViewPart() {}

	//#end region

	//#region METHODS

	/**
	 * This is a callback that allows to create a jface tree treeViewer and initialize it.
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	@Override
	public void createPartControl(Composite contentComposite) {

		//set content composite
		this.setContentComposite(contentComposite);

		//create a tree view using a tree view provider
		treeViewProvider = new TreeViewProvider(this);
		ActionProviderRefreshable treeViewActionProvider = new TreeViewActionProvider(this, treeViewProvider);
		TreeViewCodeConverter treeViewCodeConverter;
		try {
			treeViewCodeConverter = new JavaTreeViewCodeConverter(treeViewProvider);
		} catch (Exception exception) {
			String message = "Could not create treeViewCodeConverter";
			LOG.error(message, exception);
			return;
		}
		treeViewProvider.provideTreeView(null, treeViewActionProvider, treeViewCodeConverter);

	}

	@Override
	public IWorkbenchPartSite getSite() {
		return super.getSite();
	}

	@Override
	public IViewSite getViewSite() {
		return super.getViewSite();
	}

	/**
	 * Pass the focus request to the treeViewer's control.
	 */
	@Override
	public void setFocus() {
		treeViewProvider.setFocus();
	}

	//#end region

	//#region ACCESSORS

	/**
	 * Provides the content composite. The content composite has to be set before by createPartControl(Composite parent)
	 */
	@Override
	public Composite getContentComposite() {
		Objects.requireNonNull(contentComposite, "The content composite has not been set yet.");
		return contentComposite;
	}

	/**
	 * Sets the content composite
	 *
	 * @param contentComposite
	 */
	private void setContentComposite(Composite contentComposite) {
		this.contentComposite = contentComposite;
	}

	//#end region

}
