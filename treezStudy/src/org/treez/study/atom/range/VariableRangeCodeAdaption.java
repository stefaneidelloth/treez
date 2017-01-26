package org.treez.study.atom.range;

import java.util.List;

import org.treez.core.adaptable.CodeContainer;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.adjustable.AdjustableAtomCodeAdaption;

public class VariableRangeCodeAdaption extends AdjustableAtomCodeAdaption {

	//#region CONSTRUCTORS

	public VariableRangeCodeAdaption(AbstractVariableRange<?> atom) {
		super(atom);
	}

	//#end region

	//#region METHODS

	@Override
	protected CodeContainer createCodeForAttributesFromModel(AdjustableAtom parentAtom) {

		AbstractVariableRange<?> rangeAtom = (AbstractVariableRange<?>) atom;

		CodeContainer attributeContainer = new CodeContainer(scriptType);

		String relativeModelPath = rangeAtom.getRelativeSourceVariableModelPath();
		String modelPathLine = "\t\t" + VARIABLE_NAME + ".setRelativeSourceVariableModelPath(\"" + relativeModelPath
				+ "\");";
		attributeContainer.extendBulk(modelPathLine);

		List<?> range = rangeAtom.getRange();
		String rangeString = getValueCommandString(range);
		String rangeValuesLine = "\t\t" + VARIABLE_NAME + ".setRange(" + rangeString + ");";
		attributeContainer.extendBulk(rangeValuesLine);

		Boolean isDisabled = !rangeAtom.enabled.get();
		if (isDisabled) {
			String enabledLine = "\t\t" + VARIABLE_NAME + ".enabled.set(false);";
			attributeContainer.extendBulk(enabledLine);
		}

		return attributeContainer;
	}

	//#end region

	//#region ACCESSORS

	//#end region

}
