package org.treez.results.d3;

import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Uses an SWT Browser to show a d3 example html file
 */
public class D3Test {

	//#region METHODS

	/**
	 * Shows the chartist example
	 */
	public static void main(String[] args) {
		Display display = new Display();

		final Shell shell = new Shell(display);
		shell.setText("D3 Example");
		shell.setLayout(new FillLayout());

		Browser browser = new Browser(shell, SWT.NONE);
		browser.setJavascriptEnabled(true);

		String filePath = "d3.html";
		URL url = D3Test.class.getResource(filePath);
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

		Object chart = browser.evaluate("return d3.select(\".chart\")");
		Object[] objects = (Object[]) chart;
		Object a = objects[0];

		Class<?> clazz = chart.getClass();

		//Object b = (chart).selectAll("g");

		System.out.println(chart);

	}

	//#end region

}
