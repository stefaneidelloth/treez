package org.treez.core.path;

import org.treez.core.atom.base.AbstractAtom;

/**
 * Provides a method to validate model paths
 */
public final class ModelPathValidator {

	//#region ATTRIBUTES

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Private Constructor that prevents construction.
	 */
	private ModelPathValidator() {}

	//#end region

	//#region METHODS

	/**
	 * Returns true if the given path string is a valid path of the given root atom. The path has to start with the name
	 * of the given atom. If the name of the root atom is "model", the path could for example be
	 * model.page.section.textfield
	 *
	 * @param path
	 * @param root
	 * @return
	 */
	public static boolean isValidModelPath(String path, AbstractAtom<?> root) {

		String rootName = root.getName();
		int rootNameLength = rootName.length();
		String firstName = path.substring(0, rootNameLength);
		boolean pathStartsWithRootName = firstName.equals(rootName);

		if (pathStartsWithRootName) {

			String childPath = path.substring(rootNameLength + 1, path.length());

			try {
				AbstractAtom<?> targetAtom = root.getChild(childPath);
				if (targetAtom == null) {
					//could not get target atom
					return false;
				}
			} catch (IllegalArgumentException exception) {
				//could not get target atom
				return false;
			}
		} else {
			//given path is wrong: it does not start with the name of the given root atom
			return false;
		}

		return true;

	}

	//#end region

}
