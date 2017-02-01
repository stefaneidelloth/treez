package org.treez.data.table.nebula;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.EmptyControlAdaption;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.copy.CopyHelper;
import org.treez.core.data.cell.CellEditorFactory;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.table.AbstractTreezTable;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.AddChildAtomTreeViewerAction;
import org.treez.core.treeview.action.TreeViewerAction;
import org.treez.data.Activator;
import org.treez.data.cell.TreezTableNebulaLabelProvider;
import org.treez.data.column.Column;
import org.treez.data.column.Columns;

public class Table extends AbstractTreezTable<Table> {

	//#region ATTRIBUTES

	/**
	 * For columns that use enums as value, this maps from column name to EnumSet (=allowed values)
	 */
	private Map<String, List<String>> comboTextMap = null; //null;

	//#end region

	//#region CONSTRUCTORS

	public Table(String name) {
		super(name);
	}

	/**
	 * Copy Constructor
	 */
	private Table(Table tableToCopy) {
		super(tableToCopy);
		comboTextMap = CopyHelper.copyNestedStringMap(tableToCopy.comboTextMap);
	}

	//#end region

	//#region METHODS

	@Override
	public Table getThis() {
		return this;
	}

	@Override
	public Table copy() {
		return new Table(this);
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("table.png");
	}

	@Override
	public AbstractControlAdaption createControlAdaption(
			Composite parent,
			FocusChangingRefreshable treeViewRefreshable) {
		this.treeViewRefreshable = treeViewRefreshable;
		List<String> headers = null;
		try {
			headers = getHeaders();
		} catch (IllegalStateException exception) {
			//columns do not exist yet; EmptyControlAdaption will be created
		}
		boolean columnsExist = headers != null && headers.size() > 0;
		if (columnsExist) {

			//create table control
			TableControlAdaption tableControlAdaption = new TableControlAdaption(parent, this);

			if (rows != null) {
				//LOG.debug("Created new table control with " + rows.size() + " rows.");
			}
			return tableControlAdaption;
		} else {
			return new EmptyControlAdaption(parent, this, "This table does not contain any column yet.");
		}

	}

	@Override
	protected List<Object> createContextMenuActions(TreeViewerRefreshable treeViewer) {

		List<Object> actions = new ArrayList<>();

		Action addColumns = new AddChildAtomTreeViewerAction(
				Columns.class,
				"columns",
				Activator.getImage("columns.png"),
				this,
				treeViewer);
		actions.add(addColumns);

		actions.add(new TreeViewerAction(
				"Delete",
				org.treez.core.Activator.getImage("delete.png"),
				treeViewer,
				() -> Table.this.createTreeNodeAdaption().delete()));

		return actions;
	}

	@Override
	public CodeAdaption createCodeAdaption(ScriptType scriptType) {

		CodeAdaption codeAdaption;
		switch (scriptType) {
		case JAVA:
			codeAdaption = new TableCodeAdaption(this);
			break;
		default:
			String message = "The ScriptType " + scriptType + " is not yet implemented.";
			throw new IllegalStateException(message);
		}

		return codeAdaption;

	}

	public void addColumn(String header, ColumnType type) {
		initializeColumns();
		getColumns().createColumn(header, type);
	}

	/**
	 * Creates a Columns child if it does not yet exist
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private void initializeColumns() {

		try {
			getColumns();
		} catch (Exception exception) {
			//the child columns does not exist: create it
			Columns columns = new Columns("columns");
			this.addChild(columns);
		}
	}

	public void addColumn(String header, String type) {
		initializeColumns();
		getColumns().createColumn(header, type);
	}

	//#region CREATE CHILD ATOMS

	/**
	 * Creates a Columns child
	 */
	public Columns createColumns(String name) {
		Columns child = new Columns(name);
		addChild(child);
		return child;
	}

	//#end region

	//#end region

	//#region ACCESSORS

	@Override
	public List<String> getHeaders() {
		return getColumns().getHeaders();
	}

	public Columns getColumns() {
		try {
			Columns columns = getChildByClass(Columns.class);
			return columns;
		} catch (ClassCastException | IllegalArgumentException exception) {
			throw new IllegalStateException("Could not get columns of table '" + getName() + "';");
		}
	}

	public void addColumn(Column newColumn) {
		createColumnsIfNotExists();
		Columns columns = getColumns();
		columns.addChild(newColumn);
	}

	private void createColumnsIfNotExists() {
		boolean columnsExist = columnsExist();
		if (!columnsExist) {
			createColumns("columns");
		}

	}

	private boolean columnsExist() {
		for (AbstractAtom<?> currentChild : children) {
			boolean isWantedChild = currentChild.getClass().equals(Columns.class);
			if (isWantedChild) {
				return true;
			}
		}
		return false;

	}

	@Override
	public ColumnType getColumnType(String header) {
		ColumnType columnType = getColumns().getColumnType(header);
		return columnType;
	}

	@Override
	public String getColumnHeaderTooltip(String header) {
		return getColumns().getColumnHeaderTooltip(header);
	}

	@Override
	public CellLabelProvider getLabelProvider(String header, ColumnType columnType) {
		CellLabelProvider labelProvider = new TreezTableNebulaLabelProvider(header, columnType);
		return labelProvider;
	}

	@Override
	public CellEditor getCellEditor(String header, ColumnType columnType, Composite parent) {
		CellEditor cellEditor = CellEditorFactory.createCellEditor(columnType, parent);
		return cellEditor;
	}

	/**
	 * Get map from column name to allowed string values. This is used for combo text columns.
	 *
	 * @return the allowedValuesMap
	 */
	public Map<String, List<String>> getComboTextMap() {
		return comboTextMap;
	}

	@SuppressWarnings("checkstyle:illegalcatch")
	public boolean hasColumns() {
		try {
			Columns columns = getColumns();
			return columns.hasColumns();
		} catch (Exception exception) {
			return false;
		}
	}

	@SuppressWarnings("checkstyle:illegalcatch")
	public int getNumberOfColumns() {
		try {
			Columns columns = getColumns();
			return columns.getNumberOfColumns();
		} catch (Exception exception) {
			return 0;
		}
	}

	/**
	 * Returns true if the headers of the columns equal the given headers
	 *
	 * @param expectedHeaders
	 * @return
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	public boolean checkHeaders(List<String> expectedHeaders) {
		try {
			Columns columns = getColumns();
			return columns.checkHeaders(expectedHeaders);
		} catch (Exception exception) {
			return false;
		}
	}

	//#end region

}
