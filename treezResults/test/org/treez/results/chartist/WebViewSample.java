package org.treez.results.chartist;

import java.net.URL;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class WebViewSample extends Application {

	private Scene scene;

	@Override
	public void start(Stage stage) {
		// create the scene
		stage.setTitle("Web View");
		Browser browser = new Browser();
		scene = new Scene(browser, 750, 500, Color.web("#666970"));
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}

class Browser extends Region {

	final WebView browser = new WebView();

	final WebEngine webEngine = browser.getEngine();

	public Browser() {

		//add the web view to the scene
		getChildren().add(browser);

		//add finished listener
		webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
			if (newState == Worker.State.SUCCEEDED) {
				executeJavaScript();
			}
		});

		// load the web page
		URL url = WebViewSample.class.getResource("chartist.html");
		String urlPath = url.toExternalForm();
		webEngine.load(urlPath);

	}

	private void executeJavaScript() {

		String script = "var chartist = new Chartist.Line(" + "'#chart'," + " " + "{"
				+ " labels: [1, 2, 3, 4, 5, 6, 7, 8]," + "series: [" + " [5, 9, 7, 8, 5, 3, 5, 44]" + "]" + "}, " + ""
				+ "{" + "  low: 0," + "  showArea: true" + "}" + "" + ");" + " var get = function(){return chartist};";

		webEngine.executeScript(script);

		Object resultJs = webEngine.executeScript("get()");

		//get line
		JSObject line = (JSObject) resultJs;
		String getKeys = "{var keys = [];for (var key in this) {keys.push(key);} keys;}";
		JSObject linekeys = (JSObject) line.eval(getKeys);

		JSObject options = (JSObject) line.eval("this.options");
		JSObject optionkeys = (JSObject) options.eval(getKeys);

		options.eval("this.showLine=false");

	}

	@Override
	protected void layoutChildren() {
		double w = getWidth();
		double h = getHeight();
		layoutInArea(browser, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
	}

	@Override
	protected double computePrefWidth(double height) {
		return 750;
	}

	@Override
	protected double computePrefHeight(double width) {
		return 500;
	}
}
