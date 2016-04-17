package org.treez.core.data.column;

/**
 * Serves as blueprint for a Column
 */
public class ColumnBlueprint {

	//#region ATTRIBUTES

	private String name;
	private ColumnType type;
	private String description;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 * @param type
	 * @param legend
	 */
	public ColumnBlueprint(String name, ColumnType type, String legend) {
		this.setName(name);
		this.setType(type);
		this.setDescription(legend);
	}

	//#end region

	//#region ACCESSORS

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return
	 */
	public ColumnType getType() {
		return type;
	}

	/**
	 * @param type
	 */
	public void setType(ColumnType type) {
		this.type = type;
	}

	/**
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param legend
	 */
	public void setDescription(String legend) {
		this.description = legend;
	}

	//#end region

}
