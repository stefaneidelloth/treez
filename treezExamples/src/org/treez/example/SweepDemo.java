package org.treez.example;

import org.treez.core.atom.variablefield.DoubleVariableField;
import org.treez.core.scripting.ModelProvider;
import org.treez.data.table.nebula.Table;
import org.treez.model.atom.Models;
import org.treez.model.atom.executable.Executable;
import org.treez.model.atom.genericInput.GenericInputModel;
import org.treez.model.atom.inputFileGenerator.InputFileGenerator;
import org.treez.model.atom.tableImport.TableImport;
import org.treez.results.atom.results.Results;
import org.treez.study.atom.Studies;
import org.treez.study.atom.range.DoubleVariableRange;
import org.treez.study.atom.sweep.Sweep;
import org.treez.views.tree.rootAtom.Root;

public class SweepDemo extends ModelProvider {

	@SuppressWarnings({ "checkstyle:executablestatementcount", "checkstyle:javancss" })
	@Override
	public Root createModel() {

		Root root = new Root("root");

		Models models = new Models("models");
		root.addChild(models);

		//generic model
		GenericInputModel genericModel = new GenericInputModel("genericModel");
		models.addChild(genericModel);

		DoubleVariableField x = new DoubleVariableField("x");
		x.setValueString("10");
		genericModel.addChild(x);

		DoubleVariableField y = new DoubleVariableField("y");
		y.setValueString("20");
		genericModel.addChild(y);

		String resourcePath = "D:/EclipseJava/workspaceTreez/treezExamples/src/";

		//executable
		String inputFilePath = resourcePath + "input.txt";
		String importFilePath = resourcePath + "importData.txt";

		Executable executable = new Executable("executable");
		executable.executablePath.set(resourcePath + "executable.bat");
		executable.inputPath.set(inputFilePath);
		executable.outputPath.set(importFilePath);
		models.addChild(executable);

		InputFileGenerator inputFile = new InputFileGenerator("inputFileGenerator");
		inputFile.templateFilePath.set(resourcePath + "template.txt");
		inputFile.inputFilePath.set(inputFilePath);
		inputFile.nameExpression.set("<name>");
		inputFile.valueExpression.set("<value>");
		executable.addChild(inputFile);

		TableImport dataImport = new TableImport("dataImport");
		dataImport.sourceFilePath.set(importFilePath);
		dataImport.resultTableModelPath.set("root.results.data.table");
		dataImport.appendData.set(false);
		executable.addChild(dataImport);

		//studies------------------------------------------------------------
		Studies studies = new Studies("studies");
		root.addChild(studies);

		//sweep
		Sweep sweep = new Sweep("sweep");
		sweep.modelToRunModelPath.set("root.models");
		sweep.sourceModelPath.set("root.models.genericModel");
		studies.addChild(sweep);

		DoubleVariableRange xRange = new DoubleVariableRange("x");
		sweep.addChild(xRange);
		xRange.setRelativeSourceVariableModelPath("x");
		xRange.setRangeValueString("{1,2}");

		DoubleVariableRange yRange = new DoubleVariableRange("y");
		sweep.addChild(yRange);
		yRange.setRelativeSourceVariableModelPath("y");
		yRange.setRangeValueString("{1,2,3}");

		//results------------------------------------------------------------
		Results results = new Results("results");
		root.addChild(results);

		//create data table with two columns---------------------------------
		org.treez.results.atom.data.Data data = new org.treez.results.atom.data.Data("data");
		results.addChild(data);

		Table table = new Table("table");
		data.addChild(table);

		return root;

	}
}
