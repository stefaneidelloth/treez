package org.treez.model.atom;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.data.output.OutputAtom;
import org.treez.model.input.ModelInput;
import org.treez.model.interfaces.Model;
import org.treez.model.output.ModelOutput;

/**
 * Represents the root atom for all models
 */
public class AbstractModel extends AdjustableAtom implements Model {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(AbstractModel.class);

	//#region ATTRIBUTES

	/**
	 * Is true if the Model is a manual Model. This means that it is not remotely executed by a Study.
	 */
	private boolean isManualModel = false;

	/**
	 * The id for the last execution of the model. This might be the id from a model input while executing a study (e.g.
	 * sweep). It might also be an id from a manual execution that has been set by the model itself.
	 */
	protected String studyId = "1";

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public AbstractModel(String name) {
		super(name);
	}

	//#end region

	//#region METHODS

	/**
	 * Remotely runs the model with the given ModelInput
	 */
	@Override
	public ModelOutput runModel(ModelInput modelInput, Refreshable refreshable, IProgressMonitor monitor) {

		//assign the model input to variable values (also assigns model input for sub models)
		assignModelInput(modelInput);

		//run model
		ModelOutput modelOutput = runModel(refreshable, monitor);

		return modelOutput;
	}

	/**
	 * Assigns the given ModelInput to the corresponding variables of this model (and its sub models)
	 *
	 * @param modelInput
	 */
	protected void assignModelInput(ModelInput modelInput) {

		sysLog.info("Assigning model input for " + this.getClass().getSimpleName() + " '" + getName() + "'");

		if (modelInput != null) {

			//set study index
			String studyIndex = modelInput.getId();
			this.setStudyId(studyIndex);

			//set variable values
			List<String> allVariableModelPaths = modelInput.getAllVariableModelPaths();
			for (String variableModelPath : allVariableModelPaths) {
				Object quantityToAssign = modelInput.getVariableValue(variableModelPath);
				AbstractAttributeAtom<Object> variableAtom = getVariableAtom(variableModelPath);
				if (variableAtom != null) {
					variableAtom.set(quantityToAssign);
				} else {
					String message = "Could not get variable atom for model path " + variableModelPath;
					throw new IllegalStateException(message);
				}
			}
		}
	}

	/**
	 * Executes the model with the current state of its variables
	 */
	@Override
	public void execute(Refreshable refreshable) {
		runNonUiJob("AbstractModel: execute", (monitor) -> runModel(refreshable, monitor));
	}

	/**
	 * Remotely runs the model with the current model state. Should be overridden by models that have no sub models.
	 *
	 * @return
	 */
	@Override
	public ModelOutput runModel(Refreshable refreshable, IProgressMonitor monitor) {

		sysLog.info("Running " + this.getClass().getSimpleName() + " '" + getName() + "'");

		ModelOutput modelOutput = createEmptyModelOutput();
		for (AbstractAtom child : getChildAtoms()) {
			boolean isModel = child instanceof Model;
			if (isModel) {
				//get model
				Model childModel = (Model) child;
				boolean childIsManualModel = childModel.isManualModel();
				if (!childIsManualModel) {
					//run Model
					ModelOutput childModelOutput = childModel.runModel(refreshable, monitor);

					//add child model output to model output
					if (childModelOutput != null) {
						modelOutput.addChildOutput(childModelOutput);
					}
				}
			}
		}

		return modelOutput;
	}

	/**
	 * Creates an empty model output. It wraps a RootOutput that is used to organize the child model outputs in a tree
	 * structure;
	 *
	 * @return
	 */
	protected ModelOutput createEmptyModelOutput() {
		String rootOutputName = getName() + "Output";
		Image rootBaseImage = provideImage();
		AbstractAtom rootOutput = new OutputAtom(rootOutputName, rootBaseImage);
		ModelOutput modelOutput = () -> {
			//overrides getRootOutput
			return rootOutput;
		};
		return modelOutput;
	}

	/**
	 * Finds and returns the variable atom of type AttributeAtom<Quantity> for the given relative model path. Returns
	 * null if the variable model path could not be found or if the atom could not be casted to AttributeAtom<Quantity>.
	 *
	 * @param variableModelPath
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private AbstractAttributeAtom<Object> getVariableAtom(String variableModelPath) {

		AbstractAtom variableAbstractAtom = null;
		try {
			variableAbstractAtom = getChildFromRoot(variableModelPath);
		} catch (IllegalArgumentException exception) {
			String message = "Could not find a variable field for the model path '" + variableModelPath + "'.";
			sysLog.error(message);
			return null;
		}

		AbstractAttributeAtom<Object> variableAtom = null;
		try {
			variableAtom = (AbstractAttributeAtom<Object>) variableAbstractAtom;
		} catch (ClassCastException exception) {
			String simpleClassName = variableAbstractAtom.getClass().getSimpleName();
			String message = "Could not cast the variable with model path '" + variableAbstractAtom
					+ "' to AttributeAtom<Quantity>. Its simple class name is '" + simpleClassName + "'.";
			sysLog.error(message, exception);
			return null;
		}
		return variableAtom;
	}

	/**
	 * Runs the first child model with the given class and returns its ModelOutput
	 *
	 * @param wantedClass
	 */
	protected ModelOutput runChildModel(Class<?> wantedClass, Refreshable refreshable, IProgressMonitor monitor) {
		for (AbstractAtom child : children) {
			Class<?> currentClass = child.getClass();
			boolean hasWantedClass = currentClass.equals(wantedClass);
			if (hasWantedClass) {
				boolean isModel = child instanceof Model;
				if (isModel) {
					Model model = (Model) child;
					ModelOutput modelOutput = model.runModel(refreshable, monitor);
					return modelOutput;
				} else {
					String message = "The found child '" + child.getName() + "' is not a model.";
					sysLog.error(message);
					throw new IllegalStateException(message);
				}
			}
		}

		String message = "Could not find a child of wanted class '" + wantedClass.getSimpleName() + "'.";
		sysLog.error(message);
		throw new IllegalStateException(message);
	}

	/**
	 * Returns true if this atom has a child with the given class
	 *
	 * @param wantedClass
	 */
	protected boolean hasChildModel(Class<?> wantedClass) {
		for (AbstractAtom child : children) {
			Class<?> currentClass = child.getClass();
			boolean hasWantedClass = currentClass.equals(wantedClass);
			if (hasWantedClass) {
				return true;
			}
		}
		return false;

	}

	//#end region

	//#region ACCESSORS

	@Override
	public boolean isManualModel() {
		return isManualModel;
	}

	protected void setManualModel() {
		isManualModel = true;
	}

	/**
	 * @return
	 */
	@Override
	public String getStudyId() {
		return studyId;
	}

	/**
	 * Sets the study id for this model and all sub models
	 *
	 * @param studyId
	 */
	@Override
	public void setStudyId(String studyId) {
		this.studyId = studyId;
		for (AbstractAtom child : children) {
			boolean isModel = child instanceof Model;
			if (isModel) {
				Model model = (Model) child;
				model.setStudyId(studyId);
			}
		}
	}

	//#end region

}
