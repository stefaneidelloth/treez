package org.treez.example;

import org.treez.core.data.column.ColumnType;
import org.treez.core.scripting.ModelProvider;
import org.treez.data.column.Columns;
import org.treez.data.table.nebula.Table;
import org.treez.results.atom.axis.Axis;
import org.treez.results.atom.data.Data;
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
		Results results = root.createResults("results");

		//create data table with two columns---------------------------------
		Data data = results.createData("data");
		Table table = new Table("table");
		data.addChild(table);
		Columns columns = table.createColumns("columns");
		columns.createColumn("x", ColumnType.DOUBLE);
		columns.createColumn("y", ColumnType.DOUBLE);

		//add some data to the data table
		table.addRow(1.0, 10.0);
		table.addRow(2.0, 20.0);
		table.addRow(3.0, 30.0);
		table.addRow(4.0, 40.0);
		table.addRow(5.0, 50.0);
		table.addRow(6.0, 60.0);
		table.addRow(7.0, 70.0);
		table.addRow(8.0, 80.0);
		table.addRow(9.0, 90.0);
		table.addRow(10.0, 20.0);

		//create plot page--------------------------------------------
		Page page = results.createPage("page");
		Graph graph = page.createGraph("graph");

		Axis x = graph.createAxis("x");
		x.data.autoMin.set(false);
		x.data.autoMax.set(false);
		x.data.max.set(10.0);

		Axis y = graph.createAxis("y");
		y.data.direction.set("vertical");
		y.data.autoMin.set(false);
		y.data.autoMax.set(false);
		y.data.max.set(100.0);

		Xy xyplot = graph.createXy("xy plot");
		xyplot.data.xData.set("root.results.data.table.columns.x");
		xyplot.data.yData.set("root.results.data.table.columns.y");
		xyplot.data.xAxis.set("root.results.page.graph.x");
		xyplot.data.yAxis.set("root.results.page.graph.y");

		return root;

	}
}
