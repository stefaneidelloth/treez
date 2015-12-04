package org.treez.core.treeview;

/**
 * Translates a tree view to code and vice versa
 *
 */
public interface TreeViewCodeConverter {

	/**
	 * Checks the currently active document and builds the tree content
	 * from the code of the document
	 */
	void checkActiveDocumentAndBuildTreeFromCode();

	/**
	 * Checks the currently active document and builds code from
	 * the tree content
	 */
	void checkActiveDoumentAndBuildCodeFromTree();

	

}
