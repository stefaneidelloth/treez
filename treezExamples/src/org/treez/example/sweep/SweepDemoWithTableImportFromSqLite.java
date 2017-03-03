package org.treez.example.sweep;

import org.treez.core.atom.variablefield.IntegerVariableField;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.table.TableSourceType;
import org.treez.core.scripting.ModelProvider;
import org.treez.data.column.Column;
import org.treez.data.column.Columns;
import org.treez.data.output.OutputAtom;
import org.treez.data.table.nebula.Table;
import org.treez.data.tableSource.TableSource;
import org.treez.model.atom.Models;
import org.treez.model.atom.executable.Executable;
import org.treez.model.atom.genericInput.GenericInputModel;
import org.treez.model.atom.tableImport.TableImport;
import org.treez.results.atom.axis.Axis;
import org.treez.results.atom.axis.Direction;
import org.treez.results.atom.data.Data;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.page.Page;
import org.treez.results.atom.probe.SweepProbe;
import org.treez.results.atom.results.Results;
import org.treez.results.atom.xy.Xy;
import org.treez.results.atom.xyseries.XySeries;
import org.treez.study.atom.Studies;
import org.treez.study.atom.range.IntegerVariableRange;
import org.treez.study.atom.sweep.Sweep;
import org.treez.views.tree.rootAtom.Root;

public class SweepDemoWithTableImportFromSqLite extends ModelProvider {

	@Override
	public Root createModel() {

		Root root = new Root("root");

		//#region MODELS0

		Models models0 = root.createModels("models0");

		//#region GENERICINPUTMODEL0

		GenericInputModel genericInputModel0 = models0.createGenericInputModel("genericInputModel0");
		IntegerVariableField integerVariable0 = genericInputModel0.createIntegerVariableField("integerVariable0");
		integerVariable0.set(10);

		IntegerVariableField integerVariable1 = genericInputModel0.createIntegerVariableField("integerVariable1");
		integerVariable1.set(3);

		//#end region

		//#region EXECUTABLE

		Executable executable = models0.createExecutable("executable");
		executable.executablePath.set("foo");
		executable.commandInfo.set("\"foo\"");
		executable.executionStatusInfo.set("Not yet executed");
		executable.jobIndexInfo.set("31");

		TableImport tableImport = executable.createTableImport("tableImport");
		tableImport.sourceType.set(TableSourceType.SQLITE);
		tableImport.linkSource.set(true);
		tableImport.inheritSourceFilePath.set(false);
		tableImport.sourceFilePath.set("D:/EclipseJava/workspaceTreez/TreezExamples/resources/example.sqlite");
		tableImport.tableName.set("example");
		tableImport.customJobId.set("31");
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
		Table table = data.createTable("table");
		TableSource TableSource = table.createTableSource("TableSource");
		TableSource.sourceType.set(TableSourceType.SQLITE);
		TableSource.filePath.set("D:/EclipseJava/workspaceTreez/TreezExamples/resources/example.sqlite");
		TableSource.tableName.set("example");
		TableSource.jobId.set("30");
		TableSource.useCustomQuery.set(true);
		TableSource.customQuery.set("select * from example where id = {$jobId$}");

		OutputAtom sweep0Output = data.createOutputAtom("sweep0Output");

		SweepProbe sweepProbe0 = data.createSweepProbe("sweepProbe0");
		sweepProbe0.domainRange.set("root.studies0.sweep0.integerRange0");
		sweepProbe0.firstFamilyRange.set("root.studies0.sweep0.integerRange1");
		sweepProbe0.secondFamilyRange.set(null);
		sweepProbe0.sweepOutput.set("root.results.data.sweep0Output");
		sweepProbe0.firstProbeTable.set("root.results.data.sweep0Output.sweep0OutputId1.tableImportOutput");

		Table sweepProbe0Table = sweepProbe0.createTable("sweepProbe0Table");
		Columns columnsColumnsInSweepProbe0Table = sweepProbe0Table.createColumns("columns");
		Column x = columnsColumnsInSweepProbe0Table.createColumn("x");
		x.columnType.set(ColumnType.INTEGER);
		x.defaultValueString.set("null");
		x.legend.set("x");

		Column y_1 = columnsColumnsInSweepProbe0Table.createColumn("y_1");
		y_1.columnType.set(ColumnType.INTEGER);
		y_1.defaultValueString.set("null");
		y_1.legend.set("family1: 1");

		Column y_2 = columnsColumnsInSweepProbe0Table.createColumn("y_2");
		y_2.columnType.set(ColumnType.INTEGER);
		y_2.defaultValueString.set("null");
		y_2.legend.set("family1: 2");

		Column y_3 = columnsColumnsInSweepProbe0Table.createColumn("y_3");
		y_3.columnType.set(ColumnType.INTEGER);
		y_3.defaultValueString.set("null");
		y_3.legend.set("family1: 3");

		sweepProbe0Table.addRow(1, 1, 3, 2);
		sweepProbe0Table.addRow(2, 4, 6, 5);
		sweepProbe0Table.addRow(3, 7, 9, 8);
		sweepProbe0Table.addRow(4, 10, 12, 11);
		sweepProbe0Table.addRow(5, 13, 15, 14);
		sweepProbe0Table.addRow(6, 16, 18, 17);
		sweepProbe0Table.addRow(7, 19, 21, 20);
		sweepProbe0Table.addRow(8, 22, 24, 23);
		sweepProbe0Table.addRow(9, 25, 27, 26);
		sweepProbe0Table.addRow(10, 28, 30, 29);

		Page page0 = results.createPage("page0");
		Graph graph0 = page0.createGraph("graph0");
		XySeries xySeries0 = graph0.createXySeries("xySeries0");
		xySeries0.sourceTable.set("root.results.data.sweepProbe0.sweepProbe0Table");
		xySeries0.domainLabel.set("x");
		xySeries0.rangeLabel.set("y");
		xySeries0.colorMap.set("bluegreen-step");

		Xy y_1Xy = xySeries0.createXy("y_1");
		y_1Xy.data.xData.set("root.results.data.sweepProbe0.sweepProbe0Table.columns.x");
		y_1Xy.data.yData.set("root.results.data.sweepProbe0.sweepProbe0Table.columns.y_1");
		y_1Xy.data.legendText.set("y_1");
		y_1Xy.data.xAxis.set("root.results.page0.graph0.xAxis");
		y_1Xy.data.yAxis.set("root.results.page0.graph0.yAxis");
		y_1Xy.line.color.set("#1f77b4");
		y_1Xy.symbol.fillColor.set("#1f77b4");
		y_1Xy.symbol.hideLine.set(true);

		Xy y_2Xy = xySeries0.createXy("y_2");
		y_2Xy.data.xData.set("root.results.data.sweepProbe0.sweepProbe0Table.columns.x");
		y_2Xy.data.yData.set("root.results.data.sweepProbe0.sweepProbe0Table.columns.y_2");
		y_2Xy.data.legendText.set("y_2");
		y_2Xy.data.xAxis.set("root.results.page0.graph0.xAxis");
		y_2Xy.data.yAxis.set("root.results.page0.graph0.yAxis");
		y_2Xy.line.color.set("#ff7f0e");
		y_2Xy.symbol.fillColor.set("#ff7f0e");
		y_2Xy.symbol.hideLine.set(true);

		Xy y_3Xy = xySeries0.createXy("y_3");
		y_3Xy.data.xData.set("root.results.data.sweepProbe0.sweepProbe0Table.columns.x");
		y_3Xy.data.yData.set("root.results.data.sweepProbe0.sweepProbe0Table.columns.y_3");
		y_3Xy.data.legendText.set("y_3");
		y_3Xy.data.xAxis.set("root.results.page0.graph0.xAxis");
		y_3Xy.data.yAxis.set("root.results.page0.graph0.yAxis");
		y_3Xy.line.color.set("#2ca02c");
		y_3Xy.symbol.fillColor.set("#2ca02c");
		y_3Xy.symbol.hideLine.set(true);

		Axis xAxis = graph0.createAxis("xAxis");
		xAxis.data.label.set("x");

		Axis yAxis = graph0.createAxis("yAxis");
		yAxis.data.label.set("y");
		yAxis.data.direction.set(Direction.VERTICAL);

		//#end region

		return root;
	}
}
