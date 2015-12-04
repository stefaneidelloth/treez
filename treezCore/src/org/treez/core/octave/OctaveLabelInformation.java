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
	
	/**
	 * Constructor 
	 * @param lineNumber
	 * @param startIndex
	 * @param endIndex
	 */
	public OctaveLabelInformation(int lineNumber, int startIndex, int endIndex) {
		super();
		this.lineNumber = lineNumber;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}
	
	//#end region
	
	//#region ACCESSORS
	
	/**
	 * @return the lineNumber
	 */
	public int getLineNumber() {
		return lineNumber;
	}
	/**
	 * @return the startIndex
	 */
	public int getStartIndex() {
		return startIndex;
	}
	/**
	 * @return the endIndex
	 */
	public int getEndIndex() {
		return endIndex;
	}
	
	//#end region

}
