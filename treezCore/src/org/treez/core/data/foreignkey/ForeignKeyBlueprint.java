package org.treez.core.data.foreignkey;

import org.treez.core.data.column.ColumnType;

public class ForeignKeyBlueprint {

	//#region ATTRIBUTES

	private String name;

	private ColumnType type;

	private boolean isNullable;

	private boolean isPrimaryKey;

	private Object defaultValue;

	private String legend;

	//#end region

	//#region CONSTRUCTORS

	public ForeignKeyBlueprint(String name, ColumnType columnType, String legend) {
		this(name, columnType, true, false, null, legend);
	}

	public ForeignKeyBlueprint(
			String name,
			ColumnType columnType,
			boolean isNullable,
			boolean isPrimaryKey,
			Object defaultValue,
			String legend) {
		this.name = name;
		this.type = columnType;
		this.isNullable = isNullable;
		this.isPrimaryKey = isPrimaryKey;
		this.defaultValue = defaultValue;
		this.legend = legend;
	}

	//#end region

	//#region ACCESSORS

	public String getName() {
		return name;
	}

	public ColumnType getType() {
		return type;
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

	//#end region

}
