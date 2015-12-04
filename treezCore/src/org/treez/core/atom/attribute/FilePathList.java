package org.treez.core.atom.attribute;

import org.apache.log4j.Logger;
import org.treez.core.atom.list.TreezListAtom;
import org.treez.core.data.column.ColumnType;

/**
 * Allows to edit a list of file paths
 */
public class FilePathList extends StringList {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings({ "hiding", "unused" })
	private static Logger sysLog = Logger.getLogger(FilePathList.class);

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public FilePathList(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 *
	 * @param atomToCopy
	 */
	protected FilePathList(FilePathList atomToCopy) {
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
		treezList.enableFilePathButton();
	}

	//#end region

}
