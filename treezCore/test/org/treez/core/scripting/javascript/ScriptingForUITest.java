package org.treez.core.scripting.javascript;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.data.table.TreezTable;
import org.treez.testutils.PreviewWindow;
import org.treez.testutils.TestUtils;

/**
 * Tests the Abstract Atom by creating a simple test implementation TestAtom.
 */
public class ScriptingForUITest {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(ScriptingForUITest.class);

	//#region ATTRIBUTES

	/**
	 * The ScriptingForUI to test
	 */
	private JavaScriptScripting scripting;

	/**
	 * Preview window
	 */
	private PreviewWindow previewWindow;

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

		//get preview window
		previewWindow = TestUtils.getPreviewWindow();

		//create scripting
		scripting = new JavaScriptScripting();

	}

	//#end region

	//#region TESTS

	/**
	 * Tests the application of the scripting with a root atom
	 */
	@Test
	public void testScriptingWithRoot() {

		//define some test java script
		String code = "var imports = new JavaImporter(" + "    org.treez.views.treeView.rootAtom" + ");\n" + ""
				+ "with (imports) {	\n" + "    root = new Root('root');	\n" + "} ";

		//execute java script
		scripting.execute(code);

		//retrieve root atom from scripting
		AbstractAtom root = scripting.getRoot();

		//show root atom on preview window
		Composite controlComposite = previewWindow.getControlComposite();
		root.createControlAdaption(controlComposite, null);

		//show preview
		//previewWindow.showUntilManuallyClosted();

	}

	/**
	 * Tests the application of the scripting with a table
	 */
	@Test
	public void testScriptingWithTable() {

		//define some test java script
		String code = "var imports = new JavaImporter(" + "    org.treez.views.treeView.rootAtom,"
				+ "    org.treez.data.table," + "    org.treez.data.column" + ");\n" + "" + "with (imports) {	\n"
				+ "    root = new Root('root');	\n" + "    table = new Table('table');	\n"
				+ "    root.addChild(table);	\n" + "    column = new Column('id');	\n"
				+ "    table.addColumn(column);	\n" + "} ";

		//execute java script
		scripting.execute(code);

		//retrieve root atom from scripting
		AbstractAtom root = scripting.getRoot();

		//retrive table from root
		TreezTable table = (TreezTable) root.getChild("table");

		//show root atom on preview window
		Composite controlComposite = previewWindow.getControlComposite();
		table.createControlAdaption(controlComposite, null);

		//show preview
		previewWindow.showUntilManuallyClosed();

	}

	//#end region

}
