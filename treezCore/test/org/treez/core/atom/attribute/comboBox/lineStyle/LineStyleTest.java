package org.treez.core.atom.attribute.comboBox.lineStyle;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAttributeAtomConstructionTest;

/**
 * Tests the LineStyle
 */
public class LineStyleTest extends AbstractAttributeAtomConstructionTest<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(LineStyleTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		LineStyle lineStyle = new LineStyle(atomName);
		lineStyle.setLabel("My line style:");
		lineStyle.setTooltip("My line style tooltip");
		lineStyle.setDefaultValue(LineStyleValue.DASHED);
		atom = lineStyle;
	}

	//#end region

	//#end region

}
