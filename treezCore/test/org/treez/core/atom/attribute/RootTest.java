package org.treez.core.atom.attribute;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class RootTest extends AbstractAbstractAtomTest {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(RootTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		atom = new AttributeRoot(atomName);
	}

	//#end region

	//#region TESTS

	//#end region

}
