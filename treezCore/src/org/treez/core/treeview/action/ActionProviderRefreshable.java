package org.treez.core.treeview.action;

import org.treez.core.adaptable.FocusChangingRefreshable;

/**
 * Provides actions, e.g. for a tree view
 */
public interface ActionProviderRefreshable extends FocusChangingRefreshable {

	/**
	 * Provides actions
	 */
	void provideActions();

}
