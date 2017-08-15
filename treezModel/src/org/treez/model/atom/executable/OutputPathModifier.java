package org.treez.model.atom.executable;

import org.treez.core.utils.Utils;

public class OutputPathModifier {

	//#region ATTRIBUTES

	private Executable executable;

	//#end region

	//#region CONSTRUCTORS

	public OutputPathModifier(Executable executable) {
		this.executable = executable;
	}

	//#end region

	//#region METHODS

	/**
	 * Returns the output path, optionally modified by data and job index
	 */
	public String getModifiedOutputPath(String outputPath) {

		String outputPathString = outputPath.replace("\\", "/");

		//split path with point to determine file extension if one exists
		String[] subStrings = outputPathString.split("\\.");

		String pathBase = subStrings[0];
		String fileNameWithoutExtension = "";
		String pathPostFix = "";
		boolean hasFileExtension = subStrings.length > 1;
		if (hasFileExtension) {
			pathPostFix = "." + subStrings[1];
			fileNameWithoutExtension = Utils.extractFileName(pathBase);
			pathBase = Utils.extractParentFolder(pathBase);
		}

		String outputPathExpression = pathBase;

		outputPathExpression = includeDateInFolder(outputPathExpression);

		outputPathExpression = includeJobIndexInFolder(outputPathExpression);

		outputPathExpression = includeSubFolder(outputPathExpression);

		if (hasFileExtension) {
			//append file name and extension
			outputPathExpression = includeFileNameAndExtension(fileNameWithoutExtension, pathPostFix,
					outputPathExpression);
		}

		return outputPathExpression;
	}

	private String includeDateInFolder(String outputPathExpression) {

		String newOutputPath = outputPathExpression;

		boolean doIncludeDateInFolder = executable.includeDateInOutputFolder.get();
		if (doIncludeDateInFolder) {
			newOutputPath += "_" + Utils.getDateString();
		}
		return newOutputPath;
	}

	private String includeJobIndexInFolder(String outputPathExpression) {

		String newOutputPath = outputPathExpression;

		boolean doIncludejobIndexInFolder = executable.includeJobIndexInOutputFolder.get();
		if (doIncludejobIndexInFolder) {
			newOutputPath += "#" + executable.getJobId();
		}
		return newOutputPath;
	}

	private String includeSubFolder(String outputPathExpression) {

		String newOutputPath = outputPathExpression;

		boolean doIncludeDateInSubFolder = executable.includeDateInOutputSubFolder.get();
		boolean doIncludejobIndexInSubFolder = executable.includeJobIndexInOutputSubFolder.get();
		boolean doIncludeSubFolder = doIncludeDateInSubFolder || doIncludejobIndexInSubFolder;

		if (doIncludeSubFolder) {
			newOutputPath += "/";
		}

		if (doIncludeDateInSubFolder) {
			newOutputPath += Utils.getDateString();
		}

		if (doIncludejobIndexInSubFolder) {
			newOutputPath += "#" + executable.getJobId();
		}
		return newOutputPath;
	}

	private String includeFileNameAndExtension(
			String fileNameWithoutExtension,
			String pathPostFix,
			String outputPathExpression) {

		String newOutputPath = outputPathExpression;

		newOutputPath += "/";

		newOutputPath += fileNameWithoutExtension; //is empty for directories

		boolean doIncludeDateInFile = executable.includeDateInOutputFile.get();
		if (doIncludeDateInFile) {
			newOutputPath += "_" + Utils.getDateString();
		}

		boolean doIncludejobIndex = executable.includeJobIndexInOutputFile.get();
		if (doIncludejobIndex) {
			newOutputPath += "#" + executable.getJobId();
		}
		newOutputPath += pathPostFix; //is empty for directories
		return newOutputPath;
	}

	//#end region

}
