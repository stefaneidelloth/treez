package org.treez.core.attribute;

/**
 * Wraps a replaceable attribute. The methods are "hidden" if this
 * AttributeWrapper is passed as its parent interface Attribute
 *
 * @param <T>
 */
public interface AttributeWrapper<T> extends Attribute<T> {

	/**
	 * Sets the wrapped Attribute
	 *
	 * @param attribute
	 */
	void setAttribute(Attribute<T> attribute);

	/**
	 * Returns the wrapped Attribute
	 *
	 * @return
	 */
	Attribute<T> getAttribute();

}
