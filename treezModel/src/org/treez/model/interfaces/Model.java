package org.treez.model.interfaces;

import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.monitor.ObservableMonitor;
import org.treez.model.input.ModelInput;
import org.treez.model.output.ModelOutput;

/**
 * Represents an executable model that can be "remotely" executed, e.g. by a sweep analysis. This Model might have some
 * static state (e.g. temporary files) that would interfere with other parallel running models of the same kind.
 * Therefore, this kind of model must be run one after another to ensure repeatable ModelOutputs.
 */
public interface Model {

	/**
	 * Runs this Model with the given modelInput. The given Refreshable might be refreshed if it is not null. The given
	 * TreezMonitor might be used to update the progress if it is not null.
	 *
	 * @returns an AbstractAtom<?> that represents the results
	 */
	ModelOutput runModel(ModelInput modelInput, FocusChangingRefreshable refreshable, ObservableMonitor monitor);

	/**
	 * Runs this model with the current model state. The given Refreshable might be refreshed if it is not null. The
	 * given TreezMonitor might be used to update the progress if it is not null.
	 */
	ModelOutput runModel(FocusChangingRefreshable refreshable, ObservableMonitor monitor);

	/**
	 * Returns true if this Model must be run manually. A manual Model can be executed directly but is not executed by
	 * its parent Model. It therefore does not contribute to the ModelOutput if the parent Model is executed in a Study.
	 * The purpose of a manual Model is to avoid too many empty ModelOutputs. A manual Model might for example be used
	 * passively to provide attributes for other Models in a Study.
	 */
	boolean isManualModel();

	/**
	 * A study may consist of several jobs, where a job corresponds to a single model run. This sets the id of the job
	 * for the current execution of the model.
	 */
	void setJobId(String jobId);

	/**
	 * The id for the last execution of the model. This might be the id from a ModelInput while executing a study (e.g.
	 * sweep). It might also be an id from a manual execution that has been set by the model itself.
	 */
	String getJobId();

	/**
	 * The optional id of the study this model is run for.
	 */
	void setStudyId(String studyId);

	String getStudyId();

	/**
	 * The optional id of the study this model is run for.
	 */
	void setStudyDescription(String studyId);

	String getStudyDescription();

}
