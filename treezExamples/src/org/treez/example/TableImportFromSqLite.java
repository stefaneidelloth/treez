package org.treez.example;

import org.treez.core.scripting.ModelProvider;
import org.treez.data.table.nebula.Table;
import org.treez.model.atom.Models;
import org.treez.model.atom.executable.Executable;
import org.treez.model.atom.tableImport.TableImport;
import org.treez.results.atom.data.Data;
import org.treez.results.atom.results.Results;
import org.treez.views.tree.rootAtom.Root;

/**
 * Demonstrates the import from an SqLite database to Treez
 */
public class TableImportFromSqLite extends ModelProvider {

	@Override
	public Root createModel() {

		Root root = new Root("root");

		//#region MODELS

		Models models = root.createModels("models0");

		//#region EXECUTABLE

		Executable executable = models.createExecutable("executable");

		String treezExamplePath = "D:/EclipseJava/workspaceTreez/TreezExamples";
		String sqLitePath = treezExamplePath + "/resources/example.sqlite";

		TableImport tableImport = executable.createTableImport("tableImport");
		tableImport.sourceType.set("sqlite");
		tableImport.inheritSourceFilePath.set(false);
		tableImport.sourceFilePath.set(sqLitePath);
		tableImport.table.set("example");
		tableImport.rowLimit.set(1000);
		tableImport.resultTableModelPath.set("root.results.data.table");

		//#end region

		//#region RESULTS

		Results results = root.createResults("results");
		Data data = results.createData("data");
		Table table = data.createTable("table");

		//#end region

		return root;
	}
}
