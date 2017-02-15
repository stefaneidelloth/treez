package org.treez.results.atom.probe;

import org.eclipse.core.runtime.IProgressMonitor;
import org.treez.core.adaptable.FocusChangingRefreshable;

/**
 * Represents a Probe that collects data from ModelOutputs and puts it into a resulting table. While the source data is
 * typically distributed over several tables, the resulting data is put into a single table. That result table is a good
 * basis for the creation of plots.
 */
public interface Probe {

	/**
	 * Runs this Probe. The given Refreshable might be refreshed if it is not null. The given IProgressMonitor might be
	 * used to update the progress if it is not null. The collected probe data is put in a Table that is a child of the
	 * Probe.
	 */
	void runProbe(FocusChangingRefreshable refreshable, IProgressMonitor monitor);

}
