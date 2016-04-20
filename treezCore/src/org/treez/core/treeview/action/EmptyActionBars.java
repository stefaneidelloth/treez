package org.treez.core.treeview.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.services.IServiceLocator;

/**
 * Empty dummy IActionBars for test purposes. If the tree viewer is not shown
 * inside an eclipse plugin but is run in test mode, it is not possible to get
 * the IActionBars from Eclipse. In order to run the test without exceptions for
 * missing IActionBars this dummy implementation is used.
 */
public class EmptyActionBars implements IActionBars {

	//#region CONSTRUCTORS

	public EmptyActionBars() {

	}

	//#end region

	//#region METHODS

	@Override
	public void clearGlobalActionHandlers() {
		//dummy test implementation
	}

	@Override
	public IAction getGlobalActionHandler(String arg0) {
		//dummy test implementation
		return null;
	}

	@Override
	public IMenuManager getMenuManager() {
		//dummy test implementation
		return null;
	}

	@Override
	public IServiceLocator getServiceLocator() {
		//dummy test implementation
		return null;
	}

	@Override
	public IStatusLineManager getStatusLineManager() {
		//dummy test implementation
		return null;
	}

	@Override
	public IToolBarManager getToolBarManager() {
		//dummy test implementation
		return null;
	}

	@Override
	public void setGlobalActionHandler(String arg0, IAction arg1) {
		//dummy test implementation
	}

	@Override
	public void updateActionBars() {
		//dummy test implementation
	}

	//#end region

}
