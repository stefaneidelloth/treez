package org.treez.core.path;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewSite;
import org.treez.core.Activator;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.treeview.TreeViewProvider;
import org.treez.core.treeview.TreezView;
import org.treez.core.utils.Utils;

/**
 * A dialog window that shows a tree viewer for selecting a tree node
 */
public class ModelPathTreeSelectionWindow implements TreezView {

	private static final Logger LOG = Logger
			.getLogger(ModelPathTreeSelectionWindow.class);

	//#region ATTRIBUTES

	/**
	 * A shell that is used as parent composite
	 */
	private Shell shell;

	/**
	 * A composite for the tree viewer
	 */
	private Composite contentComposite;

	/**
	 * The tree view provider
	 */
	private TreeViewProvider treeViewProvider;

	/**
	 * The result model path
	 */
	private String modelPath;

	private String defaultPath;

	//#end region

	//#region CONSTRUCTORS

	@SuppressWarnings("checkstyle:magicnumber")
	public ModelPathTreeSelectionWindow() {

		//get display
		Display display = Display.getCurrent();

		//define shell
		shell = new Shell(display);
		shell.setLayout(new GridLayout());
		shell.setText("Tree node selection");
		shell.setSize(400, 400);

		//set icon
		Image treezImage = Activator.getImage("tree.png");
		shell.setImage(treezImage);

		//create label for message
		Label massageLabel = new Label(shell, SWT.NONE);
		massageLabel.setText("Please select the wanted tree node:");

		//create composite for tree viewer
		contentComposite = new Composite(shell, SWT.BORDER);
		GridData parentData = new GridData(SWT.FILL, SWT.FILL, true, true);
		contentComposite.setLayout(new GridLayout(1, true));
		contentComposite.setLayoutData(parentData);

		//create tree view provider
		treeViewProvider = new TreeViewProvider(this);

		//create composite with ok and cancel button
		Composite buttonComposite = new Composite(shell, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(5, true));

		@SuppressWarnings("unused")
		Label spacerLabel = new Label(buttonComposite, SWT.None);
		Button okButton = new Button(buttonComposite, SWT.NONE);
		okButton.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event event) {
				okAction();
			}

		});

		okButton.setText("  Ok  ");
		@SuppressWarnings("unused")
		Label spacerLabelCenter = new Label(buttonComposite, SWT.None);
		okButton.setLayoutData(parentData);
		Button cancelButton = new Button(buttonComposite, SWT.NONE);
		cancelButton.setText(" Cancel ");
		cancelButton.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event event) {
				cancelAction();
			}

		});

	}

	//#end region

	//#region METHODS

	/**
	 * Closes the Window and sets the return value to the path of the selected
	 * node
	 */
	public void okAction() {
		LOG.debug("ok");

		//get selected node
		TreeItem[] selectedTreeItems = treeViewProvider.getTreeViewer()
				.getTree().getSelection();
		boolean itemSelected = selectedTreeItems.length > 0;
		if (itemSelected) {
			//only take first selected item
			TreeItem selectedTreeItem = selectedTreeItems[0];
			AbstractAtom selectedAtom = (AbstractAtom) selectedTreeItem
					.getData();
			String currentModelPath = selectedAtom.createTreeNodeAdaption()
					.getTreePath();
			LOG.debug(currentModelPath);

			//save the selected model path in the modelPath of this dialog to
			//return it
			ModelPathTreeSelectionWindow.this.modelPath = currentModelPath;
		} else {
			//set result to the default path that was originally given to the
			//dialog
			ModelPathTreeSelectionWindow.this.modelPath = ModelPathTreeSelectionWindow.this.defaultPath;
		}

		//close the dialog
		shell.close();
	}

	/**
	 * Closes the Window and sets the return value to the path of the selected
	 * node
	 */
	public void cancelAction() {
		LOG.debug("cancel");
		//set result to the default path that was originally given to the
		//dialog
		ModelPathTreeSelectionWindow.this.modelPath = ModelPathTreeSelectionWindow.this.defaultPath;

		//close the dialog
		shell.close();
	}

	//#end region

	//#region ACCESSORS

	/**
	 * Returns the parent composite for the control preview as and
	 * implementation of the TreezView interface
	 */
	@Override
	public Composite getContentComposite() {
		return contentComposite;
	}

	@Override
	public IViewSite getSite() {
		return null;
	}

	@Override
	public IViewSite getViewSite() {
		return null;
	}

	/**
	 * Allows the user to select a model path by selecting a tree node in a tree
	 * view
	 *
	 * @param model
	 * @param targetClassNames
	 * @param defaultPath
	 */
	public void selectModelPath(AbstractAtom model, String targetClassNames,
			String defaultPath) {

		//save defaultPath and value as a default result
		this.defaultPath = defaultPath;
		modelPath = defaultPath;

		//provide the tree view with the tree view provider
		provideTreeView(targetClassNames);

		//set model as content of the tree
		TreeViewer treeViewer = treeViewProvider.getTreeViewer();
		treeViewer.setInput(model);

		//select default path
		selectNodeWithDefaultPath(treeViewer, defaultPath);

		//show dialog window and wait until it is closed
		shell.open();
		Display display = Display.getCurrent();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

	}

	/**
	 * Tries to select a node for the given model path in the given tree view
	 * and to expand the nodes so that the wanted node can immediately bee seen
	 *
	 * @param treeViewer
	 * @param defaultPath
	 */
	private void selectNodeWithDefaultPath(TreeViewer treeViewer,
			String defaultPath) {

		//expand all nodes
		treeViewer.expandAll();

		//get the tree item to be selected by looping through the tree
		TreeItem[] allRootItems = treeViewer.getTree().getItems();
		TreeItem wantedTreeItem = tryToGetTreeItemForModelPath(defaultPath,
				allRootItems);

		boolean itemWasFound = wantedTreeItem != null;
		if (itemWasFound) {
			//select the tree item
			treeViewer.getTree().setSelection(wantedTreeItem);

		}

	}

	/**
	 * Returns the TreeItem for a given model path or null if the model path
	 * cannot be found
	 *
	 * @param defaultPath
	 * @param allRootItems
	 * @return
	 */
	private TreeItem tryToGetTreeItemForModelPath(String defaultPath,
			TreeItem[] allRootItems) {

		for (TreeItem currentItem : allRootItems) {

			//get model path for current item
			AbstractAtom atom = (AbstractAtom) currentItem.getData();
			if (atom != null) {
				String currentModelPath = atom.createTreeNodeAdaption()
						.getTreePath();

				//check model path for current item
				boolean isWantedItem = currentModelPath.equals(defaultPath);
				if (isWantedItem) {
					return currentItem;
				}

				//check model paths for sub items
				TreeItem[] subItems = currentItem.getItems();
				TreeItem wantedItem = tryToGetTreeItemForModelPath(defaultPath,
						subItems);
				if (wantedItem != null) {
					return wantedItem;
				}
			}
		}

		//could not find wanted item
		return null;
	}

	/**
	 * Provides the tree view with the tree view provider
	 *
	 * @param wantedTypeName
	 */
	private void provideTreeView(String wantedTypeNames) {

		//provide tree view
		treeViewProvider.provideTreeView(wantedTypeNames, null, null);

		//set a selection changed listener that only allows the selection of
		//the
		//nodes that have the right target class
		TreeViewer treeViewer = treeViewProvider.getTreeViewer();
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				//get current adaptable
				TreeItem[] treeItems = treeViewer.getTree().getSelection();

				if (treeItems.length > 0) {

					//get adaptable
					TreeItem treeItem = treeItems[0];
					Adaptable adaptable = (Adaptable) treeItem.getData();

					//check if it has wanted type
					boolean hasWantedType = Utils
							.checkIfHasWantedType(adaptable, wantedTypeNames);
					if (!hasWantedType) {
						removeAdaptableFromTreeSelection(adaptable, treeViewer);
					}

				}
			}

		});
	}

	/**
	 * Removes a selected adaptable from the selection of the given treeViewer
	 *
	 * @param adaptable
	 * @param treeViewer
	 */
	private static void removeAdaptableFromTreeSelection(Adaptable adaptable,
			TreeViewer treeViewer) {

		//get old selection
		TreeItem[] treeItems = treeViewer.getTree().getSelection();

		//create new selection
		List<TreeItem> newTreeItems = new ArrayList<>();
		for (TreeItem currentTreeItem : treeItems) {
			Adaptable currentAdaptable = (Adaptable) currentTreeItem.getData();
			boolean doNotInclude = currentAdaptable.equals(adaptable);
			if (doNotInclude) {
				//leave the item that should be removed from the selection
			} else {
				//add the other items to the new selection
				newTreeItems.add(currentTreeItem);
			}
		}
		TreeItem[] newSelection = newTreeItems
				.toArray(new TreeItem[newTreeItems.size()]);

		//set new selection
		treeViewer.getTree().setSelection(newSelection);

	}

	/**
	 * Returns the selected model path
	 *
	 * @return
	 */
	public String getModelPath() {
		boolean selectionExists = modelPath != null;
		if (selectionExists) {
			return modelPath;
		} else {
			throw new IllegalStateException(
					"The method selectModelPath must be called before using this method.");
		}
	}

	//#end region

}
