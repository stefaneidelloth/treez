package org.treez.core.data.column;

public class ColumnBlueprint {

	//#region ATTRIBUTES

	private String name;
	private ColumnType type;
	private String description;

	//#end region

	//#region CONSTRUCTORS

	public ColumnBlueprint(String name, ColumnType type, String legend) {
		this.setName(name);
		this.setType(type);
		this.setDescription(legend);
	}

	//#end region

	//#region ACCESSORS

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ColumnType getType() {
		return type;
	}

	public void setType(ColumnType type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String legend) {
		this.description = legend;
	}

	//#end region

}
