package org.treez.results.elycharts;

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
		URL url = WebViewSample.class.getResource("elycharts.html");
		String urlPath = url.toExternalForm();
		webEngine.load(urlPath);

	}

	private void executeJavaScript() {

		Object res = webEngine.executeScript("");

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
