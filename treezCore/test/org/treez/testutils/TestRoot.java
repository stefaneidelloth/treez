package org.treez.testutils;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.Activator;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.attribute.base.EmptyControlAdaption;
import org.treez.core.atom.base.RegionsAtomCodeAdaption;
import org.treez.core.scripting.ScriptType;

/**
 * A root atom for tests
 */
public class TestRoot extends AdjustableAtom {

	//#region CONSTRUCTORS

	public TestRoot(String name) {
		super(name);
		createEmptyModel();
	}

	/**
	 * Copy constructor
	 */
	private TestRoot(TestRoot rootToCopy) {
		super(rootToCopy);
		createRootModel();
	}

	//#end region

	//#region METHODS

	@Override
	public TestRoot copy() {
		return new TestRoot(this);
	}

	public void createRootModel() {
		AttributeRoot root = new AttributeRoot("root");
		root.createPage("");
		setModel(root);
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("root.png");
	}

	@Override
	public AbstractControlAdaption createControlAdaption(
			Composite parent,
			FocusChangingRefreshable treeViewRefreshable) {
		return new EmptyControlAdaption(parent, this, "");
	}

	@Override
	public CodeAdaption createCodeAdaption(ScriptType scriptType) {
		return new RegionsAtomCodeAdaption(this);
	}

	//#end region
}
