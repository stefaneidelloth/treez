package org.treez.core.atom.attribute.attributeContainer.section;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.attributeContainer.AbstractAttributeContainerAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.TreeViewerAction;

/**
 * Represents a single action that can be performed from within a section
 */
public class SectionAction extends AbstractAttributeContainerAtom<SectionAction> {

	private static final Logger LOG = Logger.getLogger(SectionAction.class);

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "")
	private String description;

	/**
	 * A runnable that performs the actual action
	 */
	private Runnable runnable;

	/**
	 * The image for the action button
	 */
	private Image image;

	//#end region

	//#region CONSTRUCTORS

	public SectionAction(String name) {
		super(name);
		this.name = name;
		this.description = "";
		this.runnable = new Runnable() {

			@Override
			public void run() {
				LOG.debug("example action");
			}
		};
		this.image = Activator.getImage("run.png");
	}

	public SectionAction(String name, String description) {
		super(name);
		this.name = name;
		this.description = description;
		this.runnable = new Runnable() {

			@Override
			public void run() {
				LOG.debug("example action with tooltip");
			}
		};
		this.image = Activator.getImage("run.png");
	}

	public SectionAction(String name, String description, Runnable runnable) {
		super(name);
		this.name = name;
		this.description = description;
		this.runnable = runnable;
		this.image = Activator.getImage("run.png");
	}

	public SectionAction(String name, String description, Runnable runnable, Image image) {
		super(name);
		this.name = name;
		this.description = description;
		this.runnable = runnable;
		this.image = image;
	}

	/**
	 * Copy constructor
	 */
	private SectionAction(SectionAction sectionActionToCopy) {
		super(sectionActionToCopy);
		this.name = sectionActionToCopy.name;
		this.description = sectionActionToCopy.description;
		this.runnable = sectionActionToCopy.runnable;
		this.image = sectionActionToCopy.image;
	}

	//#end region

	//#region METHODS

	@Override
	public SectionAction getThis() {
		return this;
	}

	@Override
	public SectionAction copy() {
		return new SectionAction(this);
	}

	@Override
	public Image provideImage() {
		return image;
	}

	@Override
	protected ArrayList<Object> createContextMenuActions(final TreeViewerRefreshable treeViewer) {
		ArrayList<Object> actions = new ArrayList<>();

		//delete
		actions.add(new TreeViewerAction(
				"Delete",
				Activator.getImage(ISharedImages.IMG_TOOL_DELETE),
				treeViewer,
				() -> createTreeNodeAdaption().delete()));

		return actions;
	}

	@Override
	public void createAtomControl(Composite sectionClient, FocusChangingRefreshable treeViewerRefreshable) {

		//toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		//action button
		org.eclipse.ui.forms.widgets.Section section = (org.eclipse.ui.forms.widgets.Section) sectionClient.getParent();
		Composite toolbar = (Composite) section.getTextClient();

		//check if action already exist in tool bar
		boolean alreadyExists = checkIfActionAlreadyExists(toolbar);
		if (!alreadyExists) {

			ImageHyperlink actionLink = toolkit.createImageHyperlink(toolbar, SWT.NULL);
			actionLink.setData(getName());
			actionLink.setImage(provideImage());
			actionLink.setToolTipText(getDescription());
			actionLink.addHyperlinkListener(getHyperlinkAdapter());
			actionLink.setBackground(section.getTitleBarGradientBackground());
		}

	}

	private boolean checkIfActionAlreadyExists(Composite toolbar) {
		String name = getName();
		Control[] children = toolbar.getChildren();
		for (Control child : children) {
			boolean isImageHyperLink = child instanceof ImageHyperlink;
			if (isImageHyperLink) {
				ImageHyperlink existingLink = (ImageHyperlink) child;
				Object data = existingLink.getData();
				if (data != null) {
					String existingName = data.toString();
					if (existingName.equals(name)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Creates the button action
	 *
	 * @return
	 */
	public IHyperlinkListener getHyperlinkAdapter() {
		return new HyperlinkAdapter() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void linkActivated(HyperlinkEvent e) {
				//LOG.debug("section action");
				runnable.run();
			}

		};
	}

	//#end region

	//#region ACCESSORS

	public String getDescription() {
		return description;
	}

	public SectionAction setDescription(String description) {
		this.description = description;
		return getThis();
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public SectionAction setEnabled(boolean enable) {
		throw new IllegalStateException("not yet implemented");
	}

	//#end region

}
