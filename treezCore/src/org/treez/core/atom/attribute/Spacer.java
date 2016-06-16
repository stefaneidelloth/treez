package org.treez.core.atom.attribute;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.parent.AbstractAttributeContainerAtom;
import org.treez.core.atom.base.annotation.IsParameter;

public class Spacer extends AbstractAttributeContainerAtom<Spacer> {

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "100")
	private String width;

	@IsParameter(defaultValue = "100")
	private String height;

	//#end region

	//#region CONSTRUCTORS

	public Spacer(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 */
	private Spacer(Spacer spacerToCopy) {
		super(spacerToCopy);
		width = spacerToCopy.width;
		height = spacerToCopy.height;
	}

	//#end region

	//#region METHODS

	@Override
	public Spacer getThis() {
		return this;
	}

	@Override
	public Spacer copy() {
		return new Spacer(this);
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("Spacer.png");
	}

	@Override
	public void createAtomControl(Composite parent, FocusChangingRefreshable treeViewerRefreshable) {

		//get data
		String currentWidth = getWidth();
		String currentHeight = getHeight();

		Composite spacerComposite = new Composite(parent, SWT.NONE);

		GridData sizeData = new GridData();
		sizeData.widthHint = Integer.parseInt(currentWidth);
		sizeData.heightHint = Integer.parseInt(currentHeight);
		spacerComposite.setLayoutData(sizeData);

	}

	//#end region

	//#region ACCESSORS

	public String getWidth() {
		return width;
	}

	public Spacer setWidth(String width) {
		this.width = width;
		return getThis();
	}

	public String getHeight() {
		return height;
	}

	public Spacer setHeight(String height) {
		this.height = height;
		return getThis();
	}

	@Override
	public Spacer setEnabled(boolean enable) {
		throw new IllegalStateException("not yet implemented");
	}

	//#end region

}
