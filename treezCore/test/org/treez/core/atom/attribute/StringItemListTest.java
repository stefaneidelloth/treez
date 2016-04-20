package org.treez.core.atom.attribute;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;

/**
 * Tests the StringItemList
 */
public class StringItemListTest
		extends
			AbstractAttributeAtomConstructionTest<List<String>> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(StringItemListTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		String availableItems = "a,b,c";
		StringItemList stringItemList = new StringItemList(atomName,
				availableItems);
		stringItemList.setLabel("Range");

		atom = stringItemList;
	}

	//#end region

	//#region TESTS

	//#end region

}
