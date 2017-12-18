package org.treez.core.treeview;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.attribute.attributeContainer.Page;
import org.treez.core.atom.attribute.attributeContainer.section.Section;
import org.treez.core.atom.attribute.text.TextField;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.testutils.PreviewWindow;
import org.treez.testutils.TestUtils;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class TreeViewProviderTest {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(TreeViewProviderTest.class);

	//#region ATTRIBUTES

	/**
	 * The tree view provider to test
	 */
	private TreeViewProvider treeViewProvider;

	//#end region

	//#region SETUP

	/**
	 * Setup.
	 */
	@BeforeClass
	public static void setUpClass() {
		TestUtils.initializeLogging();
	}

	/**
	 * Load entities from the database.
	 */
	@Before
	public void setUp() {
		//
	}

	//#end region

	//#region TESTS

	/**
	 * Tests the construction of a tree view using the TreeViewProvider
	 */
	@Test
	public void testConstructionOfTestAtom() {

		//get preview window
		PreviewWindow previewWindow = TestUtils.getPreviewWindow();

		//create tree view provider
		treeViewProvider = new TreeViewProvider(previewWindow);

		//provide the tree view
		treeViewProvider.provideTreeView(null, null, null);

		//set example content
		AbstractAtom<?> invisibleRoot = new AttributeRoot("invisibleRoot");

		AttributeRoot root = new AttributeRoot("root");
		invisibleRoot.addChild(root);

		Page page = new Page("page");
		root.addChild(page);

		Section section = new Section("section");
		page.addChild(section);

		TextField textField = new TextField("textField");
		section.addChild(textField);

		treeViewProvider.updateTreeContent(invisibleRoot);

		//show preview
		previewWindow.showUntilManuallyClosed();

	}

	//#end region

}
