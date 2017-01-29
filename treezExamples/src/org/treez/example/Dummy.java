package org.treez.example;

import org.treez.core.atom.variablefield.DoubleVariableField;
import org.treez.core.atom.variablefield.FilePathVariableField;
import org.treez.core.atom.variablefield.IntegerVariableField;
import org.treez.core.atom.variablefield.StringVariableField;
import org.treez.core.scripting.ModelProvider;
import org.treez.model.atom.Models;
import org.treez.model.atom.executable.Executable;
import org.treez.model.atom.genericInput.GenericInputModel;
import org.treez.model.atom.inputFileGenerator.InputFileGenerator;
import org.treez.study.atom.Studies;
import org.treez.study.atom.range.DoubleVariableRange;
import org.treez.study.atom.range.FilePathVariableRange;
import org.treez.study.atom.sweep.Sweep;
import org.treez.views.tree.rootAtom.Root;

public class Dummy extends ModelProvider {

	@Override
	public Root createModel() {

		Root root = new Root("root");

		//#region MODELS

		Models models = root.createModels("models");

		//#region GENERICMODEL

		GenericInputModel genericModel = models.createGenericInputModel("genericModel");
		IntegerVariableField settingId = genericModel.createIntegerVariableField("settingId");
		settingId.set(83);

		StringVariableField countryIds = genericModel.createStringVariableField("countryIds");
		countryIds.set("{9}");

		StringVariableField countryProcessMappingIds = genericModel
				.createStringVariableField("countryProcessMappingIds");
		countryProcessMappingIds.set("{{1}}");

		StringVariableField countryStorageProcessMappingIds = genericModel
				.createStringVariableField("countryStorageProcessMappingIds");
		countryStorageProcessMappingIds.set("{{1}}");

		DoubleVariableField threshold = genericModel.createDoubleVariableField("threshold");
		threshold.set(0.005);

		FilePathVariableField pathToAccess = genericModel.createFilePathVariableField("pathToAccess");
		pathToAccess.set("D:/EclipseJava/workspace/eLOAD/forecast_data/Results_GB_EON_CV2015_cleaned.accdb");

		genericModel.createIntegerVariableField("systemLoadMappingId");
		genericModel.createIntegerVariableField("curveId");
		genericModel.createIntegerVariableField("dsmMappingId");
		genericModel.createIntegerVariableField("dsmMappingEntryId");
		genericModel.createDoubleVariableField("cost");
		genericModel.createDoubleVariableField("lowerBound");
		genericModel.createDoubleVariableField("upperBound");
		genericModel.createDoubleVariableField("cohortComputationValue");
		genericModel.createIntegerVariableField("length");
		genericModel.createIntegerVariableField("intervalInformationLength");
		genericModel.createIntegerVariableField("storageType");
		genericModel.createDoubleVariableField("storageValue");
		genericModel.createIntegerVariableField("resProductionMappingId");
		genericModel.createDoubleVariableField("production");
		genericModel.createIntegerVariableField("endUserPriceMappingId");
		genericModel.createDoubleVariableField("ht1Factor");
		genericModel.createDoubleVariableField("rtpVarianceFactor");
		DoubleVariableField storageUpperBound = genericModel.createDoubleVariableField("storageUpperBound");
		storageUpperBound.set(7000.0);

		//#end region

		//#region EXECUTABLE

		Executable executable = models.createExecutable("executable");
		executable.executablePath.set("D:/EclipseJava/App/jdk1.8/bin/java.exe");
		executable.inputArguments.set(
				"-Djava.library.path=\"D:/EclipseJava/workspace/eLoad/lib/cplex/bin/x64_win64\" -cp D:/EclipseJava/workspace/eLoad/build/eLOAD/eLOAD.jar isi.eload.ELoadStarter -db=D:/EclipseJava/workspace/eLoad/db/eLOAD_complete.sqlite -studyName=\"{$studyId$}\" -studyDescription=\"{$studyDescription$}\" -jobName=\"{$jobId$}\"  -runOptimization -inputFile");
		executable.inputPath.set("D:/EclipseJava/workspace/eLoadTreez/input_file/inputFile.txt");
		executable.outputArguments.set("-outputFolder");
		executable.outputPath.set("D:/EclipseJava/workspace/eLoadTreez/output");
		executable.commandInfo.set(
				"\"D:/EclipseJava/App/jdk1.8/bin/java.exe\" -Djava.library.path=\"D:/EclipseJava/workspace/eLoad/lib/cplex/bin/x64_win64\" -cp D:/EclipseJava/workspace/eLoad/build/eLOAD/eLOAD.jar isi.eload.ELoadStarter -db=D:/EclipseJava/workspace/eLoad/db/eLOAD_complete.sqlite -studyName=\"\" -studyDescription=\"\" -jobName=\"1\"  -runOptimization -inputFile D:/EclipseJava/workspace/eLoadTreez/input_file/inputFile.txt -outputFolder D:/EclipseJava/workspace/eLoadTreez/output");
		executable.executionStatusInfo.set("Not yet executed");

		InputFileGenerator inputFileGenerator = executable.createInputFileGenerator("inputFileGenerator");
		inputFileGenerator.templateFilePath
				.set("D:/EclipseJava/workspace/eLoadTreez/input_file/input_file_template_all.txt");
		inputFileGenerator.nameExpression.set("{$<name>$}");
		inputFileGenerator.valueExpression.set("<value><unit>");
		inputFileGenerator.inputFilePath.set("D:/EclipseJava/workspace/eLoadTreez/input_file/inputFile.txt");

		//#end region

		//#end region

		//#region STUDIES

		Studies studies = root.createStudies("studies");
		Sweep sweep = studies.createSweep("sweep");
		sweep.studyId.set("dummy_test_study");
		sweep.studyDescription.set("can be overridden and deleted");
		sweep.modelToRunModelPath.set("root.models");
		sweep.sourceModelPath.set("root.models.genericModel");
		sweep.exportStudyInfoPath.set("D:/EclipseJava/workspace/eLoad/db/eLOAD_complete.sqlite");

		DoubleVariableRange thresholdDoubleVariableRange = sweep.createDoubleVariableRange("threshold");
		thresholdDoubleVariableRange.setRelativeSourceVariableModelPath("threshold");
		thresholdDoubleVariableRange.setRange(0.005, 0.001);
		thresholdDoubleVariableRange.enabled.set(false);

		FilePathVariableRange pathRange = sweep.createFilePathVariableRange("pathRange");
		pathRange.setRelativeSourceVariableModelPath("pathToAccess");
		pathRange.setRange("D:/EclipseJava/workspace/eLoad/forecast_data/Results_GB_EON_CV2015_cleaned.accdb",
				"D:/EclipseJava/workspace/eLoad/forecast_data/Results_GB_EON_CV2015_cleaned.accdb");
		pathRange.enabled.set(false);

		DoubleVariableRange storageUpperBoundDoubleVariableRange = sweep.createDoubleVariableRange("storageUpperBound");
		storageUpperBoundDoubleVariableRange.setRelativeSourceVariableModelPath("storageUpperBound");
		storageUpperBoundDoubleVariableRange.setRange(7000.0, 8000.0);

		//#end region

		return root;
	}
}
