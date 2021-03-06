package org.treez.core.atom.attribute.attributeContainer;

import java.util.ArrayList;
import java.util.List;

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
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.attribute.attributeContainer.section.Section;
import org.treez.core.atom.base.AtomTreeNodeAdaption;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.TreeViewerAction;
import org.treez.core.utils.Utils;

public class Page extends AbstractAttributeContainerAtom<Page> {

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "Page Title")
	private String title;

	@IsParameter(defaultValue = "VERTICAL", comboItems = { "VERTICAL", "HORIZONTAL" })
	private String layout;

	//#end region

	//#region CONSTRUCTORS

	public Page(String name) {
		super(name);
		title = Utils.firstToUpperCase(name); //this default title might be overridden by explicitly setting the label
	}

	/**
	 * Copy constructor
	 */
	public Page(Page pageToCopy) {
		super(pageToCopy);
		title = pageToCopy.title;
		layout = pageToCopy.layout;
	}

	//#end region

	//#region METHODS

	@Override
	public Page getThis() {
		return this;
	}

	@Override
	public Page copy() {
		return new Page(this);
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("Page.png");
	}

	@Override
	protected ArrayList<Object> createContextMenuActions(final TreeViewerRefreshable treeViewer) {
		ArrayList<Object> actions = new ArrayList<>();

		//add
		actions.add(new TreeViewerAction(
				"Add Section",
				Activator.getImage("Section.png"),
				treeViewer,
				() -> addSection(treeViewer)));

		//delete
		actions.add(new TreeViewerAction(
				"Delete",
				Activator.getImage(ISharedImages.IMG_TOOL_DELETE),
				treeViewer,
				() -> createTreeNodeAdaption().delete()));

		return actions;
	}

	//#region CONTROL

	@Override
	public void createAtomControl(Composite tabFolderComposite, FocusChangingRefreshable treeViewerRefreshable) {

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
	 * Creates the page content. The functionality is extracted to this method to be able to do a lazy creation.
	 *
	 * @param parent
	 */
	private void createPageContent(Composite parent, FocusChangingRefreshable treeViewerRefreshable) {

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
		List<TreeNodeAdaption> sectionNodes = createTreeNodeAdaption().getChildren();
		for (TreeNodeAdaption sectionNode : sectionNodes) {

			String type = sectionNode.getAdaptable().getClass().getSimpleName();
			String sectionType = Section.class.getSimpleName();
			boolean isSection = type.equalsIgnoreCase(sectionType);
			if (isSection) {

				//get section atom
				org.treez.core.atom.attribute.attributeContainer.section.Section section = //
						(org.treez.core.atom.attribute.attributeContainer.section.Section) sectionNode.getAdaptable();

				//create section control
				section.createAtomControl(parent, treeViewerRefreshable);

			} else {
				String message = "The children of a Page have to be of type Section and not '" + type + "'";
				throw new IllegalArgumentException(message);
			}
		}
		parent.layout(true, true);

		//reactivate drawing
		parent.setRedraw(true);
	}

	void addSection(TreeViewerRefreshable treeViewer) {
		String name = AtomTreeNodeAdaption.createChildNameStartingWith(this, "mySection");
		createSection(name);
		createTreeNodeAdaption().expand(treeViewer);
	}

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

	public String getTitle() {
		return title;
	}

	public Page setTitle(String title) {
		this.title = title;
		return getThis();
	}

	public String getLayout() {
		return layout;
	}

	public Page setLayout(String layout) {
		this.layout = layout;
		return getThis();
	}

	//#end region

}
