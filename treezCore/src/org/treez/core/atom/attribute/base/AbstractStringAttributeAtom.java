package org.treez.core.atom.attribute.base;

public abstract class AbstractStringAttributeAtom<A extends AbstractStringAttributeAtom<A>>
		extends
		AbstractAttributeAtom<A, String> {

	//#region CONSTRUCTORS

	public AbstractStringAttributeAtom(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 */
	public AbstractStringAttributeAtom(AbstractStringAttributeAtom<A> attributeAtomToCopy) {
		super(attributeAtomToCopy);
	}

	//#end region

}
