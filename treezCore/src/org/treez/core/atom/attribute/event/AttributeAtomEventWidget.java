package org.treez.core.atom.attribute.event;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

/**
 * Widget to wrap the source for AttributeAtomEvents *
 */
public class AttributeAtomEventWidget extends Widget {

	//#region CONSTRUCTORS

	AttributeAtomEventWidget(Shell shell) {
		super(shell, SWT.NONE);
	}

	//#end region

	//#region METHODS

	@Override
	protected void checkSubclass() {
		//does nothing to allow this class
	}

	//#end region

}
