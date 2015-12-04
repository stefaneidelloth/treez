package org.treez.core.adaptable;

import java.util.Optional;

/**
 * The code adaption for the adaptable.
 */
public interface CodeAdaption extends Adaption {

	//#region METHODS

	/**
	 * Crates the CodeContainer for the corresponding adaptable. The CodeContainer contains several parts of code that
	 * can be put together to get the code that represents the adaptable. The creation of the CodeContainer might depend
	 * on the given parent container, the atom this code adaption corresponding to and on the given injected child
	 * container.
	 *
	 * @param parentCodeContainer
	 * @param injectedChildCodeContainer
	 * @return
	 */
	CodeContainer buildCodeContainer(
			CodeContainer parentCodeContainer,
			Optional<CodeContainer> injectedChildCodeContainer);

	/**
	 * Creates the root code container
	 *
	 * @param className
	 * @return
	 */
	CodeContainer buildRootCodeContainer(String className);

	//#end region

}
