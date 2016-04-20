package org.treez.core.treeview;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;

/**
 * A document listener for the tree view
 *
 */
public class TreeViewDocumentListener implements IDocumentListener {

	//#region ATTRIBUTES

	/**
	 * The tree view provider whose tree viewer is observed
	 */
	private TreeViewProvider treeViewProvider;

	//#end region

	//#region CONSTRUCTORS

	TreeViewDocumentListener(TreeViewProvider treeViewProvider) {
		this.treeViewProvider = treeViewProvider;
	}

	//#end region

	//#region METHODS

	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
		treeViewProvider.getTreeViewCodeConverter()
				.checkActiveDocumentAndBuildTreeFromCode();
	}

	@Override
	public void documentChanged(org.eclipse.jface.text.DocumentEvent event) {
		//not used here

	}

	//#end region

}
