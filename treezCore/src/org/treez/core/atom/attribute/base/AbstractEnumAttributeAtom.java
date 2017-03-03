package org.treez.core.atom.attribute.base;

public abstract class AbstractEnumAttributeAtom<A extends AbstractEnumAttributeAtom<A, E>, E extends Enum<E>>
		extends
		AbstractAttributeAtom<A, E> {

	//#region CONSTRUCTORS

	public AbstractEnumAttributeAtom(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 */
	public AbstractEnumAttributeAtom(AbstractEnumAttributeAtom<A, E> attributeAtomToCopy) {
		super(attributeAtomToCopy);
	}

	//#end region

}
