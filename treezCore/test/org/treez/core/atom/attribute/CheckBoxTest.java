package org.treez.core.atom.attribute;

import org.apache.log4j.Logger;
import org.junit.Before;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class CheckBoxTest extends AbstractAttributeAtomConstructionTest<Boolean> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(CheckBoxTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {
		//create test atom
		CheckBox checkBox = new CheckBox(atomName);
		checkBox.setLabel("My check box");
		checkBox.setTooltip("My check box tooltip");
		checkBox.setDefaultValue(true);
		atom = checkBox;
	}

	//#end region

	//#region TESTS

	//#end region

}
