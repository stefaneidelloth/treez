package org.treez.core.atom.attribute.text;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAttributeAtomConstructionTest;
import org.treez.core.atom.attribute.text.TextArea;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class TextAreaTest extends AbstractAttributeAtomConstructionTest<String> {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(TextAreaTest.class);

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
