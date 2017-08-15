package org.treez.model.atom.executable;

import org.treez.core.utils.Utils;

public class InputPathModifier {

	//#region ATTRIBUTES

	private InputPathProvider inputPathProvider;

	//#end region

	//#region CONSTRUCTORS

	public InputPathModifier(InputPathProvider executable) {
		this.inputPathProvider = executable;
	}

	//#end region

	//#region METHODS

	/**
	 * Returns the input path, optionally modified by data and job index
	 */
	public String getModifiedInputPath(String inputPath) {

		String inputPathString = inputPath.replace("\\", "/");

		//split path with point to determine file extension if one exists
		String[] subStrings = inputPathString.split("\\.");

		String pathBase = subStrings[0];
		String fileNameWithoutExtension = "";
		String pathPostFix = "";
		boolean hasFileExtension = subStrings.length > 1;
		if (hasFileExtension) {
			pathPostFix = "." + subStrings[1];
			fileNameWithoutExtension = Utils.extractFileName(pathBase);
			pathBase = Utils.extractParentFolder(pathBase);
		}

		String inputPathExpression = pathBase;

		inputPathExpression = includeDateInFolder(inputPathExpression);

		inputPathExpression = includeJobIndexInFolder(inputPathExpression);

		inputPathExpression = includeSubFolder(inputPathExpression);

		if (hasFileExtension) {
			//append file name and extension
			inputPathExpression = includeFileNameAndExtension(fileNameWithoutExtension, pathPostFix,
					inputPathExpression);
		}

		return inputPathExpression;
	}

	private String includeDateInFolder(String inputPathExpression) {

		String newInputPath = inputPathExpression;

		boolean doIncludeDateInFolder = inputPathProvider.getIncludeDateInInputFolder();
		if (doIncludeDateInFolder) {
			newInputPath += "_" + Utils.getDateString();
		}
		return newInputPath;
	}

	private String includeJobIndexInFolder(String inputPathExpression) {

		String newInputPath = inputPathExpression;

		boolean doIncludejobIndexInFolder = inputPathProvider.getIncludeJobIndexInInputFolder();
		if (doIncludejobIndexInFolder) {
			newInputPath += "#" + inputPathProvider.getJobId();
		}
		return newInputPath;
	}

	private String includeSubFolder(String inputPathExpression) {

		String newInputPath = inputPathExpression;

		boolean doIncludeDateInSubFolder = inputPathProvider.getIncludeDateInInputSubFolder();
		boolean doIncludejobIndexInSubFolder = inputPathProvider.getIncludeJobIndexInInputSubFolder();
		boolean doIncludeSubFolder = doIncludeDateInSubFolder || doIncludejobIndexInSubFolder;

		if (doIncludeSubFolder) {
			newInputPath += "/";
		}

		if (doIncludeDateInSubFolder) {
			newInputPath += Utils.getDateString();
		}

		if (doIncludejobIndexInSubFolder) {
			newInputPath += "#" + inputPathProvider.getJobId();
		}
		return newInputPath;
	}

	private String includeFileNameAndExtension(
			String fileNameWithoutExtension,
			String pathPostFix,
			String inputPathExpression) {

		String newInputPath = inputPathExpression;

		newInputPath += "/";

		newInputPath += fileNameWithoutExtension; //is empty for directories

		boolean doIncludeDateInFile = inputPathProvider.getIncludeDateInInputFile();
		if (doIncludeDateInFile) {
			newInputPath += "_" + Utils.getDateString();
		}

		boolean doIncludejobIndex = inputPathProvider.getIncludeJobIndexInInputFile();
		if (doIncludejobIndex) {
			newInputPath += "#" + inputPathProvider.getJobId();
		}
		newInputPath += pathPostFix; //is empty for directories
		return newInputPath;
	}

	//#end region

}
