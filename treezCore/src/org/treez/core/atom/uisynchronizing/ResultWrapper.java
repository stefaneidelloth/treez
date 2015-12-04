package org.treez.core.atom.uisynchronizing;

/**
 * This class serves as a wrapping container for results from Runnables. This is required because the values that are
 * modified by the Runnable have to be final.
 *
 * @param <T>
 */
public class ResultWrapper<T> {

	//#region ATTRIBUTES

	private T value;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param value
	 */
	public ResultWrapper(T value) {
		this.value = value;
	}

	//#end region

	//#region ACCESSORS

	/**
	 * @return
	 */
	public T getValue() {
		return value;
	}

	/**
	 * @param value
	 */
	public void setValue(T value) {
		this.value = value;
	}

	//#end region

}
