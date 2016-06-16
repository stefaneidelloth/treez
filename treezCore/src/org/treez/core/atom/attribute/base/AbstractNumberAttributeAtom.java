package org.treez.core.atom.attribute.base;

public abstract class AbstractNumberAttributeAtom<A extends AbstractNumberAttributeAtom<A>>
		extends
		AbstractAttributeAtom<A, Number> {

	//#region CONSTRUCTORS

	public AbstractNumberAttributeAtom(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 */
	public AbstractNumberAttributeAtom(AbstractNumberAttributeAtom<A> attributeAtomToCopy) {
		super(attributeAtomToCopy);
	}

	//#end region

}
