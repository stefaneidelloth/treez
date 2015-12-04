package org.treez.example;

import org.treez.core.scripting.ModelProvider;
import org.treez.results.atom.results.Results;
import org.treez.views.tree.rootAtom.Root;

public class VeuszPlottingDemo extends ModelProvider {

	@Override
	public Root createModel() {

		Root root = new Root("root");

		//#region RESULTS

		Results results = root.createResults("results");
		results.createPage("page");

		//#end region

		return root;
	}
}
