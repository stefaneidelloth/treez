package org.treez.core.atom.attribute.attributeContainer.section;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.treez.core.AbstractActivator;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.attribute.attributeContainer.AbstractAttributeContainerAtom;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;

public class SectionControlProvider {

	private static final Logger LOG = Logger.getLogger(SectionControlProvider.class);

	//#region ATTRIBUTES

	private Section section;

	private Composite parentComposite;

	private FocusChangingRefreshable treeViewerRefreshable;

	private org.eclipse.ui.forms.widgets.Section sectionComposite;

	//#end region

	//#region CONSTRUCTORS

	public SectionControlProvider(
			Section section,
			Composite parentComposite,
			FocusChangingRefreshable treeViewerRefreshable) {
		this.section = section;
		this.parentComposite = parentComposite;
		this.treeViewerRefreshable = treeViewerRefreshable;
	}

	//#end region

	//#region METHODS

	public void createAtomControl() {

		//create toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		//get default expansion state
		boolean isExpanded = section.isExpanded();
		//LOG.debug("create section. expanded: " + isExpanded);

		//define section style
		int sectionStyle;
		if (isExpanded) {
			sectionStyle = ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR;
		} else {
			sectionStyle = ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR;
		}

		boolean descriptionIsEmpty = section.getDescription().isEmpty();
		if (!descriptionIsEmpty) {
			sectionStyle = sectionStyle | org.eclipse.ui.forms.widgets.Section.DESCRIPTION;
		}

		//create section composite
		sectionComposite = toolkit.createSection(parentComposite, sectionStyle);

		//set section title
		sectionComposite.setText(section.getTitle());

		//section description
		sectionComposite.setDescription(section.getDescription());

		//register help id
		String absoluteHelpId = section.getAbsoluteHelpId();
		AbstractActivator.registerAbsoluteHelpId(absoluteHelpId, sectionComposite);

		//set layout data for the section composite to horizontally expand the
		//section to its parent width
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, false);

		sectionComposite.setLayoutData(gridData);

		//create section tool bar (some property child atoms might add actions to this
		//tool bar)
		createSectionToolbar(toolkit);

		//create section client and set its layout direction
		Composite contentComposite = toolkit.createComposite(sectionComposite);

		//GridData contentGridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		//contentComposite.setLayoutData(contentGridData);

		sectionComposite.setClient(contentComposite);

		//enabled state
		setEnabled(section.isEnabled());

		//add expansion listener to collapsed sections
		//that will create the section control when a collapsed section is
		//expanded
		sectionComposite.addExpansionListener(new IExpansionListener() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void expansionStateChanging(ExpansionEvent e) {
				//create section control
				createSectionContent(contentComposite);
			}

			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				boolean isExpanded = section.isExpanded();
				section.setExpanded(!isExpanded);
			}

		});

		//create control of expanded sections directly
		if (isExpanded) {
			createSectionContent(contentComposite);
		}

	}

	private void createSectionToolbar(FormToolkit toolkit) {
		Composite toolbar = toolkit.createComposite(sectionComposite);
		FillLayout toolbarLayout = new FillLayout();
		toolbar.setLayout(toolbarLayout);
		toolbar.setBackground(sectionComposite.getTitleBarGradientBackground());
		sectionComposite.setTextClient(toolbar);

		//add spacer to tool bar
		Label toolbarSpacer = toolkit.createLabel(toolbar, "  ");
		toolbarSpacer.setBackground(sectionComposite.getTitleBarGradientBackground());

		//add help button to tool bar
		ImageHyperlink helpToolBarLink = toolkit.createImageHyperlink(toolbar, SWT.NULL);
		//info.setText("go");
		helpToolBarLink.setImage(Activator.getImage("help.png"));
		helpToolBarLink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {

				IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
				String absoluteHelpId = section.getAbsoluteHelpId();
				if (helpSystem != null) {
					helpSystem.displayHelp(absoluteHelpId);
				}

			}
		});
		helpToolBarLink.setBackground(sectionComposite.getTitleBarGradientBackground());
		helpToolBarLink.setToolTipText("Show help.");
	}

	/**
	 * Creates the content of the section. The functionality is extracted to this method to be able to do lazy creation.
	 *
	 * @param sectionContentComposite
	 */
	@SuppressWarnings({ "checkstyle:illegalcatch", "checkstyle:magicnumber" })
	private void createSectionContent(Composite sectionContentComposite) {

		//remove old content
		for (Control child : sectionContentComposite.getChildren()) {
			child.dispose();
		}

		//set layout
		if (section.getLayout().equals("VERTICAL")) {

			GridLayout gridLayout = new GridLayout();
			gridLayout.verticalSpacing = 5;
			gridLayout.horizontalSpacing = 0;
			gridLayout.marginTop = -5;
			gridLayout.marginLeft = -5;
			gridLayout.marginRight = -5;

			sectionContentComposite.setLayout(gridLayout);
		} else {
			FillLayout fillLayout = new FillLayout();
			fillLayout.type = SWT.HORIZONTAL;
			sectionContentComposite.setLayoutData(fillLayout);
		}

		//create child composites from attribute atoms and attribute container atoms
		List<TreeNodeAdaption> childNodes = section.createTreeNodeAdaption().getChildren();
		for (TreeNodeAdaption childNode : childNodes) {

			AbstractAttributeAtom<?, ?> attributeAtom = null;
			try {
				attributeAtom = (AbstractAttributeAtom<?, ?>) childNode.getAdaptable();
			} catch (ClassCastException exception) {
				AbstractAttributeContainerAtom<?> attributeContainerAtom = null;
				try {
					attributeContainerAtom = (AbstractAttributeContainerAtom<?>) childNode.getAdaptable();

				} catch (Exception secondException) {
					LOG.error("Could not create attribute atom.", secondException);
					throw exception;
				}
				attributeContainerAtom.createAtomControl(sectionContentComposite, treeViewerRefreshable);
				continue;
			}

			attributeAtom.createAttributeAtomControl(sectionContentComposite, treeViewerRefreshable);
		}

		sectionContentComposite.layout(true, true);
	}

	private static boolean isAvailable(Control control) {
		if (control == null) {
			return false;
		}
		return !control.isDisposed();
	}

	//#end region

	//#region ATTRIBUTES

	public void setEnabled(boolean enable) {
		if (isAvailable(sectionComposite)) {
			sectionComposite.setEnabled(enable);
			if (enable) {
				sectionComposite.setExpanded(section.isExpanded());
			} else {
				sectionComposite.setExpanded(false);
			}
		}
	}

	//#end region

}
