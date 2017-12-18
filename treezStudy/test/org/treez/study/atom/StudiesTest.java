package org.treez.study.atom;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAbstractAtomTest;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class StudiesTest extends AbstractAbstractAtomTest {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(StudiesTest.class);

	//#region SETUP

	/**
	 * Load entities from the database.
	 */
	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		atom = new Studies(atomName);

	}

	//#end region

	//#region TESTS

	//#end region

}
