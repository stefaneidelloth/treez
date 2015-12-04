package org.treez.model.atom.genericInput;

import org.treez.core.adaptable.CodeContainer;
import org.treez.core.atom.adjustable.AdjustableAtomCodeAdaption;
import org.treez.core.scripting.ScriptType;

/**
 * @author eis
 *
 */
public class GenericInputModelCodeAdaption extends AdjustableAtomCodeAdaption {

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 * 
	 * @param atom
	 */
	public GenericInputModelCodeAdaption(GenericInputModel atom) {
		super(atom);
	}

	//#end region

	//#region METHODS

	/**
	 * Builds the code for setting attribute values of the atom. Might be
	 * overridden by inheriting classes.
	 * 
	 * @return
	 */
	@Override
	protected CodeContainer buildCodeContainerForAttributes() {

		// Initialize the model if required
		//GenericInputModel adjustableAtom = (GenericInputModel) atom;

		CodeContainer codeContainer = new CodeContainer(ScriptType.JAVA);
		return codeContainer;

	}

	//#end region

}
