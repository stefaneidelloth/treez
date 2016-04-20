package org.treez.core.octave;

/**
 * Stores information about a single octave command
 *
 */
public class OctaveLabelInformation {

	//#region ATTRIBUTES

	/**
	 * The octave line number
	 */
	private int lineNumber;

	/**
	 * The start index of the octave label
	 */
	private int startIndex;

	/**
	 * The end index of the octave label
	 */
	private int endIndex;

	//#end region

	//#region CONSTRUCTORS

	public OctaveLabelInformation(int lineNumber, int startIndex,
			int endIndex) {
		super();
		this.lineNumber = lineNumber;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	//#end region

	//#region ACCESSORS

	public int getLineNumber() {
		return lineNumber;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	//#end region

}
