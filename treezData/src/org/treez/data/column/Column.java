package org.treez.data.column;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.graphics.Image;
import org.treez.core.Activator;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.attribute.attributeContainer.Page;
import org.treez.core.atom.attribute.attributeContainer.section.Section;
import org.treez.core.atom.attribute.checkBox.CheckBox;
import org.treez.core.atom.attribute.comboBox.enumeration.EnumComboBox;
import org.treez.core.atom.attribute.text.TextField;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.row.Row;
import org.treez.core.data.table.LinkableTreezTable;
import org.treez.core.data.table.TreezTable;
import org.treez.core.treeview.TreeViewerRefreshable;

/**
 * Represents a table
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Column extends AdjustableAtom {

	//#region ATTRIBUTES

	public final Attribute<String> header = new Wrap<>();

	public final Attribute<String> legend = new Wrap<>();

	public final Attribute<ColumnType> columnType = new Wrap<>();

	public final Attribute<Boolean> isNullable = new Wrap<>();

	public final Attribute<Boolean> isPrimaryKey = new Wrap<>();

	public final Attribute<String> defaultValueString = new Wrap<>();

	private boolean isVirtual = false;

	private boolean isLinkedToSource = false;

	private TextField headerField;

	private EnumComboBox<ColumnType> typeCombo;

	private CheckBox isNullableCheckBox;

	private CheckBox isPrimaryCheckBox;

	private TextField defaultValueField;

	private TextField labelField;

	//#end region

	//#region CONSTRUCTORS

	public Column(String name) {
		super(name);
		createColumnAtomModel();

	}

	public Column(String name, ColumnType type) {
		this(name);
		columnType.set(type);
	}

	public Column(String name, ColumnType type, String description, boolean isLinkedToSource, boolean isVirtual) {
		super(name);
		this.isLinkedToSource = isLinkedToSource;
		this.isVirtual = isVirtual;
		createColumnAtomModel();
		columnType.set(type);
		this.legend.set(description);
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

		headerField = section.createTextField(header, this, name);

		typeCombo = section
				.createEnumComboBox(columnType, this, ColumnType.STRING) //
				.setLabel("Type");

		isNullableCheckBox = section
				.createCheckBox(isNullable, this, true) //
				.setLabel("Nullable");

		isPrimaryCheckBox = section
				.createCheckBox(isPrimaryKey, this, false) //
				.setLabel("Primary key");

		defaultValueField = section
				.createTextField(defaultValueString, this) //
				.setLabel("Default value");

		labelField = section.createTextField(legend, this, "");

		setModel(root);

	}

	@Override
	protected void afterCreateControlAdaptionHook() {
		if (isLinkedToSource) {
			disableAttributes();
		}

		if (isVirtual) {
			isPrimaryCheckBox.setVisible(false);
			defaultValueField.setVisible(false);
		} else {
			isPrimaryCheckBox.setVisible(true);
			defaultValueField.setVisible(true);
		}
	}

	private void disableAttributes() {

		headerField.setEnabled(false);
		typeCombo.setEnabled(false);
		isNullableCheckBox.setEnabled(false);
		if (!isVirtual) {
			isPrimaryCheckBox.setEnabled(false);
			defaultValueField.setEnabled(false);
		}
		labelField.setEnabled(false);

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
		case INTEGER:
			return getDoubleValuesFromNumbers(valueObjects);
		case DOUBLE:
			return getDoubleValuesFromNumbers(valueObjects);
		case STRING:
			return getDoubleValuesFromStrings(valueObjects);
		default:
			String message = "Unknown column type " + getColumnType();
			throw new IllegalStateException(message);
		}
	}

	private static List<Double> getDoubleValuesFromNumbers(List<Object> valueObjects) {
		List<Double> values = valueObjects.stream().map(element -> {
			Number number = (Number) element;
			return number.doubleValue();
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

	public List<String> getStringValues() {
		List<Object> valueObjects = getValues();
		return getStringValues(valueObjects);
	}

	private static List<String> getStringValues(List<Object> valueObjects) {
		List<String> values = valueObjects.stream().map(element -> {
			return element.toString();
		}).collect(Collectors.toList());
		return values;
	}

	/**
	 * Returns the table this column belongs to. If the table cannot be found an exception is thrown.
	 *
	 * @return
	 */
	private LinkableTreezTable getTable() {
		try {
			Columns columns = (Columns) this.createTreeNodeAdaption().getParent().getAdaptable();
			try {
				LinkableTreezTable table = (LinkableTreezTable) columns
						.createTreeNodeAdaption()
						.getParent()
						.getAdaptable();
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
		return columnType.get();
	}

	public void setColumnType(ColumnType columnType) {
		this.columnType.set(columnType);
	}

	public boolean isNumeric() {
		return getColumnType().isNumeric();
	}

	//#end region

}
