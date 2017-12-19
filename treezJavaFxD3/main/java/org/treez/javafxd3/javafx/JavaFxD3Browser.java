package org.treez.javafxd3.javafx;

import org.treez.javafxd3.functionplot.FunctionPlot;
import org.treez.javafxd3.plotly.Plotly;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

/**
 * A JavaFx Node that shows d3 content on a JavaFx WebView.
 * Also provides access to Nvd3, Plotly, and FunctionPlotter.
 * Allows to save svg content with double click action. 
 *
 */
@SuppressWarnings("checkstyle:magicnumber")
public class JavaFxD3Browser extends PlainJavaFxD3Browser {

	//#region ATTRIBUTES	
	
	private double browserWidth = 500;
	private double browserHeight = 1000;
	
	public static Background BACKGROUND = new Background(new BackgroundFill(Color.CORNFLOWERBLUE, null, null));

	//#end region

	//#region CONSTRUCTORS
	
	public JavaFxD3Browser(Runnable loadingFinishedHook) {
		super(loadingFinishedHook);
	}

	public JavaFxD3Browser(Runnable loadingFinishedHook, Boolean enableDebugMode) {
		super(loadingFinishedHook, enableDebugMode);
		
		
	}

	//#end region

	//#region METHODS	
	
	@Override
	protected void initializeSize() {
		this.setPrefSize(browserWidth, browserHeight);
		webView.setPrefSize(browserWidth, browserHeight);		
	}

	@Override
	protected void injectJavaScriptAndCss() {

		injectD3();
		injectFunctionPlotter();
		injectNvd3();
		injectPlotly();
		injectJQuery();
		injectMaterialize();

		if (enableDebugMode) {
			injectFireBug();
		}
		
		injectSaveHelper();
	}	

	private void injectFunctionPlotter() {
		// https://github.com/maurizzzio/function-plot/blob/master/dist/function-plot.js
		String functionPlotterContent = getTextFromFile("function-plot.js");
		engine.executeScript(functionPlotterContent);
	}

	private void injectNvd3() {
		// https://github.com/novus/nvd3/blob/master/build/nv.d3.min.js
		String nvd3Content = getTextFromFile("nv.d3.min.js");
		engine.executeScript(nvd3Content);
	}	

	private void injectPlotly() {
		// https://github.com/plotly/plotly.js/
		String plotlyContent = getTextFromFile("plotly.min.js");
		engine.executeScript(plotlyContent);
	}	
	
	private void injectSaveHelper() {
		SaveHelper saveHelper = new SaveHelper();
		d3.setMember("saveHelper", saveHelper);
	}

	
	@Override
	protected String createInitialBrowserContent() {	
				
		String htmlContent = "<!DOCTYPE html>\n" //
				+ "<meta charset=\"utf-8\">\n" //	
				+ "<html style=\"font-size:3px;font-family: 'Segoe UI', Consolas,monospace;\">\n" //
				+ "  <head></head>\n" //
				+ "  <body style = \"margin:0;padding:0;font-size:12px;\">" //				
				+ "    <div id=\"invisibleDummyDiv\" "//
				+ "         style=\"display: none;\""//
				+ "    ></div>\n" //
				+ "    <div id = \"root\" " //
				+ "          ondblclick=\"saveSvg()\" "//
				+ "          style = \"margin:0;padding:0\""//
				+ "    >\n" //
				+ "      <svg id=\"svg\" "//
				+ "           class=\"svg\""//
				+ "      ></svg>\n"//
				+ "    </div>\n" //
				+ "  </body>" //
				+ "  <script>\n" //
				+ "    function saveSvg(e){\n" //				
				+ "	     var svg = document.getElementById('svg');\n" //		
				+ "	     var svgXml = (new XMLSerializer).serializeToString(svg);\n" //	
				+ "      d3.saveHelper.saveSvg(svgXml);\n" //
				+ "    }\n" //
				+ "  </script>\n" //
		        + "</html>\n";

		return htmlContent;		
		
	}
	
	@Override
	protected void layoutChildren() {
		double w = getWidth();
		double h = getHeight();
		layoutInArea(webView, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
	}

	@Override
	protected double computePrefWidth(double height) {
		return browserWidth;
	}

	@Override
	protected double computePrefHeight(double width) {
		return browserHeight;
	}

	

	//#end region

	//#region ACCESSORS	

	public FunctionPlot getFunctionPlot() {
		if (d3 == null) {
			String message = "The d3 reference is null. Do not call this method directly but use "
					+ "the post loading hook to wait until the initial loading of the browser has been finished.";
			throw new IllegalStateException(message);
		}

		FunctionPlot functionPlot = new FunctionPlot(getJsEngine());
		return functionPlot;
	}

	public Plotly getPlotly() {
		if (d3 == null) {
			String message = "The d3 reference is null. Do not call this method directly but use "
					+ "the post loading hook to wait until the initial loading of the browser has been finished.";
			throw new IllegalStateException(message);
		}

		Plotly plotly = new Plotly(getJsEngine());
		return plotly;
	}	

	public void setBrowserWidth(double width) {
		browserWidth = width + 4;

		this.setPrefSize(browserWidth, browserHeight);
		webView.setPrefSize(browserWidth, browserHeight);
	}
	
	public void setBrowserHeight(double height) {
		browserHeight = height + 4;
		this.setHeight(height);
		this.setPrefSize(browserWidth, browserHeight);
		webView.setPrefSize(browserWidth, browserHeight);
	}

	//#end region

}