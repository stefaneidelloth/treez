package org.treez.javafxd3.d3.functions;

import org.treez.javafxd3.d3.scales.QuantitativeScale;
import org.treez.javafxd3.d3.scales.Scale;

import netscape.javascript.JSObject;

public class AxisScaleSizeDatumFunction implements DatumFunction<Double> {
	
	//#region ATTRIBUTES
	
	Scale<?> scale;	
	
	//#end region
	
	//#region CONSTRUCTORS
	
	/**
	 * @param webEngine
	 */
	public AxisScaleSizeDatumFunction(Scale<?> scale){
		this.scale = scale;		
	}
	
	//#end region
	
	//#region METHODS

	@Override
	public Double apply(Object context, Object datum, int index) {
		
		JSObject jsObject = (JSObject) datum;	
		
		Object secondValueObj = jsObject.eval("this.datum.size");			
		Double scaledRightValue = scale.applyForDouble(secondValueObj.toString());	
		Double scaledLeftValue = scale.applyForDouble("0.0");	
		Double size = scaledRightValue-scaledLeftValue;		
		return size;		
	}
	
	//#end region

}
