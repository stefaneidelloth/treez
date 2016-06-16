package org.treez.core.atom.attribute.base;

public abstract class AbstractDoubleAttributeAtom<A extends AbstractDoubleAttributeAtom<A>>
		extends
		AbstractNumberAttributeAtom<A> {

	//#region CONSTRUCTORS

	public AbstractDoubleAttributeAtom(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 */
	public AbstractDoubleAttributeAtom(AbstractDoubleAttributeAtom<A> attributeAtomToCopy) {
		super(attributeAtomToCopy);
	}

	//#end region

}
