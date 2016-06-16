package org.treez.core.atom.variablefield;

import org.eclipse.swt.graphics.Color;
import org.treez.core.atom.variablelist.AbstractVariableListField;

/**
 * Represents a variable field
 */
public interface VariableField<A, T> {

	String getName();

	String getLabel();

	A setLabel(String newLabel);

	String getValueString();

	A setValueString(String valueString);

	/**
	 * Returns true if the variable field is enabled
	 */
	boolean isEnabled();

	A setEnabled(boolean state);

	/**
	 * Returns the variable value
	 */
	T get();

	/**
	 * Creates a VariableListField whose variable type is the same as the type of this VariableField
	 */
	AbstractVariableListField

	<?, T> createVariableListField();

	A setBackgroundColor(Color color);

}
