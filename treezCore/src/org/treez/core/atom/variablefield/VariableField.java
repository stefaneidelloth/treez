package org.treez.core.atom.variablefield;

import org.treez.core.atom.variablelist.AbstractVariableListField;

/**
 * Represents a variable field
 */
public interface VariableField<T> {

	String getName();

	String getLabel();

	void setLabel(String newLabel);

	String getValueString();

	void setValueString(String valueString);

	/**
	 * Returns true if the variable field is enabled
	 */
	boolean isEnabled();

	void setEnabled(boolean state);

	/**
	 * Returns the variable value
	 */
	T get();

	/**
	 * Creates a VariableListField whose variable type is the same as the type
	 * of this VariableField
	 */
	AbstractVariableListField<T> createVariableListField();

}
