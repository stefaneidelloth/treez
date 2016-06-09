package org.treez.standalone;

import org.treez.core.scripting.ModelProvider;
import org.treez.data.column.Columns;
import org.treez.data.table.Table;
import org.treez.results.atom.axis.Axis;
import org.treez.results.atom.data.Data;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.page.Page;
import org.treez.results.atom.results.Results;
import org.treez.results.atom.tornado.Tornado;
import org.treez.views.tree.rootAtom.Root;

public class TornadoExample extends ModelProvider {

	@Override
	public Root createModel() {

		Root root = new Root("root");

		//#region RESULTS0

		Results results0 = root.createResults("results");
		Data data0 = results0.createData("data");
		Table table0 = data0.createTable("table");
		Columns columns0 = table0.createColumns("columns");
		columns0.createColumn("domainLabel");
		columns0.createColumn("domainBase");
		columns0.createColumn("domainLeft");
		columns0.createColumn("domainRight");
		columns0.createColumn("domainUnit");
		columns0.createColumn("rangeBase");
		columns0.createColumn("rangeLeft");
		columns0.createColumn("rangeRight");

		table0.addRow("Number of Years", "2", "1", "3", "", "29.5", "15", "40");
		table0.addRow("Annual Costs", "3", "2", "4", "€", "29.5", "31", "28");
		table0.addRow("Annual Revenue", "20", "10", "30", "€", "29.5", "12.5", "46");

		Page page = results0.createPage("page");
		Graph graph = page.createGraph("graph");
		Tornado tornado = graph.createTornado("tornado");
		tornado.data.domainAxis.set("root.results.page.graph.xAxis");
		tornado.data.rangeAxis.set("root.results.page.graph.yAxis");

		tornado.data.domainLabel.set("root.results.data.table.columns.domainLabel");
		tornado.data.domainBase.set("root.results.data.table.columns.domainBase");
		tornado.data.domainLeft.set("root.results.data.table.columns.domainLeft");
		tornado.data.domainRight.set("root.results.data.table.columns.domainRight");
		tornado.data.domainUnit.set("root.results.data.table.columns.domainUnit");

		tornado.data.rangeBase.set("root.results.data.table.columns.rangeBase");
		tornado.data.rangeLeft.set("root.results.data.table.columns.rangeLeft");
		tornado.data.rangeRight.set("root.results.data.table.columns.rangeRight");

		Axis xAxis = graph.createAxis("xAxis");
		xAxis.data.direction.set("horizontal");
		xAxis.data.min.set("-100");
		xAxis.data.max.set("100");

		Axis yAxis = graph.createAxis("yAxis");
		yAxis.data.direction.set("vertical");
		yAxis.data.max.set("4");

		graph.createLegend("legend");

		//#end region

		return root;
	}
}
