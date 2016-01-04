package org.treez.core.atom.attribute;

import java.util.List;

/**
 * This interface needs to be implemented by enums that should be used with the
 * EnumComboBox
 */
public interface EnumValueProvider<T extends Enum<?>> {

	/**
	 * Returns all enum values as a list of corresponding strings
	 *
	 * @return
	 */
	List<String> getValues();

	/**
	 * Returns the enum value that corresponds to the given string.
	 *
	 * @param value
	 * @return
	 */
	T fromString(String value);

	/**
	 * Returns the string that corresponds to the value
	 * 
	 * @return
	 */
	@Override
	String toString();

}
