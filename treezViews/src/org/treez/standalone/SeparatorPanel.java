package org.treez.standalone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Sash;

public class SeparatorPanel extends Composite {

	//#region CONSTRUCTORS

	public SeparatorPanel(Composite parent, Composite leftContent, Composite rightContent, double ratio) {
		super(parent, SWT.NONE);
		createSashAndAttachComposites(parent, leftContent, rightContent, ratio);
	}

	//#end region

	//#region METHODS

	private static void createSashAndAttachComposites(
			Composite parent,
			Composite leftContent,
			Composite rightContent,
			double ratio) {

		setParentLayout(parent);
		final Sash sash = createSash(parent, ratio);
		attachLeftCompositeToSash(leftContent, sash);
		attachRightCompositeToSash(rightContent, sash);
	}

	private static void setParentLayout(Composite parent) {
		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = 0;
		formLayout.marginHeight = 0;
		formLayout.spacing = 0;
		parent.setLayout(formLayout);
	}

	private static Sash createSash(Composite parent, double ratio) {
		final Sash sash = new Sash(parent, SWT.VERTICAL);
		final Color grey = new Color(null, 150, 150, 150);
		sash.setBackground(grey);

		final int fullSize = 100;
		FormData data = new FormData();
		data.top = new FormAttachment(0, 0); // Attach to top
		data.bottom = new FormAttachment(fullSize, 0); // Attach to bottom
		Double xPosition = fullSize * ratio;
		data.left = new FormAttachment(xPosition.intValue(), 0); // Attach at horizontal ratio, e.g. in the middle
		data.width = 2;
		sash.setLayoutData(data);
		sash.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				((FormData) sash.getLayoutData()).left = new FormAttachment(0, event.x);
				sash.getParent().layout();
			}
		});
		return sash;
	}

	private static void attachLeftCompositeToSash(Composite leftContent, final Sash sash) {
		final int fullSize = 100;
		FormData data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(fullSize, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(sash, 0);
		leftContent.setLayoutData(data);
	}

	private static void attachRightCompositeToSash(Composite rightContent, final Sash sash) {
		final int fullSize = 100;
		FormData data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(fullSize, 0);
		data.left = new FormAttachment(sash, 0);
		data.right = new FormAttachment(fullSize, 0);
		rightContent.setLayoutData(data);
	}

	//#end region

}
