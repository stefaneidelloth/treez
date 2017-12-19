package org.treez.core.atom.base;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.GraphicsAdaption;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.scripting.ScriptType;
import org.treez.testutils.PreviewWindow;
import org.treez.testutils.TestUtils;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom. The shown shell will be empty.
 */
public class AbstractAtomTest {

	/**
	 * Logger for this class
	 */
	private static final Logger LOG = Logger.getLogger(AbstractAtomTest.class);

	//#region ATTRIBUTES

	/**
	 * The TestAtom to test
	 */
	private TestAtom testAtom;

	/**
	 * The name of the test atom
	 */
	private String atomName = "testAtom";

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

		//create test atom
		testAtom = new TestAtom(atomName);
	}

	//#end region

	//#region TESTS

	/**
	 * Tests the usage of abstract atom by constructing a test atom
	 */
	@Test
	@SuppressWarnings("checkstyle:magicnumber")
	public void testConstructionOfTestAtom() {

		//get preview window
		PreviewWindow previewWindow = TestUtils.getPreviewWindow();

		//get name
		String name = testAtom.getName();

		//get image and show it
		Image atomImage = testAtom.provideImage();
		previewWindow.setImage(atomImage);

		//get control adaption and show it
		Composite controlComposite = previewWindow.getControlComposite();
		AbstractControlAdaption controlAdaption = testAtom.createControlAdaption(controlComposite, null);

		//get graphics adaption and show it
		Composite graphicsComposite = previewWindow.getGraphicsComposite();
		GraphicsAdaption graphicsAdaption = testAtom.createGraphicsAdaption(graphicsComposite);

		//get code adaption and show it
		CodeAdaption codeAdaption = testAtom.createCodeAdaption(ScriptType.JAVA);
		previewWindow.setCode(codeAdaption.buildCodeContainer(null, null).buildCode());

		//get tree node adaption
		TreeNodeAdaption treeNodeAdaption = testAtom.createTreeNodeAdaption();

		//check obtained objects

		//name
		assertEquals("name", atomName, name);

		//control adaption
		assertEquals("adaptable", testAtom, controlAdaption.getAdaptable());

		//code adaption
		assertEquals("adaptable", testAtom, codeAdaption.getAdaptable());
		String code = codeAdaption.buildCodeContainer(null, null).buildCode();
		LOG.debug("Test Atom Code:\n" + code);

		//tree node adaption
		assertEquals("adaptable", testAtom, treeNodeAdaption.getAdaptable());
		List<TreeNodeAdaption> children = treeNodeAdaption.getChildren();
		assertEquals("number of children", 0, children.size());
		TreeNodeAdaption parent = treeNodeAdaption.getParent();
		assertEquals("parent", null, parent);

		//Image nodeImage = treeNodeAdaption.getImage();
		//assertEquals("image", atomImage, nodeImage);

		assertEquals("label", atomName, treeNodeAdaption.getLabel());
		assertEquals("tree path", atomName, treeNodeAdaption.getTreePath());

		//graphics adaption
		assertEquals("adaptable", testAtom, graphicsAdaption.getAdaptable());
		assertEquals("x coordinate", 0, graphicsAdaption.getX(), 1e-6);

		previewWindow.showUntilManuallyClosed();

	}

	/**
	 *
	 */
	@Test
	public void testAddChildAtom() {

		//create two additional atoms and add them as children
		String childName = "childAtom";
		TestAtom expectedChildAtom = new TestAtom(childName);

		String grandChildName = "grandChildAtom";
		TestAtom expectedGrandChildAtom = new TestAtom(grandChildName);
		expectedChildAtom.addChild(expectedGrandChildAtom);

		testAtom.addChild(expectedChildAtom);

		//test if child can be obtained through the tree node adaption
		List<TreeNodeAdaption> children = testAtom.createTreeNodeAdaption().getChildren();
		TreeNodeAdaption childNode = children.get(0);
		TestAtom childAtom = (TestAtom) childNode.getAdaptable();
		assertEquals("child", expectedChildAtom, childAtom);

		//test if child can be obtained with its path
		String childPath = childName;
		TestAtom childAtomFromPath = (TestAtom) testAtom.getChild(childPath);
		assertEquals("child", expectedChildAtom, childAtomFromPath);

		//test if grand child can be obtained with its path
		String grandChildPath = childName + "." + grandChildName;
		TestAtom grandChildAtom = (TestAtom) testAtom.getChild(grandChildPath);
		assertEquals("child", expectedGrandChildAtom, grandChildAtom);

	}

	//#end region

}
