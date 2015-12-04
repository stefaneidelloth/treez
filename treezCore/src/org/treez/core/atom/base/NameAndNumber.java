package org.treez.core.atom.base;

/**
 * Helper class for creating new atom child names
 */
public class NameAndNumber {

	//#region ATTRIBUTES

	private String name;

	private int number;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 * @param number
	 */
	public NameAndNumber(String name, int number) {
		this.name = name;
		this.number = number;
	}

	/**
	 * Copy Constructor
	 *
	 */
	public NameAndNumber(NameAndNumber nameAndNumberToCopy) {
		this.name = nameAndNumberToCopy.name;
		this.number = nameAndNumberToCopy.number;
	}

	//#end region

	//#region METHODS

	/**
	 * Copies this NameAndNumber
	 *
	 * @return
	 */
	public NameAndNumber copy() {
		NameAndNumber newNameAndNumber = new NameAndNumber(this);
		return newNameAndNumber;
	}

	/**
	 * Increases the number
	 */
	public void increaseNumber() {
		number += 1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		int nameCode = 0;
		if (name != null) {
			nameCode = name.hashCode();
		}
		result = prime * result + nameCode;
		result = prime * result + number;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		NameAndNumber other = (NameAndNumber) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (number != other.number) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getFullName();
	}

	//#end region

	//#region ACCESSORS

	/**
	 * Returns the name
	 *
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the full name (= name + number)
	 *
	 * @return
	 */
	public String getFullName() {
		return name + number;
	}

	//#end region

}
