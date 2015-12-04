package org.treez.views.tree;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.treez.core.AbstractActivator;
import org.treez.core.Activator;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.treeview.TreeViewCodeConverter;
import org.treez.core.treeview.TreeViewProvider;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.TreezView;
import org.treez.core.treeview.action.ActionProviderRefreshable;
import org.treez.views.properties.PropertyViewPart;
import org.treez.views.tree.rootAtom.Root;

/**
 * Provides actions for the tree view
 */
@SuppressWarnings("checkstyle:classfanoutcomplexity")
public class TreeViewActionProvider implements ActionProviderRefreshable {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(TreeViewActionProvider.class);

	//#region ATTRIBUTES

	/**
	 * The tree view provider the actions are provided for
	 */
	private TreeViewProvider treeViewProvider;

	/**
	 * The treez view for which the tree view provider will provide the tree view
	 */
	private TreezView treezView;

	/**
	 * The tree viewer
	 */
	private TreeViewerRefreshable treeViewer;

	/**
	 * Converts the content of the tree view to code and vice versa
	 */
	private TreeViewCodeConverter treeViewCodeConverter;

	/**
	 * The drill down adapter
	 */
	private DrillDownAdapter drillDownAdapter;

	/**
	 * Creates an empty root atom in the tree view
	 */
	private Action createRootAction;

	/**
	 * Builds the tree from the script document
	 */
	private Action buildTreeAction;

	/**
	 * Builds the script document from the tree
	 */
	private Action buildCodeAction;

	/**
	 * Opens the Treez help
	 */
	private Action helpAction;

	/**
	 * An action that is performed on double click on a tree node
	 */
	private Action treeNodeDoubleClickAction;

	/**
	 * Represent the last mouse button that has been pressed (1: left, 2: middle, 3: right)
	 */
	private int lastMouseButton;

	/**
	 * Represents the tree item that has last been selected with a left mouse button. Tree items that are selected with
	 * other mouse buttons are ignored for this item. This is needed to update the property view, e.g. after a right
	 * click action for another tree node has been performed.
	 */
	private TreeItem lastLeftSelectedTreeItem = null;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param treezView
	 * @param treeViewProvider
	 */
	public TreeViewActionProvider(TreezView treezView, TreeViewProvider treeViewProvider) {
		this.treezView = treezView;
		this.treeViewProvider = treeViewProvider;
	}

	@Override
	public void provideActions() {
		this.treeViewer = treeViewProvider.getTreeViewer();
		this.treeViewCodeConverter = treeViewProvider.getTreeViewCodeConverter();
		this.drillDownAdapter = new DrillDownAdapter(this.treeViewer);

		createActionsWithoutAssigningThem();
		contributeToTreeViewerActionBars();
		assignTreeNodeDoubleClickAction();
		createTreeNodeContextMenu();
		assignTreeNodePropertyDisplayAction();

	}

	//#end region

	//#region METHODS

	//#region CREATE ACTIONS (WITHOUT ASSIGNING THEM)

	/**
	 * Creates some button and node actions
	 */
	public void createActionsWithoutAssigningThem() {

		//tool bar actions--------------------------------------------------------

		//(create root)- action
		createCreateRootAction();

		//(build tree)- action
		createBuildTreeAction();

		//(build code)- action
		createBuildCodeAction();

		//help action
		createHelpAction();

		//double click action ---------------------------------------------------------
		//expands the double clicked node and all its children or
		//collapses the double clicked node
		createTreeNodeDoubleClickAction();
	}

	private void createCreateRootAction() {
		createRootAction = new Action() {

			@Override
			public void run() {
				createRoot();
			}

		};

		createRootAction.setText("Create root");
		String createRootToolTipText = "Create an empty root atom.\n"
				+ "The current content of the tree viewer will be overwritten.";
		createRootAction.setToolTipText(createRootToolTipText);
		Image root = Activator.getImage("root.png");
		Image newRoot = Activator.getOverlayImageStatic(root, "add_decoration.png");
		ImageDescriptor newRootDescriptor = AbstractActivator.getImageDescriptor(newRoot);
		createRootAction.setImageDescriptor(newRootDescriptor);
	}

	private void createBuildTreeAction() {
		buildTreeAction = new Action() {

			@Override
			public void run() {
				treeViewCodeConverter.checkActiveDocumentAndBuildTreeFromCode();
			}
		};

		buildTreeAction.setText("Build tree");
		String buildTreeToolTipText = "Build a tree from the code document that is currently opened in the text editor.\n"
				+ "The current content of the tree viewer will be overwritten.";
		buildTreeAction.setToolTipText(buildTreeToolTipText);
		buildTreeAction.setImageDescriptor(Activator.getImageDescriptor("toTree.png"));
	}

	private void createBuildCodeAction() {
		buildCodeAction = new Action() {

			@Override
			public void run() {
				treeViewCodeConverter.checkActiveDoumentAndBuildCodeFromTree();
			}
		};

		buildCodeAction.setText("Build code");
		String toolTipText = "Build code from the tree.\n"
				+ "The code will be written to currently opend document in the text editor.\n"
				+ "The currently opened document will be overwritten.";
		buildCodeAction.setToolTipText(toolTipText);
		buildCodeAction.setImageDescriptor(Activator.getImageDescriptor("fromTree.png"));
	}

	private void createHelpAction() {
		helpAction = new Action() {

			@Override
			public void run() {
				showHelp();
			}
		};

		helpAction.setText("Help");
		helpAction.setToolTipText("Show the Treez help.");
		helpAction.setImageDescriptor(Activator.getImageDescriptor("help.png"));
	}

	private void createTreeNodeDoubleClickAction() {
		treeNodeDoubleClickAction = new Action() {

			@Override
			public void run() {
				totallyExpandOrCollapseCurrentlySelectedTreeNode();
			}
		};
	}

	//#end region

	//#region ASSIGN ACTIONS

	/**
	 * Adds actions to the pull down menu and the tool bar
	 */
	private void contributeToTreeViewerActionBars() {

		IViewSite viewSite = treezView.getViewSite();
		if (viewSite != null) {
			IActionBars bars = viewSite.getActionBars();
			fillLocalToolBar(bars.getToolBarManager());
			fillLocalPullDown(bars.getMenuManager());
		}

	}

	/**
	 * Add actions to the tool bar of the tree view
	 *
	 * @param manager
	 */
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(createRootAction);
		manager.add(buildTreeAction);
		manager.add(buildCodeAction);
		manager.add(helpAction);
		manager.add(new Separator());
		//drillDownAdapter.addNavigationActions(manager);
	}

	/**
	 * Fills the pull down menu
	 *
	 * @param manager
	 */
	private static void fillLocalPullDown(IMenuManager manager) {
		//manager.add(syncTreeAction);
		manager.add(new Separator());
		//manager.add(buildCodeAction);
	}

	/**
	 * Adds the double click action to the tree node
	 */
	private void assignTreeNodeDoubleClickAction() {
		treeViewer.addDoubleClickListener((event) -> treeNodeDoubleClickAction.run());

	}

	/**
	 * Adds a selection changed listener to the tree view to display a node in the property view
	 */
	private void assignTreeNodePropertyDisplayAction() {

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

		treeViewer.addSelectionChangedListener((event) -> {
			//sysLog.debug("selection changed");
			boolean isLeftClick = (lastMouseButton == 1);
			if (isLeftClick) {
				TreeItem[] treeItems = treeViewer.getTree().getSelection();
				if (treeItems.length > 0) {
					lastLeftSelectedTreeItem = treeItems[0];
					displayLastLeftSelectedTreeNodeInPropertyView();
				}
			}

		});
	}

	/**
	 * Creates the context menu for the tree nodes
	 */
	private void createTreeNodeContextMenu() {

		//menu manager that only allows own contributions to the context menu, where the id starts with org.treez
		//contributions from other plugins that do not start with de.treez are filtered out

		MenuManager menuMgr = new TreeNodeMenuManager(treeViewer);
		Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);

		IWorkbenchPartSite site = treezView.getSite();
		if (site != null) {
			site.registerContextMenu(menuMgr, treeViewer);
		}

	}

	//#end region

	//#region PERFORM ACTIONS

	//#region CREATE ROOT

	/**
	 * Creates a root atom and sets it as input of the tree viewer. The previous content of the tree viewer is lost.
	 */
	private void createRoot() {
		//create invisible root
		AbstractAtom invisibleRoot = new Root("invisibleRoot");

		//create visible root
		AbstractAtom root = new Root("root");
		invisibleRoot.addChild(root);

		//set invisible root as content of the tree viewer
		treeViewProvider.updateTreeContent(invisibleRoot);
	}

	//#end region

	//#region HELP

	/**
	 * Shows the Treez help
	 */
	private static void showHelp() {
		String reltiveHelpContextId = "Treez";
		org.treez.views.Activator.getInstance().showHelpForRelativeHelpContextId(reltiveHelpContextId);
	}

	//#end region

	//#region SHOW PROPERTIES

	/**
	 * Displays the properties of the currently selected tree node in the Treez Property view.
	 */
	private void displayLastLeftSelectedTreeNodeInPropertyView() {

		//try to get Treez Properties view
		TreezView propertyView = (TreezView) getView(PropertyViewPart.ID);
		getView(PropertyViewPart.ID);
		if (propertyView == null) {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			try {
				propertyView = (TreezView) page.showView(PropertyViewPart.ID);
			} catch (PartInitException exception) {
				String message = "Could not show Treez Properties View.";
				throw new IllegalStateException(message, exception);
			}
		}

		if (propertyView != null) {
			//get adaptable from last left-selected tree item
			TreeItem treeItem = lastLeftSelectedTreeItem;
			if (treeItem != null && !treeItem.isDisposed()) {
				Adaptable adaptable = (Adaptable) treeItem.getData();

				//get parent composite
				Composite parentComposite = propertyView.getContentComposite();
				//create property content
				createPropertyViewContent(adaptable, parentComposite);

			}
		} else {
			throw new IllegalStateException("Could not get Treez Properties view.");
		}

	}

	private void createPropertyViewContent(Adaptable adaptable, Composite parentComposite) {
		//clear old content
		for (Control child : parentComposite.getChildren()) {
			child.dispose();
		}

		//set layout
		FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		parentComposite.setLayout(fillLayout);

		//create sub composite with grid layout
		Composite contentPane = new Composite(parentComposite, SWT.NONE);

		GridLayout gridLayout = new GridLayout(1, false);
		contentPane.setLayout(gridLayout);

		Composite contentComposite = new Composite(contentPane, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		contentComposite.setLayoutData(gridData);

		//sysLog.debug("selection changed. update control");
		//create content for property view
		@SuppressWarnings("unused")
		Control propertyControl = adaptable.createControlAdaption(contentComposite, treeViewer);

		//create footer
		Composite footerComposite = new Composite(contentPane, SWT.NONE);
		GridData footerGridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		footerComposite.setLayoutData(footerGridData);
		createFooter(footerComposite, adaptable);

		//update property view
		parentComposite.layout();
	}

	private static void createFooter(Composite parent, Adaptable adaptable) {
		//set layout
		GridLayout gridLayout = new GridLayout(1, false);
		parent.setLayout(gridLayout);

		//add horizontal line
		@SuppressWarnings("unused")
		Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		GridData separatorGridData = new GridData(GridData.FILL_HORIZONTAL);
		separator.setLayoutData(separatorGridData);

		//add class name label
		Label label = new Label(parent, SWT.NONE);
		String className = adaptable.getClass().getName();
		label.setText(className);
	}

	/**
	 * Gets a view with a given id
	 *
	 * @param id
	 * @return
	 */
	public static IViewPart getView(String id) {
		IViewReference[] viewReferences = PlatformUI
				.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.getViewReferences();
		for (int i = 0; i < viewReferences.length; i++) {
			String currentId = viewReferences[i].getId();
			if (currentId.equals(id)) {
				return viewReferences[i].getView(false);
			}
		}
		return null;
	}

	//#end region

	//#region EXPAND & COLLAPSE

	/**
	 * Totally expands or collapses the currently selected tree node
	 */
	private void totallyExpandOrCollapseCurrentlySelectedTreeNode() {
		TreeItem selectedItem = treeViewer.getTree().getSelection()[0];
		boolean isExpanded = selectedItem.getExpanded();
		if (isExpanded) {
			//collapse
			selectedItem.setExpanded(false);
			treeViewer.refresh();

		} else {
			//expand
			expandAllTreeItemChildren(selectedItem);
			treeViewer.refresh();
		}

	}

	/**
	 * Expands a tree node and all its children
	 *
	 * @param selectedItem
	 */
	private void expandAllTreeItemChildren(TreeItem selectedItem) {
		selectedItem.setExpanded(true);
		treeViewer.refresh();
		TreeItem[] childItems = selectedItem.getItems();
		for (TreeItem childItem : childItems) {
			expandAllTreeItemChildren(childItem);
		}

	}

	//#end region

	//#end region

	//#region REFRESH

	@Override
	public void refresh() {
		//update the property view
		displayLastLeftSelectedTreeNodeInPropertyView();
	}

	//#end region

	//#end region

	//#region ACCESSORS

	/**
	 * Returns the drill down adapter
	 *
	 * @return
	 */
	public DrillDownAdapter getDrillDownAdapter() {
		return drillDownAdapter;
	}

	//#end region

}
