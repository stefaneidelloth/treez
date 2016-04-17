package org.treez.core.atom.variablefield;

import org.treez.core.atom.variablelist.AbstractVariableListField;

/**
 * Represents a variable field
 */
/**
 *
 */
public interface VariableField<T> {

	/**
	 * Returns the name
	 *
	 * @return
	 */
	String getName();

	/**
	 * Returns the label
	 *
	 * @return
	 */
	String getLabel();

	/**
	 * Sets the label
	 *
	 * @param newLabel
	 */
	void setLabel(String newLabel);

	/**
	 * Returns the value string
	 *
	 * @return
	 */
	String getValueString();

	/**
	 * Sets the value string
	 *
	 * @param valueString
	 */
	void setValueString(String valueString);

	/**
	 * Returns true if the variable field is enabled
	 *
	 * @return
	 */
	boolean isEnabled();

	/**
	 * Sets the enabled state
	 *
	 * @param state
	 */
	void setEnabled(boolean state);

	/**
	 * Returns the variable value
	 *
	 * @return
	 */
	T get();

	/**
	 * Creates a VariableListField whose variable type is the same as the type
	 * of this VariableField
	 * 
	 * @return
	 */
	AbstractVariableListField<T> createVariableListField();

}
