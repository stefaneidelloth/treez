package org.treez.javafxd3.plotly;

import org.treez.javafxd3.d3.wrapper.JavaScriptObject;

import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

public class Layout extends JavaScriptObject {

	//#region ATTRIBUTES

	

	//#end region

	//#region CONSTRUCTORS

	public Layout(WebEngine webEngine, JSObject jsObject) {
		super(webEngine,jsObject);		
	}
	
	public Layout(WebEngine webEngine) {
		super(webEngine);
		setEmptyObjectAsJsObject();	
	}

	//#end region

	//#region METHODS	

	//#end region

}
