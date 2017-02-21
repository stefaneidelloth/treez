package org.treez.core.data.index;

public class IndexBlueprint {

	//#region ATTRIBUTES

	private String name;

	private String fields;

	private boolean isUnique;

	//#end region

	//#region CONSTRUCTORS

	public IndexBlueprint(String name, String fields, boolean isUnique) {
		this.name = name;
		this.fields = fields;
		this.isUnique = isUnique;
	}

	//#end region

	//#region ACCESSORS

	public String getName() {
		return name;
	}

	public String getFields() {
		return fields;
	}

	public boolean isUnique() {
		return isUnique;
	}

	//#end region

}
