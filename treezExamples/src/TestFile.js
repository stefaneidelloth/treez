var imports = new JavaImporter(
							    java.util,
							    org.treez.views.treeView.rootAtom,
							    org.treez.core.atom.adjustable,
							    org.treez.core.atom.definition,
							    org.treez.data,
							    org.treez.data.table
							  );
with (imports) {

	root = new Root("root");
	
	
	adjustableAtom = new AdjustableAtom("adjustableAtom");
	root.addChild(adjustableAtom);
	
	defItem = new VariableDefinition("defItem");
	root.addChild(defItem);
	
	defItem.define("a","[1, 2; 3, 55]*u.m","");
	defItem.define("b","a * u.s","");
	defItem.define("c","a/b","");
	defItem.define("d","a+b","");
	
}