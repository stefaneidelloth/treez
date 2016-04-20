package org.treez.core.atom.attribute;

import org.apache.log4j.Logger;
import org.junit.Before;

/**
 * Tests the LineStyle
 */
public class LineStyleTest extends AbstractAttributeAtomConstructionTest<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(LineStyleTest.class);

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
