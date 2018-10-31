package org.treez.views.monitor;

import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.swt.widgets.Composite;
import org.junit.BeforeClass;
import org.junit.Test;
import org.treez.core.monitor.ObservableMonitor;
import org.treez.core.monitor.TreezMonitor;
import org.treez.testutils.PreviewWindow;
import org.treez.testutils.TestUtils;

public class MonitorViewPartTest {

	private static Logger LOG = LogManager.getLogger(MonitorViewPartTest.class);

	//#region METHODS

	protected Boolean isShowingPreviewWindow() {
		return true;
	}

	//#region SETUP

	@BeforeClass
	public static void setUpClass() {
		TestUtils.initializeLogging();
	}

	//#end region

	//#region TESTS

	@Test
	public void testConstructionOfTestAtom() {

		PreviewWindow previewWindow = TestUtils.getPreviewWindow();

		Composite controlComposite = previewWindow.getControlComposite();

		MonitorViewPart monitorView = new MonitorViewPart();
		monitorView.createPartControl(controlComposite);

		TreezMonitor treezMonitor = createTreezMonitor();
		monitorView.setMonitor(treezMonitor);

		//treezMonitor.info("FirstMessage in main console");
		treezMonitor.worked(10);

		treezMonitor.setDescription("Description");

		ObservableMonitor subMonitor1 = treezMonitor.createChild("Sub1", null, 10, 100);
		subMonitor1.worked(100);
		//subMonitor1.info("FirstMessage in first sub console");

		ObservableMonitor subMonitor2 = treezMonitor.createChild("Sub2", null, 10, 100);
		subMonitor2.worked(50);
		//subMonitor2.info("FirstMessage in second sub console");

		treezMonitor.worked(10);

		assertEquals(treezMonitor.getProgressInPercent(), 10.0 + 10.0 + 5.0 + 10.0, 1e-6);

		if (isShowingPreviewWindow()) {
			previewWindow.showUntilManuallyClosed();
		}

	}

	private static TreezMonitor createTreezMonitor() {

		IProgressMonitor progressMonitorMock = new IProgressMonitor() {

			@Override
			public void beginTask(String name, int totalWork) {}

			@Override
			public void done() {

			}

			@Override
			public void internalWorked(double work) {}

			@Override
			public boolean isCanceled() {
				return false;
			}

			@Override
			public void setCanceled(boolean value) {

			}

			@Override
			public void setTaskName(String name) {

			}

			@Override
			public void subTask(String name) {

			}

			@Override
			public void worked(int work) {

			}

		};

		SubMonitor monitor = SubMonitor.convert(progressMonitorMock);

		TreezMonitor treezMonitor = new TreezMonitor("Main", monitor, 100);

		return treezMonitor;

	}

}
