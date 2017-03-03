package org.treez.core.atom.attribute.fileSystem;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAttributeAtomConstructionTest;
import org.treez.core.atom.attribute.fileSystem.DirectoryPath;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class DirectoryPathTest extends AbstractAttributeAtomConstructionTest<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DirectoryPathTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		DirectoryPath directoryPath = new DirectoryPath(atomName);
		directoryPath.setLabel("My directory:");
		directoryPath.setTooltip("My directory tool tip");
		directoryPath.setDefaultValue("C:\\           ");
		atom = directoryPath;
	}

	//#end region

	//#region TESTS

	//#end region

}
