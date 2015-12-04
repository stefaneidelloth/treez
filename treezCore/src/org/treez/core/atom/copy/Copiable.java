package org.treez.core.atom.copy;

/**
 * Can be deeply copied with the method copy()
 * 
 * @param <T>
 *
 */
public interface Copiable<T> {

	/**
	 * Deeply copies the Copiable
	 * 
	 * @return
	 */
	T copy();

}
