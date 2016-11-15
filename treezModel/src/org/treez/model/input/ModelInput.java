package org.treez.model.input;

import java.util.List;

import org.treez.core.atom.copy.Copiable;

/**
 * Represents the input for an executable model.
 */
public interface ModelInput extends Copiable<ModelInput> {

	/**
	 * The Id of the study this input belongs to
	 */
	String getStudyId();

	/**
	 * The description of the study this input belongs to
	 */
	String getStudyDescription();

	/**
	 * Returns an id with up to 64 characters that can be used to identify this ModelInput in a list of all model inputs
	 * that belong to a parent study. (If the Quantities are chosen randomly in a study, all Quantities of two
	 * ModelInputs might be the same. Nevertheless, the Ids should be different.)
	 *
	 * @return
	 */
	String getJobId();

	/**
	 * Increases the Id to the next available value. You might want to use this after copying a ModelInput.
	 */
	void increaseJobId();

	/**
	 * Returns the model path of the parent Study this ModelInput belongs to, e.g root.studies.mySweep
	 *
	 * @return
	 */
	String getParentStudyModelPath();

	/**
	 * Returns the value for the variable with the given model path
	 *
	 * @param variableModelPath
	 *            the model path of the variable for which the Quantity is requested, e.g.
	 *            root.models.myModel.myVariable
	 * @return
	 */
	java.lang.Object getVariableValue(String variableModelPath);

	/**
	 * Returns the model paths of all variables for which a Quantity is defined in this ModelInput
	 *
	 * @return
	 */
	List<String> getAllVariableModelPaths();

	/**
	 * Returns true if this ModelInput specifies a Quantity for the variable with the given model path
	 *
	 * @param variableModelPath
	 * @return
	 */
	boolean containsVariableModelPath(String variableModelPath);

	/**
	 * Adds an additional entry to this model input
	 *
	 * @param variableModelPath
	 * @param variableValue
	 */
	void add(String variableModelPath, java.lang.Object variableValue);

	/**
	 * Returns a deep copy of the model input
	 *
	 * @return
	 */
	@Override
	ModelInput copy();

	/**
	 * Returns a display string with a length of up to 64 characters that can be used for example in table columns,
	 * plots or legends to represent this ModelInput.
	 *
	 * @return
	 */
	default String toDisplayString() {
		return getJobId();
	}

}
