package org.treez.example;

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
public class LinkedMySqlTableDemo extends ModelProvider {

	@Override
	public Root createModel() {

		Root root = new Root("root");

		//#region RESULTS

		Results results = root.createResults("results");
		Data data = results.createData("data");
		Table table = data.createTable("table");

		TableSource tableSource = table.createTableSource("source");
		tableSource.setSourceType(TableSourceType.MYSQL);
		tableSource.host.set("localhost");
		tableSource.port.set("3306");
		tableSource.schema.set("treez");
		tableSource.user.set("root");
		tableSource.password.set("password");

		tableSource.tableName.set("country");

		//#end region

		return root;
	}
}
