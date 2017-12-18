package org.treez.core.atom.attribute.fileSystem;

import java.util.List;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAttributeAtomConstructionTest;
import org.treez.core.atom.attribute.fileSystem.FilePathList;

/**
 * Tests the FilePathList
 */
public class FilePathListTest extends AbstractAttributeAtomConstructionTest<List<String>> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(FilePathListTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		FilePathList filePathList = new FilePathList(atomName);
		filePathList.setLabel("Range");
		atom = filePathList;
	}

	//#end region

	//#region TESTS

	//#end region

}
