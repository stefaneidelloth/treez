package org.treez.core.data.column;

public class ColumnBlueprint {

	//#region ATTRIBUTES

	private String name;

	private ColumnType type;

	private boolean isNullable;

	private boolean isPrimaryKey;

	private Object defaultValue;

	private String legend;

	private boolean isVirtual;

	private boolean isLinkedToSource = false;

	//#end region

	//#region CONSTRUCTORS

	public ColumnBlueprint(String name, ColumnType columnType, String legend) {
		this(name, columnType, true, false, null, legend, false);
	}

	public ColumnBlueprint(
			String name,
			ColumnType columnType,
			boolean isNullable,
			boolean isPrimaryKey,
			Object defaultValue,
			String legend,
			boolean isLinkedToSource) {
		this.name = name;
		this.type = columnType;
		this.isNullable = isNullable;
		this.isPrimaryKey = isPrimaryKey;
		this.defaultValue = defaultValue;
		this.legend = legend;
		this.isLinkedToSource = isLinkedToSource;

	}

	public ColumnBlueprint(
			String name,
			ColumnType columnType,
			boolean isNullable,
			String legend,
			boolean isLinkedToSource) {
		this.name = name;
		this.type = columnType;
		this.isNullable = isNullable;
		this.legend = legend;
		this.isLinkedToSource = isLinkedToSource;
		this.isVirtual = true;

	}

	//#end region

	//#region ACCESSORS

	public String getName() {
		return name;
	}

	public ColumnType getType() {
		return type;
	}

	public boolean isVirtual() {
		return isVirtual;
	}

	public boolean isNullable() {
		return isNullable;
	}

	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public String getLegend() {
		return legend;
	}

	public boolean isLinkedToSource() {
		return isLinkedToSource;
	}

	//#end region

}
