package org.treez.core.atom.attribute;

import org.treez.core.atom.list.TreezListAtom;
import org.treez.core.data.column.ColumnType;

public class DirectoryPathList extends StringList {

	//#region CONSTRUCTORS

	public DirectoryPathList(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 */
	protected DirectoryPathList(DirectoryPathList atomToCopy) {
		super(atomToCopy);
	}

	//#end region

	//#region METHODS

	/**
	 * Creates a treez list that contains Strings/text
	 */
	@Override
	protected void createTreezList() {
		treezList = new TreezListAtom("treezList");
		treezList.setColumnType(ColumnType.TEXT);
		treezList.setShowHeader(false);
		treezList.enableDirectoryPathButton();
	}

	//#end region

}
