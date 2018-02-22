package org.treez.study.atom;

import org.eclipse.core.runtime.SubMonitor;
import org.treez.core.adaptable.FocusChangingRefreshable;

/**
 * Represents a study, e.g. a parameter variation
 */
public interface Study {

	String getId();

	String getDescription();

	/**
	 * Returns the absolute model path to the model that is executed by this Study
	 */
	String getModelToRunModelPath();

	/**
	 * Returns the absolute model path to the source model of this Study
	 */
	String getSourceModelPath();

	ModelInputGenerator getModelInputGenerator();

	/**
	 * Runs the study. While the study is running the given Refreshable (tree view) might be refreshed and the given
	 * IProgressMonitor might be used to update the progress. The implementing class needs to handle the possible cases
	 * where the arguments are null.
	 */
	void runStudy(FocusChangingRefreshable refreshable, SubMonitor monitor);

}
