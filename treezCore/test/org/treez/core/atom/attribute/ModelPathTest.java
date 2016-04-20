package org.treez.core.atom.attribute;

import org.apache.log4j.Logger;
import org.junit.Before;

/**
 * Tests the selection of a model path with the AttributeAtom ModelPath.
 */
public class ModelPathTest extends AbstractAttributeAtomConstructionTest<String> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(ModelPathTest.class);

	//#region ATTRIBUTES

	/**
	 * The root of the model in which the ModelPath atom is located and from which the paths can be selected
	 */
	private AttributeRoot root;

	//#end region

	//#region SETUP

	/**
	 * Create test atom
	 */
	@Override
	@Before
	public void createTestAtom() {

		//crate root atom
		root = new AttributeRoot("root");

		//create test atom
		ModelPath modelPath = new ModelPath(atomName);
		modelPath.setLabel("My model path:");
		modelPath.setTooltip("My model path tooltip");
		modelPath.setSelectionType(ModelPathSelectionType.FLAT);
		modelPath.setDefaultValue("root." + atomName);
		root.addChild(modelPath);

		//create further children to have some more
		//paths available to select
		Page page = new Page("page");
		root.addChild(page);

		Section section = new Section("section");
		page.addChild(section);

	}

	//#end region

	//#region TESTS

	//#end region

}
