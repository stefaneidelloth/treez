package org.treez.core.atom.list;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAbstractAtomTest;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class TreezListAtomTest extends AbstractAbstractAtomTest {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(TreezListAtomTest.class);

	//#region SETUP

	/**
	 * Load entities from the database.
	 */
	@Override
	@Before
	public void createTestAtom() {
		//create test atom
		TreezListAtom treezListAtom = new TreezListAtom(atomName);
		treezListAtom.addRow("row1");
		treezListAtom.addRow("row2");
		atom = treezListAtom;
	}

	//#end region

	//#region TESTS

	//#end region

}
