package org.treez.core.atom.attribute.fileSystem;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAttributeAtomConstructionTest;
import org.treez.core.atom.attribute.fileSystem.DirectoryPathList;

/**
 * Tests the FilePathList
 */
public class DirectoryPathListTest extends AbstractAttributeAtomConstructionTest<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(DirectoryPathListTest.class);

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
