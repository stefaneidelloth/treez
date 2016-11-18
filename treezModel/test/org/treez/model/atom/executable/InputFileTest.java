package org.treez.model.atom.executable;

import org.apache.log4j.Logger;
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
	private static final Logger LOG = Logger.getLogger(InputFileTest.class);

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
