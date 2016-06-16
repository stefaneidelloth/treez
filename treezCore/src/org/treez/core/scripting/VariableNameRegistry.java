package org.treez.core.scripting;

import java.util.HashSet;
import java.util.Set;

import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.utils.Utils;

/**
 * A registry for the variable names. It can be used to ensure that a variable name has not already been used.
 */
public final class VariableNameRegistry {

	//#region SINGLETON

	/**
	 * The singleton instance
	 */
	private static VariableNameRegistry instance;

	/**
	 * Returns the singleton instance
	 *
	 * @return
	 */
	public static VariableNameRegistry getInstance() {
		if (instance == null) {
			instance = new VariableNameRegistry();
		}
		return instance;
	}

	/**
	 * Resets the singleton instance/deletes all variable names
	 */
	public static void reset() {
		instance = null;
	}

	//#end region

	//#region ATTRIBUTES

	private Set<String> variableNames;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Private Constructor that creates the singleton instance
	 */
	private VariableNameRegistry() {
		variableNames = new HashSet<>();
	}

	//#end region

	//#region METHODS

	/**
	 * Registers a new variable name. Throws an IllegalArgumentException if the variable name already exists;
	 *
	 * @param variableName
	 * @throws IllegalArgumentException
	 */
	public void register(String variableName) throws IllegalArgumentException {
		boolean alreadyExists = contains(variableName);
		if (alreadyExists) {
			throw new IllegalArgumentException("The variable name " + variableName + " already exists");
		}
		variableNames.add(variableName);
	}

	/**
	 * Returns true if the given variable name already exists
	 *
	 * @param variableName
	 * @return
	 */
	private boolean contains(String variableName) {
		boolean alreadyExists = variableNames.contains(variableName);
		return alreadyExists;
	}

	/**
	 * Creates a new variable name for the given AbstractAtom
	 *
	 * @param atom
	 * @return
	 */
	public String getNewVariableName(AbstractAtom<?> atom) {
		String atomName = atom.getName();
		String newName = atomName.replace(" ", "").trim();

		if (contains(newName)) {
			//add class name
			newName += atom.getClass().getSimpleName();
		}

		if (contains(newName)) {
			//add name of parent atom
			AbstractAtom<?> parentAtom = atom.getParentAtom();
			if (parentAtom != null) {
				String parentName = parentAtom.getName();
				newName += "In" + Utils.firstToUpperCase(parentName);
			}
		}

		if (contains(newName)) {
			//add increasing numbers
			int counter = 1;
			String numberName = newName + counter;
			while (contains(numberName)) {
				counter++;
				numberName = newName + counter;
			}
			newName = numberName;
		}

		register(newName);
		return newName;

	}
	//#end region

}
