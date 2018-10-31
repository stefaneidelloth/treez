package org.treez.core.atom.attribute.plot;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAttributeAtomConstructionTest;
import org.treez.core.atom.attribute.plot.FunctionPlotter;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class FunctionPlotterTest
		extends
			AbstractAttributeAtomConstructionTest<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(FunctionPlotterTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {
		//create test atom
		FunctionPlotter functionPlotter = new FunctionPlotter(atomName);

		atom = functionPlotter;
	}

	//#end region

	//#region TESTS

	//#end region

}
