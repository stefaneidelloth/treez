package org.treez.results.atom;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAbstractAtomTest;
import org.treez.results.atom.xy.Xy;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class XYTest extends AbstractAbstractAtomTest {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(XYTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		atom = new Xy(atomName);

	}

	//#end region

	//#region TESTS

	//#end region

}
