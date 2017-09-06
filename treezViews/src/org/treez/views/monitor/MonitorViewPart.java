package org.treez.views.monitor;

import java.util.List;
import java.util.concurrent.FutureTask;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.console.IOConsoleViewer;
import org.eclipse.ui.part.ViewPart;
import org.treez.core.monitor.MonitorTreezView;
import org.treez.core.monitor.ObservableMonitor;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.javafx.PlainJavaFxD3BrowserWrapper;
import org.treez.standalone.VerticalSeparatorPanel;

/**
 * Shows progress bars and log messages
 */
@SuppressWarnings("restriction")
public class MonitorViewPart extends ViewPart implements MonitorTreezView {

	private static Logger LOG = Logger.getLogger(MonitorViewPart.class);

	//#region ATTRIBUTES

	private static int SECTION_STYLE = ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR;

	/**
	 * The ID of the view as specified by the extension and used for the help system
	 */
	public static final String ID = "org.treez.views.monitor";

	private Composite contentComposite;

	private Composite progressComposite;

	private Composite loggingComposite;

	private Section loggingSection;

	private ObservableMonitor treezMonitor;

	private PlainJavaFxD3BrowserWrapper browser;

	private boolean browserIsInitialized = false;

	//#end region

	//#region CONSTRUCTORS

	public MonitorViewPart() {}

	//#end region

	//#region METHODS

	@SuppressWarnings("unused")
	@Override
	public void createPartControl(Composite parent) {

		try {
			contentComposite = parent;
			Composite progressContainer = new Composite(contentComposite, SWT.NONE);
			progressContainer.setLayout(new FillLayout());

			Composite loggingContainer = new Composite(contentComposite, SWT.NONE);
			createFillLayout(loggingContainer);

			new VerticalSeparatorPanel(contentComposite, progressContainer, loggingContainer, 0.5);

			createProgressTree(progressContainer);
			createLogReport(loggingContainer);
		} catch (Exception exception) {
			LOG.error("Could not create monitor view", exception);
			throw exception;
		}
	}

	private static void createFillLayout(Composite composite) {
		FillLayout fillLayout = new FillLayout();
		fillLayout.marginHeight = 0;
		fillLayout.marginWidth = 0;
		composite.setLayout(fillLayout);
	}

	@Override
	public void setFocus() {
		contentComposite.setFocus();
	}

	private void createProgressTree(Composite progressContainer) {

		Section progressSection = new Section(progressContainer, SECTION_STYLE);
		progressSection.setText("Progress");

		progressComposite = new Composite(progressSection, SWT.NONE);
		progressComposite.setLayout(new FillLayout());
		progressSection.setClient(progressComposite);

		Runnable afterLoadingHook = () -> {
			try {
				browserIsInitialized = true;
				updateProgessTree();

			} catch (Exception exception) {
				String message = "Could not update progress tree.";
				LOG.error(message, exception);
			}
		};

		browser = new PlainJavaFxD3BrowserWrapper(progressComposite, afterLoadingHook, false);

	}

	private synchronized void updateProgessTree() {

		if (!browserIsInitialized) {
			return; //initialization will update the progress tree
		}

		deleteOldContent();

		if (treezMonitor == null) {
			return;
		}

		D3 d3 = browser.getD3();

		Selection root = d3.select("#root");

		createProgressNodes(root, treezMonitor);
	}

	private synchronized void createProgressNodes(Selection root, ObservableMonitor monitor) {

		List<ObservableMonitor> subMonitors = monitor.getChildren();

		Selection div = root
				.append("div") //
				.attr("id", "progressContainer");

		Selection details = div
				.append("details") //
				.attr("id", "collapsible") //
				.attr("open", "true"); //expandable for progress that has children (might be hidden)

		Selection collapsibleHeader = details
				.append("summary") //
				.attr("id", "header") //
				.style("outline", "none");

		Selection collapsibleContent = details
				.append("div") //
				.attr("id", "content")
				.style("padding-left", "10px");

		Selection nonExpandableHeader = div.append("div"); //for progress that does not have children (might be hidden)

		monitor.addChildCreatedListener((newChildMonitor) -> {

			FutureTask<Void> updateUITask = new FutureTask<>(() -> {
				appendChildMonitor(monitor, nonExpandableHeader, collapsibleHeader, collapsibleContent, details,
						newChildMonitor);
			}, null);
			browser.runLater(updateUITask);

			// block until work complete:
			try {
				updateUITask.get();
			} catch (Exception e) {
				throw new IllegalStateException("Could not wait for UI", e);
			}

		});

		if (subMonitors.isEmpty()) {
			//show non-expandable header
			collapsibleHeader.style("display", "none");
			createHeaderNodes(nonExpandableHeader, monitor, null);
		} else {
			//show expandable header
			nonExpandableHeader.style("display", "none");
			createHeaderNodes(collapsibleHeader, monitor, details);

			for (ObservableMonitor subMonitor : subMonitors) {
				createProgressNodes(collapsibleContent, subMonitor);
			}

		}

	}

	private synchronized void appendChildMonitor(
			ObservableMonitor monitor,
			Selection simpleHeader,
			Selection expandableHeader,
			Selection content,
			Selection details,
			ObservableMonitor newChildMonitor) {

		String headerDisplay = expandableHeader.style("display");
		if (headerDisplay.equals("none")) {
			simpleHeader.style("display", "none");
			expandableHeader.style("display", "block");
			createHeaderNodes(expandableHeader, monitor, details);
		}

		createProgressNodes(content, newChildMonitor);
	}

	private synchronized void createHeaderNodes(Selection header, ObservableMonitor monitor, Selection details) {

		String title = monitor.getTitle();
		double progressInPercent = monitor.getProgressInPercent();
		String description = monitor.getDescription();

		header.onClick(() -> {
			showLogMessagesForMonitor(monitor);
		});

		Selection titleLabel = header
				.append("label") //
				.style("color", "black") //
				.text(title);

		if (details != null) {
			titleLabel.onClick(() -> {
				String expandedString = details.attr("open");
				if (expandedString == null) {
					details.attr("open", "true");
				} else {
					String nullAttribute = null;
					details.attr("open", nullAttribute);
				}

			});
		}

		Selection descriptionLabel = header //
				.append("label") //
				.style("color", "black"); //;

		if (description.isEmpty()) {
			descriptionLabel.text("");
		} else {
			descriptionLabel.text(": " + description);
		}

		Selection right = header
				.append("span") //
				.style("padding-right", "10px")
				.style("float", "right");

		Selection progressLabel = right //
				.append("span") //
				.style("padding-right", "10px")
				.append("label") //
				.style("color", "black") //
				.text("" + progressInPercent + " %");

		Selection cancelButton = right //
				.append("input")
				.attr("type", "button")
				.attr("title", "Cancel")
				.style("height", "12px")
				.style("padding-bottom", "2px")
				.style("border", "none")
				.style("-webkit-border-radius", "3px")
				.style("box-shadow", "inset 0 0 0 1px #e82734, inset 0 0 0 2px #f69b9c")
				.style("background-color", "#e82734");

		Selection canceledSymbol = right //
				.append("label") //
				.text("!") //
				.style("font-weight", "bold")
				.style("color", "#e82734") //
				.style("display", "none") //
				.attr("title", "Canceled!");

		cancelButton.onClick(() -> {
			monitor.cancel();
		});

		Selection progressBackground = header
				.append("div") //
				.classed("progress", true)
				.style("background-color", "lightgray");

		Selection progress = progressBackground
				.append("div") //
				.classed("determinate", true) //
				.style("background", "linear-gradient(#65c46f, green)")
				.style("width", progressInPercent + "%");

		monitor.addPropertyChangedListener(() -> {

			browser.runLater(() -> {
				//update title
				String currentTitle = monitor.getTitle();
				titleLabel.text(currentTitle);

				//update description
				String currentDescription = monitor.getDescription();
				if (currentDescription.isEmpty()) {
					descriptionLabel.text("");
				} else {
					descriptionLabel.text(": " + currentDescription);
				}

				//update progress
				double currentProgressInPercent = monitor.getProgressInPercent();
				progressLabel.text("" + currentProgressInPercent + " %");
				progress.style("width", currentProgressInPercent + "%");

				//apply issue state
				if (monitor.hasIssue()) {
					progress.style("background", "linear-gradient(#f17d85, #e82734)");
				}

				//apply canceled state
				if (monitor.isCanceled()) {
					cancelButton.style("display", "none");
					canceledSymbol.style("display", "inline");
					progress.style("background", "linear-gradient(#eb4752, #e82734)");
				}

				//update visibility of cancel button
				if (currentProgressInPercent == 100.0) {
					cancelButton.style("display", "none");
				}

			});

		});

	}

	private synchronized void showLogMessagesForMonitor(ObservableMonitor monitor) {

		loggingSection.setText("Log messages (" + monitor.getTitle() + ")");

		for (Control child : loggingComposite.getChildren()) {
			child.dispose();
		}

		MessageConsole console = monitor.getConsole();

		if (console != null) {
			@SuppressWarnings({ "unused", "restriction" })
			IOConsoleViewer consoleViewer = new IOConsoleViewer(loggingComposite, console);
			consoleViewer.setAutoScroll(true);
		}

		loggingComposite.layout(true);
		loggingSection.layout(true);

	}

	private void deleteOldContent() {
		D3 d3 = browser.getD3();
		d3.select("#root").selectAll("div").remove();
	}

	private void createLogReport(Composite loggingContainer) {
		loggingSection = new Section(loggingContainer, SECTION_STYLE);
		loggingSection.setText("Log messages");

		loggingComposite = new Composite(loggingSection, SWT.NONE);
		loggingComposite.setBackground(new Color(null, 255, 255, 255));
		createFillLayout(loggingComposite);
		loggingSection.setClient(loggingComposite);

	}

	//#end region

	//#region ACCESSORS

	@Override
	public Composite getContentComposite() {
		return contentComposite;
	}

	@Override
	public void setMonitor(ObservableMonitor treezMonitor) {
		this.treezMonitor = treezMonitor;
		updateProgessTree();
		setFocus();
	}

	//#end region

}
