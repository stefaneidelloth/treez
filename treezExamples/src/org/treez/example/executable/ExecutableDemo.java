package org.treez.example.executable;

import org.treez.core.atom.variablefield.DoubleVariableField;
import org.treez.core.scripting.ModelProvider;
import org.treez.model.atom.Models;
import org.treez.model.atom.executable.Executable;
import org.treez.model.atom.genericInput.GenericInputModel;
import org.treez.model.atom.inputFileGenerator.InputFileGenerator;
import org.treez.views.tree.rootAtom.Root;

public class ExecutableDemo extends ModelProvider {

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
		executable.includeJobIndexInInputFile.set(true);
		executable.outputPath.set(importFilePath);
		executable.includeJobIndexInOutputFile.set(true);
		models.addChild(executable);

		InputFileGenerator inputFile = new InputFileGenerator("inputFileGenerator");
		inputFile.templateFilePath.set(resourcePath + "template.txt");
		inputFile.inputFilePath.set(inputFilePath);
		inputFile.nameExpression.set("<name>");
		inputFile.valueExpression.set("<value>");
		inputFile.includeJobIndexInInputFile.set(true);
		inputFile.deleteUnassignedRows.set(false);
		executable.addChild(inputFile);

		return root;

	}
}
