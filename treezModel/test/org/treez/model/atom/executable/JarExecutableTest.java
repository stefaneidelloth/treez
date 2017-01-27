package org.treez.model.atom.executable;

import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAbstractAtomTest;

public class JarExecutableTest extends AbstractAbstractAtomTest {

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		atom = new JarExecutable(atomName);

	}

	@Override
	protected Boolean isOpeningWindowThatNeedsToBeClosed() {
		return true;
	}

	//#end region

}
