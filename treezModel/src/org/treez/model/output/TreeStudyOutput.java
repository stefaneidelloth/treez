package org.treez.model.output;

import org.treez.core.atom.base.AbstractAtom;

/**
 * Represents the complex output of a study where the ModelOutputs are organized
 * in a tree structure.
 *
 */
public interface TreeStudyOutput extends StudyOutput {

	/**
	 * Returns the root of the tree structure in which the ModelOutputs are
	 * organized
	 * 
	 * @return
	 */
	AbstractAtom getOutputRoot();

}
