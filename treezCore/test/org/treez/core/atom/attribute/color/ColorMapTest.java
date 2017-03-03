package org.treez.core.atom.attribute.color;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAttributeAtomConstructionTest;
import org.treez.core.atom.attribute.color.ColorMap;
import org.treez.core.color.ColorMapValue;

/**
 * Tests the ColorMap
 */
public class ColorMapTest
		extends
			AbstractAttributeAtomConstructionTest<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(ColorMapTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		ColorMap colorMap = new ColorMap(atomName);
		colorMap.setLabel("My color map:");
		colorMap.setTooltip("My color map tooltip");
		colorMap.setDefaultValue(ColorMapValue.GREY);
		atom = colorMap;
	}

	//#end region

	//#region TESTS

	//#end region

}
