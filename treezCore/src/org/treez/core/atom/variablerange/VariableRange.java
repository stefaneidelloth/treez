package org.treez.core.atom.variablerange;

import java.util.List;

/**
 * Represent a range, e.g. in a sweep
 *
 * @param <T>
 */
public interface VariableRange<T> {

	/**
	 * Returns the range values as a list
	 *
	 * @return
	 */
	List<T> getRange();

	/**
	 * Returns the type of the range
	 * 
	 * @return
	 */
	Class<T> getType();

}
