package org.treez.core.atom.attribute;

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
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.base.parent.AbstractAttributeContainerAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.TreeViewerAction;

/**
 * Represents a single action that can be performed from within a section
 */
public class SectionAction extends AbstractAttributeContainerAtom {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(SectionAction.class);

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

	private boolean controlHasBeenCreated;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public SectionAction(String name) {
		super(name);
		this.name = name;
		this.description = "";
		this.runnable = new Runnable() {

			@Override
			public void run() {
				sysLog.debug("example action");
			}
		};
		this.image = Activator.getImage("run.png");
	}

	/**
	 * Copy constructor
	 *
	 * @param sectionActionToCopy
	 */
	private SectionAction(SectionAction sectionActionToCopy) {
		super(sectionActionToCopy);
		this.name = sectionActionToCopy.name;
		this.description = sectionActionToCopy.description;
		this.runnable = sectionActionToCopy.runnable;
		this.image = sectionActionToCopy.image;
	}

	/**
	 * Constructor with description
	 *
	 * @param name
	 * @param description
	 */
	public SectionAction(String name, String description) {
		super(name);
		this.name = name;
		this.description = description;
		this.runnable = new Runnable() {

			@Override
			public void run() {
				sysLog.debug("example action with tooltip");
			}
		};
		this.image = Activator.getImage("run.png");
	}

	/**
	 * Constructor with description and runnable
	 *
	 * @param name
	 * @param description
	 * @param runnable
	 */
	public SectionAction(String name, String description, Runnable runnable) {
		super(name);
		this.name = name;
		this.description = description;
		this.runnable = runnable;
		this.image = Activator.getImage("run.png");
	}

	/**
	 * Constructor with description, runnable and custom image
	 *
	 * @param name
	 * @param description
	 * @param runnable
	 * @param image
	 */
	public SectionAction(String name, String description, Runnable runnable,
			Image image) {
		super(name);
		this.name = name;
		this.description = description;
		this.runnable = runnable;
		this.image = image;
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public SectionAction copy() {
		return new SectionAction(this);
	}

	//#end region

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return image;
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

		//delete
		actions.add(new TreeViewerAction("Delete",
				Activator.getImage(ISharedImages.IMG_TOOL_DELETE), treeViewer,
				() -> createTreeNodeAdaption().delete()));

		return actions;
	}

	@Override
	public void createAtomControl(Composite sectionClient,
			Refreshable treeViewerRefreshable) {

		//toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		//action button
		org.eclipse.ui.forms.widgets.Section section = (org.eclipse.ui.forms.widgets.Section) sectionClient
				.getParent();
		Composite toolbar = (Composite) section.getTextClient();

		//check if action already exist in tool bar
		boolean alreadyExists = checkIfActionAlreadyExists(toolbar);
		if (!alreadyExists) {

			ImageHyperlink actionLink = toolkit.createImageHyperlink(toolbar,
					SWT.NULL);
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
				//sysLog.debug("section action");
				runnable.run();
			}

		};
	}

	//#end region

	//#region ACCESSORS

	/**
	 * Get description
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set description
	 *
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	//#end region

}
