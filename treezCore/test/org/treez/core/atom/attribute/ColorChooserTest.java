package org.treez.core.atom.attribute;

import org.apache.log4j.Logger;
import org.junit.Before;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class ColorChooserTest extends AbstractAttributeAtomConstructionTest<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(ColorChooserTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {
		//create test atom
		ColorChooser colorChooser = new ColorChooser(atomName);
		colorChooser.setLabel("My color:");
		colorChooser.setTooltip("My color tooltip");
		colorChooser.setDefaultValue(ColorValue.GREEN);
		this.atom = colorChooser;

	}

	//#end region

	//#end region

}
