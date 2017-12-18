package org.treez.model.atom.executable;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAbstractAtomTest;
import org.treez.model.atom.inputFileGenerator.InputFileGenerator;

/**
 * Tests the ExternalInputFile
 */
public class InputFileTest extends AbstractAbstractAtomTest {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(InputFileTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		atom = new InputFileGenerator(atomName);

	}

	//#end region

	//#region TESTS

	//#end region

}
