package org.treez.core.attribute;

import java.util.function.Consumer;

/**
 * Represents an attribute
 */
public interface Attribute<T> {

	/**
	 * Returns the attribute value
	 */
	T get();

	/**
	 * Sets the attribute value
	 */
	void set(T value);

	/**
	 * Returns the attribute value as String
	 */
	@Override
	String toString();

	/**
	 * Adds a modify listener
	 */
	void addModificationConsumer(String key, Consumer<T> listener);

	/**
	 * Adds a modify listener and runs it once with null as argument
	 */
	void addModificationConsumerAndRun(String key, Consumer<T> listener);

}
