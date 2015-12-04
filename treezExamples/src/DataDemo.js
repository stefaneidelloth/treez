var imports = new JavaImporter(
								java.util,
								org.treez.views.treeView.rootAtom,
								org.treez.core.atom.definition,
								org.treez.core.atom.attribute,
								org.treez.data,
								org.treez.data.column,
								org.treez.data.table,
								org.treez.model.atom,
								org.treez.study.atom,
								org.treez.plotting.atom);
with (imports) {
	
	//create root node--------------------------------------------------	
	root = new Root("root");
	

	//create variable definition-----------------------------------------
	definitions = new VariableDefinition("definitions");
	root.addChild(definitions);
		
	//models-------------------------------------------------------------
	models = new Models("models");
	root.addChild(models);
	
	
	
	
	//generic model
	genericModel = new GenericModel("genericModel");
	models.addChild(genericModel);
	
	x = new VariableField("x");
	x.setValue("10");
	genericModel.addChild(x);
	
	y = new VariableField("y");
	y.setValue("20");
	genericModel.addChild(y);
	
	//executable
	inputFilePath = "D:/runtime-EclipseApplication/TreezExamples/inputFile.txt";
	importFilePath = "D:/runtime-EclipseApplication/TreezExamples/importData.txt";
	
	executable = new Executable("executable");
	executable.setExecutablePath("D:/runtime-EclipseApplication/TreezExamples/executable.bat");
	executable.setInputFilePath(inputFilePath);
	executable.setOutputFilePath(importFilePath);
	models.addChild(executable);
		
	inputFile = new InputFile("inputFile");
	inputFile.setTemplateFilePath("D:/runtime-EclipseApplication/TreezExamples/template.txt");
	inputFile.setInputFilePath(inputFilePath);
	inputFile.setValueExpression("<value>");
	executable.addChild(inputFile);
		
	dataImport = new DataImport("dataImport");
	dataImport.setImportFilePath(importFilePath);
	dataImport.setResultTableModelPath("root.results.data.table");
	executable.addChild(dataImport);
	
	//studies------------------------------------------------------------
	studies = new Studies("studies");
	root.addChild(studies);
	
	sweep = new Sweep("sweep");
	studies.addChild(sweep);
	
	sensitivity = new Sensitivity("sensitivity");
	studies.addChild(sensitivity);
	
	//results------------------------------------------------------------
	results = new Results("results");
	root.addChild(results);
	
	//create data table with two columns---------------------------------
	data = new org.treez.data.Data("data");
	results.addChild(data);	
	
	table = new Table("table");
	data.addChild(table);
	
    /*
	
	x = new Column("x", "Double");
	table.addColumn(x);
	
	y = new Column("y", "Double");
	table.addColumn(y);	
	
	
	
	//add some data to the data table		
	table.addRows([
					[1.0, 10.0],
					[2.0, 20.0],
					[3.0, 30.0],
					[4.0, 40.0],
					[5.0, 50.0],
					[6.0, 60.0],
					[7.0, 70.0],
					[8.0, 80.0],
					[9.0, 90.0],
					[10.0,20.0],
				]);	
	*/
	
	//create plot page--------------------------------------------
	page = new Page("page");
	results.addChild(page);
	
	graph = new Graph("graph");
	page.addChild(graph);
	
	xAxis = new Axis("x");
	graph.addChild(xAxis);
	
	yAxis = new Axis("y", "vertical");
	graph.addChild(yAxis);
	
	xy = new XY("xy plot");
	graph.addChild(xy);
	
	
	
	
	
}