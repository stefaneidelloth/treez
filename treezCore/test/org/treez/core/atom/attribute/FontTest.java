package org.treez.core.atom.attribute;

import org.apache.log4j.Logger;
import org.junit.Before;

/**
 * Tests the Font chooser
 */
public class FontTest extends AbstractAttributeAtomConstructionTest<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(FontTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		Font font = new Font(atomName);
		font.setLabel("My Font:");
		font.setTooltip("My Font tooltip");
		atom = font;
	}

	//#end region

	//#region TESTS

	//#end region

}
