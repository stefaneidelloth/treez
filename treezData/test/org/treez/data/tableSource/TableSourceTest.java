package org.treez.data.tableSource;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAbstractAtomTest;

public class TableSourceTest extends AbstractAbstractAtomTest {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(TableSourceTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		TableSource tableSource = new TableSource(atomName);
		//tableSource.jobId.set("5");

		atom = tableSource;

	}

	@Override
	protected Boolean isShowingPreviewWindow() {
		return true;
	}

	//#end region

	//#region TESTS

	//#end region

}
