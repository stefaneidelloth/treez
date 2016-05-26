package org.treez.javafxd3.plotly;

import org.treez.javafxd3.d3.arrays.Array;

import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

public class SingleData extends Array<Data> {

	//#region ATTRIBUTES

	Data data;

	//#end region

	//#region CONSTRUCTORS

	public SingleData(WebEngine webEngine, JSObject jsObject) {
		super(webEngine, jsObject);
	}

	public SingleData(WebEngine webEngine) {
		super(webEngine, null);

		String dummyName = createNewTemporaryInstanceName();
		webEngine.executeScript("var " + dummyName + "=[{}];");
		JSObject jsObject = (JSObject) webEngine.executeScript(dummyName);
		setJsObject(jsObject);
		JSObject firstEntry = evalForJsObject("this[0]");
		data = new Data(webEngine, firstEntry);
	}

	//#end region

	//#region METHODS	

	//#region TYPE

	public void setType(PlotlyType type) {
		data.setType(type);
	}

	public PlotlyType getType() {
		return data.getType();
	}

	//#end region

	//#region X

	public void setX(Double[] xData) {
		data.setX(xData);
	}

	public void setX(Array<Double> xData) {
		data.setX(xData);
	}

	public Array<Double> getX() {
		return data.getX();
	}

	//#end region

	//#region Y

	public void setY(Double[] yData) {
		data.setY(yData);
	}

	public void setY(Array<Double> yData) {
		data.setY(yData);
	}

	public Array<Double> getY() {
		return data.getY();
	}

	//#end region

	//#region Z

	public void setZ(Double[][] zData) {
		data.setZ(zData);
	}

	public void setZ(Array<Double> yData) {
		data.setZ(yData);
	}

	public Array<Double> getZ() {
		return data.getZ();
	}

	//#end region

	//#region VALUES

	public void setValues(Double[] valueData) {
		data.setValues(valueData);
	}

	public void setValues(Array<Double> valueData) {
		data.setValues(valueData);
	}

	public Array<Double> getValues() {
		return data.getValues();
	}

	//#end region

	//#region LABELS

	public void setLabels(String[] labels) {
		data.setLabels(labels);
	}

	public void setLabels(Array<String> labels) {
		data.setLabels(labels);
	}

	public Array<String> getLabels() {
		return data.getLabels();
	}

	//#end region

	//#region MARKER

	public void setMarker(Marker marker) {
		data.setMarker(marker);
	}

	public Marker getMarker() {
		return data.getMarker();
	}

	//#end region

	//#end region

}
