package org.treez.core.treeview.action;

import org.treez.core.adaptable.Refreshable;

/**
 * Provides actions, e.g. for a tree view
 */
public interface ActionProviderRefreshable extends Refreshable {

	/**
	 * Provides actions
	 */
	void provideActions();

}
