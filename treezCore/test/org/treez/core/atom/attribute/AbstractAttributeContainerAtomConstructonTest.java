package org.treez.core.atom.attribute;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.GraphicsAdaption;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.attribute.base.parent.AbstractAttributeContainerAtom;
import org.treez.core.scripting.ScriptType;
import org.treez.testutils.PreviewWindow;
import org.treez.testutils.TestUtils;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public abstract class AbstractAttributeContainerAtomConstructonTest {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(AbstractAttributeContainerAtomConstructonTest.class);

	//#region ATTRIBUTES

	/**
	 * The atom to test
	 */
	protected AbstractAttributeContainerAtom atom;

	/**
	 * The name of atom to test
	 */
	protected String atomName = "testAtom";

	/**
	 * A shell that can be used as parent composite
	 */
	protected Shell shellComposite;

	//#end region

	//#region SETUP

	/**
	 * Setup.
	 */
	@BeforeClass
	private static void setUpClass() {
		TestUtils.initializeLogging();
	}

	@Before
	protected void setUpShellComposite() {
		//define a shell as composite parent
		Display display = Display.getCurrent();
		shellComposite = new Shell(display);
		shellComposite.setLayout(new GridLayout());
		createTestAtom();
	}

	/**
	 * Creates the atom
	 */
	protected abstract void createTestAtom();

	//#end region

	//#region TESTS

	/**
	 * Tests the construction of the atom
	 */
	@Test
	@SuppressWarnings("checkstyle:magicnumber")
	private void testConstructionOfTestAtom() {

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
		AbstractControlAdaption controlAdaption = atom.createControlAdaption(controlComposite, null);

		//get and show AttributeAtom control
		Composite parameterControlComposite = previewWindow.getAttributeControlComposite();
		atom.createAtomControl(parameterControlComposite, null);

		//get and show CAD adaption
		Composite graphicsComposite = previewWindow.getGraphicsComposite();
		GraphicsAdaption graphicsAdaption = atom.createGraphicsAdaption(graphicsComposite);

		//get and show code
		CodeAdaption codeAdaption = atom.createCodeAdaption(ScriptType.JAVA);
		previewWindow.setCode(codeAdaption.buildCodeContainer(null, null).buildCode());

		//get tree node adaption
		TreeNodeAdaption treeNodeAdaption = atom.createTreeNodeAdaption();

		//check obtained objects

		checkOptainedObjects(name, atomImage, controlAdaption, graphicsAdaption, codeAdaption, treeNodeAdaption);

		//show preview
		previewWindow.showUntilManuallyClosed();

	}

	private void checkOptainedObjects(
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
		assertEquals("adaptable", atom, codeAdaption.buildCodeContainer(null, null).buildCode());
		String code = codeAdaption.buildCodeContainer(null, null).buildCode();
		sysLog.info("Test Atom Code:\n" + code);

		//tree node adaption
		assertEquals("adaptable", atom, treeNodeAdaption.getAdaptable());
		List<TreeNodeAdaption> children = treeNodeAdaption.getChildren();
		assertEquals("number of children", 0, children.size());
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

}
