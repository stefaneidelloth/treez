package org.treez.core.scripting;

import org.treez.core.atom.base.AbstractAtom;

/**
 * This interface has to be implemented by the model classes that are created by
 * the treez users.
 * 
 *
 */
public abstract class ModelProvider {

	/**
	 * Creates the model root
	 * 
	 * @return
	 */
	public abstract AbstractAtom createModel();
}
