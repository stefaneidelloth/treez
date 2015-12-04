package org.treez.core.atom.uisynchronizing;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A job that can be executed in a non ui thread.
 */
public interface NonUiJob {

	/**
	 * Executes the NonUiJob. The injected monitor can be used to report the progress of the job.
	 *
	 * @param monitor
	 */
	void run(IProgressMonitor monitor);

}
