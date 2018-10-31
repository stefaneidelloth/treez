package org.treez.data.table.nebula;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAbstractAtomTest;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.table.TableSourceType;
import org.treez.data.column.Column;
import org.treez.data.tableSource.TableSource;

public class TableTest extends AbstractAbstractAtomTest {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(TableTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {
		Table table = new Table(atomName);

		TableSource tableSource = table.createTableSource("source");
		tableSource.setSourceType(TableSourceType.SQLITE);
		tableSource.filePath.set("D:/EclipseJava/workspaceTreez/treezData/testResources/example.sqlite");
		tableSource.tableName.set("example");

		Column idColumn = new Column("id", ColumnType.INTEGER);
		table.addColumn(idColumn);

		Column nameColumn = new Column("name", ColumnType.STRING);
		table.addColumn(nameColumn);

		for (int rowIndex = 1; rowIndex < 10; rowIndex++) {
			//table.addRow(rowIndex, "name_" + rowIndex);
		}

		atom = table;

	}

	@Override
	protected Boolean isShowingPreviewWindow() {
		return true;
	}

	//#end region

	//#region TESTS

	//#end region

}
