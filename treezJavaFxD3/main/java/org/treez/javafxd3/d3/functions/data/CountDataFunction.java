package org.treez.javafxd3.d3.functions.data;

import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.functions.DataFunction;

/**
 * A {@link DataFunction} that counts something, mainly used as a debugging purpose
 * in {@link Selection#each(DataFunction)} method.
 * <p>
 * You may override the  #takeIntoAccount(Element, Datum, int) to change when the count is incremented.
 * 
 * 
 * 
 */
public class CountDataFunction implements DataFunction<Void> {
	
	//#region ATTRIBUTES
	
	private int count = 0;

	//#end region	
	
	//#region CONSTRUCTORS
	
	public CountDataFunction(){}
	
	//#end region
	
	//#region METHODS
	
	@Override
	public Void apply(final Object context, final Object d, final int index) {
		if (takeIntoAccount(context, d, index)) {
			count++;
		}
		return null;
	}

	/**
	 * Return true by default.
	 * @return true to increment the count, false otherwise.
	 */
	private boolean takeIntoAccount(final Object context, final Object d, final int index) {
		return true;
	}


	public int getCount() {
		return count;
	}


	public CountDataFunction reset() {
		count = 0;
		return this;
	}
	
	//#end region
}