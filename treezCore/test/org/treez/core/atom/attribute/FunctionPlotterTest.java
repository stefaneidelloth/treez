package org.treez.core.atom.attribute;

import org.apache.log4j.Logger;
import org.junit.Before;

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
	private static Logger sysLog = Logger.getLogger(FunctionPlotterTest.class);

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
