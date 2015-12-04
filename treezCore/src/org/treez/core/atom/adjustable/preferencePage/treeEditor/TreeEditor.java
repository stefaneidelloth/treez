package org.treez.core.atom.adjustable.preferencePage.treeEditor;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.adjustable.preferencePage.treeEditor.node.NodeComparer;
import org.treez.core.atom.adjustable.preferencePage.treeEditor.node.NodeEditingSupport;
import org.treez.core.atom.adjustable.preferencePage.treeEditor.node.NodeLabelProvider;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.scripting.ScriptType;
import org.treez.core.scripting.java.JavaScripting;
import org.treez.core.treeview.TreeViewContentProvider;
import org.treez.core.treeview.TreeViewerRefreshable;

/**
 * A field editor for the model tree of an AdjustableAtom. The underlying model tree of an AdjustableAtom is build from
 * AttributeAtoms, see package org.treez.core.atom.attribute
 */
public class TreeEditor extends FieldEditor {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(TreeEditor.class);

	//#region ATTRIBUTES

	private FormToolkit toolkit;

	private String preferenceValue;

	private TreeViewerRefreshable treeViewer;

	/**
	 * Composite for the property region of the tree editor
	 */
	private Composite propertyPanel;

	private AbstractAtom invisibleRoot;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param preferenceName
	 *            : name of the string header in which all table data is stored
	 * @param labelText
	 * @param parent
	 */
	public TreeEditor(String preferenceName, String labelText, Composite parent) {
		super(preferenceName, labelText, parent);

		sysLog.debug("constructor");
	}

	//#end region

	//#region METHODS

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {

		//sysLog.debug("do fill into grid");

		//get display and some value information
		Display display = parent.getDisplay();

		//create toolkit
		this.toolkit = new FormToolkit(display);

		//create scrollable form
		ScrolledForm form = createScrollableForm(parent);

		//set the layout of the scrolled content
		FillLayout formLayout = new FillLayout();
		formLayout.type = SWT.HORIZONTAL;
		form.getBody().setLayout(formLayout);

		//create Sections
		createTreeSection(form);
		createAttributeSection(form);
	}

	/**
	 * Creates a form that can be scrolled
	 *
	 * @param parent
	 * @return
	 */
	private ScrolledForm createScrollableForm(Composite parent) {
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setLayout(new GridLayout());
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		form.setLayoutData(gridData);
		return form;
	}

	/**
	 * Creates the section that shows the attributes
	 *
	 * @param form
	 */
	private void createAttributeSection(ScrolledForm form) {

		//create section
		Section propertySection = toolkit.createSection(form.getBody(), Section.DESCRIPTION
				| ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR);
		propertySection.setLayout(new FillLayout());
		propertySection.setText("Model Element Details");
		propertySection.setDescription("Set the attributes of the selected model elements.");

		//create stacked property panel
		Composite stackedPropertyPanel = toolkit.createComposite(propertySection);
		propertySection.setClient(stackedPropertyPanel);
		StackLayout stackedPropertyPanelLayout = new StackLayout();
		stackedPropertyPanel.setLayout(stackedPropertyPanelLayout);

		//create property panel
		propertyPanel = toolkit.createComposite(stackedPropertyPanel);
		propertyPanel.setLayout(new GridLayout());

		//create some initial content
		toolkit.createLabel(propertyPanel, "\n\nPlease select a model element to show its details.");

		//show property panel
		stackedPropertyPanelLayout.topControl = propertyPanel;

	}

	/**
	 * Creates the section that shows the model tree
	 *
	 * @param form
	 */
	private void createTreeSection(ScrolledForm form) {
		//create section
		Section treeSection = toolkit.createSection(form.getBody(), Section.DESCRIPTION | ExpandableComposite.EXPANDED
				| ExpandableComposite.TITLE_BAR);
		treeSection.setText("Model Tree");
		treeSection.setLayout(new FillLayout());
		Composite treePanel = toolkit.createComposite(treeSection);
		treePanel.setLayout(new FillLayout());
		treeSection.setClient(treePanel);

		//create tree
		treeViewer = new TreeViewerRefreshable(treePanel, null, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		treeViewer.setComparer(new NodeComparer());

		//set content provider
		TreeViewContentProvider contentProvider = new TreeViewContentProvider(null);
		treeViewer.setContentProvider(contentProvider);

		//create root node and set input
		invisibleRoot = (AbstractAtom) contentProvider.getInvisibleRoot();
		AttributeRoot root = new AttributeRoot("root");
		invisibleRoot.addChild(root);
		treeViewer.setInput(invisibleRoot);

		//set column for tree viewer
		TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.NONE);
		column.setLabelProvider(new NodeLabelProvider());
		column.setEditingSupport(new NodeEditingSupport(treeViewer));
		final int columnWidth = 300;
		column.getColumn().setWidth(columnWidth);

		//create tree actions
		@SuppressWarnings("unused")
		TreeActionProvider actionProvider = new TreeActionProvider(this);

	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
		//not used here
	}

	@Override
	protected void doLoad() {
		sysLog.debug("doLoad");
		preferenceValue = getPreferenceStore().getString(getPreferenceName());
		loadTreeData();
	}

	@Override
	protected void doLoadDefault() {
		sysLog.debug("doLoadDefault");
		preferenceValue = getPreferenceStore().getDefaultString(getPreferenceName());
		loadTreeData();
	}

	@Override
	protected void doStore() {
		//get root
		invisibleRoot = (AbstractAtom) treeViewer.getInput();
		List<TreeNodeAdaption> treeNodeAdaptions = invisibleRoot.createTreeNodeAdaption().getChildren();
		AttributeRoot root = (AttributeRoot) treeNodeAdaptions.get(0).getAdaptable();

		//save expansion state
		String expandedNodes = "";
		Object[] expandedObjects = treeViewer.getExpandedElements();
		for (Object object : expandedObjects) {
			String path = ((Adaptable) object).createTreeNodeAdaption().getTreePath();
			expandedNodes = expandedNodes + path + ",";
		}
		if (expandedNodes.length() > 1) {
			expandedNodes = expandedNodes.substring(0, expandedNodes.length() - 1); //remove last ","
		}

		root.setExpandedNodes(expandedNodes);

		//generate code and save it in preference store
		CodeAdaption codeAdaption = root.createCodeAdaption(ScriptType.JAVA);
		String className = "PreferenceModel";
		preferenceValue = codeAdaption.buildRootCodeContainer(className).buildCode();

		getPreferenceStore().setValue(getPreferenceName(), preferenceValue);
		sysLog.debug("doStore:\n" + preferenceValue);
	}

	@Override
	public int getNumberOfControls() {
		return 1;
	}

	/**
	 * Splits the current preference string into data entries and builds the tree model
	 */
	private void loadTreeData() {

		sysLog.debug("load table data");

		if (preferenceValue != null) {

			String errorCode = "";
			JavaScripting scripting = new JavaScripting();
			scripting.execute(preferenceValue);

			if (errorCode.isEmpty()) {
				AbstractAtom newInvisibleRoot = new AttributeRoot("invisibleRoot");
				AttributeRoot root = (AttributeRoot) scripting.getRoot();
				if (root != null) {
					newInvisibleRoot.addChild(root);
					updateTree(newInvisibleRoot);
					setExpansionState(root.getExpandedNodes());
				} else {
					throw new IllegalStateException("Could not get root.");
				}
			}
		}
	}

	/**
	 * Expands the nodes with the given names
	 *
	 * @param expandedNodes
	 */
	private void setExpansionState(ArrayList<String> expandedNodes) {

		if (!expandedNodes.isEmpty()) {
			TreeItem[] treeItems = treeViewer.getTree().getItems();
			for (TreeItem treeItem : treeItems) {
				expandNodes(treeItem, expandedNodes);
			}
		}
	}

	/**
	 * Expands the nodes with the given names, starting at the given treeItem.
	 *
	 * @param treeItem
	 * @param expandedNodes
	 */
	private void expandNodes(TreeItem treeItem, ArrayList<String> expandedNodes) {
		Adaptable adaptable = (Adaptable) treeItem.getData();
		if (adaptable != null) {
			String path = adaptable.createTreeNodeAdaption().getTreePath();

			sysLog.debug("path: " + path);
			if (expandedNodes.contains(path)) {

				treeItem.setExpanded(true);
				treeViewer.refresh();
				sysLog.debug("expanded" + path);
			}
			for (TreeItem child : treeItem.getItems()) {
				expandNodes(child, expandedNodes);
			}
		}
	}

	/**
	 * Updates the tree with a given root element. It tries to keep the expansion state of the tree.
	 *
	 * @param invisibleRoot
	 */
	public void updateTree(Adaptable invisibleRoot) {

		//save expansion state
		Object[] expandedObjects = treeViewer.getExpandedElements();

		//set new content
		treeViewer.setInput(invisibleRoot);

		//try to restore expansion state
		treeViewer.setExpandedElements(expandedObjects);
	}

	//#end region

	//#region ACCESSORS

	/**
	 * Returns the tree viewer
	 *
	 * @return
	 */
	public TreeViewerRefreshable getTreeViewer() {
		return treeViewer;
	}

	/**
	 * Returns the invisible root
	 *
	 * @return
	 */
	public Adaptable getInvisibleRoot() {
		return invisibleRoot;
	}

	/**
	 * Returns the property panel
	 *
	 * @return
	 */
	public Composite getPropertyPanel() {
		return propertyPanel;
	}

	//#end region

}
