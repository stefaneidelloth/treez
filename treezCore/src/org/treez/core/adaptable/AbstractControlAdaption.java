package org.treez.core.adaptable;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Basic composite for all AtomControlAdaptions. This AbstractControlAdaption derives from Composite and therefore it
 * can be added to other Composites.
 */
public abstract class AbstractControlAdaption extends Composite implements ControlAdaption {

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(AbstractControlAdaption.class);

	//#region ATTRIBUTES

	protected Adaptable adaptable;

	//#end region

	//#region CONSTRUCTORS

	public AbstractControlAdaption(Composite parent, Adaptable adaptable) {
		super(parent, SWT.NULL);
		this.adaptable = adaptable;
	}

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
