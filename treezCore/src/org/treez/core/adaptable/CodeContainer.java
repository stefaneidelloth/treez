package org.treez.core.adaptable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.treez.core.scripting.ScriptType;

/**
 * Represents a container for several parts of code. The container might be
 * altered in an iterative process while it is passed around. After finishing
 * that process the different parts of the code can be put together.
 */
public class CodeContainer {

	//#region ATTRIBUTES

	private ScriptType scriptType;

	private List<String> headerLines = new ArrayList<>();

	private Set<String> importLines = new HashSet<>();

	private List<String> openingLines = new ArrayList<>();

	private List<String> bulkLines = new ArrayList<>();

	private List<String> closingLines = new ArrayList<>();

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param scriptType
	 */
	public CodeContainer(ScriptType scriptType) {
		this.scriptType = scriptType;
	}

	//#end region

	//#region METHODS

	//#region build

	/**
	 * Returns the complete code for this code container
	 *
	 * @return
	 */
	public String buildCode() {
		String headerCode = joinLines(headerLines);
		String importCode = joinLines(importLines);
		String openingCode = joinLines(openingLines);
		String bulkCode = joinLines(bulkLines);
		String closingCode = joinLines(closingLines);

		String code = headerCode + importCode + openingCode + bulkCode
				+ closingCode;
		return code;
	}

	/**
	 * Joins the given code lines to a single string
	 *
	 * @param lines
	 * @return
	 */
	public String joinLines(Collection<String> lines) {
		String joinedCode = String.join("\n", lines);
		return joinedCode;
	}

	//#end region

	//#region EXTEND

	/**
	 * Extends this code container with the code of the given code container.
	 *
	 * @param codeContainer
	 */
	public void extend(CodeContainer codeContainer) {
		Objects.requireNonNull(codeContainer,
				"Code container must not be null.");

		boolean hasEqualType = scriptType.equals(codeContainer.scriptType);
		if (!hasEqualType) {
			String message = "The given code container does not have the expected script type '"
					+ scriptType + "' but '" + codeContainer.scriptType + "'.";
			throw new IllegalArgumentException(message);
		}

		List<String> extraHeaderLines = codeContainer.getHeaderLines();
		this.headerLines.addAll(extraHeaderLines);

		Set<String> imports = codeContainer.getImports();
		extendImports(imports);

		List<String> extraOpeningLines = codeContainer.getOpeningLines();
		this.openingLines.addAll(extraOpeningLines);

		List<String> extraBulkLines = codeContainer.getBulkLines();
		this.bulkLines.addAll(extraBulkLines);

		List<String> extraClosingLines = codeContainer.getClosingLines();
		this.closingLines.addAll(extraClosingLines);

	}

	/**
	 * Extends the header
	 *
	 * @param header
	 */
	public void extendHeader(String header) {
		if (StringUtils.isBlank(header)) {
			throw new IllegalArgumentException(
					"Extended code must not be empty.");
		}
		headerLines.add(header);

	}

	/**
	 * Extends the header with an empty line
	 */
	public void extendHeaderWithEmptyLine() {
		headerLines.add("");
	}

	/**
	 * Extends the imports with a single import
	 *
	 * @param importString
	 */
	public void extendImports(String importString) {
		if (StringUtils.isBlank(importString)) {
			throw new IllegalArgumentException(
					"Extended code must not be empty.");
		}
		this.importLines.add(importString);
	}

	/**
	 * Extends the imports with the given set of imports
	 *
	 * @param imports
	 */
	public void extendImports(Set<String> imports) {
		this.importLines.addAll(imports);
	}

	/**
	 * Extends the opening
	 *
	 * @param newOpeningLine
	 */
	public void extendOpening(String newOpeningLine) {
		if (StringUtils.isBlank(newOpeningLine)) {
			throw new IllegalArgumentException("Added code must not be empty.");
		}
		openingLines.add(newOpeningLine);
	}

	/**
	 * Extends the opening with an empty line
	 */
	public void extendOpeningWithEmptyLine() {
		openingLines.add("");
	}

	/**
	 * Extends the bulk
	 *
	 * @param newbulkLine
	 */
	public void extendBulk(String newbulkLine) {
		if (StringUtils.isBlank(newbulkLine)) {
			throw new IllegalArgumentException("Added code must not be empty.");
		}
		bulkLines.add(newbulkLine);
	}

	/**
	 * Extends the bulk with an empty line
	 */
	public void extendBulkWithEmptyLine() {
		bulkLines.add("");
	}

	/**
	 * Extends the closing
	 *
	 * @param newClosingLine
	 */
	public void extendClosing(String newClosingLine) {
		if (StringUtils.isBlank(newClosingLine)) {
			throw new IllegalArgumentException("Added code must not be empty.");
		}
		closingLines.add(newClosingLine);

	}

	/**
	 * Extends the closing with an empty line
	 */
	public void extendClosingWithEmptyLine() {
		closingLines.add("");
	}

	//#end region

	//#region REPLACE

	/**
	 * Replaces all occurrences of the first given string with the second given
	 * string
	 *
	 * @param valueToReplace
	 * @param newValue
	 */
	public void replaceInBulk(String valueToReplace, String newValue) {
		for (int index = 0; index < bulkLines.size(); index++) {
			String currentLine = bulkLines.get(index);
			boolean containsValueToReplace = currentLine
					.contains(valueToReplace);
			if (containsValueToReplace) {
				String newLine = currentLine.replace(valueToReplace, newValue);
				bulkLines.set(index, newLine);
			}
		}
	}

	//#end region

	//#region TRIM

	/**
	 * Makes sure that the bulk ends with a single empty line (removes empty
	 * lines and/or adds an empty line as required)
	 */
	public void makeBulkEndWithSingleEmptyLine() {

		//remove empty lines at the end of the bulk
		for (int index = bulkLines.size() - 1; index >= 0; index--) {
			String bulkLine = bulkLines.get(index);
			boolean isEmpty = StringUtils.isBlank(bulkLine);
			if (isEmpty) {
				bulkLines.remove(index);
			} else {
				break;
			}
		}

		//add a single empty line at the end of the bulk
		extendBulkWithEmptyLine();
	}

	//#end region

	//#end region

	//#region ACCESSORS

	//#region HEADER

	/**
	 * Returns the first lines of the code, for example a package declaration.
	 * (Part I)
	 *
	 * @return
	 */
	public List<String> getHeaderLines() {
		return headerLines;
	}

	/**
	 * @param headerLines
	 */
	public void setHeaderLines(List<String> headerLines) {
		this.headerLines = headerLines;
	}

	//#end region

	//#region IMPORTS

	/**
	 * Returns the import lines as a set of Strings. (Part II)
	 *
	 * @return
	 */
	public Set<String> getImports() {
		return importLines;
	}

	/**
	 * @param imports
	 */
	public void setImports(Set<String> imports) {
		this.importLines = imports;
	}

	//#end region

	//#region OPENING

	/**
	 * Returns the opening code, e.g. the declaration of a class and its
	 * constructor
	 *
	 * @return
	 */
	public List<String> getOpeningLines() {
		return openingLines;
	}

	/**
	 * @param openingLines
	 */
	public void setOpeningLines(List<String> openingLines) {
		this.openingLines = openingLines;
	}

	//#end region

	//#region BULK

	/**
	 * Returns the bulk of the code
	 *
	 * @return
	 */
	public List<String> getBulkLines() {
		return bulkLines;
	}

	/**
	 * Returns true if bulk is empty
	 *
	 * @return
	 */
	public boolean hasEmptyBulk() {
		if (bulkLines.isEmpty()) {
			return true;
		} else {
			for (String bulkLine : bulkLines) {
				boolean isEmpty = StringUtils.isBlank(bulkLine);
				if (!isEmpty) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @param bulkLines
	 */
	public void setBulkLines(List<String> bulkLines) {
		this.bulkLines = bulkLines;
	}

	//#end region

	//#region CLOSING

	/**
	 * Returns the end of the code, e.g. the closing brackets for a class
	 * definition
	 *
	 * @return
	 */
	public List<String> getClosingLines() {
		return closingLines;
	}

	/**
	 * @param closingLines
	 */
	public void setClosingLines(List<String> closingLines) {
		this.closingLines = closingLines;
	}

	//#end region

	//#region EMPTY

	/**
	 * Returns true if this code container does not contain any code
	 *
	 * @return
	 */
	public boolean isEmpty() {
		boolean startIsEmpty = headerLines.isEmpty() && importLines.isEmpty()
				&& openingLines.isEmpty();
		boolean isEmpty = startIsEmpty && bulkLines.isEmpty()
				&& closingLines.isEmpty();
		return isEmpty;
	}

	//#end region

	//#end region

}
