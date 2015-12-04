package org.treez.core.treeview;

import org.apache.log4j.Logger;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;

/**
 * A document listener for the tree view
 *
 */
public class TreeViewDocumentListener implements IDocumentListener {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger
			.getLogger(TreeViewDocumentListener.class);

	//#region ATTRIBUTES

	/**
	 * The tree view provider whose tree viewer is observed
	 */
	private TreeViewProvider treeViewProvider;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 * 
	 * @param treeViewProvider
	 */
	TreeViewDocumentListener(TreeViewProvider treeViewProvider) {
		this.treeViewProvider = treeViewProvider;
	}

	//#end region

	//#region METHODS

	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
		treeViewProvider.getTreeViewCodeConverter().checkActiveDocumentAndBuildTreeFromCode();
	}

	@Override
	public void documentChanged(org.eclipse.jface.text.DocumentEvent event) {
		//not used here

	}

	//#end region

}
