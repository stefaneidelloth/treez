package org.treez.core.atom.attribute.list;

import java.util.List;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAttributeAtomConstructionTest;
import org.treez.core.atom.attribute.list.StringList;

/**
 * Tests the StringList
 */
public class StringListTest extends AbstractAttributeAtomConstructionTest<List<String>> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(StringListTest.class);

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
