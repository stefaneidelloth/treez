package org.treez.core.scripting;

import org.treez.core.atom.base.AbstractAtom;

/**
 * Scripting interface. A Scripting can execute the text document (script) that is currently active in Eclipse and
 * retrieve an AbstractAtom from the executed script. The type of script that can be interpreted depends on the
 * implementation of the interface.
 */
public interface Scripting {

	/**
	 * Executes / interprets the currently active text document (=script)
	 */
	void executeDocument();

	/**
	 * Retrieves an AbstractAtom from the interpreted script. Returns null if the AbstractAtom can not be retrieved.
	 *
	 * @return
	 */
	AbstractAtom getRoot();

}
