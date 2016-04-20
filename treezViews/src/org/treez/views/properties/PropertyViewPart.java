package org.treez.views.properties;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.treez.core.treeview.TreezView;

/**
 * The property view of treez
 */
public class PropertyViewPart extends ViewPart implements TreezView {

	//#region ATTRIBUTES

	/**
	 * The ID of the view as specified by the extension and used for the help system
	 */
	public static final String ID = "org.treez.views.properties";

	/**
	 * The main form that can be filled with a control from an adaptable
	 */
	private Composite contentComposite;

	//#end region

	//#region CONSTRUCTORS

	public PropertyViewPart() {}

	//#end region

	//#region METHODS

	@Override
	public void createPartControl(Composite parent) {
		//LOG.debug("creating part control");
		contentComposite = parent;
	}

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
