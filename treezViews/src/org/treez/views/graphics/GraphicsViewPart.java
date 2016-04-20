package org.treez.views.graphics;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.treez.core.treeview.TreezView;

/**
 * The GraphicsAdaption view of treez
 */
public class GraphicsViewPart extends ViewPart implements TreezView {

	//#region ATTRIBUTES

	/**
	 * The ID of the view as specified by the extension and used for the help system
	 */
	public static final String ID = "org.treez.views.graphics";

	/**
	 * The main composite that can be filled by treez atoms
	 */
	private Composite contentComposite;

	//#end region

	//#region CONSTRUCTORS

	public GraphicsViewPart() {}

	//#end region

	//#region METHODS

	@Override
	public void createPartControl(Composite parent) {
		//LOG.debug("creating part control");
		contentComposite = parent;
	}

	/**
	 * Pass the focus request to the main form
	 */
	@Override
	public void setFocus() {
		contentComposite.setFocus();
	}

	//#end region

	//#region ACCESSORS

	@Override
	public Composite getContentComposite() {
		return contentComposite;
	}

	//#end region

}
