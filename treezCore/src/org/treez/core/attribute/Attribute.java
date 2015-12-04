package org.treez.core.attribute;

/**
 * Represents an attribute
 *
 * @param <T>
 */
public interface Attribute<T> {

	/**
	 * Returns the attribute value
	 *
	 * @return
	 */
	T get();

	/**
	 * Sets the attribute value
	 *
	 * @param value
	 */
	void set(T value);

	/**
	 * Returns the attribute value as String
	 *
	 * @return
	 */
	@Override
	String toString();

}
