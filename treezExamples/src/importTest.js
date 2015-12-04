
var imports = new JavaImporter(
	    java.util,
	    org.treez.views.treeView.rootAtom,
	    org.treez.core.atom.attribute,
	    org.treez.core.atom.adjustable,
	    org.treez.plotting							   
	  ); 

with (imports) {	
	
	
	root = new Root("root");	
	
	page = new Page("Page");
	root.addChild(page);
	
	
} 

