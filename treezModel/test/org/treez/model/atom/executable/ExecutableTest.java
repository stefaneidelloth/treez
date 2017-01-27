package org.treez.model.atom.executable;

import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAbstractAtomTest;

public class ExecutableTest extends AbstractAbstractAtomTest {

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		atom = new Executable(atomName);

	}

	//#end region

}
