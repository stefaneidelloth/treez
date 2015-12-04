package org.treez.core.atom.attribute;

import org.apache.log4j.Logger;
import org.junit.Before;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class ComboBoxTest extends AbstractAttributeAtomConstructionTest<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(ComboBoxTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		ComboBox comboBox = new ComboBox(atomName);
		comboBox.setLabel("My combo box:");
		comboBox.setTooltip("My combo box tooltip");
		comboBox.setItems("item1,item2,item3");
		comboBox.setDefaultValue("item1");
		atom = comboBox;
	}

	//#end region

	//#region TESTS

	//#end region

}
