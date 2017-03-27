package org.treez.core.attribute;

import java.util.Objects;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;

/**
 * Default implementation of the AttributeWrapper interface
 */
public class Wrap<T> implements AttributeWrapper<T> {

	//#region ATTRIBUTES

	private Attribute<T> wrappedAttribute;

	//#end region

	//#region CONSTRUCTORS

	public Wrap() {}

	public Wrap(Attribute<T> wrappedAttribute) {
		this.wrappedAttribute = wrappedAttribute;
	}

	//#end region

	//#region ACCESSORS

	@Override
	public T get() {
		Objects.requireNonNull(wrappedAttribute, "Wrapped attribute must be set before calling this method.");
		T value = wrappedAttribute.get();
		return value;
	}

	@Override
	public T getValue() {
		return get();
	}

	@Override
	public Wrap<T> set(T value) {
		Objects.requireNonNull(wrappedAttribute, "Wrapped attribute must be set before calling this method.");
		wrappedAttribute.set(value);
		return this;
	}

	@Override
	public String toString() {
		Objects.requireNonNull(wrappedAttribute, "Wrapped attribute must be set before calling this method.");
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

	@SuppressWarnings("unchecked")
	@Override
	public <C extends Attribute<T>> C addModificationConsumer(String key, Consumer consumer) {
		wrappedAttribute.addModificationConsumer(key, consumer);
		return (C) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <C extends Attribute<T>> C addModificationConsumerAndRun(String key, Consumer consumer) {
		wrappedAttribute.addModificationConsumer(key, consumer);
		consumer.consume();
		return (C) this;
	}

	@Override
	public void addListener(ChangeListener<? super T> listener) {
		wrappedAttribute.addListener(listener);

	}

	@Override
	public void removeListener(ChangeListener<? super T> listener) {
		wrappedAttribute.removeListener(listener);
	}

	@Override
	public void addListener(InvalidationListener listener) {
		wrappedAttribute.addListener(listener);

	}

	@Override
	public void removeListener(InvalidationListener listener) {
		wrappedAttribute.removeListener(listener);
	}

	//#end region

}
