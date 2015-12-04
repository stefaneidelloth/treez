package org.treez.example;

import org.treez.core.atom.variablefield.BooleanVariableField;
import org.treez.core.atom.variablefield.FilePathVariableField;
import org.treez.core.atom.variablefield.QuantityVariableField;
import org.treez.core.scripting.ModelProvider;
import org.treez.data.table.Table;
import org.treez.model.atom.Models;
import org.treez.model.atom.executable.Executable;
import org.treez.model.atom.executable.InputFileGenerator;
import org.treez.model.atom.executable.TableImport;
import org.treez.model.atom.genericInput.GenericInputModel;
import org.treez.results.atom.data.Data;
import org.treez.results.atom.probe.SweepProbe;
import org.treez.results.atom.results.Results;
import org.treez.study.atom.Studies;
import org.treez.study.atom.range.QuantityVariableRange;
import org.treez.study.atom.sweep.Sweep;
import org.treez.views.tree.rootAtom.Root;

/**
 * A treez file that can be used to execute a parameter variation
 */
public class ParameterVariation extends ModelProvider {

	@SuppressWarnings({ "checkstyle:javancss", "checkstyle:executablestatementcount" })
	@Override
	public Root createModel() {

		Root root = new Root("root");

		Models models = new Models("models");
		root.addChild(models);

		//generic model---------------------------------------------------

		GenericInputModel genericModel = new GenericInputModel("genericModel");
		models.addChild(genericModel);

		//eload_setting	- (Entity?)
		BooleanVariableField myFlag = genericModel.createBooleanVariableField("settingId");
		myFlag.setLabel("My flag");
		myFlag.setTooltip("Tooltip for my flag");
		myFlag.setDefaultValue(true);
		myFlag.set(false);
		myFlag.setEnabled(false);
		//settingId.setValueString("11");

		//eload_setting.threshold - DoubleProperty
		QuantityVariableField threshold = genericModel.createQuantityVariableField("threshold");
		threshold.setValueString("0.5");

		//eload_setting.path_to_access - StringProperty
		FilePathVariableField pathToAccess = genericModel.createFilePathVariableField("pathToAccess");
		pathToAccess.set("C:/testsdfsdsdfsdf.accdb");

		//eload_setting.system_load_mapping - Entity
		QuantityVariableField systemLoadMappingId = genericModel.createQuantityVariableField("systemLoadMappingId");
		systemLoadMappingId.setValueString("1");

		//eload_setting.system_load_mapping.system_load_mapping_entry.curve - Entity
		QuantityVariableField curveId = genericModel.createQuantityVariableField("curveId");
		curveId.setValueString("1");

		//eload_setting.dsm_mapping - Entity
		QuantityVariableField dsmMappingId = genericModel.createQuantityVariableField("dsmMappingId");
		dsmMappingId.setValueString("1");

		//eload_setting.dsm_mapping - Entity
		QuantityVariableField dsmMappingEntryId = genericModel.createQuantityVariableField("dsmMappingEntryId");
		dsmMappingEntryId.setValueString("1");

		//eload_setting.dsm_mapping.dsm_mapping_entry.cost_information.cost_information_activity.cost - double
		QuantityVariableField cost = genericModel.createQuantityVariableField("cost");
		cost.setValueString("0");

		//eload_setting.dsm_mapping.dsm_mapping_entry.bound_information.bound_information_houlry.lower_bound  - double
		QuantityVariableField lowerBound = genericModel.createQuantityVariableField("lowerBound");
		lowerBound.setValueString("0");

		//eload_setting.dsm_mapping.dsm_mapping_entry.bound_information.bound_information_houlry.upper_bound  - double
		QuantityVariableField upperBound = genericModel.createQuantityVariableField("upperBound");
		upperBound.setValueString("1");

		//eload_setting.dsm_mapping.dsm_mapping_entry.cohort_information.cohort_computation_value - float
		QuantityVariableField cohortComputationValue = genericModel
				.createQuantityVariableField("cohortComputationValue");
		cohortComputationValue.setValueString("0");

		//eload_setting.dsm_mapping.dsm_mapping_entry.interval_group_information.length - int
		QuantityVariableField length = genericModel.createQuantityVariableField("length");
		length.setValueString("1");

		//eload_setting.dsm_mapping.dsm_mapping_entry.interval_group_information.interval_information.length - int
		QuantityVariableField intervalInformationLength = genericModel
				.createQuantityVariableField("intervalInformationLength");
		intervalInformationLength.setValueString("1");

		//eload_setting.dsm_mapping.dsm_mapping_entry.storage_information.type
		QuantityVariableField storageType = genericModel.createQuantityVariableField("storageType");
		storageType.setValueString("1");

		//eload_setting.dsm_mapping.dsm_mapping_entry.storage_information.value
		QuantityVariableField storageValue = genericModel.createQuantityVariableField("storageValue");
		storageValue.setValueString("1");

		//eload_setting.res_production_mapping - Entity
		QuantityVariableField resProductionMappingId = genericModel
				.createQuantityVariableField("resProductionMappingId");
		resProductionMappingId.setValueString("1");

		//eload_setting.res_production_mapping.res_production_mapping_entry.production - Float
		QuantityVariableField production = genericModel.createQuantityVariableField("production");
		production.setValueString("1");

		//eload_setting.end_user_price_mapping - Entity
		QuantityVariableField endUserPriceMappingId = genericModel.createQuantityVariableField("endUserPriceMappingId");
		endUserPriceMappingId.setValueString("1");

		//eload_setting.end_user_price_mapping.end_user_price_mapping_entry.
		//tou_definition.tou_definition_entry.ht1_factor - Double
		QuantityVariableField ht1Factor = genericModel.createQuantityVariableField("ht1Factor");
		ht1Factor.setValueString("1");

		//eload_setting.end_user_price_mapping.end_user_price_mapping_entry.rtp_variance_factor - Double
		QuantityVariableField rtpVarianceFactor = genericModel.createQuantityVariableField("rtpVarianceFactor");
		rtpVarianceFactor.setValueString("1");

		//executable--------------------------------------------------------
		String eclipsePath = "D:/EclipseJava";
		String inputFilePath = eclipsePath + "/workspace/eLoadTreez/input_file/inputFile.txt";
		String outputFolder = eclipsePath + "/workspace/eLoadTreez/output";

		Executable executable = new Executable("executable");
		executable.executablePath.set(eclipsePath + "/App/jdk1.8/bin/java.exe");
		String jarPath = eclipsePath + "/workspace/eLoad/build/eLOAD/eLOAD.jar";
		String mainClass = "isi.eload.ELoadStarter";
		executable.inputArguments.set("-cp " + jarPath + " " + mainClass + " -inputFile");
		executable.inputPath.set(inputFilePath);
		executable.outputArguments.set("-outputFolder");
		executable.outputPath.set(outputFolder + "/output.txt");
		executable.includeStudyIndexInFile.set(true);
		models.addChild(executable);

		InputFileGenerator inputFile = new InputFileGenerator("inputFileGenerator");
		inputFile.templateFilePath.set(eclipsePath + "/workspace/eLoadTreez/input_file/input_file_template.txt");
		inputFile.inputFilePath.set(inputFilePath);
		inputFile.nameExpression.set("{$<label>$}");
		inputFile.valueExpression.set("<value><unit>");
		executable.addChild(inputFile);

		TableImport tableImport = new TableImport("tableImport");
		tableImport.resultTableModelPath.set("root.results.data.table");
		executable.addChild(tableImport);

		//studies------------------------------------------------------------
		Studies studies = new Studies("studies");
		root.addChild(studies);

		//sweep
		Sweep sweep = new Sweep("sweep");
		sweep.modelToRunModelPath.set("root.models");
		sweep.sourceModelPath.set("root.models.genericModel");
		sweep.exportStudyInfoPath.set(outputFolder + "/sweepinfo.txt");
		studies.addChild(sweep);

		QuantityVariableRange thresholdRange = sweep.createQuantityVariableRange("threshold");
		thresholdRange.setRelativeSourceVariableModelPath("threshold");
		thresholdRange.setRangeValueString("{1}");

		//FilePathVariableRange pathRange = sweep.createFilePathVariableRange("pathRange");
		//pathRange.setRelativeSourceVariableModelPath("pathToAccess");
		//pathRange.setRangeValueString("c:\\aaa.accdb,d:\\bbb.accdb");

		//results-------------------------------------------------------------
		Results results = new Results("results");
		root.addChild(results);

		Data data = new Data("data");
		results.addChild(data);

		Table table = new Table("table");
		data.addChild(table);

		SweepProbe sweepProbe = new SweepProbe("sweepProbe");
		data.addChild(sweepProbe);

		return root;

	}
}
