package org.treez.core.atom.attribute;

import org.apache.log4j.Logger;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class CheckBoxEnableTargetTest extends AbstractAttributeContainerAtomConstructonTest {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(CheckBoxEnableTargetTest.class);

	//#region SETUP

	@Override
	public void createTestAtom() {
		//create test atom
		atom = new CheckBoxEnableTarget(atomName, false, atomName);
	}

	//#end region

	//#region TESTS

	//#end region

}
