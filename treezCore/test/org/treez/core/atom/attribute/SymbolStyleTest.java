package org.treez.core.atom.attribute;

import org.apache.log4j.Logger;
import org.junit.Before;

/**
 * Tests the SymbolStyle
 */
public class SymbolStyleTest extends AbstractAttributeAtomConstructionTest<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(SymbolStyleTest.class);

	//#region ATTRIBUTES

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		SymbolStyle symbolStyle = new SymbolStyle(atomName);
		symbolStyle.setLabel("My symbol style:");
		symbolStyle.setTooltip("My symbol style tooltip");
		symbolStyle.setDefaultValue(SymbolStyleValue.DIAMOND);
		atom = symbolStyle;
	}

	//#end region

	//#region TESTS

	//#end region

}
