package org.treez.model.output;

import java.util.List;

import org.treez.model.input.ModelInput;

/**
 * Represents the output of a study.
 */
public interface StudyOutput {

	/**
	 * Returns the ModelOutput for the given ModelInput
	 *
	 * @return
	 */
	ModelOutput getModelOutput(ModelInput modelInput);

	/**
	 * Returns all ModelInputs that were processed for this StudyOutput
	 *
	 * @return
	 */
	List<ModelInput> getAllModelInputs();

	/**
	 * Returns true if this StudyOutput contains a ModelOutput for the given ModelInput
	 *
	 * @param modelInput
	 * @return
	 */
	boolean contains(ModelInput modelInput);

}
