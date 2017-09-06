package org.treez.javafxd3.d3.functions.data.wrapper;

import org.treez.javafxd3.d3.functions.DataFunction;

public class VoidDataFunctionWrapper implements DataFunction<Void> {

	//#region ATTRIBUTES

	private Runnable runnable = null;

	//#end region

	//#region CONSTRUCTORS
	
	public VoidDataFunctionWrapper(Runnable runnable) {
		this.runnable = runnable;
	}

	//#end region

	//#region METHODS

	@Override
	public Void apply(Object context, Object datum, int index) {
		runnable.run();
		return null;
	}

	//#end region

}
