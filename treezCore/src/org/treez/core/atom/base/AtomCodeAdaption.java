package org.treez.core.atom.base;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;
import org.treez.core.adaptable.CodeContainer;
import org.treez.core.atom.base.annotation.IsParameters;
import org.treez.core.scripting.ScriptType;

/**
 * CodeAdaption for AbstractAtoms: used to create java code from the tree. The creation of the code is separated in two
 * parts: one for creating the imports and one for creating the main code. This allows to put the imports for all
 * children at the beginning of the java code.
 */
public class AtomCodeAdaption extends AbstractAtomCodeAdaption {

	private static final Logger LOG = Logger.getLogger(AtomCodeAdaption.class);

	//#region ATTRIBUTES

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param atom
	 */
	public AtomCodeAdaption(AbstractAtom<?> atom) {
		super(atom, ScriptType.JAVA);
	}

	//#end region

	//#region METHODS

	//#region CODE CONTAINER

	/**
	 * Creates a CodeContainer for a root atom
	 *
	 * @return
	 */
	@Override
	public CodeContainer buildRootCodeContainer(String className) {
		CodeContainer rootCodeContainer = new RootCodeContainer(atom, className);
		return rootCodeContainer;
	}

	/**
	 * Post processes the container for all children
	 *
	 * @param allChildrenCodeContainer
	 * @return
	 */
	@Override
	protected CodeContainer postProcessAllChildrenCodeContainer(CodeContainer allChildrenCodeContainer) {

		boolean bulkIsEmpty = allChildrenCodeContainer.hasEmptyBulk();
		if (!bulkIsEmpty) {
			//make sure that the bulk code ends with an extra line if the bulk code is not empty
			//(this creates some distance to the code for the next sibling or uncle)
			allChildrenCodeContainer.makeBulkEndWithSingleEmptyLine();
		}
		return allChildrenCodeContainer;
	}

	//#end region

	//#region CREATION CODE

	/**
	 * Builds code for creating the atom without using a variable name
	 *
	 * @return
	 */
	@Override
	protected CodeContainer buildCreationCodeContainerWithoutVariableName() {

		String name = atom.getName();
		Class<?> atomClass = atom.getClass();
		String className = atomClass.getSimpleName();
		String fullClassName = atomClass.getName();
		boolean hasParent = atom.createTreeNodeAdaption().hasParent();

		//create new empty code container
		CodeContainer codeContainer = new CodeContainer(scriptType);

		//add import
		String importLine = "import " + fullClassName + ";";
		codeContainer.extendImports(importLine);

		//add bulk code
		if (hasParent) {
			codeContainer.extendBulk("\t\t" + PARENT_VARIABLE_NAME + ".create" + className + "(\"" + name + "\");");
		} else {
			String message = "The atom " + name
					+ " has no parent atom and no code to create children or set attributes. "
					+ "Creating it would be useless. If it is a root atom wihout children create a custom code adaption for it.";
			LOG.warn(message);
		}
		return codeContainer;
	}

	/**
	 * Builds code for the constructor call, using a variable names
	 *
	 * @return
	 */
	@Override
	protected CodeContainer buildCreationCodeContainerWithVariableName(String variableName) {

		String name = atom.getName();
		Class<?> atomClass = atom.getClass();
		String className = atomClass.getSimpleName();
		String fullClassName = atomClass.getName();
		boolean hasParent = atom.createTreeNodeAdaption().hasParent();

		CodeContainer codeContainer = new CodeContainer(scriptType);

		codeContainer.extendImports("import " + fullClassName + ";");

		if (hasParent) {
			codeContainer.extendBulk("\t\t" + className + " " + variableName + " = " + PARENT_VARIABLE_NAME + ".create"
					+ className + "(\"" + name + "\");");
		} else {
			codeContainer.extendBulkWithEmptyLine();
			codeContainer.extendBulk(
					"\t\t" + className + " " + variableName + "  = new " + className + "(\"" + name + "\");");
		}
		return codeContainer;
	}

	//#end region

	//#region ATTRIBUTE CODE

	/**
	 * Builds code to set the value of an attribute
	 *
	 * @param attributeContainer
	 * @param attribute
	 * @param valueString
	 * @return
	 */
	@Override
	protected CodeContainer extendCodeForAttribute(
			CodeContainer attributeContainer,
			Field attribute,
			String valueString) {

		//get name of setter method
		String setterName = IsParameters.getSetterName(attribute);

		//add quotes to the value string if the corresponding attribute represents a string
		String adjustedValueString = IsParameters.addQuotesIfAttributeRepresentsString(valueString, attribute);
		//LOG.debug("Current filePath: " + adjustedValueString);

		//build additional code
		String additionalLine = "\t\t" + VARIABLE_NAME + "." + setterName + "(" + adjustedValueString + ");";

		//return new code
		attributeContainer.extendBulk(additionalLine);
		return attributeContainer;
	}

	//#end region

	//#end region

}
