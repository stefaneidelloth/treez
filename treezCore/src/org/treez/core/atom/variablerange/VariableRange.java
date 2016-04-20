package org.treez.core.atom.variablerange;

import java.util.List;

/**
 * Represent a range, e.g. in a sweep
 */
public interface VariableRange<T> {

	/**
	 * Returns the range values as a list
	 */
	List<T> getRange();

	/**
	 * Returns the type of the range
	 */
	Class<T> getType();

}
