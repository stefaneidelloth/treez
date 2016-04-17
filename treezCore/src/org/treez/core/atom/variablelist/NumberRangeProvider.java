package org.treez.core.atom.variablelist;

import java.util.List;

/**
 * Provides a range of numbers
 */
public interface NumberRangeProvider {

	/**
	 * Provides a range of numbers
	 *
	 * @return
	 */
	List<Number> getRange();

	/**
	 * Returns the type of the number, e.g. Double
	 *
	 * @return
	 */
	Class<? extends Number> getRangeType();

}
