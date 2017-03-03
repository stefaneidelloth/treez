package org.treez.example.bar;

import org.treez.core.data.column.ColumnType;
import org.treez.core.scripting.ModelProvider;
import org.treez.data.column.Column;
import org.treez.data.column.Columns;
import org.treez.data.table.nebula.Table;
import org.treez.results.atom.axis.Axis;
import org.treez.results.atom.axis.AxisMode;
import org.treez.results.atom.axis.Direction;
import org.treez.results.atom.bar.Bar;
import org.treez.results.atom.data.Data;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.page.Page;
import org.treez.results.atom.results.Results;
import org.treez.views.tree.rootAtom.Root;

public class BarDemoOrdinalRangeHorizontalBar extends ModelProvider {

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

		Column yColumn = columns.createColumn("category");
		yColumn.setColumnType(ColumnType.STRING);

		table.addRow(1, "foo");
		table.addRow(4, "bar");
		table.addRow(6, "qux");

		Page page = results.createPage("page");
		Graph graph = page.createGraph("graph");

		Axis xAxis = graph.createAxis("x");

		Axis categoryAxis = graph.createAxis("category");
		categoryAxis.data.mode.set(AxisMode.ORDINAL);
		categoryAxis.data.direction.set(Direction.VERTICAL);

		Bar bar = graph.createBar("bar");
		bar.data.barDirection.set(Direction.HORIZONTAL);
		bar.data.horizontalAxis.set("root.results.page.graph.x");
		bar.data.verticalAxis.set("root.results.page.graph.category");

		bar.data.barPositions.set("root.results.data.table.columns.x");
		bar.data.barLengths.set("root.results.data.table.columns.category");

		bar.fill.color.set("#00ff00");

		//#end region

		return root;
	}
}
