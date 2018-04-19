package org.treez.core.monitor;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.internal.ErrorViewPart;
import org.treez.core.AbstractActivator;
import org.treez.core.atom.uisynchronizing.AbstractUiSynchronizingAtom;
import org.treez.core.standallone.StandAloneWorkbench;

public class TreezMonitor implements ObservableMonitor {

	private static Logger LOG = Logger.getLogger(TreezMonitor.class);

	//#region ATTRIBUTES

	private static Map<String, MessageConsole> consoleMap = new HashMap<>();

	public static Color ORANGE = new Color(null, 196, 60, 0);

	public static Color RED = new Color(null, 255, 0, 0);

	public static Color BLACK = new Color(null, 0, 0, 0);

	private String title;

	private String id;

	private String description = "";

	private IProgressMonitor rootMonitor;

	private Monitor parentMonitor;

	private MessageConsole console;

	private Logger logger;

	private int coveredWorkOfParentMonitor = 0;

	private Integer totalWork = null;

	private double finishedWork = 0;

	private List<ObservableMonitor> children = new ArrayList<>();

	private int workCoveredByChildren = 0;

	private boolean isCanceled = false;

	private boolean hasIssue = false;

	private boolean isDone = false;

	private List<Runnable> propertyChangedListeners = new ArrayList<>();

	private List<ChildCreatedListener> childCreatedListeners = new ArrayList<>();

	//#end region

	//#region CONSTRUCTORS

	/**
	 * This constructor uses the given logger instead of an extra console. Progress will also be reported to the given
	 * parentMonitor. The method done of the parentMonitor is not called automatically when this monitor is done.
	 * Depending on the implementation of the parent monitor and its total work, the call to done might need to be done
	 * manually by the caller of this constructor. Otherwise the parentMonitor will never finish.
	 */
	public TreezMonitor(String title, IProgressMonitor rootMonitor, int totalWork) {
		rootMonitor.beginTask(title, totalWork);
		this.rootMonitor = rootMonitor;
		this.title = title;
		this.id = null;
		this.totalWork = totalWork;
		this.coveredWorkOfParentMonitor = totalWork;
	}

	/**
	 * This constructor creates its own console. It also reports progress to the given parentMonitor.
	 */
	private TreezMonitor(String title, String id, Monitor parentMonitor, int coveredWorkOfParentMonitor) {
		this.title = title;
		this.id = id;
		this.parentMonitor = parentMonitor;
		this.coveredWorkOfParentMonitor = coveredWorkOfParentMonitor;
		console = createConsole(title, id);
	}

	private TreezMonitor(
			String title,
			String id,
			TreezMonitor parentMonitor,
			int coveredWorkOfParentMonitor,
			int totalWork) {
		this(title, id, parentMonitor, coveredWorkOfParentMonitor);
		this.totalWork = totalWork;
	}

	//#end region

	//#region METHODS

	public static void showInMonitorView(ObservableMonitor monitor) {
		AbstractUiSynchronizingAtom.runUiTaskNonBlocking(() -> {
			MonitorTreezView monitorView = getMonitoringView();
			monitorView.setMonitor(monitor);
		});

	}

	@SuppressWarnings("restriction")
	public static MonitorTreezView getMonitoringView() {

		String id = "org.treez.views.monitor";

		boolean isRunningInEclipse = AbstractActivator.isRunningInEclipse();
		if (isRunningInEclipse) {

			IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

			if (workbenchWindow == null) {
				IWorkbenchWindow[] allWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
				for (IWorkbenchWindow window : allWindows) {
					workbenchWindow = window;
					if (workbenchWindow != null) {
						break;
					}
				}
			}

			if (workbenchWindow == null) {
				throw new IllegalStateException("Could not retrieve workbench window");
			}

			IWorkbenchPage activePage = workbenchWindow.getActivePage();

			try {
				IViewPart viewPart = activePage.showView(id);

				if (viewPart instanceof ErrorViewPart) {
					ErrorViewPart errorView = (ErrorViewPart) viewPart;

					String message = "Could not retrieve view " + id;
					LOG.warn(message);
					return null;
				}
				return (MonitorTreezView) viewPart;
			} catch (PartInitException e) {
				String message = "Could not retrieve view " + id;
				LOG.warn(message, e);
				return null;
			}

		} else {
			return (MonitorTreezView) StandAloneWorkbench.getView(id);
		}
	}

	@Override
	public synchronized
			ObservableMonitor
			createChild(String title, String id, int coveredWorkOfParentMonitor, int totalWork) {

		assertTotalWorkHasBeenSet();
		assertChildWorkIsNotTooLarge(coveredWorkOfParentMonitor);

		workCoveredByChildren += coveredWorkOfParentMonitor;
		TreezMonitor treezSubMonitor = new TreezMonitor(title, id, this, coveredWorkOfParentMonitor, totalWork);
		children.add(treezSubMonitor);
		triggerChildCreatedListeners(treezSubMonitor);
		return treezSubMonitor;
	}

	@Override
	public synchronized ObservableMonitor createChild(String title, String id, int coveredWorkOfParentMonitor) {

		assertTotalWorkHasBeenSet();
		assertChildWorkIsNotTooLarge(coveredWorkOfParentMonitor);

		//Add Id to logging context, also see http://www.baeldung.com/java-logging-ndc-log4j
		NDC.push(id);

		workCoveredByChildren += coveredWorkOfParentMonitor;
		TreezMonitor treezSubMonitor = new TreezMonitor(title, id, this, coveredWorkOfParentMonitor);
		children.add(treezSubMonitor);
		triggerChildCreatedListeners(treezSubMonitor);
		return treezSubMonitor;
	}

	private void assertChildWorkIsNotTooLarge(int coveredWorkOfParentMonitor) {

		int workNotCoveredByChildren = totalWork - workCoveredByChildren;
		if (coveredWorkOfParentMonitor > workNotCoveredByChildren) {
			String message = "The parent monitor does not have enough uncovered work to create the child ("
					+ coveredWorkOfParentMonitor + " > " + workNotCoveredByChildren + ")";
			throw new IllegalStateException(message);
		}

		double freeWork = totalWork - finishedWork;
		if (coveredWorkOfParentMonitor > freeWork) {
			String message = "The parent monitor does not have enough free work to create the child ("
					+ coveredWorkOfParentMonitor + " > " + freeWork + ")";
			throw new IllegalStateException(message);
		}
	}

	@Override
	public synchronized void worked(double workIncrement) {

		assertTotalWorkHasBeenSet();

		if (isDone) {
			return;
		}

		assertWorkIncrementIsNotTooLarge(workIncrement);

		finishedWork += workIncrement;

		incrementParentWork(workIncrement);

		if (finishedWork == totalWork) {
			isDone = true;
		}
		triggerPropertyChangedListeners();
	}

	private void assertWorkIncrementIsNotTooLarge(double workIncrement) {
		if (finishedWork + workIncrement > totalWork) {
			String message = "The work increment " + workIncrement + " is too large. The finished work "
					+ (finishedWork + workIncrement) + " would be greater than the total work " + totalWork;
			throw new IllegalStateException(message);
		}
	}

	private void incrementParentWork(double workIncrement) {
		Double workIncrementForParent = 1.0 * workIncrement / totalWork * coveredWorkOfParentMonitor;

		if (parentMonitor != null) {
			parentMonitor.worked(workIncrementForParent);
		} else {
			rootMonitor.worked(workIncrementForParent.intValue());
		}

	}

	@Override
	public synchronized void done() {

		assertTotalWorkHasBeenSet();

		double workIncrement = totalWork - finishedWork;
		finishedWork = totalWork;
		isDone = true;
		triggerPropertyChangedListeners();
		incrementParentWork(workIncrement);

	}

	@Override
	public void close() throws Exception {
		cancel();
		NDC.pop();
	}

	private void assertTotalWorkHasBeenSet() {
		if (totalWork == null) {
			throw new IllegalStateException("Total work must be set before calling this method.");
		}
	}

	@Override
	public void cancelAll() {
		cancel();
		if (parentMonitor != null) {
			parentMonitor.cancel();
		}
	}

	@Override
	public void cancel() {
		if (isDone()) {
			return;
		}
		isCanceled = true;
		if (rootMonitor != null) {
			rootMonitor.setCanceled(true);
		}
		triggerPropertyChangedListeners();
	}

	@Override
	public void markIssue() {
		hasIssue = true;
		triggerPropertyChangedListeners();
	}

	private MessageConsole createConsole(String title, String id) {

		Image image = org.treez.core.Activator.getImage("tree.png");
		ImageDescriptor imageDescriptor = AbstractActivator.getImageDescriptor(image);

		ConsolePlugin consolePlugin = ConsolePlugin.getDefault();
		if (consolePlugin == null) {
			LOG.warn("Could not create console because ConsolePlugin is not available");
		} else {
			console = new MessageConsole(title, imageDescriptor, false);
			console.setWaterMarks(80000, 80001);

			//console.addPatternMatchListener(new PatternMatchListener());
			registerConsole(id, console);
		}

		return console;
	}

	@Override
	public void addPropertyChangedListener(Runnable listener) {
		propertyChangedListeners.add(listener);
	}

	private void triggerPropertyChangedListeners() {
		for (Runnable listener : propertyChangedListeners) {
			listener.run();
		}
	}

	@Override
	public void addChildCreatedListener(ChildCreatedListener listener) {
		childCreatedListeners.add(listener);
	}

	private void triggerChildCreatedListeners(ObservableMonitor newChild) {
		for (ChildCreatedListener listener : childCreatedListeners) {
			listener.handle(newChild);
		}
	}

	//#end region

	//#region ACCESSORS

	public static void registerConsole(String id, MessageConsole console) {
		consoleMap.put(id, console);
	}

	public static void unRegisterConsole(String id) {
		consoleMap.remove(id);
	}

	public static MessageConsole getConsole(String id) {
		return consoleMap.get(id);
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
		if (rootMonitor != null) {
			rootMonitor.setTaskName(description);
		}
		triggerPropertyChangedListeners();
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setTotalWork(int totalWork) {
		if (this.totalWork == null) {
			this.totalWork = totalWork;
		} else {
			throw new IllegalStateException("Total work must only be set once");
		}
	}

	@Override
	public boolean isCanceled() {
		return isCanceled;
	}

	@Override
	public boolean hasIssue() {
		return hasIssue;
	}

	@Override
	public boolean isDone() {

		if (totalWork == null) {
			return false;
		}

		return finishedWork >= totalWork;
	}

	@Override
	public int getProgressInPercent() {

		if (totalWork == null || totalWork == 0) {
			return 0;
		}
		Double progressInPercent = 1.0 * finishedWork / totalWork * 100;
		return progressInPercent.intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ObservableMonitor> getChildren() {
		return children;
	}

	@Override
	public MessageConsole getConsole() {
		return console;
	}

	@Override
	public OutputStream getOutputStream() {
		IOConsoleOutputStream stream = console.newOutputStream();

		return stream;
	}

	@Override
	public boolean isChildCanceled() {
		for (ObservableMonitor child : children) {
			if (child.isCanceled() || child.isChildCanceled()) {
				return true;
			}
		}
		return false;
	}

	//#end region

}
