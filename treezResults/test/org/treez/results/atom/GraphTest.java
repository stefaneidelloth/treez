package org.treez.results.atom;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAbstractAtomTest;
import org.treez.results.atom.graph.Graph;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class GraphTest extends AbstractAbstractAtomTest {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(GraphTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		atom = new Graph(atomName);

	}

	//#end region

	//#region TESTS

	//#end region

}
