package org.treez.example;

import org.treez.core.scripting.ModelProvider;
import org.treez.model.atom.Models;
import org.treez.model.atom.executable.Executable;
import org.treez.model.atom.genericInput.GenericInputModel;
import org.treez.model.atom.tableImport.TableImport;
import org.treez.results.atom.data.Data;
import org.treez.results.atom.results.Results;
import org.treez.study.atom.Studies;
import org.treez.study.atom.range.IntegerVariableRange;
import org.treez.study.atom.sweep.Sweep;
import org.treez.views.tree.rootAtom.Root;

public class SweepStudyWithTableImportFromSqLite extends ModelProvider {

	@Override
	public Root createModel() {

		Root root = new Root("root");

		//#region MODELS0

		Models models0 = root.createModels("models0");

		//#region GENERICINPUTMODEL0

		GenericInputModel genericInputModel0 = models0.createGenericInputModel("genericInputModel0");
		genericInputModel0.createIntegerVariableField("integerVariable0");
		genericInputModel0.createIntegerVariableField("integerVariable1");

		//#end region

		//#region EXECUTABLE

		Executable executable = models0.createExecutable("executable");
		executable.executablePath.set("foo");
		executable.commandInfo.set("\"\"");
		executable.executionStatusInfo.set("Not yet executed");

		TableImport tableImport = executable.createTableImport("tableImport");
		tableImport.sourceType.set("sqlite");
		tableImport.linkSource.set(true);
		tableImport.inheritSourceFilePath.set(false);
		tableImport.sourceFilePath.set("D:/EclipseJava/workspaceTreez/TreezExamples/resources/example.sqlite");
		tableImport.tableName.set("example");
		tableImport.useCustomQuery.set(true);
		tableImport.customQuery.set("select * from example where id = {$jobId$}");
		tableImport.resultTableModelPath.set("root.results.data.table");

		//#end region

		//#end region

		//#region STUDIES0

		Studies studies0 = root.createStudies("studies0");
		Sweep sweep0 = studies0.createSweep("sweep0");
		sweep0.modelToRunModelPath.set("root.models0.executable");
		sweep0.sourceModelPath.set("root.models0.genericInputModel0");

		IntegerVariableRange integerRange0 = sweep0.createIntegerVariableRange("integerRange0");
		integerRange0.setRelativeSourceVariableModelPath("integerVariable0");
		integerRange0.setRange(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

		IntegerVariableRange integerRange1 = sweep0.createIntegerVariableRange("integerRange1");
		integerRange1.setRelativeSourceVariableModelPath("integerVariable1");
		integerRange1.setRange(1, 2, 3);

		//#end region

		//#region RESULTS

		Results results = root.createResults("results");
		Data data = results.createData("data");
		data.createTable("table");

		//#end region

		return root;
	}
}
