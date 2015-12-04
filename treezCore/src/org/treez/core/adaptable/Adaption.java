package org.treez.core.adaptable;

/**
 * Base interface for all adaptions of the Adaptable. All Adaptions are able to return the adaptable from which they
 * originate. See the interfaces that extend this interface, e.g. "TreeNodeAdaption" for more information.
 */
public interface Adaption {

	//#region METHODS

	/**
	 * Returns the Adaptable from which this Adaption originates from.
	 * 
	 * @return
	 */
	Adaptable getAdaptable();

	//#end region

}
