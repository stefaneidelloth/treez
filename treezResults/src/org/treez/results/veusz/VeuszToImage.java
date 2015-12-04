package org.treez.results.veusz;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.treez.core.atom.attribute.FileHelper;

/**
 * This class provides functions to convert from veusz string to images
 */
public final class VeuszToImage {

	// #region ATTRIBUTES
	private static final String TEMPORARY_VEUSZ_FILE_NAME = "temporary_veusz_file.vsz";

	private static final String TEMPORARY_PNG_FILE_NAME = "temporary_veusz_image_file.png";

	private static final String FILE_SEPARATOR = "/";

	// #end region

	// #region CONSTRUCTORS

	/**
	 * Private Constructor that prevents construction
	 */
	private VeuszToImage() {}

	// #end region

	// #region METHODS

	/**
	 * Converts a veusz text to an image
	 *
	 * @param veuszText
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static Image convert(String veuszText, String veuszPath, String tempDirectory)
			throws IOException,
			InterruptedException {

		saveAsTemporaryVeuszFile(veuszText, tempDirectory);
		convertTemporaryVeuszFileToPng(veuszPath, tempDirectory);
		return readImage(tempDirectory);
	}

	/**
	 * Saves the given veusz string as temporary file
	 *
	 * @param veuszText
	 * @throws IOException
	 */
	static void saveAsTemporaryVeuszFile(String veuszText, String tempDirectory) throws IOException {
		String filePath = FileHelper.trimEndingFileSeparators(tempDirectory) + FILE_SEPARATOR
				+ TEMPORARY_VEUSZ_FILE_NAME;
		File tempFile = new File(filePath);
		FileUtils.writeStringToFile(tempFile, veuszText);
	}

	/**
	 * Converts the temporary veusz file to a temporary png image and deletes the temporary veusz file
	 *
	 * @throws IOException
	 * @throws InterruptedException
	 */
	static void convertTemporaryVeuszFileToPng(String veuszPath, String tempDirectory)
			throws IOException,
			InterruptedException {

		//construct file paths
		String veuszFilePath = FileHelper.trimEndingFileSeparators(tempDirectory) + FILE_SEPARATOR
				+ TEMPORARY_VEUSZ_FILE_NAME;
		String pngFilePath = FileHelper.trimEndingFileSeparators(tempDirectory) + FILE_SEPARATOR
				+ TEMPORARY_PNG_FILE_NAME;

		File tempFile = new File(veuszFilePath);
		if (tempFile.exists()) {

			String conversionCommand = "\"" + veuszPath + "\"  --export=\"" + pngFilePath + "\" \"" + veuszFilePath
					+ "\"";

			Process process = Runtime.getRuntime().exec(conversionCommand);
			process.waitFor();

			//tempFile.delete();

		} else {
			throw new IllegalStateException("The temporary file does not exist. Please save first.");
		}

	}

	/**
	 * Reads an image from the temporary image file and deletes the temporary file
	 *
	 * @return
	 */
	static Image readImage(String tempPath) {

		//construct file paths
		String pngFilePath = tempPath + FILE_SEPARATOR + TEMPORARY_PNG_FILE_NAME;

		File pngFile = new File(pngFilePath);
		if (pngFile.exists()) {
			Image image = new Image(Display.getCurrent(), pngFilePath);
			pngFile.delete();
			return image;
		} else {
			throw new IllegalStateException("The temporary image file does not exist. Please convert first.");
		}
	}

	// #end region

	// #region ACCESSORS

	// #end region

}
