package org.treez.core.atom.variablefield;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAttributeAtomConstructionTest;

/**
 * Tests the StringItemVariableField
 */
public class StringItemVariableFieldTest
		extends
			AbstractAttributeAtomConstructionTest<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger
			.getLogger(StringItemVariableFieldTest.class);

	//#region SETUP

	/**
	 * Load entities from the database.
	 */
	@Override
	@Before
	public void createTestAtom() {

		// create test atom
		StringItemVariableField variableField = new StringItemVariableField(
				atomName);
		variableField.setLabel("MyVariable:");
		variableField.setItems("foo,baa");
		variableField.setTooltip("My tooltip");
		variableField.set("foo");
		atom = variableField;

	}

	//#end region

	//#region TESTS

	//#end region

}
