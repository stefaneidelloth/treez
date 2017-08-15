package org.treez.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Provides static helper methods
 */
public final class Utils {

	private static final Logger LOG = Logger.getLogger(Utils.class);

	//#region CONSTRUCTORS

	/**
	 * Private Constructor that prevents construction. This class must only contain static methods.
	 */
	private Utils() {}

	//#end region

	//#region METHODS

	//#region COLORS

	/**
	 * Parses a string and converts it to a RGB value Example input: "RGB { 200,200,34 }"
	 *
	 * @param colorString
	 * @return
	 */
	public static RGB convertToRGB(String colorString) {
		try {
			String[] substrings = colorString.split(",");
			String redString = substrings[0].replace("RGB {", "").trim();
			String greenString = substrings[1].trim();
			String blueString = substrings[2].replace("}", "").trim();

			int red = Integer.parseInt(redString);
			int green = Integer.parseInt(greenString);
			int blue = Integer.parseInt(blueString);

			return new RGB(red, green, blue);
		} catch (IllegalArgumentException e) {
			LOG.error("Could not convert " + colorString + " to a value.\n" + e.getMessage());
			return new RGB(0, 0, 0);
		}
	}

	//#end region

	//#region STRINGS

	/**
	 * Convert a List of Strings to a String array
	 *
	 * @param stringList
	 * @return
	 */
	public static String[] stringListToArray(List<String> stringList) {
		String[] headerArray = new String[stringList.size()];
		for (int columnIndex = 0; columnIndex < stringList.size(); columnIndex++) {
			headerArray[columnIndex] = stringList.get(columnIndex);
		}
		return headerArray;
	}

	/**
	 * Convert an enum set to string array
	 *
	 * @param enumSet
	 * @return
	 */
	public static String[] enumSetToStringArray(EnumSet<?> enumSet) {
		int size = enumSet.size();
		String[] array = new String[size];
		Object[] values = enumSet.toArray();
		for (int index = 0; index < size; index++) {
			Enum<?> value = (Enum<?>) values[index];
			array[index] = value.toString();
		}
		return array;
	}

	/**
	 * Changes the first character of a string to upper case
	 *
	 * @param name
	 * @return
	 */
	public static String firstToUpperCase(String name) {
		if (name.isEmpty()) {
			return name;
		}
		String firstLetter = name.substring(0, 1);
		return firstLetter.toUpperCase() + name.substring(1, name.length());
	}

	/**
	 * Shows an input dialog to retrieve a new string value
	 *
	 * @param title
	 * @param defaultValue
	 * @return
	 */
	public static String getInput(String title, String defaultValue) {
		InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(), "", title, defaultValue, null);
		int result = dlg.open();
		if (result == IDialogConstants.OK_ID) {
			return dlg.getValue();
		} else {
			return defaultValue;
		}
	}

	//#end region

	//#region INFO DIALOGS

	/**
	 * Shows a message
	 *
	 * @param message
	 */
	public static void showMessage(String message) {

		LOG.info(message);

		Runnable showMessageRunnable = () -> {
			Display currentDisplay = Display.getCurrent();
			if (currentDisplay == null) {
				currentDisplay = Display.getDefault();
			}

			if (currentDisplay != null) {
				MessageDialog.openConfirm(currentDisplay.getActiveShell(), "Info", message);
			}

		};

		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		if (display != null) {
			display.syncExec(showMessageRunnable);
		} else {
			LOG.info(message);
		}
	}

	/**
	 * Shows an error message
	 *
	 * @param message
	 */
	public static void showErrorMessage(String message) {

		Runnable showMessageRunnable = () -> {
			Display currentDisplay = Display.getCurrent();
			MessageDialog.openError(currentDisplay.getActiveShell(), "Error", message);
		};
		Display currentDisplay = Display.getCurrent();
		if (currentDisplay == null) {
			currentDisplay = new Display();
		}
		currentDisplay.syncExec(showMessageRunnable);
	}

	//#end regions

	//#region CLASS & INTERFACE REFLECTION

	/**
	 * Returns true if the given class has one of the the given types or implements an interface of the given types.
	 *
	 * @param object
	 * @param wantedTypeNames
	 *            names of wanted types, separated by ","
	 * @return
	 */
	public static boolean checkIfHasWantedType(Object object, String wantedTypeNames) {

		//get class of object
		Class<?> actualClass = object.getClass();

		String[] wantedTypeNameArray = wantedTypeNames.split(",");

		for (String wantedTypeName : wantedTypeNameArray) {
			//check class type by name
			String actualClassName = actualClass.getName();
			boolean hasWantedClassType = actualClassName.equals(wantedTypeName);
			if (hasWantedClassType) {
				return true;
			}

			//check class type by class
			Class<?> wantedClass = null;
			try {
				wantedClass = Class.forName(wantedTypeName);
			} catch (ClassNotFoundException e) {
				//nothing to do (might be an interface)
			}

			if (wantedClass != null) {
				//the wanted type represents a class: use it to perform the check
				boolean hasWantedType = wantedClass.isAssignableFrom(actualClass);
				if (hasWantedType) {
					return true;
				}

			} else {
				//the wanted type might represent an interface
				//check if the actual class implements that interface
				Set<Class<?>> implementedInterfaces = getAllInterfaces(actualClass);
				for (Class<?> currentInterface : implementedInterfaces) {
					boolean implementsWantedInterface = currentInterface.getName().equals(wantedTypeName);
					if (implementsWantedInterface) {
						//implements wanted interface
						return true;
					}
				}
			}
		}

		//does not implement wanted interface or type does not represent
		//any interface
		return false;

	}

	/**
	 * Returns the interfaces that are implemented by this class and all super classes
	 *
	 * @param actualClass
	 * @return
	 */
	private static Set<Class<?>> getAllInterfaces(Class<?> actualClass) {
		Set<Class<?>> implementedInterfaces = new HashSet<>();

		//add interfaces directly implemented by the given class
		Class<?>[] interfaces = actualClass.getInterfaces();
		for (Class<?> currentInterface : interfaces) {
			implementedInterfaces.add(currentInterface);
		}

		//add interfaces implemented by the super class
		Class<?> superClass = actualClass.getSuperclass();
		if (superClass != null) {
			Set<Class<?>> superInterfaces = getAllInterfaces(superClass);
			implementedInterfaces.addAll(superInterfaces);
		}
		return implementedInterfaces;
	}

	//#end region

	//#region FILE PATHS

	/**
	 * Extracts the parent path
	 */
	public static String extractParentFolder(String path) {
		String fileName = extractFileName(path);
		int endIndex = path.length() - fileName.length() - 1;
		String parentPath = path.substring(0, endIndex);
		return parentPath;
	}

	/**
	 * Extracts the file name from the given path
	 */
	public static String extractFileName(String pathWithoutExtension) {
		String path = pathWithoutExtension.replace("\\", "/");
		String[] subStrings = path.split("/");
		String fileName = subStrings[subStrings.length - 1];
		return fileName;
	}

	/**
	 * Includes a postFix in front of the last point in a file name
	 */
	public static String includeNumberInFileName(String fileName, String postFix) {
		String[] subStrings = fileName.split("\\.");
		String newFileName = subStrings[0];
		if (subStrings.length > 2) {
			for (int index = 1; index < subStrings.length - 2; index++) {
				newFileName += "." + subStrings[index];
			}
		}
		newFileName += postFix;
		newFileName += "." + subStrings[subStrings.length - 1];
		return newFileName;
	}

	/**
	 * Returns the current date & time as string
	 */
	public static String getDateString() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate;
	}

	/**
	 * Returns true if the given file path could represent a file (last subString contains a point)
	 */
	public static boolean isFilePath(String outputPath) {
		Objects.requireNonNull(outputPath, "Output path must not be null.");
		String outputPathString = outputPath.replace("\\", "/");
		String[] subStrings = outputPathString.split("/");
		String lastSubString = subStrings[subStrings.length - 1];

		boolean isFile = lastSubString.indexOf(".") > -1;
		return isFile;

	}

	//#end region

}
