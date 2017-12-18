package org.treez.core.atom.attribute.checkBox;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.treez.core.atom.attribute.AbstractAttributeContainerAtomConstructonTest;
import org.treez.core.atom.attribute.checkBox.CheckBoxEnableTarget;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class CheckBoxEnableTargetTest extends AbstractAttributeContainerAtomConstructonTest {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(CheckBoxEnableTargetTest.class);

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
