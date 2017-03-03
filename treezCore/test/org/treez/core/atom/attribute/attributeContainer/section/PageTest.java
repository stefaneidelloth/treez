package org.treez.core.atom.attribute.attributeContainer.section;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAttributeContainerAtomConstructonTest;
import org.treez.core.atom.attribute.attributeContainer.Page;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class PageTest extends AbstractAttributeContainerAtomConstructonTest {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(PageTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		Page page = new Page(atomName);
		page.setTitle("My Page Title");
		page.setLayout("HORIZONTAL");
	}

	//#end region

	//#region TESTS

	//#end region

}
