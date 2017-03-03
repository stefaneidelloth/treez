package org.treez.javafxd3.d3.functions.data.axis;

import org.treez.javafxd3.d3.core.JsEngine;
import org.treez.javafxd3.d3.core.JsObject;
import org.treez.javafxd3.d3.functions.DataFunction;
import org.treez.javafxd3.d3.scales.Scale;

public class AxisScaleSecondDataFunction implements DataFunction<Double> {

	//#region ATTRIBUTES

	private JsEngine engine;

	private Scale<?> scale;

	//#end region

	//#region CONSTRUCTORS

	public AxisScaleSecondDataFunction(JsEngine engine, Scale<?> scale) {
		this.engine = engine;
		this.scale = scale;
	}

	//#end region

	//#region METHODS

	@Override
	public Double apply(Object context, Object datum, int index) {

		JsObject jsObject = (JsObject) engine.toJsObjectIfNotSimpleType(datum);

		Object secondValueObj = jsObject.eval("this[1]");

		Double scaledValue = scale.applyForDouble(secondValueObj.toString());
		return scaledValue;

	}

	//#end region

}
