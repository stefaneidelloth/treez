package org.treez.views.monitor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.treez.core.adaptable.AbstractControlAdaption;

public class MonitorControlAdaption extends AbstractControlAdaption {

	//#region CONSTRUCTORS

	public MonitorControlAdaption(Composite parent, MonitorAtom monitorAtom) {
		super(parent, monitorAtom);

		//clear old content
		for (Control child : parent.getChildren()) {
			child.dispose();
		}

		//set layout
		parent.setLayout(new GridLayout());

		//show text field that automatically shows scroll bars if needed
		StyledText text = new StyledText(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		GridData textLayoutData = GridDataFactory.fillDefaults().grab(true, true).span(2, 1).create();
		text.setLayoutData(textLayoutData);
		text.setText(monitorAtom.getName());

		//text.setAlwaysShowScrollBars(false);

		parent.layout();

	}

	//#end region
}
