package org.treez.core.atom.base;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.adaptable.CadAdaption;

/**
 * Default CadAdaption for atoms
 */
public class AtomCadAdaption implements CadAdaption {

	//#region ATTRIBUTES

	/**
	 * The abstract atom that is adapted
	 */
	private AbstractAtom<?> adaptedAtom;

	/**
	 * The parent composite
	 */
	private Composite parentComposite;

	/**
	 * A label that is shown as cad contents
	 */
	private Label cadLabel;

	//#end region

	//#region CONSTRUCTORS

	AtomCadAdaption(Composite parentComposite, AbstractAtom<?> adaptedAtom) {
		this.parentComposite = parentComposite;
		this.adaptedAtom = adaptedAtom;
		createCadContents(parentComposite);
	}

	//#end region

	//#region METHODS

	private void createCadContents(Composite parentComposite) {

		//delete old contents

		//set layouts
		parentComposite.setLayout(new GridLayout());

		cadLabel = new Label(parentComposite, SWT.NONE);
		cadLabel.setText("CAD adaption for " + adaptedAtom.getName());

	}

	@Override
	public void hide() {
		//default implementation that does nothing
	}

	@Override
	public void show() {
		//default implementation that does nothing
	}

	//#end region

	//#region ACCESSORS

	@Override
	public Adaptable getAdaptable() {
		return adaptedAtom;
	}

	@Override
	public Composite getParentComposite() {
		return parentComposite;
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public double getX() {
		return 0;
	}

	@Override
	public double getY() {
		return 0;
	}

	@Override
	public double getWidth() {
		Rectangle rect = cadLabel.getBounds();
		return rect.width;
	}

	@Override
	public double getHeight() {
		Rectangle rect = cadLabel.getBounds();
		return rect.height;
	}

	//#end region
}
