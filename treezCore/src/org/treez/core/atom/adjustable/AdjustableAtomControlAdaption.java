package org.treez.core.atom.adjustable;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.base.AbstractAtom;

/**
 * The control adaption for the adjustable atom. The control will be build from
 * the underlying tree model of the AdjustableAtom.
 */
public class AdjustableAtomControlAdaption extends AbstractControlAdaption {

	//#region CONSTRUCTORS

	public AdjustableAtomControlAdaption(Composite parent,
			final AdjustableAtom adjustableAtom,
			FocusChangingRefreshable treeViewerRefreshable) {
		super(parent, adjustableAtom);

		AbstractAtom model = adjustableAtom.getModel();
		if (model != null) {
			createPropertyControlFromModel(parent, model,
					treeViewerRefreshable);
		} else {
			throw new IllegalStateException(
					"Could not create control because the underlying model could not be initialized.");
		}

	}

	//#end region

	//#region METHODS

	/**
	 * Creates the composites of the property view
	 *
	 * @param parent
	 * @param model
	 */
	private static void createPropertyControlFromModel(Composite parent,
			AbstractAtom model, FocusChangingRefreshable treeViewerRefreshable) {

		//delete old contents
		for (Control child : parent.getChildren()) {
			child.dispose();
		}

		//set parent layout
		parent.setLayout(new FillLayout());

		//get the display and some colors from the parent composite
		Display display = parent.getDisplay();
		Color colorBackgroundInactive = display
				.getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT);
		Color colorBackgroundActive = display
				.getSystemColor(SWT.COLOR_TITLE_BACKGROUND);

		//create tab folder
		CTabFolder tabFolder = createTabbedFolder(parent,
				colorBackgroundInactive, colorBackgroundActive);

		//create tabs on the tab folder
		createTabbedPages(model, tabFolder, treeViewerRefreshable);

		//select first tab
		tabFolder.layout();
		tabFolder.setFocus(); //is required for the set selection to send selection chaged events
		tabFolder.setSelection(0);

	}

	/**
	 * Creates a tab folder
	 *
	 * @param parent
	 * @param colorBackgroundInactive
	 * @param colorBackgroundActive
	 * @return
	 */
	private static CTabFolder createTabbedFolder(Composite parent,
			Color colorBackgroundInactive, Color colorBackgroundActive) {

		CTabFolder tabFolder = new CTabFolder(parent, SWT.TOP);
		tabFolder.setSimple(false);

		Color[] backgroundColors = new Color[]{colorBackgroundInactive,
				colorBackgroundActive};
		final int percentage = 100;
		int[] percents = new int[]{percentage};
		boolean vertical = true;
		tabFolder.setBackground(backgroundColors, percents, vertical);

		return tabFolder;
	}

	/**
	 * Creates tab pages from the model of the AdjustableAtom. The AttributeAtom
	 * Page might contain further children that are then used to create the
	 * control for the page and so on. The pages are lazily created when a tab
	 * is selected
	 *
	 * @param model
	 * @param tabFolder
	 */
	private static void createTabbedPages(AbstractAtom model,
			CTabFolder tabFolder, FocusChangingRefreshable treeViewerRefreshable) {
		List<TreeNodeAdaption> pageNodes = model.createTreeNodeAdaption()
				.getChildren();

		for (TreeNodeAdaption pageNode : pageNodes) {

			//test the type
			String type = pageNode.getAdaptable().getClass().getSimpleName();
			String pageType = Page.class.getSimpleName();
			boolean isPage = type.equals(pageType);
			if (!isPage) {
				String message = "The type of the first children of an AdjustableAtom has to be "
						+ pageType + " and not '" + type + "'.";
				throw new IllegalArgumentException(message);
			}

			//get page from pageNode
			Page page = (Page) pageNode.getAdaptable();

			//create page control
			page.createAtomControl(tabFolder, treeViewerRefreshable);

		}

	}

	//#end region

}
