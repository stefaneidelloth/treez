package org.treez.core.path;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.utils.Utils;

/**
 * Allows to the select a model path from a treez model
 */
public final class ModelPathSelector {

	private static final Logger LOG = Logger.getLogger(ModelPathSelector.class);

	//#region CONSTRUCTORS

	/**
	 * Private Constructor that prevents construction.
	 */
	private ModelPathSelector() {}

	//#end region

	//#region METHODS

	/**
	 * Selects a model path from the given model. The targetClassName restricts the types of atoms that can be selected,
	 * e.g. AttributeAtom.
	 *
	 * @param model
	 * @param targetClassNames
	 * @param defaultModelPath
	 * @return
	 */
	public static String selectTreePath(AbstractAtom<?> model, String targetClassNames, String defaultModelPath) {

		LOG.debug("Selecting tree path");
		ModelPathTreeSelectionWindow selectionWindow = new ModelPathTreeSelectionWindow();
		selectionWindow.selectModelPath(model, targetClassNames, defaultModelPath);
		String modelPath = selectionWindow.getModelPath();

		return modelPath;
	}

	/**
	 * Returns a list with the target paths that are available in the given model for the given targetClassName(s). If
	 * several target class names are used, they have to be separated with ",".
	 *
	 * @param model
	 * @param typeNames
	 * @return
	 */
	public static List<String> getAvailableTargetPaths(
			AbstractAtom<?> model,
			String typeNames,
			boolean hasToBeEnabled,
			FilterDelegate filterDelegate) {

		//convert comma separated type names to array of type names
		String[] typeNameArray = typeNames.split(",");

		//get child nodes
		List<TreeNodeAdaption> childNodes = model.createTreeNodeAdaption().getChildren();

		//loop through the child nodes to collect the available paths
		List<String> availablePaths = new ArrayList<>();
		for (TreeNodeAdaption childNode : childNodes) {
			//get child atom
			AbstractAtom<?> child = (AbstractAtom<?>) childNode.getAdaptable();

			//add path of child atom if it has the wanted type
			for (String typeName : typeNameArray) {
				boolean hasWantedType = Utils.checkIfHasWantedType(child, typeName);
				if (!hasWantedType) {
					continue;
				}

				if (filterDelegate != null) {
					boolean passedFilter = filterDelegate.include(child);
					if (!passedFilter) {
						continue;
					}
				}

				String path = childNode.getTreePath();
				if (hasToBeEnabled) {
					boolean isEnabled = checkIfAtomIsEnabled(child);
					if (!isEnabled) {
						continue;
					}
				}

				availablePaths.add(path);

			}

			//collect available paths from sub children
			availablePaths.addAll(getAvailableTargetPaths(child, typeNames, hasToBeEnabled, filterDelegate));

		}

		return availablePaths;
	}

	/**
	 * Checks if the given atom has a method isEnabled and this method returns true
	 *
	 * @param child
	 * @return
	 */
	private static boolean checkIfAtomIsEnabled(AbstractAtom<?> atom) {

		Method method;
		try {
			Class<?>[] argumentTypes = null;
			method = atom.getClass().getMethod("isEnabled", argumentTypes);
		} catch (NoSuchMethodException e) {
			return false;
		} catch (SecurityException exception) {
			String message = "Could not access method 'isEnabled'. Returning false";
			LOG.warn(message, exception);
			return false;
		}

		boolean isEnabled = false;
		try {
			Object[] arguments = null;
			isEnabled = (boolean) method.invoke(atom, arguments);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
			String message = "Could not access method 'isEnabled'. Returning false";
			LOG.warn(message, exception);
			return false;
		}

		return isEnabled;

	}

	//#end region

}
