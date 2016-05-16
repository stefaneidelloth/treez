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
	void set(T value);

	/**
	 * Returns the attribute value as String
	 */
	@Override
	String toString();

	void addModificationConsumer(String key, Consumer listener);

	void addModificationConsumerAndRun(String key, Consumer listener);

}
