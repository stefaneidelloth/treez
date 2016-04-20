package org.treez.core.adaptable;

import org.eclipse.swt.widgets.Composite;
import org.treez.core.scripting.ScriptType;

/**
 * Root interface for all treez atoms. The interface shows that an adaptable can
 * have several appearances or "adaptions" (see interface Adaption). The
 * Adaption "TreeNodeAdaption" is the most important Adaption since treez is
 * organized with a tree structure.
 */
public interface Adaptable {

	/**
	 * Creates a tree node adaption. The tree node adaption represents the atom
	 * as a node in a tree structure.
	 */
	TreeNodeAdaption createTreeNodeAdaption();

	/**
	 * Creates a code adaption. The code adaption can be shown in a code view.
	 * Further more, the code adaption is able to create code that represents
	 * the atom.
	 */
	CodeAdaption createCodeAdaption(ScriptType scriptType);

	/**
	 * Creates a graphics adaption. The GraphicsAdaption can be shown in a
	 * graphics view
	 */
	GraphicsAdaption createGraphicsAdaption(Composite parent);

	/**
	 * Creates an AbstractControlAdaption on the given parent Composite. Before
	 * (re-) creating the ControlAdaption with this method you might want to
	 * clear old content of the parent composite.
	 */
	AbstractControlAdaption createControlAdaption(Composite parent,
			Refreshable refreshableTreeViewer);

}
