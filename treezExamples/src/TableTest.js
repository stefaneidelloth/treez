var imports = new JavaImporter(
		org.treez.views.treeView.rootAtom,
		org.treez.data.table, 
		org.treez.data.column
		);

with (imports) {
	
	root = new Root('root');
	table = new Table('table');
	root.addChild(table);
	column = new Column('id');
	table.addColumn(column);
	
}