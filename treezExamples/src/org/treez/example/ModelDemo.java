package org.treez.example;

import org.treez.core.atom.variablefield.QuantityVariableField;
import org.treez.core.scripting.ModelProvider;
import org.treez.data.table.Table;
import org.treez.model.atom.Models;
import org.treez.model.atom.executable.Executable;
import org.treez.model.atom.executable.InputFileGenerator;
import org.treez.model.atom.executable.TableImport;
import org.treez.model.atom.genericInput.GenericInputModel;
import org.treez.results.atom.axis.Axis;
import org.treez.results.atom.axis.Direction;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.page.Page;
import org.treez.results.atom.results.Results;
import org.treez.results.atom.xy.Xy;
import org.treez.study.atom.Studies;
import org.treez.study.atom.range.QuantityVariableRange;
import org.treez.study.atom.sensitivity.Sensitivity;
import org.treez.study.atom.sweep.Sweep;
import org.treez.views.tree.rootAtom.Root;

/**
 * @author eis
 */
public class ModelDemo extends ModelProvider {

	@SuppressWarnings({ "checkstyle:executablestatementcount", "checkstyle:javancss" })
	@Override
	public Root createModel() {

		Root root = new Root("root");

		Models models = new Models("models");
		root.addChild(models);

		//generic model
		GenericInputModel genericModel = new GenericInputModel("genericModel");
		models.addChild(genericModel);

		QuantityVariableField x = new QuantityVariableField("x");
		x.setValueString("10");
		genericModel.addChild(x);

		QuantityVariableField y = new QuantityVariableField("y");
		y.setValueString("20");
		genericModel.addChild(y);

		//executable
		String inputFilePath = "D:/runtime-EclipseApplication/TreezExamples/inputFile.txt";
		String importFilePath = "D:/runtime-EclipseApplication/TreezExamples/importData.txt";

		Executable executable = new Executable("executable");
		executable.executablePath.set("D:/runtime-EclipseApplication/TreezExamples/executable.exe");
		executable.inputPath.set(inputFilePath);
		executable.outputPath.set(importFilePath);
		models.addChild(executable);

		InputFileGenerator inputFile = new InputFileGenerator("inputFileGenerator");
		inputFile.templateFilePath.set("D:/runtime-EclipseApplication/TreezExamples/template.txt");
		inputFile.inputFilePath.set(inputFilePath);
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

		QuantityVariableRange xRange = new QuantityVariableRange("x");
		sweep.addChild(xRange);
		xRange.setRelativeSourceVariableModelPath("x");
		xRange.setRangeValueString("[1,2]");

		QuantityVariableRange yRange = new QuantityVariableRange("y");
		sweep.addChild(yRange);
		yRange.setRelativeSourceVariableModelPath("y");
		yRange.setRangeValueString("[1,2,3]");

		//sensitivity
		Sensitivity sensitivity = new Sensitivity("sensitivity");
		studies.addChild(sensitivity);

		//results------------------------------------------------------------
		Results results = new Results("results");
		root.addChild(results);

		//create data table with two columns---------------------------------
		org.treez.results.atom.data.Data data = new org.treez.results.atom.data.Data("data");
		results.addChild(data);

		Table table = new Table("table");
		data.addChild(table);

		/*
		 *
		 * x = new Column("x", "Double"); table.addColumn(x);
		 *
		 * y = new Column("y", "Double"); table.addColumn(y);
		 *
		 *
		 *
		 * //add some data to the data table table.addRows([ [1.0, 10.0], [2.0,
		 * 20.0], [3.0, 30.0], [4.0, 40.0], [5.0, 50.0], [6.0, 60.0], [7.0,
		 * 70.0], [8.0, 80.0], [9.0, 90.0], [10.0,20.0], ]);
		 */

		//create plot page--------------------------------------------
		Page page = new Page("page");
		results.addChild(page);

		Graph graph = new Graph("graph");
		page.addChild(graph);

		Axis xAxis = new Axis("x");
		graph.addChild(xAxis);

		Axis yAxis = new Axis("y", Direction.VERTICAL);
		graph.addChild(yAxis);

		Xy xy = new Xy("xy plot");
		graph.addChild(xy);

		return root;

	}
}
