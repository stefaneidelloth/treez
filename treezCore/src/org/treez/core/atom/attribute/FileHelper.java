package org.treez.core.atom.attribute;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Provides some helper methods for files and directories
 */
public final class FileHelper {

	//#region CONSTRUCTORS

	/**
	 * Private Constructor that prevents construction
	 */
	private FileHelper() {}

	//#end region

	//#region METHODS

	/**
	 * Opens a directory with the given path
	 *
	 * @param directoryPath
	 */
	public static void openDirectory(String directoryPath) {
		openFile(directoryPath);
	}

	/**
	 * Opens a file with the given path
	 * 
	 * @param filePath
	 */
	public static void openFile(String filePath) {
		File file = new File(filePath);
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			String cmd;
			try {
				cmd = "rundll32 url.dll,FileProtocolHandler " + file.getCanonicalPath();
				Runtime.getRuntime().exec(cmd);
			} catch (IOException e) {
				showErrorForWrongPath();
			}
		} else {
			try {
				Desktop.getDesktop().edit(file);
			} catch (IOException e) {
				showErrorForWrongPath();
			}
		}
	}

	/**
	 * Shows an error dialog telling the user that the file path is wrong
	 */
	private static void showErrorForWrongPath() {
		Display display = Display.getCurrent();
		Shell shell = display.getActiveShell();
		MessageDialog.openError(shell, "Error", "Could not open file or directory. Please check the path.");
	}

	/**
	 * Removes file separators at the end of a directory path, e.g. D:\ => D:
	 * 
	 * @param directoryThatMightEndWithSeparator
	 * @return
	 */
	public static String trimEndingFileSeparators(String directoryThatMightEndWithSeparator) {

		String directoryPath = directoryThatMightEndWithSeparator;
		int length = directoryPath.length();

		String lastCharacter = directoryPath.substring(length - 1, length);
		boolean hasBackward = lastCharacter.equals("\\");
		boolean hasForward = lastCharacter.equals("/");
		if (hasBackward || hasForward) {
			directoryPath = directoryPath.substring(0, length - 1);
		}

		return directoryPath;
	}

	/**
	 * Returns true if the text represents a valid directory path
	 *
	 * @param text
	 * @return
	 */
	public static boolean isValidDirectoryPath(String text) {
		return isValidFilePath(text);
	}

	/**
	 * Returns true if the text represents a valid file path
	 *
	 * @param text
	 * @return
	 */
	public static boolean isValidFilePath(String text) {
		if (text.isEmpty()) {
			return true;
		}
		File file = new File(text);
		return file.exists();
	}

	/**
	 * Returns the file extension of a given file path (including the point, e.g. ".txt") Returns null if the file
	 * extension can not be found.
	 * 
	 * @param dataFilePath
	 * @return
	 */
	public static String getFileExtension(String dataFilePath) {
		int lastIndexOfPoint = dataFilePath.lastIndexOf(".");
		if (lastIndexOfPoint == -1) {
			return null;
		}
		return dataFilePath.substring(lastIndexOfPoint);
	}

	//#end region

}
