package org.treez.data.variable;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.treez.core.Activator;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.adjustable.AdjustableAtomCodeAdaption;
import org.treez.core.atom.copy.CopyHelper;
import org.treez.core.atom.uisynchronizing.AbstractUiSynchronizingAtom;
import org.treez.core.data.row.Row;
import org.treez.core.data.table.TreezTable;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.TreeViewerAction;
import org.treez.data.evaluation.VariableDefinitionEvaluator;
import org.treez.data.row.VariableDefinitionRow;
import org.treez.data.table.VariableDefinitionTable;

/**
 * An item example
 */
public class VariableDefinition extends AbstractUiSynchronizingAtom<VariableDefinition> {

	private static final Logger LOG = Logger.getLogger(VariableDefinition.class);

	//#region ATTRIBUTES

	/**
	 * List of the variable definitions.
	 */
	private List<Row> definitionRows = new ArrayList<Row>();

	/**
	 * Evaluates the variable definitions
	 */
	private VariableDefinitionEvaluator evaluator = null;

	/**
	 * A table that contains the variable definitions
	 */
	private TreezTable table = null;

	//#end region

	//#region CONSTRUCTORS

	public VariableDefinition(String name) {
		super(name);
		initialize();
	}

	/**
	 * Copy constructor
	 */
	private VariableDefinition(VariableDefinition variableDefinitionToCopy) {
		super(variableDefinitionToCopy);
		initialize();
		definitionRows = CopyHelper.copyRowsForTargetTable(variableDefinitionToCopy.definitionRows, this.table);

	}

	//#end region

	//#region METHODS

	@Override
	public VariableDefinition getThis() {
		return this;
	}

	@Override
	public VariableDefinition copy() {
		return new VariableDefinition(this);
	}

	/**
	 * Initializes this VariableDefinition
	 */
	private void initialize() {
		updateTable();
		evaluator = new VariableDefinitionEvaluator();
	}

	/**
	 * Updates the table with the current definition rows
	 */
	private void updateTable() {
		this.table = new VariableDefinitionTable(this, definitionRows);
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("Definition.png");
	}

	/**
	 * Provides a control adaption for this atom
	 */
	@Override
	public AbstractControlAdaption createControlAdaption(
			Composite propertyMainForm,
			FocusChangingRefreshable treeViewRefreshable) {
		this.treeViewRefreshable = treeViewRefreshable;
		return new VariableDefinitionControlAdaption(propertyMainForm, this);
	}

	/**
	 * Provides a code adaption for this atom
	 */
	@Override
	public CodeAdaption createCodeAdaption(ScriptType scriptType) {
		return new AdjustableAtomCodeAdaption(this);
	}

	/**
	 * Creates the context menu actions
	 */
	@Override
	protected ArrayList<Object> createContextMenuActions(final TreeViewerRefreshable treeViewer) {

		ArrayList<Object> actions = new ArrayList<>();

		actions.add(new TreeViewerAction(
				"Add Atom",
				Activator.getImage(ISharedImages.IMG_OBJ_ADD),
				treeViewer,
				() -> LOG.debug("add")));

		return actions;
	}

	/**
	 * Defines a new variable
	 *
	 * @param name
	 * @param definition
	 * @param description
	 */
	public void define(String name, String definition, String description) {
		//LOG.debug("define");
		VariableDefinitionRow newRow = new VariableDefinitionRow(this, name, definition, description);

		int rowIndex = Math.max(0, getRowCount() - 1); //insert before empty row if empty row exists

		definitionRows.add(rowIndex, newRow);
		updateTable();
	}

	/**
	 * Creates a new variable name from a given name by checking existence of the name and adding a number
	 *
	 * @param oldName
	 * @return
	 */
	public String createNewVariableName(String oldName) {
		String newName = oldName;
		int number = 0;
		while (variableNameExists(newName)) {
			number = number + 1;
			newName = oldName + number;
		}
		return newName;
	}

	/**
	 * Checks if a variable name already exists
	 *
	 * @param name
	 * @return
	 */
	private boolean variableNameExists(String name) {
		for (Row definitionRow : getRows()) {
			if (definitionRow.getEntry("Name").equals(name)) {
				return true;
			}
		}
		return false;
	}

	//#region ACCESSORS

	public TreezTable getTable() {
		return table;
	}

	public VariableDefinitionEvaluator getEvaluator() {
		return evaluator;
	}

	public List<Row> getRows() {
		return definitionRows;
	}

	public int getRowCount() {
		return getRows().size();
	}

	public void setDefinitionRows(List<Row> definitionRows) {
		this.definitionRows = definitionRows;
		updateTable();
	}

	//#end region

}
