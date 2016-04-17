package org.treez.core.atom.variablelist;

import java.util.List;

import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.variablefield.VariableField;

/**
 * Abstract parent class for all variable list fields
 *
 * @param <T>
 */
public abstract class AbstractVariableListField<T>
		extends
			AbstractAttributeAtom<List<T>> {

	//#region ATTRIBUTES

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public AbstractVariableListField(String name) {
		super(name);
	}

	/**
	 * Copy Constructor
	 *
	 * @param abstractVariableListFieldToCopy
	 */
	public AbstractVariableListField(
			AbstractVariableListField<T> abstractVariableListFieldToCopy) {
		super(abstractVariableListFieldToCopy);
	}

	//#end region

	//#region METHODS

	@Override
	public abstract AbstractVariableListField<T> copy();

	/**
	 * Creates a VariableField whose type corresponds to the type of this
	 * VariableListField
	 *
	 * @return
	 */
	public abstract VariableField<T> createVariableField();

	//#end region

	//#region ACCESSORS

	@Override
	public abstract List<T> get();

	@Override
	public abstract void set(List<T> valueList);

	/**
	 * Sets the label
	 *
	 * @param newLabel
	 */
	public abstract void setLabel(String newLabel);

	//#end region

}
