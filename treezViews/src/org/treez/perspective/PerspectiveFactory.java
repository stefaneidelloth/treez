package org.treez.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.treez.views.graphics.GraphicsViewPart;
import org.treez.views.monitor.MonitorViewPart;
import org.treez.views.properties.PropertyViewPart;
import org.treez.views.tree.TreeViewPart;

/**
 * Factory for the Treez perspective (is referenced from corresponding extension point)
 */
public class PerspectiveFactory implements IPerspectiveFactory {

	//#region ATTRIBUTES

	private static final String TREEZ_TREE_VIEW_ID = TreeViewPart.ID;

	private static final String TREEZ_PROPERTIES_VIEW_ID = PropertyViewPart.ID;

	private static final String TREEZ_GRAPHICS_VIEW_ID = GraphicsViewPart.ID;

	private static final String TREEZ_MONITOR_VIEW_ID = MonitorViewPart.ID;

	private static final String PROJECT_EXPLORER_ID = IPageLayout.ID_PROJECT_EXPLORER;

	//#end region

	//#region METHODS

	@SuppressWarnings("checkstyle:illegalcatch")
	@Override
	public void createInitialLayout(IPageLayout layout) {

		//set general layout properties
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);

		//get id of editor area
		String editorAreaId = layout.getEditorArea();

		//create tab folder left to editor area
		final float widthRatioTreezMiddleTabFolderToEditor = 0.5f;
		IFolderLayout middleFolderLayout = layout.createFolder("org.treez.views.middleTabFolder", IPageLayout.LEFT,
				widthRatioTreezMiddleTabFolderToEditor, editorAreaId);

		//put property view on middle tab folder
		middleFolderLayout.addView(TREEZ_PROPERTIES_VIEW_ID);

		//add graphics view as tab on editor area
		middleFolderLayout.addView(TREEZ_GRAPHICS_VIEW_ID);

		//add monitor view as tab on editor area
		middleFolderLayout.addView(TREEZ_MONITOR_VIEW_ID);

		//create tab folder left to middle tab folder
		final float widthRatioTreezTabFolderToProertyView = 0.4f;
		IFolderLayout leftFolderLayout = layout.createFolder("org.treez.views.leftTabFolder", IPageLayout.LEFT,
				widthRatioTreezTabFolderToProertyView, TREEZ_PROPERTIES_VIEW_ID);

		//put tree view on left tab folder
		leftFolderLayout.addView(TREEZ_TREE_VIEW_ID);

		//put project explorer on tab folder
		leftFolderLayout.addView(PROJECT_EXPLORER_ID);

	}

	//#end region

}
