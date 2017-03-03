package org.treez.core.atom.adjustable;

import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAbstractAtomTest;
import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.attribute.attributeContainer.Page;
import org.treez.core.atom.attribute.attributeContainer.section.Section;
import org.treez.core.atom.attribute.text.TextField;
import org.treez.core.atom.base.AbstractAtom;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class AdjustableAtomTest extends AbstractAbstractAtomTest {

	/**
	 * Load entities from the database.
	 */
	@Override
	@Before
	public void createTestAtom() {

		//create model for adjustable atom
		AbstractAtom<?> model = new AttributeRoot("root");
		Page page = new Page("properties");
		model.addChild(page);
		Section section = new Section("section");
		page.addChild(section);
		TextField text = new TextField("text");
		text.setDefaultValue("default_text");
		section.addChild(text);

		//create test atom
		//AbstractAdjustableAtom<?> adjustableAtom = new AbstractAdjustableAtom(atomName);
		//adjustableAtom.setModel(model);
		//adjustableAtom.setRunnable();
		//atom = adjustableAtom;

	}

}
