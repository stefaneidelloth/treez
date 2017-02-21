package org.treez.data.table.nebula;

import org.treez.core.atom.attribute.DemoForAbstractAtom;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.table.TableSourceType;
import org.treez.data.column.Column;
import org.treez.data.tableSource.TableSource;

public class TableDemo extends DemoForAbstractAtom {

	@SuppressWarnings({ "unused", "checkstyle:uncommentedmain" })
	public static void main(final String[] args) {
		new TableDemo();
	}

	@Override
	protected void createDemoAtom() {
		Table table = new Table(atomName);

		TableSource tableSource = new TableSource("source");
		tableSource.setSourceType(TableSourceType.SQLITE);
		tableSource.filePath.set("D:/EclipseJava/workspaceTreez/treezData/testResources/example.sqlite");
		tableSource.tableName.set("example");

		table.addChild(tableSource);

		Column idColumn = new Column("id", ColumnType.INTEGER);
		table.addColumn(idColumn);

		Column nameColumn = new Column("name", ColumnType.STRING);
		table.addColumn(nameColumn);

		for (int rowIndex = 1; rowIndex < 10; rowIndex++) {
			//table.addRow(rowIndex, "name_" + rowIndex);
		}

		atom = table;

	}

}
