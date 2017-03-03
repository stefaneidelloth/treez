package org.treez.core.path;

import org.treez.core.atom.base.AbstractAtom;

public interface FilterDelegate {

	/**
	 * Returns true if the given atom should be included
	 */
	boolean include(AbstractAtom<?> object);

}
