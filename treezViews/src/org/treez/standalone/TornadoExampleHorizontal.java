package org.treez.standalone;

import org.treez.core.scripting.ModelProvider;
import org.treez.data.column.Columns;
import org.treez.data.table.nebula.Table;
import org.treez.results.atom.axis.Axis;
import org.treez.results.atom.axis.AxisMode;
import org.treez.results.atom.data.Data;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.page.Page;
import org.treez.results.atom.results.Results;
import org.treez.results.atom.tornado.Tornado;
import org.treez.views.tree.rootAtom.Root;

public class TornadoExampleHorizontal extends ModelProvider {

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
		tornado.data.inputAxis.set("root.results.page.graph.yAxis");
		tornado.data.outputAxis.set("root.results.page.graph.xAxis");

		tornado.data.inputLabel.set("root.results.data.table.columns.domainLabel");
		tornado.data.inputBase.set("root.results.data.table.columns.domainBase");
		tornado.data.inputLeft.set("root.results.data.table.columns.domainLeft");
		tornado.data.inputRight.set("root.results.data.table.columns.domainRight");
		tornado.data.inputUnit.set("root.results.data.table.columns.domainUnit");

		tornado.data.outputBase.set("root.results.data.table.columns.rangeBase");
		tornado.data.outputLeft.set("root.results.data.table.columns.rangeLeft");
		tornado.data.outputRight.set("root.results.data.table.columns.rangeRight");

		Axis xAxis = graph.createAxis("xAxis");
		xAxis.data.direction.set("horizontal");
		xAxis.data.min.set(-100.0);
		xAxis.data.max.set(100.0);

		Axis yAxis = graph.createAxis("yAxis");
		yAxis.data.direction.set("vertical");
		yAxis.data.mode.set(AxisMode.ORDINAL.toString());

		graph.createLegend("legend");

		//#end region

		return root;
	}
}
