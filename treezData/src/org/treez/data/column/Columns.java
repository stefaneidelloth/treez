package org.treez.data.column;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.Activator;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.attribute.ColumnTypeComboBox;
import org.treez.core.atom.attribute.base.EmptyControlAdaption;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.data.column.ColumnBlueprint;
import org.treez.core.data.column.ColumnType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.AddChildAtomTreeViewerAction;

/**
 * Represents all columns of a table. This will be a child of a table and the individual columns will be children of
 * this atom. (In addition to the columns the table might have other attributes. The tree structure is then similar to
 * the tree structure that can be found in relational data bases. In addition to the columns child there might be
 * foreign key child ect.)
 */
public class Columns extends AdjustableAtom {

	private static final Logger LOG = Logger.getLogger(Columns.class);

	//#region CONSTRUCTORS

	public Columns(String name) {
		super(name);
		createEmptyModel();
	}

	/**
	 * Copy Constructor
	 */
	private Columns(Columns columnsToCopy) {
		super(columnsToCopy);
		createEmptyModel();
	}

	//#end region

	//#region METHODS

	@Override
	public Columns getThis() {
		return this;
	}

	@Override
	public Columns copy() {
		return new Columns(this);
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("columns.png");
	}

	/**
	 * Provides a control to represent this atom
	 */
	@Override
	public AbstractControlAdaption createControlAdaption(
			Composite parent,
			FocusChangingRefreshable treeViewRefreshable) {
		this.treeViewRefreshable = treeViewRefreshable;
		return new EmptyControlAdaption(parent, this, "This atom represents all columns of its parent table.");
	}

	/**
	 * Creates the context menu actions for this atom
	 */
	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {

		Action addColumn = new AddChildAtomTreeViewerAction(
				Column.class,
				"column",
				Activator.getImage("column.png"),
				this,
				treeViewer);
		actions.add(addColumn);

		return actions;
	}

	//#region HEADERS

	/**
	 * Returns the column headers as string list
	 *
	 * @return
	 */
	public List<String> getHeaders() {
		List<String> headers = new ArrayList<>();
		for (TreeNodeAdaption childNode : createTreeNodeAdaption().getChildren()) {
			Adaptable child = childNode.getAdaptable();

			String type = child.getClass().getSimpleName();

			if (type.equals("Column")) {

				Column column = (Column) child;

				String columnHeader = column.header.get();

				if (columnHeader != null) {
					headers.add(columnHeader);
				} else {
					throw new IllegalStateException(
							"Could not read header for column '" + column.getName() + "'. The value is null.");
				}

			} else {
				throw new IllegalStateException("The type of the child has to be Column but is '" + type + "'.");

			}

		}
		return headers;
	}

	/**
	 * Returns true if the headers of the columns equal the given headers
	 *
	 * @param expectedHeaders
	 * @return
	 */
	public boolean checkHeaders(List<String> expectedHeaders) {
		List<String> existingHeader = this.getHeaders();
		boolean hasSameLength = (expectedHeaders.size() == existingHeader.size());
		if (!hasSameLength) {
			String message = "The given number of columns is " + expectedHeaders.size()
					+ " and the expected numboer of columns is " + existingHeader.size() + ".";
			LOG.warn(message);
			return false;
		}

		for (int index = 0; index < expectedHeaders.size(); index++) {
			boolean headerIsEqual = expectedHeaders.get(index).equals(existingHeader.get(index));
			if (!headerIsEqual) {
				return false;
			}
		}
		return true;

	}

	//#end region

	//#region COLUMN TYPE

	/**
	 * Returns the ColumnType for the column with the given header
	 *
	 * @param header
	 * @return
	 */
	public ColumnType getColumnType(String header) {
		Attribute<String> currentColumnType = getColumn(header).columnType;
		Wrap<String> columnTypeWrapper = (Wrap<String>) currentColumnType;
		ColumnTypeComboBox combo = (ColumnTypeComboBox) columnTypeWrapper.getAttribute();
		ColumnType columnType = combo.getType();
		return columnType;
	}

	//#end region

	//#region COLUMN HEADER TOOL TIP

	/**
	 * Returns the column header tool tip for the column with the given header
	 *
	 * @param header
	 * @return
	 */
	public String getColumnHeaderTooltip(String header) {
		return getColumn(header).legend.get();
	}

	//#end region

	//#region COLUMN

	/**
	 * Returns the Column with the given column header
	 *
	 * @param header
	 * @return
	 */
	public Column getColumn(String header) {
		try {
			Column column = (Column) getChild(header);
			return column;
		} catch (ClassCastException exception) {
			throw new IllegalStateException("Could not get column '" + header + "'.");
		}
	}

	/**
	 * Returns the column with the given index
	 *
	 * @param columnIndex
	 * @return
	 */
	public Column getColumnByIndex(int columnIndex) {
		List<AbstractAtom<?>> children = this.getChildAtoms();
		if (children.size() > columnIndex) {
			Column column = (Column) children.get(columnIndex);
			return column;
		} else {
			String message = "The table only has " + children.size() + " columns and the index " + columnIndex
					+ " is invalid.";
			throw new IllegalArgumentException(message);
		}

	}

	/**
	 * Returns true if at least one column exists
	 *
	 * @return
	 */
	public boolean hasColumns() {
		for (TreeNodeAdaption childNode : this.createTreeNodeAdaption().getChildren()) {
			boolean isColumn = childNode.getAdaptable().getClass().getSimpleName().equals("Column");
			if (isColumn) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the number of existing columns
	 *
	 * @return
	 */
	public int getNumberOfColumns() {
		int numberOfColumns = 0;
		for (TreeNodeAdaption childNode : this.createTreeNodeAdaption().getChildren()) {
			boolean isColumn = childNode.getAdaptable().getClass().getSimpleName().equals("Column");
			if (isColumn) {
				numberOfColumns++;
			}
		}
		return numberOfColumns;
	}

	//#end region

	//#region CREATE CHILD ATOMS

	/**
	 * Creates a new column with given header
	 *
	 * @param header
	 */
	public Column createColumn(String header) {
		Column column = new Column(header);
		addChild(column);
		return column;

	}

	/**
	 * Creates a new column with given header and type
	 *
	 * @param header
	 * @param type
	 */
	public Column createColumn(String header, String type) {
		Column column = new Column(header, type);
		addChild(column);
		return column;

	}

	/**
	 * Creates a new column with given header, type and description
	 *
	 * @param header
	 * @param type
	 */
	public Column createColumn(String header, ColumnType type, String description) {
		Column column = new Column(header, type, description);
		addChild(column);
		return column;

	}

	/**
	 * Creates a new column with given ColumnBlueprint
	 *
	 * @param columnBlueprint
	 */
	public Column createColumn(ColumnBlueprint columnBlueprint) {
		String columnHeader = columnBlueprint.getName();
		ColumnType columnType = columnBlueprint.getType();
		String legendText = columnBlueprint.getLegend();
		Column column = createColumn(columnHeader, columnType, legendText);
		column.isNullable.set(columnBlueprint.isNullable());
		column.isPrimaryKey.set(columnBlueprint.isPrimaryKey());

		Object defaultValue = columnBlueprint.getDefaultValue();
		if (defaultValue == null) {
			column.defaultValueString.set("null");
		} else {
			column.defaultValueString.set(defaultValue.toString());
		}

		return column;
	}

	/**
	 * Creates a new column with given header and type
	 *
	 * @param header
	 * @param type
	 */
	public Column createColumn(String header, ColumnType type) {
		Column column = new Column(header, type);
		addChild(column);
		return column;
	}

	//#end region

	//#end region

}
