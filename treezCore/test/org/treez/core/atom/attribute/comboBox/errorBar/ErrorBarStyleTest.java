package org.treez.core.atom.attribute.comboBox.errorBar;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAttributeAtomConstructionTest;

/**
 * Tests the ErrorBarStyle
 */
public class ErrorBarStyleTest extends AbstractAttributeAtomConstructionTest<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(ErrorBarStyleTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		ErrorBarStyle errorBarStyle = new ErrorBarStyle(atomName);
		errorBarStyle.setLabel("My error bar style:");
		errorBarStyle.setTooltip("My error bar style tooltip");
		errorBarStyle.setDefaultValue(ErrorBarStyleValue.BAR);
		atom = errorBarStyle;
	}

	//#end region

	//#region TESTS

	//#end region

}
