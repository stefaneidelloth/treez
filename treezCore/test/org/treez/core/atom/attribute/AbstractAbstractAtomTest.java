package org.treez.core.atom.attribute;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.CodeContainer;
import org.treez.core.adaptable.GraphicsAdaption;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.scripting.ScriptType;
import org.treez.testutils.PreviewWindow;
import org.treez.testutils.TestUtils;

/**
 * Parent class for tests that test atoms which derive from AbstractAtoms
 */
public abstract class AbstractAbstractAtomTest {

	private static final Logger LOG = LogManager.getLogger(AbstractAbstractAtomTest.class);

	//#region ATTRIBUTES

	/**
	 * The atom to test
	 */
	protected AbstractAtom<?> atom;

	/**
	 * The name of atom to test
	 */
	protected String atomName = "testAtom";

	//#end region

	//#region METHODS

	protected Boolean isShowingPreviewWindow() {
		return false;
	}

	//#region SETUP

	@BeforeClass
	public static void setUpClass() {
		TestUtils.initializeLogging();
	}

	/**
	 * Prepare each individual test
	 */
	@Before
	public abstract void createTestAtom();

	//#end region

	//#region TESTS

	/**
	 * Tests the construction of the atom
	 */
	@Test
	public void testConstructionOfTestAtom() {

		//get preview window
		PreviewWindow previewWindow = TestUtils.getPreviewWindow();

		//get name
		String name = atom.getName();

		//get and show image
		Image atomImage = atom.provideImage();
		previewWindow.setImage(atomImage);

		//get and show control
		Composite controlComposite = previewWindow.getControlComposite();
		AbstractControlAdaption controlAdaption = atom.createControlAdaption(controlComposite, null);

		//get and show graphics
		Composite graphicsComposite = previewWindow.getGraphicsComposite();
		GraphicsAdaption graphicsAdaption = atom.createGraphicsAdaption(graphicsComposite);

		//get and show code
		CodeAdaption codeAdaption = atom.createCodeAdaption(ScriptType.JAVA);
		CodeContainer rootContainer = new CodeContainer(ScriptType.JAVA);
		Optional<CodeContainer> injectedChildContainer = Optional.ofNullable(null);
		previewWindow.setCode(codeAdaption.buildCodeContainer(rootContainer, injectedChildContainer).buildCode());

		//get tree node adaption
		TreeNodeAdaption treeNodeAdaption = atom.createTreeNodeAdaption();

		//check obtained objects
		checkOptainedObjects(name, atomImage, controlAdaption, graphicsAdaption, codeAdaption, treeNodeAdaption);

		if (isShowingPreviewWindow()) {
			previewWindow.showUntilManuallyClosed();
		}

	}

	protected void checkOptainedObjects(
			String name,
			Image atomImage,
			AbstractControlAdaption controlAdaption,
			GraphicsAdaption graphicsAdaption,
			CodeAdaption codeAdaption,
			TreeNodeAdaption treeNodeAdaption) {
		//name
		assertEquals("name", atomName, name);

		//control adaption
		assertEquals("adaptable", atom, controlAdaption.getAdaptable());

		//code adaption
		assertEquals("adaptable", atom, codeAdaption.getAdaptable());

		CodeContainer rootContainer = new CodeContainer(ScriptType.JAVA);
		Optional<CodeContainer> injectedChildContainer = Optional.ofNullable(null);
		String code = codeAdaption.buildCodeContainer(rootContainer, injectedChildContainer).buildCode();
		LOG.info("Test Atom Code:\n" + code);

		//tree node adaption
		assertEquals("adaptable", atom, treeNodeAdaption.getAdaptable());
		List<TreeNodeAdaption> children = treeNodeAdaption.getChildren();

		//assertEquals("number of children", 0, children.size());

		TreeNodeAdaption parent = treeNodeAdaption.getParent();
		assertEquals("parent", null, parent);
		Image nodeImage = treeNodeAdaption.getImage();
		assertEquals("image", atomImage.getBackground(), nodeImage.getBackground());
		assertEquals("label", atomName, treeNodeAdaption.getLabel());
		assertEquals("tree path", atomName, treeNodeAdaption.getTreePath());

		//graphics adaption
		assertEquals("adaptable", atom, graphicsAdaption.getAdaptable());
		final double tolerance = 1e-6;
		assertEquals("x coordinate", 0, graphicsAdaption.getX(), tolerance);
	}

	//#end region

	//#end region

}
