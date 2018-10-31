package org.treez.core.path;

import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.attribute.attributeContainer.Page;
import org.treez.core.atom.attribute.attributeContainer.section.Section;
import org.treez.core.atom.attribute.text.TextField;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.testutils.TestUtils;

/**
 * Tests the validation of a model path.
 */
public class ModelPathValidatorTest {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(ModelPathValidatorTest.class);

	//#region ATTRIBUTES

	/**
	 * The model that is used for the tests
	 */
	private AbstractAtom<?> model;

	/**
	 * The name of the model
	 */
	private String atomName = "model";

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
		model = new AttributeRoot(atomName);

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

		//define path to test
		String modelPath = "model.page.section.text";

		//validate path
		boolean pathIsValid = ModelPathValidator.isValidModelPath(modelPath, model);

		//check result
		assertEquals("Path is valid", true, pathIsValid);

	}

	/**
	 * Tests the validation of a wrong model paths
	 */
	@Test
	public void testValidationOfWrongModelPaths() {

		//define path to test with wrong name of model
		String modelPath = "modelGG.page.section.text";

		//validate path
		boolean pathIsValid = ModelPathValidator.isValidModelPath(modelPath, model);

		//check result
		assertEquals("Path is valid", false, pathIsValid);

		//define path to test with wrong name of target
		modelPath = "modelGG.page.section.textGG";

		//validate path
		pathIsValid = ModelPathValidator.isValidModelPath(modelPath, model);

		//check result
		assertEquals("Path is valid", false, pathIsValid);

	}

	//#end region

}
