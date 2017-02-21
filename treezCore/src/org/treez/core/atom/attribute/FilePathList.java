package org.treez.core.atom.attribute;

import org.treez.core.atom.list.TreezListAtom;
import org.treez.core.data.column.ColumnType;

/**
 * Allows to edit a list of file paths
 */
public class FilePathList extends StringList {

	//#region CONSTRUCTORS

	public FilePathList(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 */
	protected FilePathList(FilePathList atomToCopy) {
		super(atomToCopy);
	}

	//#end region

	//#region METHODS

	@Override
	public FilePathList getThis() {
		return this;
	}

	/**
	 * Creates a treez list that contains Strings/text
	 */
	@Override
	protected void createTreezList() {
		treezList = new TreezListAtom("treezList");
		treezList.setColumnType(ColumnType.STRING);
		treezList.setShowHeader(false);
		treezList.enableFilePathButton();
	}

	//#end region

}
