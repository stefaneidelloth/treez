package org.treez.core.atom.attribute.comboBox;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAttributeAtomConstructionTest;
import org.treez.core.atom.attribute.comboBox.ComboBox;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class ComboBoxTest extends AbstractAttributeAtomConstructionTest<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(ComboBoxTest.class);

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
