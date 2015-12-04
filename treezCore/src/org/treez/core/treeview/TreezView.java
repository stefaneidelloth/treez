package org.treez.core.treeview;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * All views of treez that are used by other plugins should implement 
 * this interface to be able to provide a content composite to the
 * other plugins. Other plugins are then able to fill the content composite.
 *
 */
public interface TreezView {
	
	//#region METHODS
	
	/**
	 * Provides a content composite that can be filled by treez atoms
	 * @return
	 */
	Composite getContentComposite();
	
	/**
	 * Returns the site of the view part
	 * @return
	 */
	IWorkbenchPartSite getSite();
	
	
	/**
	 * Returns the view site of the view part
	 * @return
	 */
	 IViewSite getViewSite();
	
	//#end region

}
