package org.treez.core.atom.variablelist;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.variablefield.VariableField;
import org.treez.core.attribute.Consumer;

/**
 * Allows to edit a list of VariableFields with a combo box for each value. The paths of the variables are shown in a
 * first column. A second column is used to show additional information for the variable (for example its current
 * value).
 */
public class VariableListWithInfo extends VariableList {

	//#region CONSTRUCTORS

	public VariableListWithInfo(String name) {
		super(name);

	}

	public VariableListWithInfo(String name, List<VariableField<?, ?>> availableVariables) {
		super(name);
		createTreezList(availableVariables);
	}

	/**
	 * Copy constructor
	 */
	protected VariableListWithInfo(VariableListWithInfo atomToCopy) {
		super(atomToCopy);
	}

	//#end region

	//#region METHODS

	@Override
	public VariableListWithInfo copy() {
		return new VariableListWithInfo(this);
	}

	@Override
	public AbstractAttributeAtom<VariableList, List<VariableField<?, ?>>> createAttributeAtomControl(
			Composite parent,
			FocusChangingRefreshable treeViewerRefreshable) {
		treezList.enableInfoColumn();
		treezList.setShowHeaders(true);
		super.createAttributeAtomControl(parent, treeViewerRefreshable);

		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public VariableListWithInfo addModificationConsumer(String key, Consumer consumer) {
		super.addModificationConsumer(key, consumer);
		treezList.addModificationConsumer(key, consumer);
		return this;
	}

	//#end region

	//#region ACCESSORS

	//#region Variable Information

	public void setVariableInfo(String variableName, String info) {
		treezList.setItemInfo(variableName, info);
	}

	//#end region

	//#end region

}
