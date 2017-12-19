package org.treez.javafxd3.javafx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.JsEngine;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * A JavaFx Node that shows d3 content on a JavaFx WebView.
 *
 */
@SuppressWarnings("checkstyle:magicnumber")
public class PlainJavaFxD3Browser extends StackPane implements Browser {
	
	private static Logger LOG = Logger.getLogger(PlainJavaFxD3Browser.class);

	//#region ATTRIBUTES

	/**
	 * A JavaFx WebView (= "browser") that shows the html content that is
	 * created using the d3 wrapper
	 */
	protected WebView webView;

	/**
	 * Controls the browser and provides access to JavaScript functionality
	 */
	protected WebEngine engine;

	/**
	 * The d3.js wrapper
	 */
	protected D3 d3;

	/**
	 * This runnable is executed after the initial loading of the browser has
	 * been finished. Put your custom code into this runnable. This work flow
	 * ensures that the JavaScript d3 object already exists in the browser
	 * before your custom code makes use of it.
	 */
	private Runnable loadingFinishedHook;

	protected Boolean enableDebugMode = false;	

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 * 
	 * @param loadingFinishedHook
	 *            This runnable is executed after the initial loading of the
	 *            browser has been finished. Put your custom code into this
	 *            runnable. This work flow ensures that the JavaScript d3 object
	 *            already exists in the browser before your custom code makes
	 *            use of it.
	 */
	public PlainJavaFxD3Browser(Runnable loadingFinishedHook) {
		this.loadingFinishedHook = loadingFinishedHook;
		initialize();
		
		this.setBackground(new Background(new BackgroundFill(Color.CORNFLOWERBLUE, null, null)));
	}

	/**
	 * Constructor with possibility to enable debug mode (= show fire bug)
	 */
	public PlainJavaFxD3Browser(Runnable loadingFinishedHook, Boolean enableDebugMode) {
		this.loadingFinishedHook = loadingFinishedHook;
		this.enableDebugMode = enableDebugMode;
		initialize();
	}

	//#end region

	//#region METHODS

	private void initialize() {		
		
		
			
		
			webView = new WebView();			
			getChildren().add(webView);	
				
			engine = webView.getEngine();
			Objects.requireNonNull(engine);		
			engine.setJavaScriptEnabled(true);		
			engine.setOnAlert((eventArgs) -> {
				String message = eventArgs.getData();
				LOG.debug("PlainJavaFxD3Browser-Alert: " + message);
				showAlert(message);
			});
			
			registerLoadingFinishedHook();
			
			String initialBrowserContent = createInitialBrowserContent();		
			engine.loadContent(initialBrowserContent);

			//delete cookies
			java.net.CookieHandler.setDefault(new java.net.CookieManager());
			
			initializeSize();

			//note: after asynchronous loading has been finished, the
			//loading finished hook will be executed.
		
		

	}

	protected void initializeSize() {
			
	}

	private void registerLoadingFinishedHook() {
		Worker<Void> loadWorker = engine.getLoadWorker();
		ReadOnlyObjectProperty<State> state = loadWorker.stateProperty();
		state.addListener((obs, oldState, newState) -> {

			boolean isSucceeded = (newState == Worker.State.SUCCEEDED);
			if (isSucceeded) {
				injectJavaScriptAndCss();
				if (loadingFinishedHook != null) {
					try {
						loadingFinishedHook.run();
					} catch (Exception exception) {
						String message = "Could not execute loading finished hook";
						LOG.error(message, exception);
					}
					
				}
			}

			boolean isFailed = (newState == Worker.State.FAILED);
			if (isFailed) {
				LOG.error("Loading initial html page failed");
			}

		});
	}

	protected void injectJavaScriptAndCss() {
		injectD3();		
		injectJQuery();
		injectMaterialize();
		if (enableDebugMode) {
			injectFireBug();
		}
	}

	protected void injectD3() {
		// https://github.com/mbostock/d3/blob/master/d3.min.js
		String d3Content = getTextFromFile("d3.min.js");
		engine.executeScript(d3Content);
		
		createD3Wrapper();
	}	
	
	protected void injectJQuery() {
		// https://code.jquery.com/jquery-2.2.4.js
		String jQueryContent = getTextFromFile("jquery-3.2.1.min.js");
		engine.executeScript(jQueryContent);
	}
	
	protected void injectMaterialize() {		
		
		// https://github.com/Dogfalo/materialize/releases/latest
		String styleSheet = getTextFromFile("materialize/css/materialize.min.css");
				
		d3.select("head") //
		.append("style") //
		.attr("type","text/css") //
		.attr("media", "screen,projection") //
		.html(styleSheet);
				
		String materializeJsContent = getTextFromFile("materialize/js/materialize.min.js");			
		engine.executeScript(materializeJsContent);
	}	

	protected void injectFireBug() {
		// also see 
		// https://stackoverflow.com/questions/9398879/html-javascript-debugging-in-javafx-webview
		// and
		// https://getfirebug.com/firebug-lite.js#startOpened
		//
		String fireBugCommand = "if (!document.getElementById('FirebugLite')){"
				+ "E = document['createElement' + 'NS'] && " + "document.documentElement.namespaceURI;E = E ? "
				+ "document['createElement' + 'NS'](E, 'script') : " + "document['createElement']('script');"
				+ "E['setAttribute']('id', 'FirebugLite');"
				+ "E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');"
				+ "E['setAttribute']('FirebugLite', '4');"
				+ "(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);"
				+ "E = new Image;" + "E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');" + "}";

		engine.executeScript(fireBugCommand);
	}

	private void createD3Wrapper() {
		d3 = new D3(getJsEngine());
	}	

	public void showAlert(String message) {
		Runnable alertRunnable = () -> {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Alert");
			alert.setHeaderText(message);
			alert.showAndWait();
		};
		Platform.runLater(alertRunnable);
	}

	/**
	 * Creates the html content that will be initially loaded in the browser
	 * before executing any JavaScript
	 */
	protected String createInitialBrowserContent() {	
				
		String htmlContent = "<!DOCTYPE html>\n" //
				+ "<meta charset=\"utf-8\">\n" //	
				+ "<html style=\"font-family: 'Segoe UI', Consolas,monospace;\">\n" //
				+ "  <head></head>\n" //
				+ "  <body style = \"margin:0;padding:0;\">" //					
				+ "    <div id = \"root\" " //				
				+ "          style = \"margin:0;padding:0\""//
				+ "    >\n" //				
				+ "    </div>\n" //
				+ "  </body>" //				
		        + "</html>\n";

		return htmlContent;		
		
	}
		

	protected String getTextFromFile(String fileName) {

		StringBuilder libraryContents = new StringBuilder();

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
					
			if(inputStream==null){
				throw new IllegalStateException("Could not read file " + fileName);
			}		

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));
			String line = reader.readLine();
			while (line != null) {
				libraryContents.append(line);
				line = reader.readLine();
			}
		} catch (IOException exception) {
			throw new IllegalStateException("Could not read file " + fileName);
		}
		return libraryContents.toString();

	}		

	//#end region

	//#region ACCESSORS

	@Override
	public D3 getD3() {
		if (d3 == null) {
			String message = "The d3 reference is null. Do not call this method directly but use "
					+ "the post loading hook to wait " + "until the initial loading of the browser has been finished.";
			throw new IllegalStateException(message);
		}
		return d3;
	}	

	@Override
	public JsEngine getJsEngine() {
		return new JavaFxJsEngine(engine);
	}	

	//#end region

}