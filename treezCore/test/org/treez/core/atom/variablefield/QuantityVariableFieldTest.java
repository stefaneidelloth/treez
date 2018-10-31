package org.treez.core.atom.variablefield;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAttributeAtomConstructionTest;
import org.treez.core.quantity.Quantity;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class QuantityVariableFieldTest extends AbstractAttributeAtomConstructionTest<Quantity> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(QuantityVariableFieldTest.class);

	//#region SETUP

	/**
	 * Load entities from the database.
	 */
	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		QuantityVariableField variableField = new QuantityVariableField(atomName);
		variableField.setLabel("MyVariable:");
		variableField.setDefaultValueString("10");
		variableField.setTooltip("My tooltip");
		atom = variableField;

	}

	//#end region

	//#region TESTS

	//#end region

}
