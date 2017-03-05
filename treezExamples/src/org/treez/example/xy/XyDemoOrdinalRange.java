package org.treez.example.xy;

import org.treez.core.data.column.ColumnType;
import org.treez.core.scripting.ModelProvider;
import org.treez.data.column.Column;
import org.treez.data.column.Columns;
import org.treez.data.table.nebula.Table;
import org.treez.results.atom.axis.Axis;
import org.treez.results.atom.axis.AxisMode;
import org.treez.results.atom.axis.Direction;
import org.treez.results.atom.data.Data;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.page.Page;
import org.treez.results.atom.results.Results;
import org.treez.results.atom.xy.Xy;
import org.treez.views.tree.rootAtom.Root;

public class XyDemoOrdinalRange extends ModelProvider {

	@Override
	public Root createModel() {

		Root root = new Root("root");

		//#region RESULTS

		Results results = root.createResults("results");

		Data data = results.createData("data");
		Table table = data.createTable("table");
		Columns columns = table.createColumns("columns");
		Column xColumn = columns.createColumn("x");
		xColumn.setColumnType(ColumnType.INTEGER);

		Column yColumn = columns.createColumn("y");
		yColumn.setColumnType(ColumnType.STRING);

		table.addRow(1, "foo");
		table.addRow(2, "bar");
		table.addRow(3, "qux");

		Page page = results.createPage("page");
		Graph graph = page.createGraph("graph");

		Axis xAxis = graph.createAxis("x");

		Axis yAxis = graph.createAxis("y");
		yAxis.data.mode.set(AxisMode.ORDINAL);
		yAxis.data.direction.set(Direction.VERTICAL);

		Xy xy = graph.createXy("xy");
		xy.data.xAxis.set("root.results.page.graph.x");
		xy.data.yAxis.set("root.results.page.graph.y");

		xy.data.xData.set("root.results.data.table.columns.x");
		xy.data.yData.set("root.results.data.table.columns.y");

		//#end region

		return root;
	}
}
