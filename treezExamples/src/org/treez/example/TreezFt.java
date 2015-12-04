package org.treez.example;

import org.treez.core.scripting.ModelProvider;
import org.treez.model.atom.Models;
import org.treez.views.tree.rootAtom.Root;

public class TreezFt extends ModelProvider {

	@Override
	public Root createModel() {

		Root root = new Root("root");

		//#region MODELS

		Models models = root.createModels("models");
		//#end region

		return root;
	}
}
