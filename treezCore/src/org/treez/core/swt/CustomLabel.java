package org.treez.core.swt;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * A label composite that vertically aligns with Text
 */
public class CustomLabel extends Composite {

	//#region ATTRIBUTES

	/**
	 * The wrapped label
	 */
	private Label label;

	/**
	 * Grid data for the layout of the label
	 */
	private GridData gridData;

	//#end region

	//#region CONSTRUCTORS

	public CustomLabel(FormToolkit toolkit, Composite parent, String text) {
		super(parent, SWT.NONE);
		setGridLayoutWithoutMargins(this);
		Composite verticalAlignmentContainer = createContainer(parent, toolkit);
		label = toolkit.createLabel(verticalAlignmentContainer, text);

		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = false;
		gridData.horizontalAlignment = GridData.BEGINNING;
		gridData.horizontalIndent = 0;

		label.setLayoutData(gridData);
	}

	//#end region

	//#region METHODS

	/**
	 * Creates a wrapping container
	 *
	 * @param parent
	 * @param toolkit
	 * @return
	 */
	private static Composite createContainer(Composite parent,
			FormToolkit toolkit) {
		Composite container = toolkit.createComposite(parent, SWT.NONE);
		setGridLayoutWithoutMargins(container);
		return container;
	}

	/**
	 * Sets the grid layout
	 *
	 * @param container
	 */
	private static void setGridLayoutWithoutMargins(Composite container) {
		org.eclipse.swt.layout.GridLayout gridLayout = new org.eclipse.swt.layout.GridLayout(
				1, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		container.setLayout(gridLayout);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.BEGINNING;
		gridData.grabExcessHorizontalSpace = false;
		gridData.horizontalIndent = 0;
		container.setLayoutData(gridData);
	}

	/**
	 * Sets a preferred width for short labels
	 *
	 * @param preferredWidth
	 */
	public void setPrefferedWidth(int preferredWidth) {

		int labelWidth = label.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		if (labelWidth < preferredWidth) {
			GridDataFactory.fillDefaults().hint(preferredWidth, SWT.DEFAULT)
					.applyTo(label);
		}
	}

	/**
	 * Hides this CustomLabel
	 */
	public void hide() {
		GridData currentGridData = (GridData) label.getLayoutData();
		currentGridData.exclude = true;
		label.setLayoutData(currentGridData);
		label.setVisible(false);
		refreshGrandParent();
	}

	/**
	 * Shows this CustomLabel
	 */
	public void show() {
		GridData currentGridData = (GridData) label.getLayoutData();
		currentGridData.exclude = false;
		label.setLayoutData(currentGridData);
		label.setVisible(true);
		this.layout(true);
		refreshGrandParent();

	}

	/**
	 * Tells the parent and grand parent composites to update
	 */
	private void refreshGrandParent() {
		Composite parent = this.getParent();
		if (parent != null && !parent.isDisposed()) {
			parent.layout(true);
			Composite grandParent = parent.getParent();
			if (grandParent != null && !grandParent.isDisposed()) {
				grandParent.layout(true);
			}
		}
	}

	//#end region

	//#region ACCESSORS

	public void setText(String text) {
		label.setText(text);
	}

	public String getText() {
		return label.getText();
	}

	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		label.setBackground(color);
	}

	@Override
	public void setForeground(Color color) {
		super.setForeground(color);
		label.setForeground(color);
	}

	@Override
	public Point getSize() {
		return label.getSize();
	}

	@Override
	public Rectangle getBounds() {
		return label.getBounds();
	}

	//#end region

}
