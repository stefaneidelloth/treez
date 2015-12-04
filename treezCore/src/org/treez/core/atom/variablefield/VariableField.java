package org.treez.core.atom.variablefield;

/**
 * Represents a variable field
 */
public interface VariableField {

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

}
