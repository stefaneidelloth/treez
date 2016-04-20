package org.treez.core.adaptable;

/**
 * The CadAdaption of an adaptable. This visual representation of the adaptable
 * can be shown in a CAD view.
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

	boolean isVisible();

	double getX();

	double getY();

	double getWidth();

	double getHeight();

	//end region

}
