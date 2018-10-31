package org.treez.core.treeview;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.treez.core.AbstractActivator;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.error.ErrorAtom;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.action.ActionProviderRefreshable;
import org.treez.core.treeview.action.EmptyActionBars;

/**
 * Provides the tree view for the TreeViewPart
 */
@SuppressWarnings("restriction")
public class TreeViewProvider {

	private static final Logger LOG = LogManager.getLogger(TreeViewProvider.class);

	//#region ATTRIBUTES

	/**
	 * The treez view (typically an eclipse view part) for which the tree view
	 * will be provided
	 */
	private TreezView treezView;

	/**
	 * The tree viewer that will actually show the tree
	 */
	private TreeViewerRefreshable treeViewer;

	/**
	 * The content Composite
	 */
	private Composite contentComposite;

	/**
	 * Provides the content (the nodes) for the shown tree
	 */
	private TreeViewContentProvider contentProvider;

	/**
	 * Converter that converts a java script to a tree and vice versa
	 */
	private TreeViewCodeConverter treeViewCodeConverter;

	/**
	 * ID for the help system
	 */
	public static final String HELP_CONTEXT_ID = "TreezTreeView";

	//#end region

	//#region CONSTRUCTORS

	public TreeViewProvider(TreezView treezView) {
		this.treezView = treezView;

	}

	//#end region

	//#region METHODS

	/**
	 * Provides a tree view to the treezView (which has been passed to the
	 * constructor of this class before) The argument treeViewActionProvider
	 * might provide actions to the tree view or you pass null if no actions
	 * should be provided.
	 *
	 * @param targetClassNames
	 *            : class for the elements that are shown in the tree; can be
	 *            used to filter the nodes for specific type
	 * @param treeViewActionProvider
	 * @param treeViewCodeConverter
	 */
	public void provideTreeView(String targetClassNames,
			ActionProviderRefreshable treeViewActionProvider,
			TreeViewCodeConverter treeViewCodeConverter) {

		//set the tree view code converter
		this.treeViewCodeConverter = treeViewCodeConverter;

		//get the contents composite where the tree will be shown
		contentComposite = treezView.getContentComposite();

		//set layout
		contentComposite.setLayout(new FillLayout());

		//create the tree viewer
		treeViewer = new TreeViewerRefreshable(contentComposite,
				treeViewActionProvider,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		//set the label provider that determines how the nodes are displayed
		treeViewer.setLabelProvider(new TreeViewLabelAndImageProvider());

		//set the comparer that compares two nodes by their names
		treeViewer.setComparer(new NodeComparer());

		//set the content provider that provides the nodes for the tree
		contentProvider = new TreeViewContentProvider(targetClassNames);
		treeViewer.setContentProvider(contentProvider);

		//set the invisible root node. the children of this
		//invisible root node will be shown as the main nodes in the tree
		Adaptable invisibleRoot = contentProvider.getInvisibleRoot();
		treeViewer.setInput(invisibleRoot); //sets the input to the invisible root of the content provider

		//create some actions (e.g. context menu actions)
		if (treeViewActionProvider != null) {
			treeViewActionProvider.provideActions();
		}

		//set id for help system
		if (AbstractActivator.isRunningInEclipse()) {
			setIdForHelpSystem();
		}

	}

	/**
	 * Set the Id for the help system
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private void setIdForHelpSystem() {
		//try to set the help context id for the treeViewer's control
		//If this class is not used inside eclipse, but in a test mode
		//it will not be possible to get the workbench
		IWorkbench workbench = null;
		try {
			workbench = PlatformUI.getWorkbench();
		} catch (Exception error) {
			LOG.warn("Could not get workbench.");
		}

		if (workbench != null) {
			try {
				workbench.getHelpSystem().setHelp(treeViewer.getControl(),
						HELP_CONTEXT_ID);
			} catch (Exception error) {
				LOG.warn("Could not set help id for treeViewer control.");
			}
		}
	}

	/**
	 * Passes the focus request to the treeViewer's control. The eclipse plugin
	 * (the part view) has a setFocus method that has to be overridden. This
	 * focus request is redirected to this method which further redirects the
	 * focus to the control of the tree viewer.
	 */
	public void setFocus() {
		treeViewer.getControl().setFocus();
	}

	/**
	 * Returns the file name of the currently active document
	 *
	 * @return
	 */
	public String getFileNameOfCurrentlyActiveDocument() {
		IEditorPart activeEditor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		String filename = activeEditor.getEditorInput().getName();
		return filename;
	}

	/**
	 * Checks if the given filename ends with a known extension and returns the
	 * corresponding script type. If the extension is not known
	 * ScriptType.UNKNOWN is returned.
	 *
	 * @param fileName
	 * @return
	 */
	@SuppressWarnings("checkstyle:magicnumber")
	public ScriptType getScriptTypeFromFileName(String fileName) {

		int length = fileName.length();
		boolean isJava = fileName.substring(length - 5, length).equals(".java");
		if (isJava) {
			return ScriptType.JAVA;
		}

		boolean isJavaScript = fileName.substring(length - 3, length)
				.equals(".js");
		if (isJavaScript) {
			return ScriptType.JAVA_SCRIPT;
		}

		return ScriptType.UNKNOWN;

	}

	/**
	 * Updates the tree content with a given root element. It tries to restore
	 * the expansion state of the tree after updating the content.
	 *
	 * @param invisibleRoot
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	public void updateTreeContent(Adaptable invisibleRoot) {

		//save expansion state
		Object[] expandedObjects = treeViewer.getExpandedElements();

		//set new content
		treeViewer.setInput(invisibleRoot);

		//try to restore expansion state
		try {
			treeViewer.setExpandedElements(expandedObjects);
		} catch (Exception e) {
			LOG.warn("Could not automatically expand nodes.");
		}
	}

	public void expandAll() {
		treeViewer.expandAll();
	}

	/**
	 * Visually shows if the tree contains errors by setting the background of
	 * the tree viewer to orange
	 *
	 * @param parsingState
	 * @param executionException
	 */
	public void visualizeErrorState(TreeErrorState parsingState,
			Exception executionException) {

		final Color errorColor = new Color(Display.getCurrent(), 250, 200, 128);
		final Color normalColor = new Color(Display.getCurrent(), 255, 255,
				255);

		switch (parsingState) {
			case OK :
				treeViewer.getTree().setBackground(normalColor);
				treeViewer.getTree().setToolTipText("");
				break;
			case ERROR :
				//show tool tip
				treeViewer.getTree().setBackground(errorColor);
				String errorCode = ExceptionUtils
						.getStackTrace(executionException);
				treeViewer.getTree().setToolTipText(errorCode);

				//create atom to display error
				AttributeRoot invisibleRoot = new AttributeRoot(
						"invisibleRoot");
				String messageTitle = "Could not build tree from code!";
				ErrorAtom errorAtom = new ErrorAtom("error", messageTitle,
						executionException);
				invisibleRoot.addChild(errorAtom);
				treeViewer.setInput(invisibleRoot);

				break;
			default :
				throw new IllegalStateException("The parsing state "
						+ parsingState + " is not yet implementet.");
		}

	}

	/**
	 * Shows an info message for the user
	 *
	 * @param message
	 */
	public void showMessage(String message) {
		MessageDialog.openInformation(treeViewer.getControl().getShell(),
				"TreeView", message);
	}

	/**
	 * Registers the given context menu
	 *
	 * @param menuMgr
	 */
	public void registerContextMenu(MenuManager menuMgr) {

		String parentType = treezView.getClass().getSimpleName();
		if (parentType.equals("TreeViewPart")) {

			TreeViewer currentTreeViewer = getTreeViewer();
			IWorkbenchPartSite site = treezView.getSite();
			if (site != null) {
				site.registerContextMenu(menuMgr, currentTreeViewer);
			}
		} else {
			LOG.warn("Could not register context menu");
		}

	}

	/**
	 * Creates dummy IActionBars for test purposes. If the tree viewer is not
	 * shown inside an eclipse plugin but is run in test mode, it is not
	 * possible to get the IActionBars from Eclipse. In order to run the test
	 * without exceptions for missing IActionBars this dummy implementation is
	 * used.
	 *
	 * @return
	 */
	private static IActionBars createDummyActionBars() {
		IActionBars actionBars = new EmptyActionBars();
		return actionBars;
	}

	//#end region

	//#region ACCESSORS

	public IActionBars getActionBars() {

		String parentType = treezView.getClass().getSimpleName();
		if (parentType.equals("TreeViewPart")) {
			//get action bars from eclipse view part
			IActionBars bars = treezView.getViewSite().getActionBars();
			return bars;
		} else {
			//return dummy action bars for test purposes
			LOG.warn("Could not get action bars.");
			IActionBars actionBars = createDummyActionBars();
			return actionBars;
		}
	}

	public TreeViewContentProvider getContentProvider() {
		return contentProvider;
	}

	public TreeViewerRefreshable getTreeViewer() {
		return treeViewer;
	}

	public TreeViewCodeConverter getTreeViewCodeConverter() {
		return treeViewCodeConverter;
	}

	public Composite getContentComposite() {
		return contentComposite;
	}

	//#end region

}
