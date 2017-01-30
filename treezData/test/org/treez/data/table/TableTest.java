package org.treez.data.table;

import java.util.ArrayList;
import java.util.List;
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
import org.treez.core.data.row.Row;
import org.treez.core.scripting.ScriptType;
import org.treez.data.column.Column;
import org.treez.testutils.PreviewWindow;
import org.treez.testutils.TestUtils;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class TableTest extends AbstractAbstractAtomTest {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(TableTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create test atom
		atom = new Table(atomName);

	}

	//#end region

	//#region TESTS

	/**
	 * Tests the construction of the atom
	 */
	@Test
	public void testConstructionWithColumns() {

		Table table = (Table) atom;

		Column idColumn = new Column("id", ColumnType.INTEGER);
		table.addColumn(idColumn);

		Column nameColumn = new Column("name", ColumnType.TEXT);
		table.addColumn(nameColumn);

		List<Row> rows = new ArrayList<>();
		Row row1 = new Row(table);
		row1.setEntry("id", 1);
		row1.setEntry("name", "foo");
		rows.add(row1);

		Row row2 = new Row(table);
		row2.setEntry("id", 2);
		row2.setEntry("name", "bar");
		rows.add(row2);

		Row row3 = new Row(table);
		row3.setEntry("id", 3);
		row3.setEntry("name", "qux");
		rows.add(row3);

		table.setRows(rows);

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
