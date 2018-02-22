package org.treez.example.sweep;

import org.treez.core.atom.variablefield.IntegerVariableField;
import org.treez.core.scripting.ModelProvider;
import org.treez.data.table.nebula.Table;
import org.treez.model.atom.Models;
import org.treez.model.atom.executable.Executable;
import org.treez.model.atom.genericInput.GenericInputModel;
import org.treez.model.atom.inputFileGenerator.InputFileGenerator;
import org.treez.model.atom.tableImport.TableImport;
import org.treez.results.atom.results.Results;
import org.treez.study.atom.Studies;
import org.treez.study.atom.exportStudyInfo.StudyInfoExport;
import org.treez.study.atom.exportStudyInfo.StudyInfoExportType;
import org.treez.study.atom.range.IntegerVariableRange;
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

		IntegerVariableField x = new IntegerVariableField("x");
		x.setValueString("10");
		genericModel.addChild(x);

		IntegerVariableField y = new IntegerVariableField("y");
		y.setValueString("20");
		genericModel.addChild(y);

		String resourcePath = "D:/EclipseJava/workspaceTreez/treezExamples/src/";

		//executable
		String inputFilePath = resourcePath + "input.txt";
		String importFilePath = resourcePath + "importData.txt";

		Executable executable = new Executable("executable");
		executable.executablePath.set(resourcePath + "executable.bat");
		executable.inputPath.set(inputFilePath);
		executable.includeJobIndexInInputFile.set(true);
		executable.outputPath.set(importFilePath);
		executable.includeJobIndexInOutputFile.set(true);
		models.addChild(executable);

		InputFileGenerator inputFile = new InputFileGenerator("inputFileGenerator");
		inputFile.templateFilePath.set(resourcePath + "template.txt");
		inputFile.nameExpression.set("<name>");
		inputFile.valueExpression.set("<value>");
		inputFile.inputFilePath.set(inputFilePath);
		inputFile.includeJobIndexInInputFile.set(true);
		inputFile.deleteUnassignedRows.set(false);
		executable.addChild(inputFile);

		TableImport dataImport = new TableImport("dataImport");
		dataImport.resultTableModelPath.set("root.results.data.table");
		dataImport.appendData.set(false);
		executable.addChild(dataImport);

		//studies------------------------------------------------------------
		Studies studies = new Studies("studies");
		root.addChild(studies);

		//sweep
		Sweep sweep = new Sweep("sweep");
		sweep.studyId.set("myStudyId");
		sweep.studyDescription.set("myStudyDescription");
		sweep.modelToRunModelPath.set("root.models");
		sweep.sourceModelPath.set("root.models.genericModel");
		studies.addChild(sweep);

		IntegerVariableRange xRange = new IntegerVariableRange("x");
		sweep.addChild(xRange);
		xRange.setRelativeSourceVariableModelPath("x");
		xRange.setRangeValueString("{1,2,3,4,5,6,7,8,9}");

		IntegerVariableRange yRange = new IntegerVariableRange("y");
		sweep.addChild(yRange);
		yRange.setRelativeSourceVariableModelPath("y");
		yRange.setRangeValueString("{10,20,30,40,50,60,70,80,90}");

		StudyInfoExport studyInfoExport = sweep.createStudyInfoExport("stuyInfoExport");
		studyInfoExport.studyInfoExportType.set(StudyInfoExportType.MYSQL);
		studyInfoExport.host.set("dagobah");
		studyInfoExport.port.set("3366");
		studyInfoExport.user.set("root");
		studyInfoExport.password.set("***");
		studyInfoExport.schema.set("170817_eload_hh+mob+hp");

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
