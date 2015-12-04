package org.treez.core.atom.base;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.adaptable.GraphicsAdaption;

/**
 * Default GraphicsAdaption for atoms
 *
 */
public class AtomGraphicsAdaption implements GraphicsAdaption {

	//#region ATTRIBUTES

	/**
	 * The abstract atom that is adapted
	 */
	private AbstractAtom adaptedAtom;

	/**
	 * The parent composite
	 */
	private Composite parentComposite;

	/**
	 * A label that is shown as graphics contents
	 */
	private Label graphicsLabel;

	//#end region

	//#region CONSTRUCTORS

	AtomGraphicsAdaption(Composite parentComposite, AbstractAtom adaptedAtom) {
		this.parentComposite = parentComposite;
		this.adaptedAtom = adaptedAtom;
		createGraphicsContents(parentComposite);
	}

	//#end region

	//#region METHODS

	private void createGraphicsContents(Composite parentComposite) {

		//delete old contents

		//set layouts
		parentComposite.setLayout(new GridLayout());

		graphicsLabel = new Label(parentComposite, SWT.NONE);
		graphicsLabel.setText("Graphics adaption for " + adaptedAtom.getName());

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
		Rectangle rect = graphicsLabel.getBounds();
		return rect.width;
	}

	@Override
	public double getHeight() {
		Rectangle rect = graphicsLabel.getBounds();
		return rect.height;
	}

	//#end region
}
