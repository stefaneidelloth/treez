package org.treez.core.atom.uisynchronizing;

/**
 * This class serves as a wrapping container for results from Runnables. This is
 * required because the values that are modified by the Runnable have to be
 * final.
 */
public class ResultWrapper<T> {

	//#region ATTRIBUTES

	private T value;

	//#end region

	//#region CONSTRUCTORS

	public ResultWrapper(T value) {
		this.value = value;
	}

	//#end region

	//#region ACCESSORS

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	//#end region

}
