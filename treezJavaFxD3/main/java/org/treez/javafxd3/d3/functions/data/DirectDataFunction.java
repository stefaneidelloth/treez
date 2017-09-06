package org.treez.javafxd3.d3.functions.data;

import org.treez.javafxd3.d3.functions.DataFunction;

public class DirectDataFunction<T> implements DataFunction<T> {

	
	//#region CONSTRUCTORS

	public DirectDataFunction() {}

	//#end region

	//#region METHODS

	@Override
	public T apply(Object context, Object datum, int index) {
		return (T) datum;
	}

	//#end region

}
