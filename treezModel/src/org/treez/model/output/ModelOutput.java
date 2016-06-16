package org.treez.model.output;

import org.treez.core.atom.base.AbstractAtom;

/**
 * Represents the output of a single model run.
 */
public interface ModelOutput {

	/**
	 * Returns the root atom of this ModelOutput
	 *
	 * @return
	 */
	AbstractAtom<?> getOutputAtom();

	/**
	 * Adds a child model output to this model output
	 *
	 * @param childModelOutput
	 */
	default void addChildOutput(ModelOutput childModelOutput) {
		AbstractAtom<?> childAtom = childModelOutput.getOutputAtom();
		AbstractAtom<?> rootAtom = getOutputAtom();
		rootAtom.addChild(childAtom);
	}

}
