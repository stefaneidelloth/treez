package org.treez.example.root;

import org.treez.core.scripting.ModelProvider;
import org.treez.views.tree.rootAtom.Root;

public class RootDemo extends ModelProvider {

	@Override
	public Root createModel() {

		Root root = new Root("root");

		return root;

	}
}
