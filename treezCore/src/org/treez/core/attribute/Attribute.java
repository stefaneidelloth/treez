package org.treez.core.attribute;

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
	Attribute<T> set(T value);

	/**
	 * Returns the attribute value as String
	 */
	@Override
	String toString();

	<C extends Attribute<T>> C addModificationConsumer(String key, Consumer listener);

	<C extends Attribute<T>> C addModificationConsumerAndRun(String key, Consumer listener);

}
