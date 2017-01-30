package org.treez.data.table.nebula;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.junit.Before;
import org.junit.Test;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.CodeContainer;
import org.treez.core.atom.attribute.AbstractAbstractAtomTest;
import org.treez.core.data.column.ColumnType;
import org.treez.core.scripting.ScriptType;
import org.treez.data.column.Column;
import org.treez.testutils.PreviewWindow;
import org.treez.testutils.TestUtils;

public class TableTest extends AbstractAbstractAtomTest {

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(TableTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {
		atom = new Table(atomName);
	}

	@Override
	protected Boolean isOpeningWindowThatNeedsToBeClosed() {
		return true;
	}

	//#end region

	//#region TESTS

	@Test
	@Override
	public void testConstructionOfTestAtom() {

		Table table = (Table) atom;

		Column idColumn = new Column("id", ColumnType.INTEGER);
		table.addColumn(idColumn);

		Column nameColumn = new Column("name", ColumnType.TEXT);
		table.addColumn(nameColumn);

		for (int rowIndex = 1; rowIndex < 10; rowIndex++) {
			table.addRow(rowIndex, "name_" + rowIndex);
		}

		//get preview window
		PreviewWindow previewWindow = TestUtils.getPreviewWindow();

		//get and show name
		String name = table.getName();
		previewWindow.setName(name);

		//get and show image
		Image atomImage = table.provideImage();
		previewWindow.setImage(atomImage);

		//get ant show control
		Composite controlComposite = previewWindow.getControlComposite();
		table.createControlAdaption(controlComposite, null);

		//get and show CAD adaption
		Composite cadComposite = previewWindow.getGraphicsComposite();
		table.createGraphicsAdaption(cadComposite);

		//get and show code
		CodeAdaption codeAdaption = table.createCodeAdaption(ScriptType.JAVA);
		CodeContainer rootContainer = new CodeContainer(ScriptType.JAVA);
		Optional<CodeContainer> injectedChildContainer = Optional.ofNullable(null);
		previewWindow.setCode(codeAdaption.buildCodeContainer(rootContainer, injectedChildContainer).buildCode());

		//get tree node adaption
		table.createTreeNodeAdaption();

		//show preview
		previewWindow.showUntilManuallyClosed();

	}

	//#end region

}
