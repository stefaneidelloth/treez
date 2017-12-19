package org.treez.core.atom.base.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

import org.apache.log4j.Logger;

/**
 * This class provides static methods that help using the IsParameter
 * annotation.
 */
public final class IsParameters {

	private static final Logger LOG = Logger.getLogger(IsParameters.class);

	//#region CONSTRUCTORS

	/**
	 * Private Constructor that prevents construction
	 */
	private IsParameters() {
	}

	//#end region

	//#region METHODS

	/**
	 * Extracts the default value from an annotated field
	 *
	 * @param attribute
	 * @return
	 */
	public static String getDefaultValueString(Field attribute) {
		IsParameter annotation = attribute.getAnnotation(IsParameter.class);
		String defaultValueString = annotation.defaultValue();
		return defaultValueString;
	}

	/**
	 * Extracts the combo items of an an annotated field
	 *
	 * @param attribute
	 * @param parent
	 * @return
	 */
	public static String[] getComboItems(Field attribute, Object parent) {
		IsParameter annotation = attribute.getAnnotation(IsParameter.class);

		String[] comboItems;
		boolean isEnum = isEnum(attribute);
		if (isEnum) {
			//get combo box item by evaluating possible enum values
			comboItems = IsParameters.getPossibleEnumValues(attribute, parent);
		} else {
			//get combo box items from the values that have been specified
			//at the annotation with the argument comboItems
			comboItems = annotation.comboItems();
		}

		return comboItems;

	}

	/**
	 * Gets the current value of an attribute in form of a value string
	 *
	 * @param attribute
	 * @param parent
	 * @return
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	public static String getCurrentValueString(Field attribute, Object parent) {
		String currentValueString = null;
		try {
			Object currentValue = attribute.get(parent);
			if (currentValue != null) {
				currentValueString = attribute.get(parent).toString();
			}

		} catch (Exception exception) {
			String message = "Could not get current value for the attribute '"
					+ attribute.getName() + "' of '" + parent + "'.";
			LOG.error(message, exception);

		}
		return currentValueString;
	}

	/**
	 * Gets the current value of an attribute in form of an enum
	 *
	 * @param attribute
	 * @param parent
	 * @return
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	public static Enum<?> getCurrentValueEnum(Field attribute, Object parent) {
		Enum<?> currentValueEnum = null;
		try {
			Object currentValue = attribute.get(parent);
			if (currentValue != null) {
				currentValueEnum = (Enum<?>) currentValue;
			}

		} catch (Exception exception) {
			String message = "Could not get current value for the attribute '"
					+ attribute.getName() + "' of '" + parent + " as Enum'.";
			LOG.error(message, exception);

		}
		return currentValueEnum;
	}

	/**
	 * Gets the possible enum values from an enum attribute
	 *
	 * @param attribute
	 * @param parent
	 * @return
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	public static String[] getPossibleEnumValues(Field attribute,
			Object parent) {
		String[] enumItems = null;
		try {

			//get enum constants as objects
			Class<?> enumType = attribute.getType();
			Object[] enumConstants = enumType.getEnumConstants();

			if (enumConstants == null) {
				return null;
			} else {
				//object array to string array
				enumItems = new String[enumConstants.length];
				for (int k = 0; k < enumConstants.length; k++) {
					enumItems[k] = enumConstants[k].toString();
				}
			}

		} catch (Exception e) {
			LOG.error(
					"Could not get possible enum value for the attribute '"
							+ attribute.getName() + "' of '" + parent + "'.",
					e);
		}
		return enumItems;
	}

	/**
	 * Returns true if the given attribute is annotated with the annotation
	 * IsParameter
	 *
	 * @param attribute
	 * @return
	 */
	public static boolean isAnnotated(final Field attribute) {
		return attribute.isAnnotationPresent(IsParameter.class);
	}

	/**
	 * Sets the value of an attribute
	 *
	 * @param attribute
	 * @param parent
	 * @param valueString
	 */
	public static void setAttributeValue(Field attribute, Object parent,
			String valueString) {

		String type = attribute.getType().getSimpleName().toUpperCase();
		boolean isEnum = isEnum(attribute);
		if (isEnum) {
			type = "ENUM";
		}

		switch (type) {
			case "BOOLEAN" :
				setAttributeBooleanValue(attribute, parent, valueString);
				break;
			case "ENUM" :
				setAttributeEnumValue(attribute, parent, valueString);
				break;
			case "DOUBLE" :
				setAttributeDoubleValue(attribute, parent, valueString);
				break;
			case "FLOAT" :
				setAttributeFloatValue(attribute, parent, valueString);
				break;
			case "INTEGER" :
				setAttributeIntegerValue(attribute, parent, valueString);
				break;
			case "STRING" :
				setAttributeStringValue(attribute, parent, valueString);
				break;
			default :
				throw new IllegalStateException(
						"The type '" + type + "' is not yet implemented.");

		}
	}

	/**
	 * Returns true if the attribute is an Enum
	 *
	 * @param attribute
	 * @return
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	public static boolean isEnum(Field attribute) {
		try {
			return attribute.getType().isEnum();
		} catch (Exception e) {
			throw new IllegalStateException("Could not determine if attribute '"
					+ attribute.getName() + "' is an Enum.", e);
		}

	}

	/**
	 * Sets the Enum value of an attribute with the given valueString
	 *
	 * @param attribute
	 * @param parent
	 * @param valueString
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private static void setAttributeEnumValue(Field attribute, Object parent,
			String valueString) {
		Objects.requireNonNull(attribute, "The attribute must not be null");
		Objects.requireNonNull(parent, "The parent must not be null");
		Objects.requireNonNull(valueString, "The valueString must not be null");

		//convert valueString to enum value
		Enum<?> value = convertStringToEnumValue(attribute, valueString);

		//set the new enum value
		try {
			attribute.set(parent, value);
		} catch (Exception e) {
			throw new IllegalStateException("Could not set value of '"
					+ attribute.getName() + "' to " + valueString + "");
		}

	}

	/**
	 * Converts a string to an enum value for the given attribute
	 *
	 * @param attribute
	 * @param valueString
	 * @return
	 */
	private static Enum<?> convertStringToEnumValue(Field attribute,
			String valueString) {
		Class<?> enumType = attribute.getType();
		Enum<?> value = null;
		for (Object enumConstant : enumType.getEnumConstants()) {
			boolean isWantedConstant = enumConstant.toString()
					.equals(valueString);
			if (isWantedConstant) {
				value = (Enum<?>) enumConstant;
			}
		}

		if (value == null) {
			throw new IllegalStateException(
					"Could not convert the string value '" + valueString
							+ "' to an enum value.");
		}
		return value;
	}

	/**
	 * Sets the Double value of an attribute with the given valueString
	 *
	 * @param attribute
	 * @param parent
	 * @param valueString
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private static void setAttributeDoubleValue(Field attribute, Object parent,
			String valueString) {
		Objects.requireNonNull(attribute, "The attribute must not be null");
		Objects.requireNonNull(parent, "The parent must not be null");
		Objects.requireNonNull(valueString, "The valueString must not be null");

		Double value = null;
		try {
			value = Double.parseDouble(valueString);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Could not parse the value '"
					+ attribute.getName() + "' to a Double.");
		}

		try {
			attribute.set(parent, value);
		} catch (Exception e) {
			throw new IllegalStateException("Could not set value of '"
					+ attribute.getName() + "' to " + valueString + "", e);
		}

	}

	/**
	 * Sets the Float value of an attribute with the given valueString
	 *
	 * @param attribute
	 * @param parent
	 * @param valueString
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private static void setAttributeFloatValue(Field attribute, Object parent,
			String valueString) {
		Objects.requireNonNull(attribute, "The attribute must not be null");
		Objects.requireNonNull(parent, "The parent must not be null");
		Objects.requireNonNull(valueString, "The valueString must not be null");

		Float value = null;
		try {
			value = Float.parseFloat(valueString);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Could not parse the value '"
					+ attribute.getName() + "' to a Float.");
		}

		try {
			attribute.set(parent, value);
		} catch (Exception e) {
			throw new IllegalStateException("Could not set value of '"
					+ attribute.getName() + "' to " + valueString + "", e);
		}

	}

	/**
	 * Sets the Integer value of an attribute with the given valueString
	 *
	 * @param attribute
	 * @param parent
	 * @param valueString
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private static void setAttributeIntegerValue(Field attribute, Object parent,
			String valueString) {
		Objects.requireNonNull(attribute, "The attribute must not be null");
		Objects.requireNonNull(parent, "The parent must not be null");
		Objects.requireNonNull(valueString, "The valueString must not be null");

		Integer value = null;
		try {
			value = Integer.decode(valueString);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Could not parse the value '"
					+ attribute.getName() + "' to an Integer.");
		}

		try {
			attribute.set(parent, value);
		} catch (Exception e) {
			throw new IllegalStateException("Could not set value of '"
					+ attribute.getName() + "' to " + valueString + "", e);
		}

	}

	/**
	 * Sets the String value of an attribute
	 *
	 * @param attribute
	 * @param parent
	 * @param value
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private static void setAttributeStringValue(Field attribute, Object parent,
			String value) {
		Objects.requireNonNull(attribute, "The attribute must not be null");
		Objects.requireNonNull(parent, "The parent must not be null");
		Objects.requireNonNull(value, "The value must not be null");
		try {
			attribute.set(parent, value);
		} catch (Exception e) {
			throw new IllegalStateException("Could not set value of '"
					+ attribute.getName() + "' to " + value + "", e);
		}

	}

	/**
	 * Sets the Boolean value of an attribute with the given valueString
	 *
	 * @param attribute
	 * @param parent
	 * @param valueString
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private static void setAttributeBooleanValue(Field attribute, Object parent,
			String valueString) {
		Objects.requireNonNull(attribute, "The attribute must not be null");
		Objects.requireNonNull(parent, "The parent must not be null");
		Objects.requireNonNull(valueString, "The valueString must not be null");
		boolean isTrue = valueString.toUpperCase().equals("TRUE");
		boolean isFalse = valueString.toUpperCase().equals("FALSE");
		boolean valueIsOk = isTrue || isFalse;

		if (!valueIsOk) {
			throw new IllegalArgumentException("The value '" + valueString
					+ "' can not be interpreted as a Boolean.");
		}

		try {
			if (isTrue) {
				attribute.set(parent, true);
			} else {
				attribute.set(parent, false);
			}
		} catch (Exception e) {
			throw new IllegalStateException(
					"Could not set the value of attribute '"
							+ attribute.getName() + "'.",
					e);
		}
	}

	/**
	 * Returns a proposal for the setter name for a given field.
	 *
	 * @param attribute
	 * @return
	 */
	public static String getSetterName(Field attribute) {
		String name = attribute.getName();
		String setterName = "set" + name.substring(0, 1).toUpperCase()
				+ name.substring(1, name.length());
		return setterName;
	}

	/**
	 * Returns if a setter method exits for the given attribute, where the name
	 * is simply "set" + name of the attribute.
	 *
	 * @param attribute
	 * @param parent
	 * @return
	 */
	public static boolean simpleSetterExists(Field attribute, Object parent) {
		String setterName = getSetterName(attribute);
		boolean setterExists = methodExists(setterName, parent);
		return setterExists;
	}

	/**
	 * Checks if a method exists in the class of the given parent
	 *
	 * @param methodName
	 * @param parent
	 * @return
	 */
	private static boolean methodExists(String methodName, Object parent) {
		Method[] methods = parent.getClass().getMethods();
		boolean setterExists = false;
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				setterExists = true;
				break;
			}
		}
		return setterExists;
	}

	/**
	 * Adds quotes to a given string if the given attribute represents a string
	 *
	 * @param attribute
	 * @param content
	 * @return
	 */
	public static String addQuotesIfAttributeRepresentsString(String content,
			Field attribute) {
		String result = content;
		//LOG.debug("type: " + field.getType().getSimpleName());
		boolean isString = (attribute.getType().getSimpleName()
				.equals("String"));
		if (isString) {
			result = "\"" + content + "\"";
		}
		return result;
	}

	//#end region

}
