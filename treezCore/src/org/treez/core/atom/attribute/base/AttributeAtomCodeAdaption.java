package org.treez.core.atom.attribute.base;

import org.apache.log4j.Logger;
import org.treez.core.adaptable.CodeContainer;
import org.treez.core.atom.attribute.base.parent.AttributeParentCodeAdaption;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.quantity.Quantity;

/**
 * The CodeAdaption for AttributeAtom
 *
 * @param <T>
 */
public class AttributeAtomCodeAdaption<T> extends AttributeParentCodeAdaption {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger
			.getLogger(AttributeAtomCodeAdaption.class);

	//#region ATTRIBUTES

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param atom
	 */
	public AttributeAtomCodeAdaption(AbstractAttributeAtom<T> atom) {
		super(atom);
	}

	//#end region

	//#region METHODS

	/**
	 * Creates a new code container that contains code for setting the attribute
	 * values of the AttributeAtom that corresponds to this code adaption. Might
	 * be overridden by inheriting classes.
	 *
	 * @return
	 */
	@Override
	protected CodeContainer buildCodeContainerForAttributes() {

		CodeContainer attributeContainer = new CodeContainer(scriptType);

		@SuppressWarnings("unchecked")
		AbstractAttributeAtom<T> attributeAtom = (AbstractAttributeAtom<T>) atom;
		boolean hasDefaultValue = attributeAtom.hasDefaultValue();
		if (hasDefaultValue) {
			return attributeContainer;
		} else {
			CodeContainer nonDefaultContainer = buildCodeContainerForNonDefaultAttribute(
					attributeContainer, attributeAtom);
			return nonDefaultContainer;

		}

	}

	/**
	 * Creates a new code container for an attribute atom that does not have the
	 * default value
	 *
	 * @param attributeContainer
	 * @param attributeAtom
	 * @return
	 */
	private CodeContainer buildCodeContainerForNonDefaultAttribute(
			CodeContainer attributeContainer,
			AbstractAttributeAtom<T> attributeAtom) {
		T value = attributeAtom.get();
		String valueClassName = value.getClass().getSimpleName();

		//code for setting a simple value string
		String valueString = getValueString();
		if (valueString != null) {
			String code = "\t\t" + VARIABLE_NAME + ".set(" + valueString + ");";
			attributeContainer.extendBulk(code);
			return attributeContainer;
		}

		//code for setting a Quantity
		boolean isQuantity = value.getClass().equals(Quantity.class);
		if (isQuantity) {

			//add import for quantity
			//String importLine = "import " + Quantity.class.getName() + ";";
			//attributeContainer.extendImports(importLine);

			//check if a corresponding setter exists
			checkIfAtomHasSettersForQuantity();

			//get quantities
			Quantity quantity = (Quantity) value;
			Quantity defaultQuantity = (Quantity) attributeAtom
					.getDefaultValue();

			//get values
			valueString = quantity.getValue();
			String defaultValueString = defaultQuantity.getValue();
			boolean hasDefaultValueString = (valueString == defaultValueString);

			//get units
			String unitString = quantity.getUnit();
			String defaultUnitString = defaultQuantity.getUnit();
			boolean hasDefaultUnitString = (unitString == defaultUnitString);

			//create bulk code
			if (!hasDefaultValueString) {
				attributeContainer.extendBulk("\t\t" + VARIABLE_NAME
						+ ".setValueString(\"" + valueString + "\");");
			}
			if (!hasDefaultUnitString) {
				attributeContainer.extendBulk("\t\t" + VARIABLE_NAME
						+ ".setUnitString(\"" + unitString + "\");");
			}

			return attributeContainer;
		}

		String message = "The type " + valueClassName
				+ " is not yet implemented";
		throw new IllegalStateException(message);
	}

	/**
	 * Returns a String that represents the current attribute value. If the
	 * attribute value can not be returned as a String, null is returned.
	 *
	 * @return
	 */
	private String getValueString() {

		@SuppressWarnings("unchecked")
		AbstractAttributeAtom<T> attributeAtom = (AbstractAttributeAtom<T>) this
				.getAdaptable();
		T value = attributeAtom.get();
		String valueClassName = value.getClass().getSimpleName();

		boolean isString = valueClassName.equals("String");
		if (isString) {
			String valueString = createValueStringForString(value);
			return valueString;
		}

		boolean isEnum = value.getClass().isEnum();
		if (isEnum) {
			String valueString = createValueStringForEnum(value);
			return valueString;
		}

		boolean isPrimitive = value.getClass().isPrimitive();
		if (isPrimitive) {
			String valueString = String.valueOf(value);
			return valueString;
		}

		boolean isBoolean = value.getClass().equals(Boolean.class);
		if (isBoolean) {
			String valueString = String.valueOf(value);
			return valueString;
		}

		boolean isFloat = value.getClass().equals(Float.class);
		if (isFloat) {
			String valueString = String.valueOf(value);
			return valueString;
		}

		boolean isDouble = value.getClass().equals(Double.class);
		if (isDouble) {
			String valueString = String.valueOf(value);
			return valueString;
		}

		return null;
	}

	private String createValueStringForEnum(T value) {
		Enum<?> enumValue = (Enum<?>) value;
		String valueString = getEnumValueString(enumValue);
		return valueString;
	}

	private String createValueStringForString(T value) {
		String valueString = (String) value;
		valueString = valueString.replace("\"", "\\\"");
		valueString = "\"" + valueString + "\"";
		return valueString;
	}

	private static String getEnumValueString(Enum<?> enumValue) {
		String enumClassName = enumValue.getClass().getSimpleName();
		String enumValueName = enumValue.name();
		String valueString = enumClassName + "." + enumValueName;
		return valueString;
	}

	/**
	 * @param intermediateAtom
	 * @param parentContainer
	 * @return
	 */
	@Override
	public CodeContainer extendAttributeCodeContainerForModelParent(
			AbstractAtom intermediateAtom, CodeContainer parentContainer) {

		CodeContainer extendedContainer = parentContainer;

		@SuppressWarnings("unchecked")
		AbstractAttributeAtom<T> attributeAtom = (AbstractAttributeAtom<T>) atom;
		boolean hasDefaultValue = attributeAtom.hasDefaultValue();
		if (hasDefaultValue) {
			return extendedContainer;
		} else {
			CodeContainer extendedAttributeContainer = extendAttributeCodeContainerForModelParentForNonDefaultAttribute(
					extendedContainer, intermediateAtom, attributeAtom);
			return extendedAttributeContainer;
		}

	}

	private CodeContainer extendAttributeCodeContainerForModelParentForNonDefaultAttribute(
			CodeContainer extendedContainer, AbstractAtom intermediateAtom,
			AbstractAttributeAtom<T> attributeAtom) {

		String attributeName = attributeAtom.getName();

		T value = attributeAtom.get();
		Class<?> valueClass = value.getClass();
		String valueClassName = valueClass.getSimpleName();

		//code for setting a simple value string
		String valueString = getValueString();
		if (valueString != null) {

			String newBulkLine = "\t\t" + VARIABLE_NAME + ".";

			if (intermediateAtom != null) {
				String parentName = intermediateAtom.getName();
				newBulkLine += parentName + ".";
			}

			newBulkLine += attributeName + ".set(" + valueString + ");";

			extendedContainer.extendBulk(newBulkLine);
			return extendedContainer;
		}

		//code for setting a quantity value
		boolean isQuantity = value.getClass().equals(Quantity.class);
		if (isQuantity) {
			CodeContainer exdentedWithQuantityContainer = extendContainerForQuantityValue(
					extendedContainer, intermediateAtom, attributeAtom, value);
			return exdentedWithQuantityContainer;
		}

		String message = "The type " + valueClassName
				+ " is not yet implemented";
		throw new IllegalStateException(message);
	}

	private CodeContainer extendContainerForQuantityValue(
			CodeContainer extendedContainer, AbstractAtom parentAtom,
			AbstractAttributeAtom<T> attributeAtom, T value) {

		String parentName = parentAtom.getName();
		String attributeName = attributeAtom.getName();

		String valueString;

		//add import for quantity
		//String importLine = "import " + Quantity.class.getName() + ";";
		//extendedContainer.extendImports(importLine);

		//check if corresponding setter exists
		checkIfAtomHasSettersForQuantity();

		//get quantities
		Quantity quantity = (Quantity) value;
		Quantity defaultQuantity = (Quantity) attributeAtom.getDefaultValue();

		//get values
		valueString = quantity.getValue();
		String defaultValueString = defaultQuantity.getValue();
		boolean hasDefaultValueString = (valueString == defaultValueString);

		//get units
		String unitString = quantity.getUnit();
		String defaultUnitString = defaultQuantity.getUnit();
		boolean hasDefaultUnitString = (unitString == defaultUnitString);

		//create code
		boolean setQuantity = !hasDefaultValueString || !hasDefaultUnitString;
		if (setQuantity) {
			String quantityString = "new Quantity(" + valueString + ", "
					+ unitString + ")";
			extendedContainer
					.extendBulk("\t\t" + parentName + "." + VARIABLE_NAME + "."
							+ attributeName + ".set(" + quantityString + ");");
		}

		return extendedContainer;
	}

	/**
	 * Checks if the atom has methods setValue and getValue to set a Quantity.
	 * Throws an exception if the required setters do not exist.
	 */
	private void checkIfAtomHasSettersForQuantity() {
		Class<?> atomClass = atom.getClass();
		try {

			atomClass.getMethod("setValueString", new Class<?>[]{String.class});
		} catch (NoSuchMethodException exception) {
			String message = "The AttributeAtom '" + atom.getName()
					+ "' of type '" + atom.getClass().getSimpleName()
					+ "' has no setter setValueString(String valueString).";
			throw new IllegalStateException(message);
		}

		try {
			atomClass.getMethod("setUnitString", new Class<?>[]{String.class});
		} catch (NoSuchMethodException exception) {
			String message = "The AttributeAtom '" + atom.getName()
					+ "' of type '" + atom.getClass().getSimpleName()
					+ "' has no setter setUnitString(String valueString).";
			throw new IllegalStateException(message);
		}

	}

	/**
	 * Checks if the parentAtom has a setter with the given name and value
	 * class. Throws an IllegalStateException if the setter does not exist.
	 *
	 * @param parentAtom
	 * @param setterName
	 * @param valueClass
	 */
	protected static void checkIfSetterExists(AbstractAtom parentAtom,
			String setterName, Class<?> valueClass) {
		Class<?> parentClass = parentAtom.getClass();

		try {
			parentClass.getMethod(setterName, new Class<?>[]{valueClass});
		} catch (NoSuchMethodException e) {
			String treePath = parentAtom.createTreeNodeAdaption().getTreePath();
			String message = "The atom '" + treePath + "' of type '"
					+ parentAtom.getClass().getSimpleName()
					+ "' has no setter '" + setterName + "("
					+ valueClass.getSimpleName() + " valueToSet)'.";
			throw new IllegalStateException(message);
		}

	}

	//#end region
}
