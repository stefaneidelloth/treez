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

public class VerticalSeparatorPanel extends Composite {

	//#region CONSTRUCTORS

	public VerticalSeparatorPanel(Composite parent, Composite topContent, Composite bottomContent, double ratio) {
		super(parent, SWT.NONE);
		createSashAndAttachComposites(parent, topContent, bottomContent, ratio);
	}

	//#end region

	//#region METHODS

	private static void createSashAndAttachComposites(
			Composite parent,
			Composite topContent,
			Composite bottomContent,
			double ratio) {

		setParentLayout(parent);
		final Sash sash = createSash(parent, ratio);
		attachTopCompositeToSash(topContent, sash);
		attachBottomCompositeToSash(bottomContent, sash);
	}

	private static void setParentLayout(Composite parent) {
		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = 0;
		formLayout.marginHeight = 0;
		formLayout.spacing = 0;
		parent.setLayout(formLayout);
	}

	private static Sash createSash(Composite parent, double ratio) {
		final Sash sash = new Sash(parent, SWT.HORIZONTAL);
		final Color grey = new Color(null, 150, 150, 150);
		sash.setBackground(grey);

		final int fullSize = 100;
		Double yDoublePosition = fullSize * ratio;
		int yPosition = yDoublePosition.intValue();

		FormData data = new FormData();
		data.top = new FormAttachment(yPosition, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(fullSize, 0);
		data.height = 2;

		sash.setLayoutData(data);
		sash.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				((FormData) sash.getLayoutData()).top = new FormAttachment(0, event.y);
				sash.getParent().layout();
			}
		});
		return sash;
	}

	private static void attachTopCompositeToSash(Composite topContent, final Sash sash) {
		final int fullSize = 100;
		FormData data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(sash, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(fullSize, 0);
		topContent.setLayoutData(data);
	}

	private static void attachBottomCompositeToSash(Composite bottomContent, final Sash sash) {
		final int fullSize = 100;
		FormData data = new FormData();
		data.top = new FormAttachment(sash, 2);
		data.bottom = new FormAttachment(fullSize, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(fullSize, 0);
		bottomContent.setLayoutData(data);
	}

	//#end region

}
