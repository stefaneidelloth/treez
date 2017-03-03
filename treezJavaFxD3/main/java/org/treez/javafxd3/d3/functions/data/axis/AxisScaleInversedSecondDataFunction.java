package org.treez.javafxd3.d3.functions.data.axis;

import org.treez.javafxd3.d3.core.JsEngine;
import org.treez.javafxd3.d3.core.JsObject;
import org.treez.javafxd3.d3.functions.DataFunction;
import org.treez.javafxd3.d3.scales.Scale;


public class AxisScaleInversedSecondDataFunction implements DataFunction<Double> {
	
	//#region ATTRIBUTES
	
	private JsEngine engine;
	
	private Scale<?> scale;	
	
	private double maxValue;
	
	//#end region
	
	//#region CONSTRUCTORS
	
	/**
	 * @param engine
	 */
	public AxisScaleInversedSecondDataFunction(JsEngine engine, Scale<?> scale, double maxValue){
		this.engine = engine;
		this.scale = scale;		
		this.maxValue = maxValue;
	}
	
	//#end region
	
	//#region METHODS

	@Override
	public Double apply(Object context, Object datum, int index) {
		
		JsObject jsObject = (JsObject) engine.toJsObjectIfNotSimpleType(datum);	
		
		Object secondValue = jsObject.eval("this[1]");	
				
		Double scaledValue = scale.applyForDouble(secondValue.toString());					
		
		double inversedValue = maxValue-scaledValue;
		return inversedValue;			
	}
	
	//#end region

}
