package org.treez.data.tableSource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAbstractAtomTest;
import org.treez.data.tableSource.TableSource;

public class TableSourceTest extends AbstractAbstractAtomTest {

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(TableSourceTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		atom = new TableSource(atomName);

	}

	@Override
	protected Boolean isShowingPreviewWindow() {
		return true;
	}

	//#end region

	//#region TESTS

	//#end region

}
