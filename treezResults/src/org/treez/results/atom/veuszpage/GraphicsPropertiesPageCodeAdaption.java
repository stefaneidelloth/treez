package org.treez.results.atom.veuszpage;

import org.treez.core.adaptable.CodeContainer;
import org.treez.core.atom.adjustable.AdjustableAtomCodeAdaption;
import org.treez.core.scripting.ScriptType;

/**
 * Code adaption for GraphicsPropertiesPage
 */
public class GraphicsPropertiesPageCodeAdaption extends AdjustableAtomCodeAdaption {

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param atom
	 */
	public GraphicsPropertiesPageCodeAdaption(GraphicsPropertiesPage atom) {
		super(atom);
	}

	//#end region

	//#region METHODS

	/**
	 * Builds the code for setting attribute values of the atom. Might be overridden by inheriting classes.
	 *
	 * @return
	 */
	@Override
	protected CodeContainer buildCodeContainerForAttributes() {

		// Initialize the model if required
		//VeuszPropertiesPage veuszPropertiesPage = (VeuszPropertiesPage) atom;

		CodeContainer codeContainer = new CodeContainer(ScriptType.JAVA);
		return codeContainer;

	}

	//#end region

}
