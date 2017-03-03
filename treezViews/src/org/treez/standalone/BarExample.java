package org.treez.standalone;

import org.treez.core.scripting.ModelProvider;
import org.treez.data.column.Columns;
import org.treez.data.table.nebula.Table;
import org.treez.results.atom.axis.Axis;
import org.treez.results.atom.axis.Direction;
import org.treez.results.atom.bar.Bar;
import org.treez.results.atom.data.Data;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.page.Page;
import org.treez.results.atom.results.Results;
import org.treez.views.tree.rootAtom.Root;

public class BarExample extends ModelProvider {

	@Override
	public Root createModel() {

		Root root = new Root("root");

		//#region RESULTS0

		Results results0 = root.createResults("results");
		Data data0 = results0.createData("data");
		Table table0 = data0.createTable("table");
		Columns columns0 = table0.createColumns("columns");
		columns0.createColumn("x");
		columns0.createColumn("y1");
		columns0.createColumn("y2");

		table0.addRow("0", "0", "0");
		table0.addRow("1", "1", "10");
		table0.addRow("2", "2", "20");
		table0.addRow("3", "3", "30");

		Page page = results0.createPage("page");
		Graph graph = page.createGraph("graph");
		Bar bar = graph.createBar("bar");
		bar.data.horizontalAxis.set("root.results.page.graph.xAxis");
		bar.data.verticalAxis.set("root.results.page.graph.yAxis");

		bar.data.barLengths.set("root.results.data.table.columns.x");
		bar.data.barPositions.set("root.results.data.table.columns.y1");

		Axis xAxis = graph.createAxis("xAxis");
		xAxis.data.direction.set(Direction.HORIZONTAL);
		xAxis.data.max.set(4.0);

		Axis yAxis = graph.createAxis("yAxis");
		yAxis.data.direction.set(Direction.VERTICAL);
		yAxis.data.max.set(4.0);

		graph.createLegend("legend");

		//#end region

		return root;
	}
}
