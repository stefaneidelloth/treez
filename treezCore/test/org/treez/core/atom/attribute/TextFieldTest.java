package org.treez.core.atom.attribute;

import org.apache.log4j.Logger;
import org.junit.Before;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class TextFieldTest extends AbstractAttributeAtomConstructionTest<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(TextFieldTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		TextField textField = new TextField(atomName);
		textField.setLabel("My Textfield:");
		textField.setDefaultValue("My default value");
		textField.setTooltip("My tooltip");
		textField.setPatternValidation(true);
		textField.setValidationPattern("\\d*");
		textField.setNumberValidation(true);
		textField.setMin("0");
		textField.setMax("100");
		textField.setErrorMessage("My invalid value message.");
		atom = textField;

	}

	//#end region

	//#region TESTS

	//#end region

}
