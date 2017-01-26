package org.treez.core.atom.attribute.base;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.Adaptable;

/**
 * A ControlAdaption that only displays a text label. If neither want to display a text label, pass an empty string to
 * the constructor.
 */
public class EmptyControlAdaption extends AbstractControlAdaption {

	//#region CONSTRUCTORS

	public EmptyControlAdaption(Composite parent, Adaptable adaptable, String labelText) {
		super(parent, adaptable);

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
		text.setText(labelText);

		//text.setAlwaysShowScrollBars(false);

		parent.layout();

	}

	//#end region
}
