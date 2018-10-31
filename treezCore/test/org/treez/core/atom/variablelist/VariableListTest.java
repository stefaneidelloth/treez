package org.treez.core.atom.variablelist;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.treez.core.atom.attribute.AbstractAttributeAtomConstructionTest;
import org.treez.core.atom.variablefield.BooleanVariableField;
import org.treez.core.atom.variablefield.StringVariableField;
import org.treez.core.atom.variablefield.VariableField;
import org.treez.testutils.PreviewWindow;

/**
 * Tests the variable list
 */
public class VariableListTest extends AbstractAttributeAtomConstructionTest<List<VariableField<?, ?>>> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(VariableListTest.class);

	//#region SETUP

	@Override
	@Before
	public void createTestAtom() {

		//create some variable fields
		List<VariableField<?, ?>> variables = new ArrayList<>();

		VariableField<?, ?> firstField = new BooleanVariableField("myFlag");
		variables.add(firstField);

		VariableField<?, ?> secondField = new StringVariableField("myString");
		variables.add(secondField);

		//create test atom
		VariableList variableList = new VariableList(atomName);
		variableList.setLabel("Variables");
		variableList.setAvailableVariables(variables);

		atom = variableList;
	}

	@Override
	protected void performAdditionalTests(PreviewWindow previewWindow) {

		previewWindow.setPostClosingHook(() -> {

			VariableList variableList = (VariableList) atom;
			@SuppressWarnings("unused")
			List<VariableField<?, ?>> variables = variableList.get();

			//note: here you can set a break point to check if the selected variable fields
			//are correctly returned

			LOG.info("finished");
		});

	}

	//#end region

}
