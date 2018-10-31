package org.treez.core.scripting.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

/**
 * Provides helping methods to extract properties (e.g. the full class name) from the code of a class that is given as a
 * string.
 */
public final class ScriptingPropertyExtractor {

	private static final Logger LOG = LogManager.getLogger(ScriptingPropertyExtractor.class);

	//#region CONSTRUCTORS

	/**
	 * Private Constructor that prevents construction.
	 */
	private ScriptingPropertyExtractor() {}

	//#end region

	//#region METHODS

	/**
	 * Extracts the full class name from the java code, e.g. "org.treez.views.scripting.java.DynamicClass"
	 *
	 * @param javaCode
	 * @return
	 */
	public static String extractFullClassName(String javaCode) {
		String packageName = extractPackage(javaCode);
		String simpleClassName = extractSimpleClassName(javaCode);
		String className = packageName + "." + simpleClassName;
		return className;
	}

	/**
	 * Extracts the required bundle ids (e.g. "org.treez.core") from the import statements of the given javaCode.
	 *
	 * @param javaCode
	 * @return
	 */
	public static Set<String> extractBundleIds(String javaCode) {
		List<String> imports = extractImports(javaCode);

		Set<String> bundleIds = new HashSet<>();
		for (String importString : imports) {
			String bundleId = extractTreezBundleIdFromImport(importString);
			if (bundleId != null) {
				bundleIds.add(bundleId);
			} else {
				String message = "The import '" + importString + "' does not belong to a treez bundle.";
				LOG.warn(message);
			}
		}
		return bundleIds;
	}

	/**
	 * Extracts the simple class name from the given java code, e.g "DynamicClass"
	 *
	 * @param javaCode
	 * @return
	 */
	@SuppressWarnings("checkstyle:magicnumber")
	private static String extractSimpleClassName(String javaCode) {
		String regularExpression = "(?m)^public\\sclass\\s.*extends\\sModelProvider";
		String completeString = findWithRegularExpression(regularExpression, javaCode);
		if (completeString == null) {
			String message = "Could not extract simple class name from java code with regular expression "
					+ regularExpression;
			throw new IllegalArgumentException(message);
		} else {
			final int startIndex = 13;
			int endIndex = completeString.length() - 22;
			String simpleClassName = completeString.substring(startIndex, endIndex);
			return simpleClassName;
		}
	}

	/**
	 * Extracts the package from the given java code, e.g. "org.treez.views.scripting.java"
	 *
	 * @param javaCode
	 * @return
	 */
	private static String extractPackage(String javaCode) {
		String regularExpression = "(?m)^package\\s.*;$";
		String completeString = findWithRegularExpression(regularExpression, javaCode);
		if (completeString == null) {
			String message = "Could not extract package name from java code with regular expression "
					+ regularExpression;
			throw new IllegalArgumentException(message);
		} else {
			final int startIndex = 8;
			int endIndex = completeString.length() - 1;
			String packageName = completeString.substring(startIndex, endIndex);
			return packageName;
		}
	}

	/**
	 * Extracts the treez bundle id from the given package name, e.g. org.treez.core. Returns null if the bundle id
	 * could not be extracted.
	 */
	private static String extractTreezBundleIdFromImport(String importString) {
		String[] subStrings = importString.split("\\.");
		boolean containsBundleId = subStrings[0].equals("org") && subStrings[1].equals("treez")
				&& (subStrings.length > 2);
		if (containsBundleId) {
			String bundleId = "org.treez." + subStrings[2];
			return bundleId;
		} else {
			return null;
		}
	}

	/**
	 * Extracts the imports from the given java code
	 */
	private static List<String> extractImports(String javaCode) {
		String regularExpression = "(?m)^import\\s.*;$";
		List<String> completeStrings = findAllWithRegularExpression(regularExpression, javaCode);
		if (completeStrings == null) {
			String message = "Could not extract imports from java code with regular expression " + regularExpression;
			throw new IllegalArgumentException(message);
		} else {
			final int startIndex = 7;
			List<String> importStrings = new ArrayList<>();
			for (String completeString : completeStrings) {
				int endIndex = completeString.length() - 1;
				String importString = completeString.substring(startIndex, endIndex);
				importStrings.add(importString);
			}

			return importStrings;
		}
	}

	/**
	 * Extracts a substring that matches a regular expression. Returns null if no matching substring could be found.
	 * Only the first match will be returned.
	 *
	 * @param regularExpression
	 * @param parentString
	 * @return
	 */
	private static String findWithRegularExpression(String regularExpression, String parentString) {
		Pattern pattern = Pattern.compile(regularExpression);
		Matcher matcher = pattern.matcher(parentString);
		if (matcher.find()) {
			String subString = matcher.group(0);
			return subString;
		} else {
			return null;
		}
	}

	/**
	 * Extracts all substrings that matches a regular expression. Returns null if no matching substring could be found.
	 *
	 * @param regularExpression
	 * @param parentString
	 * @return
	 */
	private static List<String> findAllWithRegularExpression(String regularExpression, String parentString) {
		Pattern pattern = Pattern.compile(regularExpression);
		Matcher matcher = pattern.matcher(parentString);
		List<String> subStrings = new ArrayList<>();
		while (matcher.find()) {
			String subString = matcher.group(0);
			subStrings.add(subString);
		}

		if (subStrings.size() > 0) {
			return subStrings;
		} else {
			return null;
		}

	}

	//#end region

}
