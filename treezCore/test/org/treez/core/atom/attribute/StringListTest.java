package org.treez.core.atom.attribute;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;

/**
 * Tests the StringList
 */
public class StringListTest extends AbstractAttributeAtomConstructionTest<List<String>> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(StringListTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		StringList stringList = new StringList(atomName);
		stringList.setLabel("Range");
		atom = stringList;
	}

	//#end region

	//#region TESTS

	//#end region

}
