package org.treez.core.atom.attribute.comboBox.fillStyle;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAttributeAtomConstructionTest;

/**
 * Tests the FillStyle
 */
public class FillStyleTest extends AbstractAttributeAtomConstructionTest<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(FillStyleTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		FillStyle fillStyle = new FillStyle(atomName);
		fillStyle.setLabel("My Fill style:");
		fillStyle.setTooltip("My Fill style tooltip");
		fillStyle.setDefaultValue(FillStyleValue.CROSS);
		atom = fillStyle;
	}

	//#end region

	//#region TESTS

	//#end region

}
