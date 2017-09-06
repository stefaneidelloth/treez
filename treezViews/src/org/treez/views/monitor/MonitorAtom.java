package org.treez.views.monitor;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.views.Activator;

public class MonitorAtom extends AbstractAtom<MonitorAtom> {

	//#region CONSTRUCTORS

	public MonitorAtom(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 */
	private MonitorAtom(MonitorAtom atomToCopy) {
		super(atomToCopy);
	}

	//#end region

	//#region METHODS

	@Override
	public MonitorAtom copy() {
		return new MonitorAtom(this);
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("root.png");
	}

	@Override
	public
			AbstractControlAdaption
			createControlAdaption(Composite parent, FocusChangingRefreshable refreshableTreeViewer) {
		return new MonitorControlAdaption(parent, this);
	}

	@Override
	protected MonitorAtom getThis() {
		return this;
	}

	//#end region
}
