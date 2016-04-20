package org.treez.core.atom.variablelist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.atom.list.TreezListAtom;
import org.treez.core.atom.variablefield.VariableField;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.row.Row;
import org.treez.core.swt.CustomLabel;

/**
 * Allows to edit a list of VariableFields with a combo box for each value
 */
public class VariableList
		extends
			AbstractAttributeAtom<List<VariableField<?>>> {

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "Values:")
	private String label;

	@IsParameter(defaultValue = "")
	private String defaultValueString;

	private CustomLabel labelComposite;

	/**
	 * The wrapped treez list atom
	 */
	protected TreezListAtom treezList;

	/**
	 * The parent composite for the list
	 */
	private Composite listContainerComposite;

	/**
	 * Maps from variable name to VariableField
	 */
	private Map<String, VariableField<?>> availableVariables;

	//#end region

	//#region CONSTRUCTORS

	public VariableList(String name) {
		super(name);
		label = name;
		createTreezList(null);
	}

	public VariableList(String name,
			List<VariableField<?>> availableVariables) {
		super(name);
		label = name;
		createTreezList(availableVariables);
	}

	/**
	 * Copy constructor
	 */
	protected VariableList(VariableList atomToCopy) {
		super(atomToCopy);
		label = atomToCopy.label;
		treezList = atomToCopy.treezList;

	}

	//#end region

	//#region METHODS

	/**
	 * Creates a treez list that contains Strings/text
	 */
	protected void createTreezList(
			List<VariableField<?>> availableVariableFields) {
		treezList = new TreezListAtom("treezList");
		treezList.setColumnType(ColumnType.TEXT);

		setAvailableVariables(availableVariableFields);

		treezList.setShowHeader(false);
		treezList.setFirstRowAutoCreation(false);
	}

	@Override
	public VariableList copy() {
		return new VariableList(this);
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("column.png");
	}

	@Override
	public AbstractAttributeAtom<List<VariableField<?>>> createAttributeAtomControl(
			Composite parent, Refreshable treeViewerRefreshable) {

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
		GridData fillData = new GridData(GridData.FILL, GridData.FILL, true,
				true);
		listContainerComposite.setLayoutData(fillData);

		//create treez list control
		createTreezListControl();

		return this;
	}

	/**
	 * Creates the control for the treezList by calling the corresponding method
	 * of the wrapped TreezListAtom
	 */
	private void createTreezListControl() {
		treezList.createControlAdaption(listContainerComposite,
				treeViewRefreshable);
	}

	/**
	 * Creates a container layout where the label and the check box are put in
	 * individual lines
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
			List<VariableField<?>> variableFields = get();
			List<Row> rows = new ArrayList<>();
			for (VariableField<?> variableField : variableFields) {
				if (variableField != null) {
					Row newRow = new Row(treezList);
					String variableName = variableField.getName();
					newRow.setEntry(treezList.getHeader(), variableName);
					rows.add(newRow);
				}
			}
			treezList.setRows(rows);
		}
	}

	/**
	 * Splits the given valueString with ",", maps the variable names to
	 * VariableFields and returns them as a VariableField list
	 *
	 * @param valueString
	 * @return
	 */
	private List<VariableField<?>> valueStringToList(String valueString) {
		List<VariableField<?>> variableFields = new ArrayList<>();
		if (!valueString.isEmpty()) {
			String[] individualValues = valueString.split(",");
			for (String variableName : individualValues) {
				VariableField<?> variableField = availableVariables
						.get(variableName);
				variableFields.add(variableField);
			}
		}
		return variableFields;
	}

	@Override
	public void setBackgroundColor(
			org.eclipse.swt.graphics.Color backgroundColor) {
		throw new IllegalStateException("Not yet implemented");

	}

	//#end region

	//#region ACCESSORS

	//#region LABEL

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	//#end region

	//#region VALUE

	/**
	 * Sets the variable list with a given comma separated value string that
	 * contains the names of the variables
	 *
	 * @param valueString
	 */
	public void setValue(String valueString) {
		Objects.requireNonNull(availableVariables,
				"Available variables must be set before calling this method.");
		List<VariableField<?>> variableFields = valueStringToList(valueString);
		set(variableFields);
	}

	@Override
	public List<VariableField<?>> get() {
		Objects.requireNonNull(availableVariables,
				"Available variables must be set before calling this method.");
		if (isInitialized()) {
			String data = treezList.getData(",");
			List<VariableField<?>> variableFields = valueStringToList(data);
			return variableFields;
		} else {
			return getDefaultValue();
		}
	}

	//#end region

	//#region DEFAULT VALUE

	@Override
	public List<VariableField<?>> getDefaultValue() {
		List<VariableField<?>> stringValues = valueStringToList(
				defaultValueString);
		return stringValues;
	}

	public void setDefaultValue(String defaultValueString) {
		this.defaultValueString = defaultValueString;
	}

	//#end region

	//#region AVAILABLE VARIABLES

	/**
	 * Sets the available variables
	 *
	 * @param availableVariableFields
	 */
	public void setAvailableVariables(
			List<VariableField<?>> availableVariableFields) {

		//get list of previously selected variables
		List<VariableField<?>> oldFields = new ArrayList<>();
		if (availableVariables != null) {
			oldFields = get();
		}

		//get names and create a map from variable names to variable fields

		List<String> availableVariableNames = new ArrayList<>(); //keeps order of names (map wont do so)
		availableVariables = new HashMap<>();

		if (availableVariableFields != null) {
			for (VariableField<?> variableField : availableVariableFields) {
				String variableName = variableField.getName();
				availableVariableNames.add(variableName);
				availableVariables.put(variableName, variableField);
			}
		}

		//get single string that contains all available variable names
		String availableVariableNameString = String.join(",",
				availableVariableNames);

		//set available string items
		treezList.setAvailableStringItems(availableVariableNameString);

		//filter old non existing values
		List<VariableField<?>> newVariableFields = new ArrayList<>();
		for (VariableField<?> oldField : oldFields) {
			String oldName = oldField.getName();
			boolean variableExists = availableVariables.containsKey(oldName);
			if (variableExists) {
				newVariableFields.add(oldField);
			}
		}

		//update selected variable fields
		set(newVariableFields);

	}

	public void addVariable(VariableField<?> variableField) {
		String variableName = variableField.getName();
		treezList.addRow(variableName);

	}

	//#end region

	//#end region

}
