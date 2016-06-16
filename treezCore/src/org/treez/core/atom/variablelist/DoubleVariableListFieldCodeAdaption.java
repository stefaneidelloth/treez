package org.treez.core.atom.variablelist;

import java.util.List;

import org.treez.core.adaptable.CodeContainer;
import org.treez.core.atom.attribute.base.AttributeAtomCodeAdaption;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.utils.Utils;

/**
 * Code adaption for the double variable list field
 */
public class DoubleVariableListFieldCodeAdaption extends AttributeAtomCodeAdaption<List<Double>> {

	//#region CONSTRUCTORS

	public DoubleVariableListFieldCodeAdaption(DoubleVariableListField atom) {
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
			AbstractAtom<?> parentAtom,
			CodeContainer parentContainer) {

		CodeContainer extendedContainer = parentContainer;

		DoubleVariableListField variableListField = (DoubleVariableListField) atom;
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

			return extendedContainer;
		}

	}

	//#end region

}
