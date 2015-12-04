package org.treez.core.adaptable;

/**
 * The CadAdaption of an adaptable. This visual representation of the adaptable can be shown in a CAD view.
 */
public interface CadAdaption extends ControlAdaption {

	//#region METHODS

	/**
	 * Hides this CadAdaption on its parent composite
	 */
	void hide();

	/**
	 * Shows this CadAdaption on its parent composite.
	 */
	void show();

	//#end region

	//#region ACCESSORS	

	/**
	 * @return
	 */
	boolean isVisible();

	/**
	 * @return
	 */
	double getX();

	/**
	 * @return
	 */
	double getY();

	/**
	 * @return
	 */
	double getWidth();

	/**
	 * @return
	 */
	double getHeight();

	//end region

}
