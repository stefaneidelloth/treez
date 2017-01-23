package org.treez.results.atom.page;

import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAbstractAtomTest;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class PageTest extends AbstractAbstractAtomTest {

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
