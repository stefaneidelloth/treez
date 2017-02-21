package org.treez.data.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.adaptable.GraphicsAdaption;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.data.cell.CellEditorFactory;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.row.Row;
import org.treez.core.data.table.TreezTable;
import org.treez.core.scripting.ScriptType;
import org.treez.data.row.VariableDefinitionLabelProvider;
import org.treez.data.row.VariableDefinitionRow;
import org.treez.data.variable.VariableDefinition;

/**
 * The table that is used for the VariableDefinition
 */
public class VariableDefinitionTable implements TreezTable {

	//#region ATTRIBUTES

	private List<String> headers = null;

	private Map<String, ColumnType> columnTypes = null;

	private Map<String, Boolean> editable = null;

	private List<Row> definitionRows;

	private VariableDefinition parent;

	//#end region

	//#region METHODS

	public VariableDefinitionTable(VariableDefinition parent, List<Row> definitionRows) {
		this.parent = parent;
		this.definitionRows = definitionRows;
	}

	//#end region

	//#region ACCESSORS

	@Override
	public List<Row> getRows() {
		return definitionRows;
	}

	@Override
	public VariableDefinitionTable addEmptyRow() {
		VariableDefinitionRow emptyRow = new VariableDefinitionRow(parent, "", "", "");
		definitionRows.add(emptyRow);
		return this;
	}

	@Override
	public List<String> getHeaders() {
		if (headers == null) {
			headers = new ArrayList<String>(
					Arrays.asList("Name", "Definition", "Value", "Unit", "Type", "Description"));
		}
		return headers;
	}

	@Override
	public ColumnType getColumnType(String header) {
		if (columnTypes == null) {
			columnTypes = new HashMap<>();
			for (int index = 0; index < getHeaders().size(); index++) {
				columnTypes.put(headers.get(index), ColumnType.STRING);
			}
		}
		return columnTypes.get(header);
	}

	@Override
	public String getColumnHeaderTooltip(String header) {
		return "";
	}

	@Override
	public Boolean isEditable(String header) {
		if (editable == null) {
			editable = new HashMap<>();
			editable.put("Name", true);
			editable.put("Definition", true);
			editable.put("Value", false);
			editable.put("Unit", false);
			editable.put("Type", false);
			editable.put("Description", true);
		}
		return editable.get(header);
	}

	@Override
	public CellLabelProvider getLabelProvider(String header, ColumnType columnType) {
		CellLabelProvider labelProvider = new VariableDefinitionLabelProvider(header);
		return labelProvider;
	}

	@Override
	public CellEditor getCellEditor(String header, ColumnType columnType, Composite parent) {
		CellEditor cellEditor = CellEditorFactory.createCellEditor(columnType, parent);
		return cellEditor;
	}

	@Override
	public TreeNodeAdaption createTreeNodeAdaption() {
		return null; //dummy implementation: not used here
	}

	@Override
	public CodeAdaption createCodeAdaption(ScriptType scriptType) {
		return null; //dummy implementation: not used here
	}

	@Override
	public GraphicsAdaption createGraphicsAdaption(Composite parent) {
		return null; //dummy implementation: not used here
	}

	@Override
	public AbstractControlAdaption createControlAdaption(
			Composite parent,
			FocusChangingRefreshable treeViewRefreshable) {
		return null; //dummy implementation: not used here
	}

	@Override
	public Class<?> getColumnDataClass(String columnHeader) {
		return getColumnType(columnHeader).getAssociatedClass();
	}

	//#end region

}
