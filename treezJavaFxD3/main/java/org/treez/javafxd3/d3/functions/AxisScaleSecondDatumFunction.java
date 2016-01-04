package org.treez.javafxd3.d3.functions;

import org.treez.javafxd3.d3.scales.QuantitativeScale;

import netscape.javascript.JSObject;

/**
 *  A datum function that extracts the second value from a data array
 * and scales it with the given scale
 *  
 */
public class AxisScaleSecondDatumFunction implements DatumFunction<Double> {
	
	//#region ATTRIBUTES
	
	QuantitativeScale<?> scale;	
	
	//#end region
	
	//#region CONSTRUCTORS
	
	/**
	 * @param webEngine
	 */
	public AxisScaleSecondDatumFunction(QuantitativeScale<?> scale){
		this.scale = scale;		
	}
	
	//#end region
	
	//#region METHODS

	@Override
	public Double apply(Object context, Object datum, int index) {
		
		JSObject jsObject = (JSObject) datum;	
		
		Object secondValueObj = jsObject.eval("this.datum[1]");	
		Double secondValue = Double.parseDouble(secondValueObj.toString());		
		
		Double scaledValue = scale.apply(secondValue).asDouble();		
		return scaledValue;			
	}
	
	//#end region

}
