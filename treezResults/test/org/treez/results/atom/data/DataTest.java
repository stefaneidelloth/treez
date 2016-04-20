package org.treez.results.atom.data;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAbstractAtomTest;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class DataTest extends AbstractAbstractAtomTest {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DataTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		atom = new Data(atomName);

	}

	//#end region

	//#region TESTS

	//#end region

}
