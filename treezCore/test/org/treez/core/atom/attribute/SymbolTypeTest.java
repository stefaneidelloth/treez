package org.treez.core.atom.attribute;

import org.apache.log4j.Logger;
import org.junit.Before;

/**
 * Tests the SymbolStyle
 */
public class SymbolTypeTest extends AbstractAttributeAtomConstructionTest<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(SymbolTypeTest.class);

	//#region ATTRIBUTES

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		SymbolType symbolStyle = new SymbolType(atomName);
		symbolStyle.setLabel("My symbol style:");
		symbolStyle.setTooltip("My symbol style tooltip");
		symbolStyle.setDefaultValue(SymbolStyleValue.DIAMOND);
		atom = symbolStyle;
	}

	//#end region

	//#region TESTS

	//#end region

}
