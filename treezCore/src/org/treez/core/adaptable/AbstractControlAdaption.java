package org.treez.core.adaptable;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Basic composite for all AtomControlAdaptions. This AbstractControlAdaption derives from Composite and therefore it
 * can be added to other Composites.
 */
public abstract class AbstractControlAdaption extends Composite implements ControlAdaption {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(AbstractControlAdaption.class);

	//#region ATTRIBUTES

	/**
	 * The adaptable that is adapted
	 */
	protected Adaptable adaptable;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param parent
	 * @param adaptable
	 */
	public AbstractControlAdaption(Composite parent, Adaptable adaptable) {
		super(parent, SWT.NULL);
		this.adaptable = adaptable;
	}

	//#end region

	//#region METHODS

	//#end region

	//#region ACCESSORS

	@Override
	public Adaptable getAdaptable() {
		return adaptable;
	}

	@Override
	public Composite getParentComposite() {
		return super.getParent();
	}

	//#end region

}
