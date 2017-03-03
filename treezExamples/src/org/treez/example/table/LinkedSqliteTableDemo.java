package org.treez.example.table;

import org.treez.core.data.table.TableSourceType;
import org.treez.core.scripting.ModelProvider;
import org.treez.data.table.nebula.Table;
import org.treez.data.tableSource.TableSource;
import org.treez.results.atom.data.Data;
import org.treez.results.atom.results.Results;
import org.treez.views.tree.rootAtom.Root;

/**
 * Demonstrates the import from an SqLite database to Treez
 */
public class LinkedSqliteTableDemo extends ModelProvider {

	@Override
	public Root createModel() {

		Root root = new Root("root");

		//#region RESULTS

		Results results = root.createResults("results");
		Data data = results.createData("data");
		Table table = data.createTable("table");

		String treezExamplePath = "D:/EclipseJava/workspaceTreez/TreezExamples";
		String sqLitePath = treezExamplePath + "/resources/example.sqlite";

		TableSource tableSource = table.createTableSource("source");
		tableSource.setSourceType(TableSourceType.SQLITE);
		tableSource.filePath.set(sqLitePath);
		tableSource.tableName.set("example");

		//#end region

		return root;
	}
}
