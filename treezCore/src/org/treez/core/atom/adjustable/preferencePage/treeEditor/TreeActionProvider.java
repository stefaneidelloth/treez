package org.treez.core.atom.adjustable.preferencePage.treeEditor;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.treeview.TreeViewerRefreshable;

/**
 * Provides actions for the TreeEditor
 */
public class TreeActionProvider implements Refreshable {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(TreeActionProvider.class);

	//#region ATTRIBUTES

	private TreeEditor treeEditor;

	private TreeViewerRefreshable treeViewer;

	/**
	 * Represent the last mouse button that has been pressed (1: left, 2: middle, 3: right)
	 */
	private int lastMouseButton;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param treeEditor
	 */
	public TreeActionProvider(TreeEditor treeEditor) {
		this.treeEditor = treeEditor;
		this.treeViewer = treeEditor.getTreeViewer();

		createContextMenuAction();
		addTreeListeners();

	}

	//#end region

	//#region METHODS

	/**
	 * Adds a selection changed listener to the tree viewer
	 */
	private void addTreeListeners() {

		//add a mouse listener to determine the last pressed
		//mouse button
		treeViewer.getControl().addMouseListener(new MouseListener() {

			@Override
			public void mouseDown(MouseEvent event) {
				lastMouseButton = event.button;
			}

			@Override
			public void mouseDoubleClick(MouseEvent event) {
				//nothing to do

			}

			@Override
			public void mouseUp(MouseEvent event) {
				//nothing to do
			}

		});

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				boolean isLeftClick = (lastMouseButton == 1);
				if (isLeftClick) {

					updatePropertyView();

				}
			}

		});

	}

	private void updatePropertyView() {

		//get current adaptable
		TreeItem[] treeItems = treeViewer.getTree().getSelection();

		if (treeItems.length > 0) {
			TreeItem treeItem = treeItems[0];
			Adaptable adaptable = (Adaptable) treeItem.getData();

			//get main form
			Composite mainForm = treeEditor.getPropertyPanel();

			//remove old content
			clearComposite(mainForm);

			//create property content
			adaptable.createControlAdaption(mainForm, treeViewer);

			//update
			updateComposite(mainForm);

		}

	}

	/**
	 * Clears the content of a given composite
	 *
	 * @param composite
	 */
	void clearComposite(Composite composite) {
		for (Control child : composite.getChildren()) {
			child.dispose();
		}
	}

	/**
	 * Updates the given component
	 *
	 * @param composite
	 */
	static void updateComposite(Composite composite) {
		composite.layout();

	}

	/**
	 * Creates the context menu
	 */
	private void createContextMenuAction() {

		//menu manager that only allows own contributions to the context menu,
		//where the id starts with id.org.treez.
		//contributions from other plugins that do not start with org.treez are filtered out

		MenuManager menuMgr = new MenuManager() {

			@Override
			public IContributionItem[] getItems() {

				IContributionItem[] items = super.getItems();
				List<IContributionItem> filteredItems = new ArrayList<IContributionItem>();
				for (IContributionItem item : items) {

					//sysLog.debug("context menu item:" + item.getId());

					if (item != null && item.getId() != null && item.getId().startsWith("org.treez")) {

						filteredItems.add(item);
					}
				}

				items = new IContributionItem[filteredItems.size()];
				return filteredItems.toArray(items);
			}
		};

		menuMgr.setRemoveAllWhenShown(true);

		menuMgr.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				//get current adaptable
				TreeItem[] treeItems = treeViewer.getTree().getSelection();
				TreeItem treeItem = treeItems[0];
				Adaptable adaptable = (Adaptable) treeItem.getData();

				//let the tree node of the adaptable fill the context menu
				adaptable.createTreeNodeAdaption().fillContextMenu(treeViewer, manager);
			}
		});

		Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
		//treeEditor.registerContextMenu(menuMgr, treeViewer);
	}

	@Override
	public void refresh() {
		updatePropertyView();

	}

	//#end region

}
