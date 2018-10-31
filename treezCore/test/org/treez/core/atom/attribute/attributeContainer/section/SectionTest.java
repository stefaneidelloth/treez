package org.treez.core.atom.attribute.attributeContainer.section;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAttributeContainerAtomConstructonTest;
import org.treez.core.atom.attribute.attributeContainer.section.Section;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class SectionTest extends AbstractAttributeContainerAtomConstructonTest {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(SectionTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		Section section = new Section(atomName);
		section.setLabel("Section Title");
		section.setDescription("Section Description");
		section.setLayout("HORIZONTAL");
		section.setExpanded(true);
		atom = section;
	}

	//#end region

	//#region TESTS

	//#end region

}
