package org.treez.data.column;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.ColumnTypeComboBox;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.row.Row;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.data.Activator;
import org.treez.data.table.Table;

/**
 * Represents a table
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Column extends AdjustableAtom {

	// #region ATTRIBUTES

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(Column.class);

	/**
	 * Header
	 */
	public final Attribute<String> header = new Wrap<>();

	/**
	 * Description
	 */
	public final Attribute<String> description = new Wrap<>();

	/**
	 * The column type
	 */
	public final Attribute<String> columnType = new Wrap<>();

	/**
	 * The enum values that are allowed for the column
	 */
	@SuppressWarnings("unused")
	public final Attribute<String> enumValues = new Wrap<>();

	// #end region

	// #region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public Column(String name) {
		super(name);
		sysLog.debug("creating column " + name);
		createColumnAtomModel();
	}

	/**
	 * Constructor
	 *
	 * @param name
	 * @param type
	 */
	public Column(String name, ColumnType type) {
		super(name);
		createColumnAtomModel();
		Wrap<String> columnTypeWrap = (Wrap<String>) columnType;
		ColumnTypeComboBox combo = (ColumnTypeComboBox) columnTypeWrap.getAttribute();
		combo.set(type);

	}

	/**
	 * Constructor
	 *
	 * @param name
	 * @param type
	 */
	public Column(String name, ColumnType type, String description) {
		super(name);
		createColumnAtomModel();
		Wrap<String> wrap = (Wrap<String>) columnType;
		Attribute<String> attribute = wrap.getAttribute();
		ColumnTypeComboBox combo = (ColumnTypeComboBox) attribute;
		combo.set(type);
		this.description.set(description);
	}

	/**
	 * Constructor
	 *
	 * @param name
	 * @param columnType
	 */
	public Column(String name, String columnType) {
		super(name);
		createColumnAtomModel();
		this.columnType.set(columnType);

	}

	/**
	 * Copy constructor
	 *
	 * @param name
	 * @param columnType
	 */
	private Column(Column columnToCopy) {
		super(columnToCopy);
		createColumnAtomModel();
		columnType.set(columnToCopy.columnType.get());

	}

	// #end region

	// #region METHODS

	@Override
	public Column copy() {
		return new Column(this);
	}

	/**
	 * Creates the model for this adjustable atom
	 */
	private void createColumnAtomModel() {
		// create and set model
		AttributeRoot root = new AttributeRoot("root");
		Page page = root.createPage("page");

		Section section = page.createSection("section");

		section.createTextField(header, "header", name);

		section.createTextField(description, "description", "");

		section.createColumnTypeComboBox(columnType, "columnType", "Type", ColumnType.TEXT);

		section.createTextField(enumValues, "enumValues", "item1,item2");

		setModel(root);

	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("column.png");
	}

	/**
	 * Creates the context menu actions for this atom
	 */
	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {

		return actions;
	}

	//#region VALUES

	/**
	 * Returns the values this column contains in the table
	 *
	 * @return
	 */
	public List<Object> getValues() {

		//create empty value list
		List<Object> valueList = new ArrayList<>();

		//get all rows of the table
		Table table = getTable();
		List<Row> rows = table.getRows();

		//iterate over the rows to fill the value list with all data of this column
		String columnHeader = this.header.get();
		for (Row row : rows) {
			Object entry = row.getEntry(columnHeader);
			valueList.add(entry);
		}

		return valueList;

	}

	/**
	 * Returns the table this column belongs to. If the table cannot be found an exception is thrown.
	 *
	 * @return
	 */
	private Table getTable() {
		try {
			Columns columns = (Columns) this.createTreeNodeAdaption().getParent().getAdaptable();
			try {
				Table table = (Table) columns.createTreeNodeAdaption().getParent().getAdaptable();
				return table;
			} catch (ClassCastException e) {
				throw new IllegalStateException(
						"The parent of the Columns '" + columns.getName() + "' is not a Table atom. ");
			}
		} catch (ClassCastException e) {
			throw new IllegalStateException(
					"The parent of the Column '" + this.getName() + "' is not a Columns atom. ");
		}
	}

	//#end region

	/**
	 * @return
	 */
	public <T> List<T> makeList() {
		return new ArrayList<T>();
	}

	//#end region

}
