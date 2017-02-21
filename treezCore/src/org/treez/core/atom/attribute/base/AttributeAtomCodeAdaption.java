package org.treez.core.atom.attribute.base;

import org.treez.core.adaptable.CodeContainer;
import org.treez.core.atom.attribute.base.parent.AbstractAttributeParentAtom;
import org.treez.core.atom.attribute.base.parent.AttributeParentCodeAdaption;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.quantity.Quantity;

/**
 * The CodeAdaption for AttributeAtom
 *
 * @param <T>
 */
public class AttributeAtomCodeAdaption<T> extends AttributeParentCodeAdaption {

	//#region CONSTRUCTORS

	public AttributeAtomCodeAdaption(AbstractAttributeAtom<?, T> atom) {
		super(atom);
	}

	//#end region

	//#region METHODS

	/**
	 * Creates a new code container that contains code for setting the attribute values of the AttributeAtom that
	 * corresponds to this code adaption. Might be overridden by inheriting classes.
	 *
	 * @return
	 */
	@Override
	protected CodeContainer buildCodeContainerForAttributes() {

		CodeContainer attributeContainer = new CodeContainer(scriptType);

		CodeContainer extendedContainer = attributeContainer;

		@SuppressWarnings("unchecked")
		AbstractAttributeAtom<?, T> attributeAtom = (AbstractAttributeAtom<?, T>) atom;
		boolean hasDefaultValue = attributeAtom.hasDefaultValue();
		if (!hasDefaultValue) {
			extendedContainer = buildCodeContainerForNonDefaultAttribute(attributeContainer, attributeAtom);
		}

		extendedContainer = setEnabledStateIfAtomIsDisabled(extendedContainer);

		return extendedContainer;

	}

	private CodeContainer setEnabledStateIfAtomIsDisabled(CodeContainer extendedContainer) {
		AbstractAttributeParentAtom<?> attributeParentAtom = (AbstractAttributeParentAtom<?>) atom;
		boolean isEnabled = attributeParentAtom.isEnabled();
		if (!isEnabled) {
			String newBulkLine = "\t\t" + VARIABLE_NAME + ".setEnabled(false);";
			extendedContainer.extendBulk(newBulkLine);
		}
		return extendedContainer;
	}

	/**
	 * Creates a new code container for an attribute atom that does not have the default value
	 *
	 * @param attributeContainer
	 * @param attributeAtom
	 * @return
	 */
	private CodeContainer buildCodeContainerForNonDefaultAttribute(
			CodeContainer attributeContainer,
			AbstractAttributeAtom<?, T> attributeAtom) {
		T value = attributeAtom.get();

		Class<?> valueClass = value.getClass();

		//code for the special case of setting a Quantity
		boolean isQuantity = valueClass.equals(Quantity.class);
		if (isQuantity) {

			//add import for quantity
			//String importLine = "import " + Quantity.class.getName() + ";";
			//attributeContainer.extendImports(importLine);

			//check if a corresponding setter exists
			checkIfAtomHasSettersForQuantity();

			//get quantities
			Quantity quantity = (Quantity) value;
			Quantity defaultQuantity = (Quantity) attributeAtom.getDefaultValue();

			//get values
			String valueString = getValueCommandString(value);
			valueString = quantity.getValue();
			String defaultValueString = defaultQuantity.getValue();
			boolean hasDefaultValueString = (valueString == defaultValueString);

			//get units
			String unitString = quantity.getUnit();
			String defaultUnitString = defaultQuantity.getUnit();
			boolean hasDefaultUnitString = (unitString == defaultUnitString);

			//create bulk code
			if (!hasDefaultValueString) {
				attributeContainer.extendBulk("\t\t" + VARIABLE_NAME + ".setValueString(\"" + valueString + "\");");
			}
			if (!hasDefaultUnitString) {
				attributeContainer.extendBulk("\t\t" + VARIABLE_NAME + ".setUnitString(\"" + unitString + "\");");
			}

			return attributeContainer;
		}

		//code for setting other value
		String valueString = getValueCommandString(value);
		if (valueString != null) {
			String code = "\t\t" + VARIABLE_NAME + ".set(" + valueString + ");";
			attributeContainer.extendBulk(code);
			return attributeContainer;
		}

		String valueClassName = valueClass.getSimpleName();
		String message = "The type " + valueClassName + " is not yet implemented";
		throw new IllegalStateException(message);
	}

	/**
	 * @param intermediateAtom
	 * @param parentContainer
	 * @return
	 */
	@Override
	public CodeContainer extendAttributeCodeContainerForModelParent(
			AbstractAtom<?> parentAtom,
			AbstractAtom<?> intermediateAtom,
			CodeContainer parentContainer) {

		CodeContainer extendedContainer = parentContainer;

		@SuppressWarnings("unchecked")
		AbstractAttributeAtom<?, T> attributeAtom = (AbstractAttributeAtom<?, T>) atom;
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
			CodeContainer extendedContainer,
			AbstractAtom<?> intermediateAtom,
			AbstractAttributeAtom<?, T> attributeAtom) {

		String attributeName = attributeAtom.getName();

		T value = attributeAtom.get();
		Class<?> valueClass = value.getClass();

		//code for special case of setting a quantity value
		boolean isQuantity = valueClass.equals(Quantity.class);
		if (isQuantity) {
			CodeContainer exdentedWithQuantityContainer = extendContainerForQuantityValue(extendedContainer,
					intermediateAtom, attributeAtom, value);
			return exdentedWithQuantityContainer;
		}

		//code for setting other values
		String valueCommandString = getValueCommandString(value);
		if (valueCommandString != null) {

			String newBulkLine = "\t\t" + VARIABLE_NAME + ".";

			if (intermediateAtom != null) {
				String parentName = intermediateAtom.getName();
				newBulkLine += parentName + ".";
			}

			newBulkLine += attributeName + ".set(" + valueCommandString + ");";

			extendedContainer.extendBulk(newBulkLine);
			return extendedContainer;
		}

		String valueClassName = valueClass.getSimpleName();
		String message = "The type " + valueClassName
				+ " is not yet implemented. The method getValueCommandString needs to be extended.";
		throw new IllegalStateException(message);
	}

	private CodeContainer extendContainerForQuantityValue(
			CodeContainer extendedContainer,
			AbstractAtom<?> parentAtom,
			AbstractAttributeAtom<?, T> attributeAtom,
			T value) {

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
			String quantityString = "new Quantity(" + valueString + ", " + unitString + ")";
			extendedContainer.extendBulk(
					"\t\t" + parentName + "." + VARIABLE_NAME + "." + attributeName + ".set(" + quantityString + ");");
		}

		return extendedContainer;
	}

	/**
	 * Checks if the atom has methods setValue and getValue to set a Quantity. Throws an exception if the required
	 * setters do not exist.
	 */
	private void checkIfAtomHasSettersForQuantity() {
		Class<?> atomClass = atom.getClass();
		try {

			atomClass.getMethod("setValueString", new Class<?>[] { String.class });
		} catch (NoSuchMethodException exception) {
			String message = "The AttributeAtom '" + atom.getName() + "' of type '" + atom.getClass().getSimpleName()
					+ "' has no setter setValueString(String valueString).";
			throw new IllegalStateException(message);
		}

		try {
			atomClass.getMethod("setUnitString", new Class<?>[] { String.class });
		} catch (NoSuchMethodException exception) {
			String message = "The AttributeAtom '" + atom.getName() + "' of type '" + atom.getClass().getSimpleName()
					+ "' has no setter setUnitString(String valueString).";
			throw new IllegalStateException(message);
		}

	}

	/**
	 * Checks if the parentAtom has a setter with the given name and value class. Throws an IllegalStateException if the
	 * setter does not exist.
	 *
	 * @param parentAtom
	 * @param setterName
	 * @param valueClass
	 */
	protected static void checkIfSetterExists(AbstractAtom<?> parentAtom, String setterName, Class<?> valueClass) {
		Class<?> parentClass = parentAtom.getClass();

		try {
			parentClass.getMethod(setterName, new Class<?>[] { valueClass });
		} catch (NoSuchMethodException e) {
			String treePath = parentAtom.createTreeNodeAdaption().getTreePath();
			String message = "The atom '" + treePath + "' of type '" + parentAtom.getClass().getSimpleName()
					+ "' has no setter '" + setterName + "(" + valueClass.getSimpleName() + " valueToSet)'.";
			throw new IllegalStateException(message);
		}

	}

	//#end region
}
