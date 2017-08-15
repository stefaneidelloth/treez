package org.treez.example.genericInput;

import org.treez.core.atom.variablefield.DoubleVariableField;
import org.treez.core.scripting.ModelProvider;
import org.treez.model.atom.Models;
import org.treez.model.atom.genericInput.GenericInputModel;
import org.treez.views.tree.rootAtom.Root;

public class GenericInputDemo extends ModelProvider {

	@Override
	public Root createModel() {

		Root root = new Root("root");

		Models models = new Models("models");
		root.addChild(models);

		GenericInputModel genericModel = new GenericInputModel("genericModel");
		models.addChild(genericModel);

		DoubleVariableField x = new DoubleVariableField("x");
		x.setValueString("10");
		genericModel.addChild(x);

		DoubleVariableField y = new DoubleVariableField("y");
		y.setValueString("20");
		genericModel.addChild(y);

		return root;

	}
}
