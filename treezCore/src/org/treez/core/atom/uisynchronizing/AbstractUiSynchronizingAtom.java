package org.treez.core.atom.uisynchronizing;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.base.AtomControlAdaption;

/**
 * Provides methods to easily run long tasks without blocking UI
 */
public abstract class AbstractUiSynchronizingAtom<A extends AbstractUiSynchronizingAtom<A>> extends AbstractAtom<A>
		implements
		FocusChangingRefreshable {

	private static Logger LOG = LogManager.getLogger(AbstractUiSynchronizingAtom.class);

	//#region ATTRIBUTES

	/**
	 * The refreshable tree view
	 */
	protected FocusChangingRefreshable treeViewRefreshable = null;

	//#end region

	//#region CONSTRUCTORS

	public AbstractUiSynchronizingAtom(String name) {
		super(name);
	}

	/**
	 * Copy Constructor
	 */
	public AbstractUiSynchronizingAtom(AbstractUiSynchronizingAtom<A> atomToCopy) {
		super(atomToCopy);
		this.treeViewRefreshable = atomToCopy.treeViewRefreshable;
	}

	//#end region

	//#region METHODS

	@Override
	public
			AbstractControlAdaption
			createControlAdaption(Composite parent, FocusChangingRefreshable treeViewRefreshable) {

		//store refreshable tree view
		this.treeViewRefreshable = treeViewRefreshable;

		//create control adaption in UI thread
		final ResultWrapper<AtomControlAdaption> controlAdaptionWrapper = new ResultWrapper<AtomControlAdaption>(null);
		Runnable createControlAdaptionRunnable = () -> {

			//remove old content and reset parent layout
			resetContentAndLayoutOfParentComposite(parent);

			//create the control adaption and return it
			AtomControlAdaption newControlAdaption = new AtomControlAdaption(parent, this);
			controlAdaptionWrapper.setValue(newControlAdaption);
		};
		runUiTaskBlocking(createControlAdaptionRunnable);
		AtomControlAdaption controlAdaption = controlAdaptionWrapper.getValue();

		return controlAdaption;
	}

	/**
	 * Removes old content from the given parent composite and updates its layout
	 *
	 * @param parent
	 */
	protected static void resetContentAndLayoutOfParentComposite(Composite parent) {
		for (Control child : parent.getChildren()) {
			child.dispose();
		}
		parent.setLayout(new FillLayout());
		parent.layout();
	}

	/**
	 * Executes a (long running job) without blocking the UI. The calling method will "immediately" continue.
	 */
	public static synchronized void runNonUiTask(String taskName, NonUiJob nonUiJob) {
		Objects.requireNonNull(nonUiJob, "Runnable must not be null.");
		Job job = new Job(taskName) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {

				SubMonitor subMonitor = SubMonitor.convert(monitor);
				try {
					nonUiJob.run(subMonitor);
					return Status.OK_STATUS;
				} catch (Exception exception) {
					String message = "Could not run non-ui job.";
					LOG.error(message, exception);
					return Status.CANCEL_STATUS;
				}

			}
		};

		//start the Job
		job.schedule();
	}

	public static synchronized void runNonUiTask(String taskName, Runnable taskRunnable) {
		Objects.requireNonNull(taskRunnable, "Runnable must not be null.");
		Job job = new Job(taskName) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					taskRunnable.run();
					return Status.OK_STATUS;
				} catch (Exception exception) {
					String message = "Could not run non-ui job.";
					LOG.error(message, exception);
					return Status.CANCEL_STATUS;
				}
			}
		};

		//start the Job
		job.schedule();
	}

	/**
	 * Execute a runnable in the UI thread. The calling method will "immediately" continue. Please make sure to catch
	 * exceptions inside the runnable. Otherwise exception information might get lost.
	 */
	public static synchronized void runUiTaskNonBlocking(Runnable taskRunnable) {
		Objects.requireNonNull(taskRunnable, "Runnable must not be null.");

		Display display = Display.getDefault();
		if (display == null || display.isDisposed()) {
			return;
		}

		display.asyncExec(taskRunnable);

	}

	/**
	 * Can be used from within non-UI tasks (see method runNonUiTask) to execute a runnable in the UI thread. The
	 * calling method will wait until this method is finished before it continues.
	 */
	public static synchronized void runUiTaskBlocking(Runnable taskRunnable) {
		Objects.requireNonNull(taskRunnable, "Runnable must not be null.");

		Display display = Display.getDefault();
		if (display == null || display.isDisposed()) {
			return;
		}

		try {
			display.syncExec(taskRunnable);
		} catch (Exception exception) {
			String message = "Could not execute blocking UI job";
			LOG.error(message, exception);
			throw new IllegalStateException(message, exception);
		}

	}

	/**
	 * Refreshes the treeViewRefreshable
	 */
	@Override
	public synchronized void refresh() {

		runUiTaskNonBlocking(() -> {
			if (treeViewRefreshable != null) {
				treeViewRefreshable.refresh();
			}
		});

	}

	/**
	 * Sets the focus on the given atom
	 */
	@Override
	public synchronized void setFocus(AbstractAtom<?> atomToFocus) {

		runUiTaskNonBlocking(() -> {
			if (treeViewRefreshable != null) {
				treeViewRefreshable.setFocus(atomToFocus);
			}
		});

	}

	//#end region

}
