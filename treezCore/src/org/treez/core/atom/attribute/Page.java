package org.treez.core.atom.attribute;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.treez.core.Activator;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.attribute.base.parent.AbstractAttributeContainerAtom;
import org.treez.core.atom.base.AtomTreeNodeAdaption;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.TreeViewerAction;
import org.treez.core.utils.Utils;

/**
 * An item example
 */
public class Page extends AbstractAttributeContainerAtom {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(Page.class);

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "Page Title")
	private String title;

	@IsParameter(defaultValue = "VERTICAL", comboItems = {"VERTICAL",
			"HORIZONTAL"})
	private String layout;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public Page(String name) {
		super(name);
		title = Utils.firstToUpperCase(name); //this default title might be overridden by explicitly setting the label
	}

	/**
	 * Copy constructor
	 *
	 * @param pageToCopy
	 */
	public Page(Page pageToCopy) {
		super(pageToCopy);
		title = pageToCopy.title;
		layout = pageToCopy.layout;
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public Page copy() {
		return new Page(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("Page.png");
	}

	/**
	 * Creates the context menu actions
	 *
	 * @return
	 */
	@Override
	protected ArrayList<Object> createContextMenuActions(
			final TreeViewerRefreshable treeViewer) {
		ArrayList<Object> actions = new ArrayList<>();

		//add
		actions.add(new TreeViewerAction("Add Section",
				Activator.getImage("Section.png"), treeViewer,
				() -> addSection(treeViewer)));

		//delete
		actions.add(new TreeViewerAction("Delete",
				Activator.getImage(ISharedImages.IMG_TOOL_DELETE), treeViewer,
				() -> createTreeNodeAdaption().delete()));

		return actions;
	}

	//#region CONTROL

	@Override
	public void createAtomControl(Composite tabFolderComposite,
			Refreshable treeViewerRefreshable) {

		//get toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		//get tab folder
		CTabFolder tabFolder = (CTabFolder) tabFolderComposite;

		//create new tabItem that corresponds to the pageNode
		CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);

		//set the title of the tab item
		String currentTitle = getTitle();
		tabItem.setText(currentTitle);

		//create scrolled content form
		ScrolledForm contentForm = toolkit.createScrolledForm(tabFolder);
		contentForm.setLayout(new GridLayout());
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		contentForm.setLayoutData(gridData);
		//tabFolder.setLayoutData(new GridLayout());

		tabItem.setControl(contentForm);

		//add selection listener hat will create the page when the
		//tab item is selected
		tabFolder.addSelectionListener(new SelectionListener() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void widgetSelected(SelectionEvent e) {
				//create page content
				createPageContent(contentForm.getBody(), treeViewerRefreshable);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//not used here
			}
		});

		//(comment this out for lazy creation)
		createPageContent(contentForm.getBody(), treeViewerRefreshable);

	}

	/**
	 * Creates the page content. The functionality is extracted to this method
	 * to be able to do a lazy creation.
	 *
	 * @param parent
	 */
	private void createPageContent(Composite parent,
			Refreshable treeViewerRefreshable) {

		//disable drawing to avoid flickering
		parent.setRedraw(false);

		//remove old content
		for (Control child : parent.getChildren()) {
			child.dispose();
		}

		//create layout
		String currentLayout = getLayout();
		if (currentLayout.equals("VERTICAL")) {
			GridLayout gridLayout = new GridLayout();
			parent.setLayout(gridLayout);
		} else {
			FillLayout fillLayout = new FillLayout();
			fillLayout.type = SWT.HORIZONTAL;
			parent.setLayoutData(fillLayout);
		}

		//create sections
		List<TreeNodeAdaption> sectionNodes = createTreeNodeAdaption()
				.getChildren();
		for (TreeNodeAdaption sectionNode : sectionNodes) {

			String type = sectionNode.getAdaptable().getClass().getSimpleName();
			String sectionType = Section.class.getSimpleName();
			boolean isSection = type.equalsIgnoreCase(sectionType);
			if (isSection) {

				//get section atom
				org.treez.core.atom.attribute.Section section = (org.treez.core.atom.attribute.Section) sectionNode
						.getAdaptable();

				//create section control
				section.createAtomControl(parent, treeViewerRefreshable);

			} else {
				String message = "The children of a Page have to be of type Section and not '"
						+ type + "'";
				throw new IllegalArgumentException(message);
			}
		}
		parent.layout(true, true);

		//reactivate drawing
		parent.setRedraw(true);
	}

	/**
	 * Adds a new section
	 *
	 * @param treeViewer
	 */
	void addSection(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this,
				"mySection");
		createSection(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

	/**
	 * Create a new section
	 *
	 * @param name
	 * @return
	 */
	public Section createSection(String name) {
		Section section = new Section(name);
		addChild(section);
		return section;
	}

	/**
	 * Create a new section with given title and helpId
	 *
	 * @param name
	 * @return
	 */
	public Section createSection(String name, String absoluteHelpId) {
		Section section = createSection(name);
		section.setAbsoluteHelpId(absoluteHelpId);
		return section;
	}

	/**
	 * Creates a new section with given title and expansion state
	 *
	 * @param name
	 * @param expanded
	 * @return
	 */
	public Section createSection(String name, boolean expanded) {
		Section section = createSection(name);
		section.setExpanded(expanded);
		return section;
	}

	//#end region

	/**
	 * Getter for title
	 *
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Setter for title
	 *
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Setter for layout
	 *
	 * @param layout
	 */
	public void setLayout(String layout) {
		this.layout = layout;
	}

	/**
	 * Getter for layout
	 *
	 * @return
	 */
	public String getLayout() {
		return layout;
	}

	//#end region

}
