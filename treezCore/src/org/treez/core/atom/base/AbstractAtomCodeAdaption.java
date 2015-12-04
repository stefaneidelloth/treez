package org.treez.core.atom.base;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.CodeContainer;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.base.annotation.IsParameters;
import org.treez.core.scripting.ScriptType;
import org.treez.core.scripting.VariableNameRegistry;

/**
 * CodeAdaption for AbstractAtoms: used to create java code from the tree. The creation of the code is separated in two
 * parts: one for creating the imports and one for creating the main code. This allows to put the imports for all
 * children at the beginning of the java code.
 */
public abstract class AbstractAtomCodeAdaption implements CodeAdaption {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(AbstractAtomCodeAdaption.class);

	//#region ATTRIBUTES

	/**
	 * The AbstractAtom that corresponds to this code adaption
	 */
	protected AbstractAtom atom;

	/**
	 * The script type
	 */
	protected ScriptType scriptType;

	/**
	 * Place holder for the variable name
	 */
	protected static final String VARIABLE_NAME = "{#VariableName#}";

	/**
	 * Place holder for the parent variable name
	 */
	protected static final String PARENT_VARIABLE_NAME = "{#ParentVariableName#}";

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param atom
	 * @param scriptType
	 */
	public AbstractAtomCodeAdaption(AbstractAtom atom, ScriptType scriptType) {
		Objects.requireNonNull(atom, "The corresponding atom must not be null.");
		Objects.requireNonNull(scriptType, "The ScriptType must not be null.");
		this.atom = atom;
		this.scriptType = scriptType;
	}

	//#end region

	//#region METHODS

	//#region CODE CONTAINER

	/**
	 * Returns the CodeContainer for the AbstractAtom that corresponds to this code adaption and for all of its
	 * children.
	 */
	@Override
	public CodeContainer buildCodeContainer(
			CodeContainer parentCodeContainer,
			Optional<CodeContainer> injectedChildCodeContainer) {
		Objects.requireNonNull(parentCodeContainer, "Parent code container must not be null.");
		Objects.requireNonNull(parentCodeContainer,
				"Child code container must not be null. Pass at least an Optional off null.");

		CodeContainer codeContainer = extendParentCodeContainer(parentCodeContainer, injectedChildCodeContainer);
		return codeContainer;

	}

	/**
	 * Creates a CodeContainer for a root atom
	 *
	 * @param className
	 * @return
	 */
	@Override
	public abstract CodeContainer buildRootCodeContainer(String className);

	/**
	 * Extends the given parent code container with the code for the AbstractAtom that corresponds to this code adaption
	 * and all of its children. (This basic implementation of the method, that only modifies the imports and the bulk,
	 * might be overridden by inheriting classes.)
	 *
	 * @param parentContainer
	 * @param injectedChildContainer
	 * @return
	 */
	protected CodeContainer extendParentCodeContainer(
			CodeContainer parentContainer,
			Optional<CodeContainer> injectedChildContainer) {
		Objects.requireNonNull(parentContainer, "Parent code container must not be null.");
		Objects.requireNonNull(injectedChildContainer,
				"Child code container must not be null. Pass at least an optional of null.");

		String atomName = atom.getName();
		sysLog.debug("Getting code container for atom '" + atomName + "'.");

		//Create code container for the children of the atom and check if it is empty.
		//Instead of actual variable names place holders will be used. Those
		//place holders need to be replaced after the actual variable names are known.
		CodeContainer childContainer = createCodeContainerForChildAtoms();
		boolean childContainerHasEmptyBulk = childContainer.hasEmptyBulk();

		//check if bulk of injected child container is empty
		boolean injectedChildContainerHasEmptyBulk = checkIfOptionalContainerHasEmptyBulk(injectedChildContainer);

		//check if bulk child code exists
		boolean hasEmptyChildBulk = (childContainerHasEmptyBulk && injectedChildContainerHasEmptyBulk);

		//create attribute code container and check if it its bulk is empty.
		//Instead of actual variable names place holders will be used. Those
		//place holders need to be replaced after the actual variable names are known.
		CodeContainer attributeContainer = buildCodeContainerForAttributes();
		boolean hasEmptyAttributeBulk = attributeContainer.hasEmptyBulk();
		if (!hasEmptyAttributeBulk) {
			attributeContainer.makeBulkEndWithSingleEmptyLine();
		}

		boolean useVariableName = !hasEmptyChildBulk || !hasEmptyAttributeBulk;

		//create new code container for the current atom (might depend on hasNoBulkChildCode)
		//create code container (depends on existence of attribute bulk code)
		CodeContainer currentContainer;
		if (useVariableName) {
			//create variable name
			String variableName = createVariableName(atom);

			//create current container using the variable name
			currentContainer = buildCreationCodeContainerWithVariableName(variableName);

			//replace variable name place holders with the actual variable names
			childContainer.replaceInBulk(PARENT_VARIABLE_NAME, variableName);
			attributeContainer.replaceInBulk(VARIABLE_NAME, variableName);

		} else {
			//create current container
			//Instead of actual variable names place holders will be used. Those
			//place holders need to be replaced after the actual variable names are known.
			currentContainer = buildCreationCodeContainerWithoutVariableName();
		}

		//extend current container with attribute container
		currentContainer.extend(attributeContainer);

		//extend current container with injected child container
		if (injectedChildContainer.isPresent()) {
			currentContainer.extend(injectedChildContainer.get());
		}

		//extend current container with child container
		currentContainer.extend(childContainer);

		//extend parent container with current container
		parentContainer.extend(currentContainer);

		//return extended parent container
		return parentContainer;
	}

	/**
	 * Creates a code container that contains the code for all children of the atom that corresponds to this code
	 * adaption.
	 *
	 * @return
	 */
	protected CodeContainer createCodeContainerForChildAtoms() {

		//get child node adaptions
		List<TreeNodeAdaption> childNodes = atom.createTreeNodeAdaption().getChildren();

		//loop through the child nodes and create code container
		CodeContainer allChildrenCodeContainer = new CodeContainer(scriptType);
		for (TreeNodeAdaption childNode : childNodes) {
			sysLog.debug("creating code container for child " + childNode.getName());
			Adaptable childAdaptable = childNode.getAdaptable();
			CodeAdaption childCodeAdaption = childAdaptable.createCodeAdaption(ScriptType.JAVA);
			allChildrenCodeContainer = childCodeAdaption.buildCodeContainer(allChildrenCodeContainer,
					Optional.ofNullable(null));
		}

		//post process the container
		allChildrenCodeContainer = postProcessAllChildrenCodeContainer(allChildrenCodeContainer);

		return allChildrenCodeContainer;
	}

	/**
	 * Post processes the container for all children
	 *
	 * @param allChildrenCodeContainer
	 * @return
	 */
	protected abstract CodeContainer postProcessAllChildrenCodeContainer(CodeContainer allChildrenCodeContainer);

	/**
	 * Returns true if the given optional container is not present or has an empty bulk.
	 *
	 * @param optionalContainer
	 * @return
	 */
	protected static boolean checkIfOptionalContainerHasEmptyBulk(Optional<CodeContainer> optionalContainer) {
		if (optionalContainer.isPresent()) {
			CodeContainer container = optionalContainer.get();
			return container.hasEmptyBulk();
		} else {
			return true;
		}
	}

	//#end region

	//#region CREATION CODE

	/**
	 * Builds code for creating the atom without using a variable names
	 *
	 * @param variableName
	 * @return
	 */
	protected abstract CodeContainer buildCreationCodeContainerWithVariableName(String variableName);

	/**
	 * Builds code for the constructor call, using a variable names
	 *
	 * @return
	 */
	protected abstract CodeContainer buildCreationCodeContainerWithoutVariableName();

	/**
	 * Returns true if the TreeNodeAdaption that corresponds to this CodeAdaption is the first child of its parent
	 * TreeNodeAdaption. Throws an exception if the TreeNodeAdaption has no parent.
	 *
	 * @return
	 */
	protected boolean isFirstChild() {
		TreeNodeAdaption thisNode = this.getAdaptable().createTreeNodeAdaption();
		TreeNodeAdaption parentNode = thisNode.getParent();
		if (parentNode == null) {
			throw new IllegalStateException("There is no parent tree node adaption.");
		}
		TreeNodeAdaption firstChildNode = parentNode.getChildren().get(0);
		boolean isFirstChild = thisNode.getTreePath().equals(firstChildNode.getTreePath());
		return isFirstChild;
	}

	/**
	 * Returns true if the previous sibling TreeNodeAdaption has children. Throws an exception if the TreeNodeAdaption
	 * has no parent or no previous sibling.
	 *
	 * @return
	 */
	protected boolean previousSiblingHasChildren() {
		TreeNodeAdaption thisNode = this.getAdaptable().createTreeNodeAdaption();

		//get parent tree node adaption
		TreeNodeAdaption parentNode = thisNode.getParent();
		if (parentNode == null) {
			throw new IllegalStateException("There is no parent tree node adaption.");
		}

		//get child index of current TreeNodeAdaption
		List<TreeNodeAdaption> children = parentNode.getChildren();
		String thisTreePath = thisNode.getTreePath();
		int thisIndex = -1;
		for (TreeNodeAdaption child : children) {
			String treePath = child.getTreePath();
			boolean isWantedChild = treePath.equals(thisTreePath);
			if (isWantedChild) {
				thisIndex = children.indexOf(child);
				break;
			}
		}

		//get previous sibling
		int previousSiblingIndex = thisIndex - 1;
		boolean hasNoPreviousSibling = previousSiblingIndex < 0;
		if (hasNoPreviousSibling) {
			throw new IllegalStateException("There is no previous sibling.");
		}
		TreeNodeAdaption previousSibling = children.get(previousSiblingIndex);

		//check if previous Sibling has children
		boolean previousSiblingHasChildren = previousSibling.hasChildren();

		return previousSiblingHasChildren;

	}

	/**
	 * Creates a distinct variable name for the given atom
	 *
	 * @param atom
	 * @return
	 */
	protected static String createVariableName(AbstractAtom atom) {
		String variableName = VariableNameRegistry.getInstance().getNewVariableName(atom);
		return variableName;
	}

	//#end region

	//#region ATTRIBUTE CODE

	/**
	 * Creates a new code container that contains code for setting the attribute values of the AbstractAtom that
	 * corresponds to this code adaption. Might be overridden by inheriting classes.
	 *
	 * @return
	 */
	protected CodeContainer buildCodeContainerForAttributes() {

		CodeContainer attributeContainer = new CodeContainer(scriptType);
		Field[] attributes = atom.getClass().getDeclaredFields();
		for (Field attribute : attributes) {
			attribute.setAccessible(true);
			boolean isAnnotated = IsParameters.isAnnotated(attribute);
			if (isAnnotated) {
				attributeContainer = extendCodeForChangedAttribute(attributeContainer, attribute);
			}
		}
		return attributeContainer;
	}

	/**
	 * Adds the code for the given attribute to the given initial code container if the value of the attribute is not
	 * the default value.
	 *
	 * @param attributeContainer
	 * @param attribute
	 * @return
	 */
	private CodeContainer extendCodeForChangedAttribute(CodeContainer attributeContainer, Field attribute) {
		//sysLog.debug("Existing field: " + field.getName());

		//sysLog.debug("The field " + field.getName() + " is annotated.");

		//get data from the attribute
		String defaultValueString = IsParameters.getDefaultValueString(attribute);
		String currentValueString = IsParameters.getCurrentValueString(attribute, atom);

		//sysLog.debug("Default value: " + defaultValueString);
		//sysLog.debug("Current value: " + currentValueString);

		//create code if the value is not the default value
		boolean isDefaultValue = currentValueString.equals(defaultValueString);
		if (isDefaultValue) {
			//parameter does not need to be included in code
			return attributeContainer;
		} else {
			CodeContainer extendedContainer = extendCodeForAttributeIfSetterExists(attributeContainer, attribute,
					currentValueString);
			return extendedContainer;
		}
	}

	/**
	 * Adds the code for the given attribute to the given initial code if a setter exists
	 *
	 * @param attributeContainer
	 * @param attribute
	 * @param valueString
	 * @return
	 */
	private CodeContainer extendCodeForAttributeIfSetterExists(
			CodeContainer attributeContainer,
			Field attribute,
			String valueString) {

		//check if setter exists
		boolean setterExists = IsParameters.simpleSetterExists(attribute, atom);
		if (setterExists) {
			//generate code
			CodeContainer extendedContainer = extendCodeForAttribute(attributeContainer, attribute, valueString);
			return extendedContainer;
		} else {
			String message = "Could not find the setter for the attribute '" + attribute.getName() + "'!";
			sysLog.error(message);
			throw new IllegalStateException(message);
		}
	}

	/**
	 * Builds code to set the value of an attribute
	 *
	 * @param attributeContainer
	 * @param attribute
	 * @param valueString
	 * @return
	 */
	protected abstract CodeContainer extendCodeForAttribute(
			CodeContainer attributeContainer,
			Field attribute,
			String valueString);

	//#end region

	//#end region

	//#region ACCESSORS

	@Override
	public Adaptable getAdaptable() {
		return atom;
	}

	//#end region

}
