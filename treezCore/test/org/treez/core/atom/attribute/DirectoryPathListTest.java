package org.treez.core.atom.attribute;

import org.apache.log4j.Logger;
import org.junit.Before;

/**
 * Tests the FilePathList
 */
public class DirectoryPathListTest extends AbstractAttributeAtomConstructionTest<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DirectoryPathListTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		DirectoryPathList atom = new DirectoryPathList(atomName);
		atom.setLabel("Range");
	}

	//#end region

	//#region TESTS

	//#end region

}
