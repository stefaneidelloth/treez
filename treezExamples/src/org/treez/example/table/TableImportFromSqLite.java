package org.treez.example.table;

import org.treez.core.data.table.TableSourceType;
import org.treez.core.scripting.ModelProvider;
import org.treez.model.atom.Models;
import org.treez.model.atom.executable.Executable;
import org.treez.model.atom.tableImport.TableImport;
import org.treez.results.atom.data.Data;
import org.treez.results.atom.results.Results;
import org.treez.views.tree.rootAtom.Root;

public class TableImportFromSqLite extends ModelProvider {

	@Override
	public Root createModel() {

		Root root = new Root("root");

		//#region MODELS0

		Models models0 = root.createModels("models0");

		//#region EXECUTABLE

		Executable executable = models0.createExecutable("executable");
		TableImport tableImport = executable.createTableImport("tableImport");
		tableImport.sourceType.set(TableSourceType.SQLITE);
		tableImport.linkSource.set(true);
		tableImport.inheritSourceFilePath.set(false);
		tableImport.sourceFilePath.set("D:/EclipseJava/workspaceTreez/TreezExamples/resources/example.sqlite");
		tableImport.tableName.set("example");
		tableImport.resultTableModelPath.set("root.results.data.table");

		//#end region

		//#end region

		//#region RESULTS

		Results results = root.createResults("results");
		Data data = results.createData("data");
		data.createTable("table");

		//#end region

		return root;
	}
}
