package org.treez.core.atom.attribute;

import org.apache.log4j.Logger;

/**
 * Allows to choose a size in pt with a combo box, e.g. 1pt or 10pt
 */
public class Size extends ComboBox {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings({ "hiding", "unused" })
	private static Logger sysLog = Logger.getLogger(Size.class);

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public Size(String name) {
		super(name);
		setItems("0pt,0.25pt,0.5pt,1pt,1.5pt,2pt,3pt,4pt,5pt,6pt,8pt,10pt,12pt,14pt,16pt,18pt,20pt,"
				+ "22pt,24pt,26pt,28pt,30pt,34pt,40pt,44pt,50pt,60pt,70pt");
		set("0.5pt");
	}

	/**
	 * Copy constructor
	 *
	 * @param sizeToCopy
	 */
	private Size(Size sizeToCopy) {
		super(sizeToCopy);
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public Size copy() {
		return new Size(this);
	}

	//#end region

	//#end region

}
