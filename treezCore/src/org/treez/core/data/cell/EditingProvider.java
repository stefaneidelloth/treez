package org.treez.core.data.cell;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.treez.core.data.row.Row;
import org.treez.core.data.table.TreezTable;

/**
 * Provides the cell editors and delegates other work to class Row
 */
public class EditingProvider extends EditingSupport {

	//#region ATTRIBUTES

	private ColumnViewer tableViewer;

	private TreezTable table;

	private String header;

	private CellEditor cellEditor;

	//#end region

	//#region CONSTRUCTORS

	public EditingProvider(ColumnViewer tableViewer, TreezTable table,
			String header, CellEditor cellEditor) {
		super(tableViewer);
		this.tableViewer = tableViewer;
		this.table = table;
		this.header = header;
		this.cellEditor = cellEditor;
	}

	//#end region

	//#region METHODS

	@Override
	protected boolean canEdit(Object element) {
		return table.isEditable(header);
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return cellEditor;
	}

	@Override
	protected Object getValue(Object element) {
		Row row = (Row) element;
		Object value = row.getObject(header, cellEditor);
		return value;
	}

	@Override
	protected void setValue(Object element, Object input) {
		Row row = (Row) element;
		row.setEntry(header, input);
		tableViewer.refresh();
	}

	//#end region
}
