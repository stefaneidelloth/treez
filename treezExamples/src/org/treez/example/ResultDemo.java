package org.treez.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.treez.core.scripting.ModelProvider;
import org.treez.data.column.Column;
import org.treez.data.table.Table;
import org.treez.results.atom.axis.Axis;
import org.treez.results.atom.axis.Direction;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.page.Page;
import org.treez.results.atom.results.Results;
import org.treez.results.atom.xy.Xy;
import org.treez.views.tree.rootAtom.Root;

public class ResultDemo extends ModelProvider {

	@SuppressWarnings({ "checkstyle:executablestatementcount", "checkstyle:javancss" })
	@Override
	public Root createModel() {

		Root root = new Root("root");

		//results------------------------------------------------------------
		Results results = new Results("results");
		root.addChild(results);

		//create data table with two columns---------------------------------
		org.treez.results.atom.data.Data data = new org.treez.results.atom.data.Data("data");
		results.addChild(data);

		Table table = new Table("table");
		data.addChild(table);

		Column xColumn = new Column("x", "Double");
		table.addColumn(xColumn);

		Column yColumn = new Column("y", "Double");
		table.addColumn(yColumn);

		//add some data to the data table
		List<List<Object>> tableData = new ArrayList<>();
		tableData.add(Arrays.asList(1.0, 10.0));
		tableData.add(Arrays.asList(2.0, 20.0));
		tableData.add(Arrays.asList(3.0, 30.0));
		tableData.add(Arrays.asList(4.0, 40.0));
		tableData.add(Arrays.asList(5.0, 50.0));
		tableData.add(Arrays.asList(6.0, 60.0));
		tableData.add(Arrays.asList(7.0, 70.0));
		tableData.add(Arrays.asList(8.0, 80.0));
		tableData.add(Arrays.asList(9.0, 90.0));
		tableData.add(Arrays.asList(10.0, 20.0));
		table.addRows(tableData);

		//create plot page--------------------------------------------
		Page page = new Page("page");
		results.addChild(page);

		Graph graph = page.createGraph("graph");

		Axis xAxis = graph.createAxis("x");
		xAxis.data.autoMin.set(false);
		xAxis.data.min.set(0.0);

		xAxis.data.autoMax.set(false);
		xAxis.data.max.set(10.0);

		Axis yAxis = new Axis("y", Direction.VERTICAL);
		graph.addChild(yAxis);

		yAxis.data.autoMin.set(false);
		yAxis.data.min.set(0.0);

		yAxis.data.autoMax.set(false);
		yAxis.data.max.set(100.0);

		Xy xy = new Xy("xy plot");
		graph.addChild(xy);

		xy.data.xAxis.set("root.results.page.graph.x");
		xy.data.yAxis.set("root.results.page.graph.y");

		xy.data.xData.set("root.results.data.table.columns.x");
		xy.data.yData.set("root.results.data.table.columns.y");

		return root;

	}
}
