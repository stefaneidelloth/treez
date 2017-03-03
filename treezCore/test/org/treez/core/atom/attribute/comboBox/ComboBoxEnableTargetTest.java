package org.treez.core.atom.attribute.comboBox;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAttributeContainerAtomConstructonTest;
import org.treez.core.atom.attribute.comboBox.ComboBoxEnableTarget;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class ComboBoxEnableTargetTest extends AbstractAttributeContainerAtomConstructonTest {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(ComboBoxEnableTargetTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {
		//create test atom
		atom = new ComboBoxEnableTarget(atomName, "false", atomName);

	}

	//#end region

	//#region TESTS

	//#end region

}
