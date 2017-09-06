package org.treez.core.monitor;

import java.io.OutputStream;
import java.util.List;

import org.eclipse.ui.console.MessageConsole;

public interface Monitor extends AutoCloseable {

	String getId();

	String getTitle();

	String getDescription();

	void setDescription(String string);

	Monitor createChild(String title, String id, int coveredWorkOfParentMonitor);

	Monitor createChild(String title, String id, int coveredWorkOfParentMonitor, int totalWork);

	void setTotalWork(int totalWork);

	<T extends Monitor> List<T> getChildren();

	int getProgressInPercent();

	MessageConsole getConsole();

	OutputStream getOutputStream();

	/**
	 * Increases the finished work by the given increment.
	 */
	void worked(double workIncrement);

	/**
	 * Sets finished work to the total work (=> progress will be 100 %).
	 */
	void done();

	/**
	 * Returns true if the finished work equals the total work
	 */
	boolean isDone();

	/**
	 * Requests cancellation. The request is passed to the parent monitor if it exists. A cancel request is ignored if
	 * the monitor is already done.
	 */
	void cancel();

	/**
	 * Requests cancellation. The request is passed to the parent monitor if it exists. A cancel request is ignored if
	 * the monitor is already done.
	 */
	void cancelAll();

	/**
	 * Returns whether cancellation of current operation has been requested. Long-running operations should poll to see
	 * if cancellation has been requested.
	 *
	 * @return <code>true</code> if cancellation has been requested, and <code>false</code> otherwise
	 * @see #cancel()
	 */
	boolean isCanceled();

	/**
	 * Returns true if one of the children requested cancellation.
	 */
	boolean isChildCanceled();

	/**
	 * Marks the monitor to haven an issue. A corresponding progress bar can highlight this state with a different
	 * color.
	 */
	void markIssue();

	/**
	 * Returns true if this monitor has been marked to have an issue
	 */
	boolean hasIssue();

}
