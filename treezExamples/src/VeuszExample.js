var imports = new JavaImporter(
	    java.util,
	    org.treez.views.treeView.rootAtom,
	    org.treez.core.atom.attribute,
	    org.treez.core.atom.adjustable,
	    org.treez.core.atom.definition,
	    org.treez.data,
	    org.treez.data.table,
	    org.treez.plotting
	  );

with (imports) {

	root = new Root("root");
	
	page = new Page("Page");
	root.addChild(page);
	
	adjustable = new AdjustableAtom("adjustable");
	root.addChild(adjustable);
	
	//javax.swing.JOptionPane.showMessageDialog(null, "message");

}