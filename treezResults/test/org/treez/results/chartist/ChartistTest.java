package org.treez.results.chartist;

import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Uses an SWT Browser to show a chartist example html file
 */
public class ChartistTest {

	//#region METHODS

	/**
	 * Shows the chartist example
	 */
	public static void main(String[] args) {
		Display display = new Display();

		final Shell shell = new Shell(display);
		shell.setText("Chartist Example");
		shell.setLayout(new FillLayout());

		Browser browser = new Browser(shell, SWT.NONE);
		browser.setJavascriptEnabled(true);

		String filePath = "chartist.html";
		URL url = ChartistTest.class.getResource(filePath);
		browser.setUrl(url.toString());

		browser.addProgressListener(new ProgressListener() {

			@Override
			public void completed(ProgressEvent event) {
				System.out.println("Page loaded");
				executeJavaScript(browser);
			}

			@Override
			public void changed(ProgressEvent event) {}
		});

		shell.pack();
		final int windowSize = 500;
		shell.setSize(windowSize, windowSize);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
	}

	private static void executeJavaScript(Browser browser) {

		String script = "var chartist = new Chartist.Line(" + "'#chart'," + " " + "{"
				+ " labels: [1, 2, 3, 4, 5, 6, 7, 8]," + "series: [" + " [5, 9, 7, 8, 5, 3, 5, 44]" + "]" + "}, " + ""
				+ "{" + "  low: 0," + "  showArea: true" + "}" + "" + ");" + "return chartist;";

		Object chartist = browser.evaluate("return Chartist");

	}

	//#end region

}
