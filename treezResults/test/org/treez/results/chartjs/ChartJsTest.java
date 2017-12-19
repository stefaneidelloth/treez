package org.treez.results.chartjs;

import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import jdk.nashorn.internal.objects.NativeObject;

/**
 * Uses an SWT Browser to show a d3 example html file
 */
public class ChartJsTest {

	//#region METHODS

	/**
	 * Shows the chartist example
	 */
	public static void main(String[] args) {
		Display display = new Display();

		final Shell shell = new Shell(display);
		shell.setText("ChartJs Example");
		shell.setLayout(new FillLayout());

		Browser browser = new Browser(shell, SWT.NONE);
		browser.setJavascriptEnabled(true);

		String filePath = "chartjs.html";
		URL url = ChartJsTest.class.getResource(filePath);
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

		String script = "var ctx = document.getElementById(\"canvas\").getContext(\"2d\");"
				+ "var chart = new Chart(ctx).Line(lineChartData, {" + "	responsive: true" + "});"
				+ "window.myLine = chart;" + "var obj = new Object;" + "obj.name = 'hallo';" + "obj.chart = chart;";

		boolean success = browser.execute(script);

		Object chart = browser.evaluate("return obj;");
		NativeObject object = (NativeObject) chart;

		System.out.println(object);

	}

	//#end region

}
