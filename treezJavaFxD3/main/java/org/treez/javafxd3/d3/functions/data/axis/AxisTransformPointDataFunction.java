package org.treez.javafxd3.d3.functions.data.axis;

import org.treez.javafxd3.d3.core.JsEngine;
import org.treez.javafxd3.d3.core.JsObject;
import org.treez.javafxd3.d3.functions.DataFunction;
import org.treez.javafxd3.d3.scales.Scale;

/**
 * A datum function that extracts an xy pair, scales the data with the
 * given scales and creates a transform string
 *  
 */
public class AxisTransformPointDataFunction implements DataFunction<String> {
	
	//#region ATTRIBUTES
	
	private JsEngine engine;
	
	private Scale<?> xScale;
	
	private Scale<?> yScale;
	
	//#end region
	
	//#region CONSTRUCTORS	

	public AxisTransformPointDataFunction(JsEngine engine, Scale<?> xScale, Scale<?> yScale){
		this.engine = engine;
		this.xScale = xScale;
		this.yScale = yScale;
	}
	
	//#end region
	
	//#region METHODS

	@Override
	public String apply(Object context, Object datum, int index) {
		
		JsObject jsObject = (JsObject) engine.toJsObjectIfNotSimpleType(datum);	
		
		String x = jsObject.eval("this[0]").toString();			
		String y = jsObject.eval("this[1]").toString();	
		
		
		Double scaledX = xScale.applyForDouble(x);
		Double scaledY = yScale.applyForDouble(y);
		
		String transformString = "translate("+scaledX+","+scaledY+")";
		return transformString;		
	}
	
	//#end region

}
