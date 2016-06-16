package org.treez.core.atom.attribute.base;

public abstract class AbstractBooleanAttributeAtom<A extends AbstractBooleanAttributeAtom<A>>
		extends
		AbstractAttributeAtom<A, Boolean> {

	//#region CONSTRUCTORS

	public AbstractBooleanAttributeAtom(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 */
	public AbstractBooleanAttributeAtom(AbstractBooleanAttributeAtom<A> attributeAtomToCopy) {
		super(attributeAtomToCopy);
	}

	//#end region

}
