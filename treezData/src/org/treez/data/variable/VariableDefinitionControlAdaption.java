package org.treez.data.variable;

import org.eclipse.swt.widgets.Composite;
import org.treez.core.adaptable.Adaptable;
import org.treez.data.table.TableControlAdaption;

/**
 * control adaption for the variable definition atom
 */
public class VariableDefinitionControlAdaption extends TableControlAdaption {

	//#region ATTRIBUTES

	private VariableDefinition variableDefinition;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param parent
	 * @param variableDefinition
	 */
	public VariableDefinitionControlAdaption(Composite parent, VariableDefinition variableDefinition) {
		super(parent, variableDefinition.getTable());
		this.variableDefinition = variableDefinition;
	}

	//#end region

	//#region METHODS		

	@Override
	public Adaptable getAdaptable() {
		return variableDefinition;
	}

	//#end region

	//#region ACCESSORS

	//#end region

}
