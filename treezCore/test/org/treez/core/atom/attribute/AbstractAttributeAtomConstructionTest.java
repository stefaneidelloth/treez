package org.treez.core.atom.attribute;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
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
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.scripting.ScriptType;
import org.treez.testutils.PreviewWindow;
import org.treez.testutils.TestUtils;

/**
 * Parent class for testing atoms that derive from AbstractAtom.
 */
public abstract class AbstractAttributeAtomConstructionTest<T> {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger
			.getLogger(AbstractAttributeAtomConstructionTest.class);

	//#region ATTRIBUTES

	/**
	 * The atom to test
	 */
	protected AbstractAttributeAtom<T> atom;

	/**
	 * The name of atom to test
	 */
	protected String atomName = "testAtom";

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
	 * Creates the test atom
	 */
	@Before
	public abstract void createTestAtom();

	//#end region

	//#region TESTS

	/**
	 * Tests the construction of the atom
	 */
	@Test
	@SuppressWarnings("checkstyle:magicnumber")
	public void testConstructionOfTestAtom() {

		//get preview window
		PreviewWindow previewWindow = TestUtils.getPreviewWindow();

		//get and show name
		String name = atom.getName();
		previewWindow.setName(name);

		//get and show image
		Image atomImage = atom.provideImage();
		previewWindow.setImage(atomImage);

		//get ant show control
		Composite controlComposite = previewWindow.getControlComposite();
		AbstractControlAdaption controlAdaption = atom
				.createControlAdaption(controlComposite, null);

		//get and show AttributeAtom control
		Composite parameterControlComposite = previewWindow
				.getAttributeControlComposite();
		atom.createAttributeAtomControl(parameterControlComposite, null);

		//get and show CAD adaption
		Composite graphicsComposite = previewWindow.getGraphicsComposite();
		GraphicsAdaption graphicsAdaption = atom
				.createGraphicsAdaption(graphicsComposite);

		//get and show code
		CodeAdaption codeAdaption = atom.createCodeAdaption(ScriptType.JAVA);

		CodeContainer codeContainer = new CodeContainer(ScriptType.JAVA);
		Optional<CodeContainer> injectedChildContainer = Optional
				.ofNullable(null);

		String code = "#Error#";
		try {
			code = codeAdaption
					.buildCodeContainer(codeContainer, injectedChildContainer)
					.buildCode();
		} catch (IllegalStateException exception) {
			String message = "Could not build code";
			sysLog.error(message, exception);
		}
		previewWindow.setCode(code);

		//get tree node adaption
		TreeNodeAdaption treeNodeAdaption = atom.createTreeNodeAdaption();

		//perform additional tests of inheriting class
		performAdditionalTests(previewWindow);

		//check obtained objects
		checkOptainedObjects(name, atomImage, controlAdaption, graphicsAdaption,
				codeAdaption, treeNodeAdaption);

		//show preview window
		previewWindow.showUntilManuallyClosed();

		//note: after closing the preview window the post closing hook of the
		//preview window will be executed if it has been set

	}

	/**
	 * Might be overridden by inheriting classes
	 */
	@SuppressWarnings("unused")
	protected void performAdditionalTests(PreviewWindow previewWindow) {
		//empty implementation
	}

	private void checkOptainedObjects(String name, Image atomImage,
			AbstractControlAdaption controlAdaption,
			GraphicsAdaption graphicsAdaption, CodeAdaption codeAdaption,
			TreeNodeAdaption treeNodeAdaption) {

		//name
		assertEquals("name", atomName, name);

		//control adaption
		assertEquals("adaptable", atom, controlAdaption.getAdaptable());

		//code adaption
		assertEquals("adaptable", atom, codeAdaption.getAdaptable());

		CodeContainer codeContainer = new CodeContainer(ScriptType.JAVA);
		Optional<CodeContainer> injectedChildContainer = Optional
				.ofNullable(null);

		String code = "#Error#";
		try {
			code = codeAdaption
					.buildCodeContainer(codeContainer, injectedChildContainer)
					.buildCode();
		} catch (IllegalStateException exception) {
			String message = "Could not build code";
			sysLog.error(message, exception);
		}
		sysLog.info("Test Atom Code:\n" + code);

		//tree node adaption
		assertEquals("adaptable", atom, treeNodeAdaption.getAdaptable());
		List<TreeNodeAdaption> children = treeNodeAdaption.getChildren();
		assertEquals("number of children", 0, children.size());
		TreeNodeAdaption parent = treeNodeAdaption.getParent();
		assertEquals("parent", null, parent);
		Image nodeImage = treeNodeAdaption.getImage();
		assertEquals("image", atomImage.getBackground(),
				nodeImage.getBackground());
		assertEquals("label", atomName, treeNodeAdaption.getLabel());
		assertEquals("tree path", atomName, treeNodeAdaption.getTreePath());

		//graphics adaption
		assertEquals("adaptable", atom, graphicsAdaption.getAdaptable());
		final double tolerance = 1e-6;
		assertEquals("x coordinate", 0, graphicsAdaption.getX(), tolerance);

	}

	//#end region

}
