package org.treez.core.path;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.attribute.TextField;
import org.treez.testutils.TestUtils;

/**
 * Tests the selection of a model path.
 */
public class ModelPathSelectorTest {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(ModelPathSelectorTest.class);

	//#region ATTRIBUTES

	/**
	 * The model that is used for the tests
	 */
	private AttributeRoot model;

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

		//create model
		model = new AttributeRoot("model");

		Page page = new Page("page");
		model.addChild(page);

		Section section = new Section("section");
		page.addChild(section);

		TextField text = new TextField("text");
		section.addChild(text);

	}

	//#end region

	//#region TESTS

	/**
	 * Tests the validation of a correct model path
	 */
	@Test
	public void testValidationOfCorrectModelPath() {

		//define default model path
		String defaultModelPath = "model.page.section";

		//select new path
		//String targetClassName = "org.treez.core.atom.base.AbstractAtom";
		String targetClassName = "org.treez.core.atom.attribute.Section";

		String newModelPath = ModelPathSelector.selectTreePath(model, targetClassName, defaultModelPath);

		sysLog.info("Selected model path: " + newModelPath);

	}

	//#end region

}
