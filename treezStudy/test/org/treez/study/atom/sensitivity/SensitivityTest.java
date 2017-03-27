package org.treez.study.atom.sensitivity;

import org.eclipse.swt.graphics.Image;
import org.junit.Before;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.GraphicsAdaption;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.attribute.AbstractAbstractAtomTest;
import org.treez.testutils.TestRoot;

public class SensitivityTest extends AbstractAbstractAtomTest {

	@Override
	@Before
	public void createTestAtom() {
		TestRoot root = new TestRoot("root");

		Sensitivity sensitivity = new Sensitivity(atomName);
		root.addChild(sensitivity);

		atom = sensitivity;
	}

	@Override
	protected Boolean isShowingPreviewWindow() {
		return true;
	}

	@Override
	protected void checkOptainedObjects(
			String name,
			Image atomImage,
			AbstractControlAdaption controlAdaption,
			GraphicsAdaption graphicsAdaption,
			CodeAdaption codeAdaption,
			TreeNodeAdaption treeNodeAdaption) {

		return;

	}

}
