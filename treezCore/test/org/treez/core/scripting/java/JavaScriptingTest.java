package org.treez.core.scripting.java;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.testutils.TestUtils;

/**
 * Tests the scripting with java
 */
public class JavaScriptingTest {

	/**
	 * Logger for this class
	 */
	private static final Logger LOG = LogManager.getLogger(JavaScriptingTest.class);

	//#region ATTRIBUTES

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
		//not used here
	}

	//#end region

	//#region TESTS

	/**
	 * Tests the application of the scripting with a root atom
	 */
	@Test
	public void testCompiler() {

		//Here we specify the source code of the class to be compiled
		StringBuilder src = new StringBuilder();
		src.append("package org.treez.views.scripting.java;\n");
		src.append("import org.treez.views.treeView.rootAtom.*;\n");
		src.append("public class DynaClass extends ModelProvider {\n");
		src.append("    public Root createModel() {\n");
		src.append("        Root root = new Root(\"root\");\n");
		src.append("        return root;\n");
		src.append("    }\n");
		src.append("}\n");
		String code = src.toString();

		JavaScripting javaScripting = new JavaScripting();

		javaScripting.execute(code);

		AbstractAtom<?> root = javaScripting.getRoot();

		LOG.debug(root.getName());

	}

}
