package org.treez.core.data.foreignkey;

public class ForeignKeyBlueprint {

	//#region ATTRIBUTES

	private String name;

	private String fields;

	private String referencedTable;

	private String referencedFields;

	private String onDelete;

	private String onUpdate;

	//#end region

	//#region CONSTRUCTORS

	public ForeignKeyBlueprint(
			String name,
			String fields,
			String referencedTable,
			String referencedFields,
			String onDelete,
			String onUpdate) {
		this.name = name;
		this.fields = fields;
		this.referencedTable = referencedTable;
		this.referencedFields = referencedFields;
		this.onDelete = onDelete;
		this.onUpdate = onUpdate;
	}

	//#end region

	//#region ACCESSORS

	public String getName() {
		return name;
	}

	public String getFields() {
		return fields;
	}

	public String getReferencedTable() {
		return referencedTable;
	}

	public String getReferencedFields() {
		return referencedFields;
	}

	public String getOnDelete() {
		return onDelete;
	}

	public String getOnUpdate() {
		return onUpdate;
	}

	//#end region

}
