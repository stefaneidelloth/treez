package org.treez.core.attribute;

import java.util.function.Consumer;

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

	/**
	 * Adds an modify listener * @param listener
	 */
	void addModificationConsumer(Consumer<T> listener);

}
