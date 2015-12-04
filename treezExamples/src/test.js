var imports = new JavaImporter(
							    java.io,
							    java.util,
							    org.treez.core
							  );
with (imports) {
	
	var currentDir = new java.io.File('.');
	print(currentDir.getCanonicalPath());
	
	load("Library.js");
	
	var descr = Math1.Description;
	print(descr);	
	
	var root = new org.treez.core.MyClass();
	
	
}