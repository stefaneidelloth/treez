package org.treez.data.row;

import java.util.HashMap;
import java.util.List;

import org.treez.core.data.row.Row;
import org.treez.data.evaluation.VariableDefinitionEvaluator;
import org.treez.data.evaluation.VariableDefinitionResult;
import org.treez.data.variable.VariableDefinition;

/**
 * Represents a table row and evaluates the variable definition that it contains
 */

public class VariableDefinitionRow extends Row {

	//#region ATTRIBUTES

	/**
	 * The VariableDefintion this row belongs to
	 */
	private VariableDefinition variableDefinition;

	/**
	 * The tool tip for the definition; contains error messages if evaluation fails
	 */
	private String definitionTooltip;

	/**
	 * The tool tip for the name; contains error messages if name is not allowed
	 */
	private String nameTooltip;

	//#end region

	//#region CONSTRUCTORS

	@SuppressWarnings("checkstyle:magicnumber")
	public VariableDefinitionRow(
			VariableDefinition variableDefinition,
			String name,
			String definition,
			String description) {
		super(variableDefinition.getTable());
		this.variableDefinition = variableDefinition;
		this.definitionTooltip = "";
		this.nameTooltip = "";

		List<String> headers = table.getHeaders();

		entryMap = new HashMap<String, Object>();
		entryMap.put(headers.get(0), name);
		entryMap.put(headers.get(1), definition);
		entryMap.put(headers.get(2), "");
		entryMap.put(headers.get(3), "");
		entryMap.put(headers.get(4), "");
		entryMap.put(headers.get(5), description);

		if (!name.equals("")) {
			checkName();
		}

		if (!definition.equals("")) {
			evaluateDefinition();
		}
	}

	/**
	 * Copy constructor (creates a new variable name)
	 */
	public VariableDefinitionRow(VariableDefinitionRow variableDefinitionRow) {
		super(variableDefinitionRow.table);
		this.variableDefinition = variableDefinitionRow.variableDefinition;
		this.definitionTooltip = "";
		this.nameTooltip = "";
		this.entryMap = new HashMap<>();

		//set new variable name
		String oldName = variableDefinitionRow.getEntryAsString("Name");
		String newName = variableDefinition.createNewVariableName(oldName);
		setEntry("Name", newName);

		//set and evaluate definition
		setEntry("Definition", variableDefinitionRow.getEntry("Definition"));

		//copy description
		setEntry("Description", variableDefinitionRow.getEntry("Description"));
	}

	//#end region

	//#region METHODS

	/**
	 * Sets an entry of this row for the column with the given column header
	 *
	 * @param columnHeader
	 * @param entry
	 */
	@Override
	public void setEntry(String columnHeader, Object entry) {
		List<String> headers = table.getHeaders();
		if (headers.contains(columnHeader)) {
			this.entryMap.put(columnHeader, entry);

			String nameHeader = headers.get(0);
			boolean isNameEntry = columnHeader.equals(nameHeader);
			if (isNameEntry) {
				checkName();
			}

			String definitionHeader = headers.get(1);
			boolean isDefinitionEntry = columnHeader.equals(definitionHeader);

			if (isDefinitionEntry) {
				evaluateDefinition();

				boolean createNewRow = isLastRow() && !isEmpty();
				if (createNewRow) {
					table.addEmptyRow();
				}
			}

		} else {
			throw new IllegalArgumentException(
					"The columnHeader " + columnHeader + " for entry " + entry + " is not valid");
		}
	}

	/**
	 * checks if a name exits more than once
	 */
	public void checkName() {
		String name = getEntryAsString("Name");
		int numberOfOccurences = getNumberOfEntries("Name", name);
		boolean errorExists = numberOfOccurences > 1;
		if (errorExists) {
			nameTooltip = "Duplicate variable name";
		} else {
			nameTooltip = "";
		}
	}

	/**
	 * Counts the number of occurrences for the given filePath in the column with given header
	 *
	 * @param header
	 * @param value
	 * @return
	 */
	private int getNumberOfEntries(String header, String value) {
		int count = 0;
		for (Row row : variableDefinition.getRows()) {
			if (row.getEntry(header).equals(value)) {
				count = count + 1;
			}
		}
		return count;
	}

	/**
	 * Evaluates a definition and sets the filePath, unit and type entries
	 */
	public void evaluateDefinition() {

		//get variable name and definition
		String name = getEntryAsString("Name");
		String definition = getEntryAsString("Definition");

		//evaluate definition
		VariableDefinitionEvaluator evaluator = variableDefinition.getEvaluator();
		VariableDefinitionResult result = evaluator.evaluate(name, definition);
		String value = result.getValue();
		String unit = result.getUnit();
		String type = result.getType();
		String error = result.getError();

		//write results to this row
		setEntry("Value", value);
		setEntry("Unit", unit);
		setEntry("Type", type);
		definitionTooltip = error;
	}

	public String getToolTip(String header) {
		if (header.equals("Name")) {
			return nameTooltip;
		} else if (header.equals("Definition")) {
			return definitionTooltip;
		} else {
			return "";
		}
	}

	public VariableDefinition getVariableDefinition() {
		return variableDefinition;
	}

	//#end region

}
