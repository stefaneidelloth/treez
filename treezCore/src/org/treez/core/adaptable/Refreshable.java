package org.treez.core.adaptable;

import org.treez.core.atom.base.AbstractAtom;

/**
 * Represents something (e.g. a TreeViewer) that can be refreshed and that can
 * set the focus on a given atom
 */
public interface Refreshable {

	/**
	 * Refreshes this Refreshable
	 */
	void refresh();

	/**
	 * Sets the focus/selects the given atom
	 *
	 * @param atomToFocus
	 */
	void setFocus(AbstractAtom atomToFocus);

}
