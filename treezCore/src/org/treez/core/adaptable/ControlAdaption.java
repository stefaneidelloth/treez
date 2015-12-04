package org.treez.core.adaptable;

import org.eclipse.swt.widgets.Composite;

/**
 * The ControlAdaption of an adaptable. It is able to return a parent Composite
 * on which is it shown. (It can not be directly added to another Composite. Use
 * for example the AbstractControlAdaption or another implementing class for
 * that purpose.)
 */
public interface ControlAdaption extends Adaption {

	//#region ACCESSORS

	/**
	 * Returns the Composite on which this ControlAdaption is shown. (This might
	 * be a general Composite or for example the GraphicsAdaption of the parent
	 * adaptable). Returns null if no parent Composite exists.
	 *
	 * @return
	 */
	Composite getParentComposite();

	//end region

}
