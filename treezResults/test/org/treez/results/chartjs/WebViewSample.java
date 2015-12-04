package org.treez.results.chartjs;

import java.net.URL;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
		URL url = ChartJsTest.class.getResource("chartjs.html");
		String urlPath = url.toExternalForm();
		webEngine.load(urlPath);

	}

	private void executeJavaScript() {

		String getKeys = "{var keys = [];for (var key in this) {keys.push(key);} keys;}";

		String script = "var ctx = document.getElementById(\"canvas\").getContext(\"2d\");"
				+ "var chart = new Chart(ctx).Line(lineChartData);" + "window.myLine = chart;" + "var obj = new Object;"
				+ "obj.name = \"hallo\";" + "obj.chart = chart;" + "var get = function(){return obj};";

		Object res = webEngine.executeScript(script);

		Object result = webEngine.executeScript("get();");
		JSObject resultJs = (JSObject) result;
		JSObject chart = (JSObject) resultJs.eval("this.chart");

		JSObject keys = (JSObject) chart.eval(getKeys);

		chart.eval("this.addData([40], 'foo');");
		chart.eval("this.options.datasetFill=false;");

		String key = (String) keys.getSlot(0);

	}

	private static Node createSpacer() {
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		return spacer;
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
