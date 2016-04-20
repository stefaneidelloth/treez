package org.treez.example;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.scripting.ModelProvider;
import org.treez.data.variable.VariableDefinition;

public class VariableDefinitionDemo extends ModelProvider {

	@Override
	public AttributeRoot createModel() {
		AttributeRoot root = new AttributeRoot("root");

		VariableDefinition defItem = new VariableDefinition("defItem");
		root.addChild(defItem);

		defItem.define("x", "1", "");
		defItem.define("y", "2", "");
		defItem.define("z", "x+y", "");

		defItem.define("a", "[1, 2; 3, 55]*u.m", "");
		defItem.define("b", "a * u.s", "");
		defItem.define("c", "a/b", "");
		defItem.define("d", "a+b", "");

		return root;

	}
}
