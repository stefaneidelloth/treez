package org.treez.example;

import org.treez.core.scripting.ModelProvider;
import org.treez.data.variable.VariableDefinition;
import org.treez.views.tree.rootAtom.Root;

public class DynaClass extends ModelProvider {

	@Override
	public Root createModel() {

		Root root = new Root("root");

		//!! This example requires Octave to be installed
		//!! since Octave is applied  for symbolic calculations and unit conversion.

		VariableDefinition defItem = new VariableDefinition("defItem");
		root.addChild(defItem);

		defItem.define("a", "[1, 2; 3, 55]*u.m", "");
		defItem.define("b", "a * u.s", "");
		defItem.define("c", "a/b", "");
		defItem.define("d", "a+b", "");

		return root;

	}
}
