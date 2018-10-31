package org.treez.core.atom.attribute.attributeContainer;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAttributeContainerAtomConstructonTest;
import org.treez.core.atom.attribute.attributeContainer.Spacer;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class SpacerTest extends AbstractAttributeContainerAtomConstructonTest {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(SpacerTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		Spacer spacer = new Spacer(atomName);
		spacer.setHeight("100");
		spacer.setWidth("200");
		atom = spacer;
	}

	//#end region

	//#region TESTS

	//#end region

}
