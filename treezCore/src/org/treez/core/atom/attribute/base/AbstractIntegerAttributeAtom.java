package org.treez.core.atom.attribute.base;

public abstract class AbstractIntegerAttributeAtom<A extends AbstractIntegerAttributeAtom<A>>
		extends
		AbstractNumberAttributeAtom<A> {

	//#region CONSTRUCTORS

	public AbstractIntegerAttributeAtom(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 */
	public AbstractIntegerAttributeAtom(AbstractIntegerAttributeAtom<A> attributeAtomToCopy) {
		super(attributeAtomToCopy);
	}

	//#end region

}
