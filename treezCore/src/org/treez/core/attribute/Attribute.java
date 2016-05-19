package org.treez.core.attribute;

import javafx.beans.value.ObservableValue;

/**
 * Represents an attribute
 */
public interface Attribute<T> extends ObservableValue<T> {

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
