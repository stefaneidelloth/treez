package org.treez.results.atom.page;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAbstractAtomTest;
import org.treez.results.atom.page.Page;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class PageTest extends AbstractAbstractAtomTest {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(PageTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		atom = new Page(atomName);

	}

	//#end region

	//#region TESTS

	//#end region

}
