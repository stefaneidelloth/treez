package org.treez.core.atom.attribute.base;

import java.time.ZonedDateTime;

public abstract class AbstractZonedDateTimeAttributeAtom<A extends AbstractZonedDateTimeAttributeAtom<A>>
		extends
		AbstractAttributeAtom<A, ZonedDateTime> {

	//#region CONSTRUCTORS

	public AbstractZonedDateTimeAttributeAtom(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 */
	public AbstractZonedDateTimeAttributeAtom(AbstractZonedDateTimeAttributeAtom<A> attributeAtomToCopy) {
		super(attributeAtomToCopy);
	}

	//#end region

}
