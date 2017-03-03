package org.treez.core.atom.base;

import org.treez.core.atom.attribute.modelPath.ModelPathSelectionType;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.atom.uisynchronizing.AbstractUiSynchronizingAtom;

/**
 * A simple test implementation that extends AbstractAtom<?> to be used by AbstractAtomTest
 */
public class TestAtom extends AbstractUiSynchronizingAtom<TestAtom> {

	//#region ATTRIBUTES

	//check boxes

	@IsParameter(defaultValue = "true")
	private Boolean checkBox1;

	@IsParameter(defaultValue = "True")
	private Boolean checkBox2;

	@IsParameter(defaultValue = "TRUE")
	private Boolean checkBox3;

	@IsParameter(defaultValue = "false")
	private Boolean checkBox4;

	@IsParameter(defaultValue = "False")
	private Boolean checkBox5;

	@IsParameter(defaultValue = "FALSE")
	private Boolean checkBox6;

	//enum combo box
	@IsParameter(defaultValue = "TREE")
	private ModelPathSelectionType enumComboBox;

	//string combo box
	@IsParameter(defaultValue = "item2", comboItems = { "item1", "item2" })
	private String stringComboBox;

	//integer combo box
	@IsParameter(defaultValue = "2", comboItems = { "1", "2" })
	private Integer integerComboBox;

	//float combo box
	@IsParameter(defaultValue = "2.0", comboItems = { "1.0", "2.0" })
	private Float floatComboBox;

	//double combo box
	@IsParameter(defaultValue = "2.0", comboItems = { "1.0", "2.0" })
	private Double doubleComboBox;

	//text field
	@IsParameter(defaultValue = "defaultValue")
	private String textField;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public TestAtom(String name) {
		super(name);
	}

	@Override
	public TestAtom copy() {
		//not used here
		return null;
	}

	@Override
	protected TestAtom getThis() {
		return this;
	}

	//#end region

}
