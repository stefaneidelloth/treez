package org.treez.core.atom.attribute;

import org.apache.log4j.Logger;
import org.junit.Before;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class TextAreaTest extends AbstractAttributeAtomConstructionTest<String> {

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(TextAreaTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		TextArea textArea = new TextArea(atomName);
		textArea.setLabel("My Textfield:");
		textArea.setDefaultValue("My default value");
		textArea.setTooltip("My tooltip");
		atom = textArea;

	}

	//#end region

	//#region TESTS

	//#end region

}
