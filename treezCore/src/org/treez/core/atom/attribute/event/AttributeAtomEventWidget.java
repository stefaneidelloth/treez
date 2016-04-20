package org.treez.core.atom.attribute.event;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

/**
 * Widget to wrap the source for AttributeAtomEvents *
 */
public class AttributeAtomEventWidget extends Widget {

	//#region CONSTRUCTORS

	AttributeAtomEventWidget() {
		super(determineShell(), SWT.NONE);
	}

	//#end region

	//#region METHODS

	private static Widget determineShell() {

		Display display = Display.getCurrent();
		if (display == null) {
			display = new Display();
		}

		Shell shell = display.getActiveShell();
		if (shell == null) {
			shell = new Shell(display);
		}

		return shell;
	}

	@Override
	protected void checkSubclass() {
		//does nothing to allow this class
	}

	//#end region

}
