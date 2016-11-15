package org.treez.model.atom.executable;

import org.treez.core.utils.Utils;

/**
 * Modifies the output path for the Executable
 */
public class ExecutableOutputPathModifier {

	//#region ATTRIBUTES

	private Executable executable;

	//#end region

	//#region CONSTRUCTORS

	public ExecutableOutputPathModifier(Executable executable) {
		this.executable = executable;
	}

	//#end region

	//#region METHODS

	/**
	 * Returns the output path, optionally modified by data and job index
	 *
	 * @return
	 */
	/**
	 * @return
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

		boolean doIncludeDateInFolder = executable.includeDateInFolder.get();
		if (doIncludeDateInFolder) {
			newOutputPath += Utils.getDateString();
		}
		return newOutputPath;
	}

	private String includeJobIndexInFolder(String outputPathExpression) {

		String newOutputPath = outputPathExpression;

		boolean doIncludejobIndexInFolder = executable.includeJobIndexInFolder.get();
		if (doIncludejobIndexInFolder) {
			newOutputPath += "#" + executable.getJobId();
		}
		return newOutputPath;
	}

	private String includeSubFolder(String outputPathExpression) {

		String newOutputPath = outputPathExpression;

		boolean doIncludeDateInSubFolder = executable.includeDateInSubFolder.get();
		boolean doIncludejobIndexInSubFolder = executable.includeJobIndexInSubFolder.get();
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

		boolean doIncludeDateInSubFolder = executable.includeDateInSubFolder.get();
		boolean doIncludejobIndexInSubFolder = executable.includeJobIndexInSubFolder.get();
		boolean doIncludeSubFolder = doIncludeDateInSubFolder || doIncludejobIndexInSubFolder;

		if (doIncludeSubFolder) {
			newOutputPath += "/";
		}

		newOutputPath += fileNameWithoutExtension; //is empty for directories

		boolean doIncludeDateInFile = executable.includeDateInFile.get();
		if (doIncludeDateInFile) {
			newOutputPath += "_" + Utils.getDateString();
		}

		boolean doIncludejobIndex = executable.includeJobIndexInFile.get();
		if (doIncludejobIndex) {
			newOutputPath += "#" + executable.getJobId();
		}
		newOutputPath += pathPostFix; //is empty for directories
		return newOutputPath;
	}

	//#end region

}
