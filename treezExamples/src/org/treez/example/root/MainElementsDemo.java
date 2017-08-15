package org.treez.example.root;

import org.treez.core.scripting.ModelProvider;
import org.treez.views.tree.rootAtom.Root;

public class MainElementsDemo extends ModelProvider {

	@Override
	public Root createModel() {

		Root root = new Root("root");

		//#region MODELS

		root.createModels("models");
		//#end region

		//#region STUDIES

		root.createStudies("studies");
		//#end region

		//#region RESULTS

		root.createResults("results");
		//#end region

		return root;
	}
}
