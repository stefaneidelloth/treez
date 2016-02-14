package org.treez.results.atom.axis;

import org.treez.javafxd3.d3.core.Selection;

/**
 * Container for two selections
 */
public class PrimaryAndSecondarySelection {

	//#region ATTRIBUTES

	private Selection primary;

	private Selection secondary;

	//#end region

	//#region CONSTRUCTORS

	PrimaryAndSecondarySelection() {

	}

	//#end region

	//#region METHODS

	//#end region

	//#region ACCESSORS

	/**
	 * @return
	 */
	public Selection getPrimary() {
		return primary;
	}

	/**
	 * @param primary
	 */
	public void setPrimary(Selection primary) {
		this.primary = primary;
	}

	/**
	 * @return
	 */
	public Selection getSecondary() {
		return secondary;
	}

	/**
	 * @param secondary
	 */
	public void setSecondary(Selection secondary) {
		this.secondary = secondary;
	}

	//#end region

}
