package org.treez.standalone;

import org.treez.core.scripting.ModelProvider;
import org.treez.data.column.Columns;
import org.treez.data.table.Table;
import org.treez.results.atom.data.Data;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.page.Page;
import org.treez.results.atom.results.Results;
import org.treez.results.atom.xyseries.XySeries;
import org.treez.views.tree.rootAtom.Root;

public class XySeriesExample extends ModelProvider {

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

		Page page0 = results0.createPage("page");
		Graph graph0 = page0.createGraph("graph");
		XySeries xySeries0 = graph0.createXySeries("xySeries");
		xySeries0.sourceTable.set("root.results.data.table");
		xySeries0.domainLabel.set("x");
		xySeries0.rangeLabel.set("y");

		graph0.createLegend("legend");

		//#end region

		return root;
	}
}
