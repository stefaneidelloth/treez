package org.treez.core.atom.variablelist;

import java.util.List;

import org.apache.log4j.Logger;
import org.treez.core.adaptable.CodeContainer;
import org.treez.core.atom.attribute.base.AttributeAtomCodeAdaption;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.quantity.Quantity;
import org.treez.core.utils.Utils;

/**
 * Code adaption for the quantity variable list field
 */
public class QuantityVariableListFieldCodeAdaption extends AttributeAtomCodeAdaption<List<Quantity>> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(QuantityVariableListFieldCodeAdaption.class);

	//#region ATTRIBUTES

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param atom
	 */
	public QuantityVariableListFieldCodeAdaption(QuantityVariableListField atom) {
		super(atom);
	}

	//#end region

	//#region METHODS

	/**
	 * @param parentAtom
	 * @param parentContainer
	 * @return
	 */
	@Override
	public CodeContainer extendAttributeCodeContainerForModelParent(
			AbstractAtom parentAtom,
			CodeContainer parentContainer) {

		CodeContainer extendedContainer = parentContainer;

		QuantityVariableListField variableListField = (QuantityVariableListField) atom;
		boolean hasDefaultValue = variableListField.hasDefaultValue();
		if (hasDefaultValue) {
			return extendedContainer;
		} else {

			String attributeName = variableListField.getName();

			String valueString = variableListField.getValueString();
			if (valueString != null) {
				String setterName = "set" + Utils.firstToUpperCase(attributeName) + "ValueString";
				checkIfSetterExists(parentAtom, setterName, String.class);
				extendedContainer.extendBulk("\t\t" + VARIABLE_NAME + "." + setterName + "(\"" + valueString + "\");");
			}

			String unitString = variableListField.getUnitString();
			if (unitString != null) {
				String setterName = "set" + Utils.firstToUpperCase(attributeName) + "UnitString";
				checkIfSetterExists(parentAtom, setterName, String.class);
				extendedContainer.extendBulk("\t\t" + VARIABLE_NAME + "." + setterName + "(\"" + unitString + "\");");
			}

			return extendedContainer;
		}

	}

	//#end region

}
