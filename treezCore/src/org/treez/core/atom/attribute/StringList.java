package org.treez.core.atom.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.atom.list.TreezListAtom;
import org.treez.core.atom.list.TreezListAtomControlAdaption;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.row.Row;
import org.treez.core.swt.CustomLabel;

/**
 * Allows to edit a list of strings
 */
public class StringList extends AbstractAttributeAtom<StringList, List<String>> {

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "Values:")
	private String label;

	@IsParameter(defaultValue = "a,b")
	private String defaultValueString;

	private CustomLabel labelComposite;

	/**
	 * The wrapped treez list atom
	 */
	protected TreezListAtom treezList;

	/**
	 * The control adaption of the treez list atom
	 */
	private TreezListAtomControlAdaption treezListControlAdaption;

	/**
	 * The parent composite for the list
	 */
	private Composite listContainerComposite;

	//#end region

	//#region CONSTRUCTORS

	public StringList(String name) {
		super(name);
		label = name;
		createTreezList();
	}

	/**
	 * Copy constructor
	 */
	protected StringList(StringList atomToCopy) {
		super(atomToCopy);
		label = atomToCopy.label;
		treezList = atomToCopy.treezList;

	}

	//#end region

	//#region METHODS

	@Override
	public StringList getThis() {
		return this;
	}

	/**
	 * Creates a treez list that contains Strings/text
	 */
	protected void createTreezList() {
		treezList = new TreezListAtom("treezList");
		treezList.setColumnType(ColumnType.STRING);
		treezList.setShowHeader(false);
	}

	@Override
	public StringList copy() {
		return new StringList(this);
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("column.png");
	}

	@Override
	public AbstractAttributeAtom<StringList, List<String>> createAttributeAtomControl(
			Composite parent,
			FocusChangingRefreshable treeViewerRefreshable) {

		//initialize value at the first call
		if (!isInitialized()) {
			setValue(defaultValueString);
		}

		//create toolkit
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		//create content composite for label and list
		Composite contentContainer = toolkit.createComposite(parent);
		createLayoutForTwoLines(contentContainer);

		//create label
		labelComposite = new CustomLabel(toolkit, contentContainer, label);
		final int prefferedLabelWidth = 80;
		labelComposite.setPrefferedWidth(prefferedLabelWidth);

		//create parent composite for treez list
		listContainerComposite = toolkit.createComposite(contentContainer);
		GridData fillData = new GridData(GridData.FILL, GridData.FILL, true, true);
		listContainerComposite.setLayoutData(fillData);

		//create treez list control
		createTreezListControl();

		return this;
	}

	/**
	 * Creates the control for the treezList by calling the corresponding method of the wrapped TreezListAtom
	 */
	private void createTreezListControl() {
		treezListControlAdaption = (TreezListAtomControlAdaption) treezList
				.createControlAdaption(listContainerComposite, treeViewRefreshable);
	}

	/**
	 * Creates a container layout where the label and the check box are put in individual lines
	 *
	 * @param contentContainer
	 */
	private static void createLayoutForTwoLines(Composite contentContainer) {

		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 2;
		gridLayout.marginWidth = 2;
		contentContainer.setLayout(gridLayout);

		GridData fillData = new GridData();
		fillData.grabExcessHorizontalSpace = true;
		fillData.horizontalAlignment = GridData.FILL;
		fillData.grabExcessVerticalSpace = true;
		fillData.verticalAlignment = GridData.FILL;

		contentContainer.setLayoutData(fillData);

	}

	@Override
	public void refreshAttributeAtomControl() {
		if (treezList != null) {
			List<String> values = get();
			List<Row> rows = new ArrayList<>();
			for (String value : values) {
				Row newRow = new Row(treezList);
				newRow.setEntry(treezList.getHeader(), value);
				rows.add(newRow);
			}
			treezList.setRows(rows);

		}

		if (treezListControlAdaption != null) {
			createTreezListControl();
		}
	}

	/**
	 * Splits the given valueString with "," and returns the individual values as a String list
	 *
	 * @param valueString
	 * @return
	 */
	private static List<String> valueStringToList(String valueString) {
		String[] individualValues = valueString.split(",");
		List<String> stringValues = Arrays.asList(individualValues);
		return stringValues;
	}

	@Override
	public StringList setBackgroundColor(org.eclipse.swt.graphics.Color backgroundColor) {
		throw new IllegalStateException("Not yet implemented");

	}

	//#end region

	//#region ACCESSORS

	//#region LABEL

	public String getLabel() {
		return label;
	}

	public StringList setLabel(String label) {
		this.label = label;
		return getThis();
	}

	//#end region

	//#region VALUE

	/**
	 * Sets the list with a given comma separated value string
	 *
	 * @param valueString
	 */
	public StringList setValue(String valueString) {
		List<String> stringValues = valueStringToList(valueString);
		set(stringValues);
		return getThis();
	}

	//#end region

	//#region DEFAULT VALUE

	@Override
	public List<String> getDefaultValue() {
		List<String> stringValues = valueStringToList(defaultValueString);
		return stringValues;
	}

	public StringList setDefaultValue(String defaultValueString) {
		this.defaultValueString = defaultValueString;
		return getThis();
	}

	//#end region

	//#end region

}
