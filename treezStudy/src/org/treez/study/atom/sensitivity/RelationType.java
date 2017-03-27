package org.treez.study.atom.sensitivity;

public enum RelationType {

	/**
	 * Specify values indirectly, in terms of percentages in relation to the working point value, e.g. a percentage of
	 * "10" with a working point value of 1000 gives a final value of 1000 * 10/100= 100.
	 */
	PERCENTAGE, //

	/**
	 * Specify values indirectly, in terms of factors in relation to the working point value, e.g. a factor of "0.1"
	 * with a working point value of 1000 gives a final value of 0.1*1000 = 100
	 */
	FACTOR,

	/**
	 * Specify values indirectly, in terms of a change in exponent (magnitude, order), e.g. an exponent of "2" with a
	 * working point value of 100 gives a final value of 10^2 * 1000 = 100.000
	 */
	EXPONENT;

}
