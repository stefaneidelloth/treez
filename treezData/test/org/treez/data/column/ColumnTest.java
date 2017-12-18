package org.treez.data.column;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAbstractAtomTest;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class ColumnTest extends AbstractAbstractAtomTest {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(ColumnTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		atom = new Column(atomName);

	}

	//#end region

	//#region TESTS

	//#end region

}
