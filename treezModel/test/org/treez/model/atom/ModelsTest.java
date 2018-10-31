package org.treez.model.atom;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAbstractAtomTest;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class ModelsTest extends AbstractAbstractAtomTest {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(ModelsTest.class);

	//#region SETUP

	/**
	 * Load entities from the database.
	 */
	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		atom = new Models(atomName);

	}

	//#end region

	//#region TESTS

	//#end region

}
