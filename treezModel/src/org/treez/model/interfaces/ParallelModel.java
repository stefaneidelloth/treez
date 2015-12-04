package org.treez.model.interfaces;

import org.eclipse.core.runtime.IProgressMonitor;
import org.treez.core.adaptable.Refreshable;
import org.treez.model.input.ModelInput;
import org.treez.model.output.ModelOutput;

/**
 * Represents an executable model that can be "remotely" executed, e.g. by a sweep analysis. Several models of this kind
 * can be run in parallel. In order to ensure repeatable ModelOutputs, this Model has to make sure that no internal
 * static state (e.g. temporary files) exists that could interfere with other model runs. In other words: each model has
 * to run completely independent from other models running at the same time. If a model implements ParallelModel, all
 * child models of that model must also implement ParallelModel.
 */
public interface ParallelModel extends Model {

	/**
	 * Returns an id that identifies the model instance. Can be used to distinguish it from other model instances of the
	 * same study that might run at the same time.
	 *
	 * @return
	 */
	String getModelId();

	/**
	 * Executes the model with the given ModelInput. Can be executed in parallel with other model instances.
	 *
	 * @param modelInput
	 * @returns
	 */
	@Override
	ModelOutput runModel(ModelInput modelInput, Refreshable refreshable, IProgressMonitor monitor);

	/**
	 * Runs the model with the current model state. Can be executed in parallel with other model instances.
	 */
	@Override
	ModelOutput runModel(Refreshable refreshable, IProgressMonitor monitor);

}
