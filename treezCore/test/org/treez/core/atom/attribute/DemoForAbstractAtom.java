package org.treez.core.atom.attribute;

import java.util.Optional;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.CodeContainer;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.scripting.ScriptType;
import org.treez.testutils.PreviewWindow;
import org.treez.testutils.TestUtils;

/**
 * Parent class for demo applications (that have a main method) for demonstration of atoms that derive from AbstractAtom
 */
public class DemoForAbstractAtom {

	private static final Logger LOG = LogManager.getLogger(DemoForAbstractAtom.class);

	//#region ATTRIBUTES

	protected AbstractAtom<?> atom;

	protected String atomName = "demoAtom";

	//#end region

	//#region CONSTRUCTORS

	public DemoForAbstractAtom() {
		TestUtils.initializeLogging();
		createDemoAtom();
		showDemoAtomInPreviewWindow();
	}

	//#end region

	//#region METHODS

	/**
	 * Has to be overridden
	 */
	protected void createDemoAtom() {

	}

	public void showDemoAtomInPreviewWindow() {

		if (atom == null) {
			LOG.error("You have to create an atom befor calling this method. Override 'createDemoAtom' ");
			return;
		}

		//get preview window
		PreviewWindow previewWindow = TestUtils.getPreviewWindow();

		//get and show image
		Image atomImage = atom.provideImage();
		previewWindow.setImage(atomImage);

		//get and show control
		Composite controlComposite = previewWindow.getControlComposite();
		atom.createControlAdaption(controlComposite, null);

		//get and show graphics
		Composite graphicsComposite = previewWindow.getGraphicsComposite();
		atom.createGraphicsAdaption(graphicsComposite);

		//get and show code
		CodeAdaption codeAdaption = atom.createCodeAdaption(ScriptType.JAVA);
		CodeContainer rootContainer = new CodeContainer(ScriptType.JAVA);
		Optional<CodeContainer> injectedChildContainer = Optional.ofNullable(null);
		previewWindow.setCode(codeAdaption.buildCodeContainer(rootContainer, injectedChildContainer).buildCode());

		//get tree node adaption
		atom.createTreeNodeAdaption();

		previewWindow.showUntilManuallyClosed();

	}

	//#end region

}
