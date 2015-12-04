package org.treez.core.atom.attribute;

import org.apache.log4j.Logger;
import org.junit.Before;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class FilePathTest extends AbstractAttributeAtomConstructionTest<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(FilePathTest.class);

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
