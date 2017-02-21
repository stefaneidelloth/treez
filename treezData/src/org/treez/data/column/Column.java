package org.treez.data.column;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.treez.core.Activator;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.ColumnTypeComboBox;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.row.Row;
import org.treez.core.data.table.TreezTable;
import org.treez.core.treeview.TreeViewerRefreshable;

/**
 * Represents a table
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Column extends AdjustableAtom {

	private static final Logger LOG = Logger.getLogger(Column.class);

	//#region ATTRIBUTES

	public final Attribute<String> header = new Wrap<>();

	public final Attribute<String> legend = new Wrap<>();

	public final Attribute<String> columnType = new Wrap<>();

	public final Attribute<Boolean> isNullable = new Wrap<>();

	public final Attribute<Boolean> isPrimaryKey = new Wrap<>();

	public final Attribute<String> defaultValueString = new Wrap<>();

	/**
	 * The enum values that are allowed for the column
	 */
	@SuppressWarnings("unused")
	public final Attribute<String> enumValues = new Wrap<>();

	//#end region

	//#region CONSTRUCTORS

	public Column(String name) {
		super(name);
		LOG.debug("creating column " + name);
		createColumnAtomModel();
	}

	public Column(String name, ColumnType type) {
		super(name);
		createColumnAtomModel();
		Wrap<String> columnTypeWrap = (Wrap<String>) columnType;
		ColumnTypeComboBox combo = (ColumnTypeComboBox) columnTypeWrap.getAttribute();
		combo.set(type);

	}

	public Column(String name, ColumnType type, String description) {
		super(name);
		createColumnAtomModel();
		Wrap<String> wrap = (Wrap<String>) columnType;
		Attribute<String> attribute = wrap.getAttribute();
		ColumnTypeComboBox combo = (ColumnTypeComboBox) attribute;
		combo.set(type);
		this.legend.set(description);
	}

	public Column(String name, String columnType) {
		super(name);
		createColumnAtomModel();
		this.columnType.set(columnType);

	}

	/**
	 * Copy constructor
	 */
	private Column(Column columnToCopy) {
		super(columnToCopy);
		createColumnAtomModel();
		columnType.set(columnToCopy.columnType.get());

	}

	//#end region

	//#region METHODS

	@Override
	public Column getThis() {
		return this;
	}

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

		section.createTextField(header, this, name);

		section.createTextField(legend, this, "");

		section
				.createColumnTypeComboBox(columnType, this, ColumnType.TEXT) //
				.setLabel("Type");

		section
				.createCheckBox(isNullable, this, true) //
				.setLabel("Nullable");

		section
				.createCheckBox(isPrimaryKey, this, false) //
				.setLabel("Primary key");

		section
				.createTextField(defaultValueString, this) //
				.setLabel("Default value");

		section
				.createTextField(enumValues, this, "item1,item2") //
				.setEnabled(false);

		setModel(root);

	}

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

	/**
	 * @return
	 */
	public <T> List<T> makeList() {
		return new ArrayList<T>();
	}

	//#end region

	//#region ACCESSORS

	/**
	 * Returns the values this column contains in the table
	 *
	 * @return
	 */
	public List<Object> getValues() {

		//create empty value list
		List<Object> valueList = new ArrayList<>();

		//get all rows of the table
		TreezTable table = getTable();
		List<Row> rows = table.getRows();

		//iterate over the rows to fill the value list with all data of this column
		String columnHeader = this.header.get();
		for (Row row : rows) {
			Object entry = row.getEntry(columnHeader);
			valueList.add(entry);
		}

		return valueList;

	}

	public List<Double> getDoubleValues() {
		List<Object> valueObjects = getValues();
		switch (getColumnType()) {
		case BOOLEAN:
			return getDoubleValuesFromBooleans(valueObjects);
		case COLOR:
			return getDoubleValuesFromColors(valueObjects);
		case DOUBLE:
			return getDoubleValuesFromNumbers(valueObjects);
		case ENUM:
			return getDoubleValuesFromEnums(valueObjects);
		case INTEGER:
			return getDoubleValuesFromNumbers(valueObjects);
		case TEXT:
			return getDoubleValuesFromStrings(valueObjects);
		default:
			String message = "Unknown column type " + getColumnType();
			throw new IllegalStateException(message);
		}
	}

	private static List<Double> getDoubleValuesFromBooleans(List<Object> valueObjects) {
		List<Double> values = valueObjects.stream().map(element -> {
			Boolean bool = (Boolean) element;
			if (bool) {
				return 1.0;
			} else {
				return 0.0;
			}
		}).collect(Collectors.toList());
		return values;
	}

	private static List<Double> getDoubleValuesFromColors(List<Object> valueObjects) {

		List<Double> values = valueObjects.stream().map(element -> {
			Color color = (Color) element;
			int rgb = color.getRGB();
			return new Double(rgb);
		}).collect(Collectors.toList());
		return values;
	}

	private static List<Double> getDoubleValuesFromNumbers(List<Object> valueObjects) {
		List<Double> values = valueObjects.stream().map(element -> {
			Number number = (Number) element;
			return number.doubleValue();
		}).collect(Collectors.toList());
		return values;
	}

	private static List<Double> getDoubleValuesFromEnums(List<Object> valueObjects) {
		List<Double> values = valueObjects.stream().map(element -> {
			Enum<?> enumeration = (Enum<?>) element;
			int ordinal = enumeration.ordinal();
			return new Double(ordinal);
		}).collect(Collectors.toList());
		return values;
	}

	private static List<Double> getDoubleValuesFromStrings(List<Object> valueObjects) {
		List<Double> values = valueObjects.stream().map(element -> {
			String stringValue = (String) element;
			return Double.parseDouble(stringValue);
		}).collect(Collectors.toList());
		return values;
	}

	/**
	 * Returns the table this column belongs to. If the table cannot be found an exception is thrown.
	 *
	 * @return
	 */
	private TreezTable getTable() {
		try {
			Columns columns = (Columns) this.createTreeNodeAdaption().getParent().getAdaptable();
			try {
				TreezTable table = (TreezTable) columns.createTreeNodeAdaption().getParent().getAdaptable();
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

	public ColumnType getColumnType() {
		ColumnType columnTypeEnumValue = ColumnType.getType(columnType.get());
		return columnTypeEnumValue;
	}

	//#end region

}
