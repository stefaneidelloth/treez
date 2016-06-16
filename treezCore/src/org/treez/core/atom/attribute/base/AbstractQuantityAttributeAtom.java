package org.treez.core.atom.attribute.base;

import org.treez.core.quantity.Quantity;

public abstract class AbstractQuantityAttributeAtom<A extends AbstractQuantityAttributeAtom<A>>
		extends
		AbstractAttributeAtom<A, Quantity> {

	//#region CONSTRUCTORS

	public AbstractQuantityAttributeAtom(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 */
	public AbstractQuantityAttributeAtom(AbstractQuantityAttributeAtom<A> attributeAtomToCopy) {
		super(attributeAtomToCopy);
	}

	//#end region

}
