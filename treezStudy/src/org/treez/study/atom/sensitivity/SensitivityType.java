package org.treez.study.atom.sensitivity;

public enum SensitivityType {

	//#region VALUES

	/**
	 * Specify variation using relative distance of neighboring points to working point {p}: specify "how far" the other
	 * points are away, e.g. the distance is +-10 % of the absolute working point value => (p-p*0.1), {p}, (p+p*0.1)
	 */
	RELATIVE_DISTANCE, //

	/**
	 * Specify variation using positions of neighboring points that are given in relation to the working point value,
	 * e.g. with factors 0.1, 1.1 => (0.1*p), {p}, (1.1*p)
	 */
	RELATIVE_POSITION, //

	/**
	 * Specify variation using distances of neighboring points to the working point {p} : specify "how far" the other
	 * points are away, e.g.the absolute distance is 1 => (p-1), {p}, (p+1)
	 */
	ABSOLUTE_DISTANCE; //

	/**
	 * Specify variation using absolute positions of neighboring points e.g. 5, 20 for a working point value of {p=10}
	 * => 5, {p=10}, 20
	 */
	//ABSOLUTE_POSITION; //this is represented by the CustomSensitivity and not relevant for the generalized Sensitivity

	//#end region

	//#region METHODS

	public boolean isRelative() {
		return this.equals(RELATIVE_DISTANCE) || this.equals(RELATIVE_POSITION);
	}

	//#end region

}
