package org.treez.example.xy;

import org.treez.core.scripting.ModelProvider;
import org.treez.data.column.Columns;
import org.treez.data.table.nebula.Table;
import org.treez.results.atom.data.Data;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.page.Page;
import org.treez.results.atom.results.Results;
import org.treez.results.atom.xySeries.XySeries;
import org.treez.views.tree.rootAtom.Root;

public class XySeriesDemo extends ModelProvider {

	@Override
	public Root createModel() {

		Root root = new Root("root");

		//this example does not work yet since the xy series is not finished

		//#region RESULTS0

		Results results0 = root.createResults("results0");
		Data data0 = results0.createData("data0");
		Table table0 = data0.createTable("table0");
		Columns columns0 = table0.createColumns("columns0");
		columns0.createColumn("x");
		columns0.createColumn("y1");
		columns0.createColumn("y2");

		table0.addRow("0", "0", "0");
		table0.addRow("1", "1", "10");
		table0.addRow("2", "2", "20");
		table0.addRow("3", "3", "30");

		Page page0 = results0.createPage("page0");
		Graph graph0 = page0.createGraph("graph0");
		XySeries xySeries0 = graph0.createXySeries("xySeries0");
		xySeries0.sourceTable.set("root.results0.data0.table0");

		//#end region

		return root;
	}
}
