package org.treez.core.octave;

/**
 * Implementing classes handle the output- and error- strings from octave process
 */
public abstract class OctaveProcessHandler {

	//#region METHODS

	/**
	 * Handle output
	 * 
	 * @param outputString
	 */
	public abstract void handleOutput(String outputString);

	/**
	 * Handle error
	 * 
	 * @param errorString
	 */
	public abstract void handleError(String errorString);

	//#end region

}
