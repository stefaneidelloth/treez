package org.treez.core.atom.attribute;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.Activator;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.base.parent.AbstractAttributeContainerAtom;
import org.treez.core.atom.base.annotation.IsParameter;

public class Spacer extends AbstractAttributeContainerAtom {

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
	public Spacer copy() {
		return new Spacer(this);
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("Spacer.png");
	}

	@Override
	public void createAtomControl(Composite parent,
			Refreshable treeViewerRefreshable) {

		//get data
		String currentWidth = getWidth();
		String currentHeight = getHeight();

		Composite spacerComposite = new Composite(parent, SWT.NONE);

		GridData sizeData = new GridData();
		sizeData.widthHint = Integer.parseInt(currentWidth);
		sizeData.heightHint = Integer.parseInt(currentHeight);
		spacerComposite.setLayoutData(sizeData);

		//create spacer	without border and title and with empty content

		/*
		 * Composite spacerComposite; if (parentID.equals("0")){
		 * //spacerComposite = new Composite(form, SWT.NONE); spacerComposite =
		 * toolkit.createComposite(form);
		 *
		 * } else{ Composite parentSection = mapOfSections.get(parentID);
		 * spacerComposite = new Composite(parentSection, SWT.NONE); }
		 */

	}

	//#end region

	//#region ACCESSORS

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	//#end region

}
