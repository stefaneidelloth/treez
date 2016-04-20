package org.treez.core.attribute;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Default implementation of the AttributeWrapper interface
 */
public class Wrap<T> implements AttributeWrapper<T> {

	//#region ATTRIBUTES

	private Attribute<T> wrappedAttribute;

	//#end region

	//#region CONSTRUCTORS

	public Wrap() {
	}

	public Wrap(Attribute<T> wrappedAttribute) {
		this.wrappedAttribute = wrappedAttribute;
	}

	//#end region

	//#region ACCESSORS

	@Override
	public T get() {
		Objects.requireNonNull(wrappedAttribute,
				"Wrapped attribute must be set before calling this method.");
		T value = wrappedAttribute.get();
		return value;
	}

	@Override
	public void set(T value) {
		Objects.requireNonNull(wrappedAttribute,
				"Wrapped attribute must be set before calling this method.");
		wrappedAttribute.set(value);
	}

	@Override
	public String toString() {
		Objects.requireNonNull(wrappedAttribute,
				"Wrapped attribute must be set before calling this method.");
		String valueString = wrappedAttribute.toString();
		return valueString;
	}

	@Override
	public Attribute<T> getAttribute() {
		return wrappedAttribute;
	}

	@Override
	public void setAttribute(Attribute<T> wrappedAttribute) {
		this.wrappedAttribute = wrappedAttribute;
	}

	@Override
	public void addModificationConsumer(String key, Consumer<T> consumer) {
		wrappedAttribute.addModificationConsumer(key, consumer);
	}

	@Override
	public void addModificationConsumerAndRun(String key,
			Consumer<T> consumer) {
		wrappedAttribute.addModificationConsumer(key, consumer);
		consumer.accept(null);
	}

	//#end region

}
