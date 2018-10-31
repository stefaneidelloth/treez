package org.treez.core.atom.attribute.fileSystem;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAttributeAtomConstructionTest;
import org.treez.core.atom.attribute.fileSystem.FilePath;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class FilePathTest extends AbstractAttributeAtomConstructionTest<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(FilePathTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		FilePath filePath = new FilePath(atomName);
		filePath.setLabel("My file path:");
		filePath.setTooltip("My filepath tooltip");
		filePath.setDefaultValue("D:\\                               ");
		filePath.setFileExtensions("*.txt,*.xlsx");
		filePath.setFileExtensionNames("*.txt Text, *.xlsx Excel");
		atom = filePath;

	}

	//#end region

	//#region TESTS

	//#end region

}
