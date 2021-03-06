package org.treez.javafxd3.d3.svg;

import org.treez.javafxd3.d3.functions.DataFunction;

import org.treez.javafxd3.d3.core.JsEngine;
import org.treez.javafxd3.d3.core.JsObject;

/**
 * Generates path data for a closed piecewise linear curve, or polygon, as in an
 * area chart:
 * <p>
 * <img src="https://github.com/mbostock/d3/wiki/area.png"/>
 * <p>
 * Conceptually, the polygon is formed using two lines: the top line is formed
 * using the x- and y1-accessor functions, and proceeds from left-to-right; the
 * bottom line is added to this line, using the x- and y0-accessor functions,
 * and proceeds from right-to-left. By setting the transform attribute to rotate
 * the path element by 90 degrees, you can also generate vertical areas. By
 * changing the interpolation, you can also generate splines and step functions.
 * <p>
 * The area generator is designed to work in conjunction with the line
 * generator. For example, when producing an area chart, you might use an area
 * generator with a fill style, and a line generator with a stroke style to
 * emphasize the top edge of the area. Since the area generator is only used to
 * set the d attribute, you can control the appearance of the area using
 * standard SVG styles and attributes, such as fill.
 * <p>
 * To create streamgraphs (stacked area charts), use the stack layout. This
 * layout sets the y0 attribute for each value in a series, which can be used
 * from the y0- and y1-accessors. Note that each series must have the same
 * number of values per series, and each value must have the same x-coordinate;
 * if you have missing data or inconsistent x-coordinates per series, you must
 * resample and interpolate your data before computing the stacked layout.
 * <p>
 */
public class Area extends PathDataGenerator {

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 * 
	 * @param engine
	 * @param wrappedJsObject
	 */
	public Area(JsEngine engine, JsObject wrappedJsObject) {
		super(engine, wrappedJsObject);
	}

	//#end region

	//#region METHODS

	/**
	 * Returns the current interpolation mode.
	 * 
	 * @return the current interpolation mode.
	 */
	public InterpolationMode interpolate() {
		String mode = callForString("interpolate");
		if (mode == null) {
			return null;
		}
		InterpolationMode result = InterpolationMode.fromValue(mode);
		return result;
	}

	/**
	 * Set the current interpolation mode.
	 * 
	 * @param i
	 *            the interpolation mode
	 * @return the current area
	 */
	public Area interpolate(final InterpolationMode mode) {
		String modeString = mode.getValue();
		JsObject result = call("interpolate", modeString);
		if (result == null) {
			return null;
		}
		return new Area(engine, result);
	}

	/**
	 * Set the function used to compute x coordinates of points generated by
	 * this area generator. The function is invoked for each element in the data
	 * array passed to the area generator.
	 * <p>
	 * The default accessor assumes that each input element is a two-element
	 * array of numbers.
	 * 
	 * @param datumFunction
	 * @return
	 */
	public Area x(final DataFunction<?> callback) {

		assertObjectIsNotAnonymous(callback);

		String memberName = createNewTemporaryInstanceName();
		JsObject d3jsObj = getD3();
		d3jsObj.setMember(memberName, callback);

		String command = "this.x(function(d, i) { " + //
				"   return d3." + memberName + ".apply(this,d,i); " + //
				"});";
		JsObject result = evalForJsObject(command);
		if (result == null) {
			return null;
		}
		return new Area(engine, result);
	}

	/**
	 * Set the x coordinates of points generated by this generator.
	 * 
	 * @param d
	 * @return
	 */
	public Area x(double d) {
		JsObject result = call("x", d);
		if (result == null) {
			return null;
		}
		return new Area(engine, result);
	}

	/**
	 * Set the x0 coordinates of points generated by this generator.
	 * 
	 * @param d
	 * @return
	 */
	public Area x0(double d) {
		JsObject result = call("x0", d);
		return new Area(engine, result);
	}

	/**
	 * @see #x(DataFunction).
	 * @param callback
	 * @return
	 */
	public Area x0(final DataFunction<Double> callback) {

		assertObjectIsNotAnonymous(callback);

		String memberName = createNewTemporaryInstanceName();
		JsObject d3jsObj = getD3();
		d3jsObj.setMember(memberName, callback);

		String command = "this.x0(function(d, i) { " + //
				"  return d3." + memberName + ".apply(this,d,i); " + //
				"});";
		JsObject result = evalForJsObject(command);
		if (result == null) {
			return null;
		}
		return new Area(engine, result);

	}

	/**
	 * Set the x1 coordinates of points generated by this generator.
	 * 
	 * @param d
	 * @return
	 */
	public Area x1(double d) {
		JsObject result = call("x1", d);
		if (result == null) {
			return null;
		}
		return new Area(engine, result);

	}

	/**
	 * @see #x(DataFunction).
	 * @param callback
	 * @return
	 */
	public Area x1(final DataFunction<Double> callback) {

		assertObjectIsNotAnonymous(callback);

		String memberName = createNewTemporaryInstanceName();
		JsObject d3jsObj = getD3();
		d3jsObj.setMember(memberName, callback);

		String command = "this.x1(function(d, i) { " + //
				"  return d3." + memberName + ".apply(this,d,i); " + //
				"});";
		JsObject result = evalForJsObject(command);
		if (result == null) {
			return null;
		}
		return new Area(engine, result);
	}

	/**
	 * Set the y coordinates of points generated by this generator.
	 * 
	 * @param d
	 * @return
	 */
	public Area y(double d) {
		JsObject result = call("y", d);
		if (result == null) {
			return null;
		}
		return new Area(engine, result);
	}

	/**
	 * Set the y0 coordinates of points generated by this generator.
	 * 
	 * @param d
	 * @return
	 */
	public Area y0(double d) {
		JsObject result = call("y0", d);
		if (result == null) {
			return null;
		}
		return new Area(engine, result);
	}

	/**
	 * Set the y1 coordinates of points generated by this generator.
	 * 
	 * @param d
	 * @return
	 */
	public Area y1(double d) {
		JsObject result = call("y1", d);
		if (result == null) {
			return null;
		}
		return new Area(engine, result);
	}

	/**
	 * See {@link #x(DataFunction)}.
	 * <p>
	 * Note that, like most other graphics libraries, SVG uses the top-left
	 * corner as the origin and thus higher values of y are lower on the screen.
	 * For visualization we often want the origin in the bottom-left corner
	 * instead; one easy way to accomplish this is to invert the range of the
	 * y-scale by using range([h, 0]) instead of range([0, h]).
	 * 
	 * @param callback
	 * @return the current area
	 */
	public Area y(final DataFunction<Double> callback) {

		assertObjectIsNotAnonymous(callback);

		String memberName = createNewTemporaryInstanceName();
		JsObject d3jsObj = getD3();
		d3jsObj.setMember(memberName, callback);

		String command = "this.y(function(d, i) { " + //
				"   return d3." + memberName + ".apply(this,d,i); " + //
				"});";
		JsObject result = evalForJsObject(command);
		if (result == null) {
			return null;
		}
		return new Area(engine, result);
	}

	/**
	 * See {@link #y(DataFunction)}.
	 * <p>
	 * Note that, like most other graphics libraries, SVG uses the top-left
	 * corner as the origin and thus higher values of y are lower on the screen.
	 * For visualization we often want the origin in the bottom-left corner
	 * instead; one easy way to accomplish this is to invert the range of the
	 * y-scale by using range([h, 0]) instead of range([0, h]).
	 * 
	 * @param callback
	 * @return the current area
	 */
	public Area y0(final DataFunction<Double> callback) {

		assertObjectIsNotAnonymous(callback);

		String memberName = createNewTemporaryInstanceName();
		JsObject d3jsObj = getD3();
		d3jsObj.setMember(memberName, callback);

		String command = "this.y0(function(d, i) { " + //
				"   return d3." + memberName + ".apply(this,d,i); " + //
				"});";
		JsObject result = evalForJsObject(command);
		if (result == null) {
			return null;
		}
		return new Area(engine, result);
	}

	/**
	 * See {@link #y(DataFunction)}.
	 * <p>
	 * Note that, like most other graphics libraries, SVG uses the top-left
	 * corner as the origin and thus higher values of y are lower on the screen.
	 * For visualization we often want the origin in the bottom-left corner
	 * instead; one easy way to accomplish this is to invert the range of the
	 * y-scale by using range([h, 0]) instead of range([0, h]).
	 * 
	 * @param callback
	 * @return the current area
	 */
	public Area y1(final DataFunction<Double> callback) {

		assertObjectIsNotAnonymous(callback);

		String memberName = createNewTemporaryInstanceName();
		JsObject d3jsObj = getD3();
		d3jsObj.setMember(memberName, callback);

		String command = "this.y1(function(d, i) { " + //
				"   return d3." + memberName + ".apply(this,d,i); " + //
				" });";
		JsObject result = evalForJsObject(command);
		if (result == null) {
			return null;
		}
		return new Area(engine, result);

	}

	/**
	 * FIXME: D3 bug ??? Does not seem to work...
	 * 
	 * Sets the function used to controls where the area is defined.
	 * <p>
	 * The defined accessor can be used to define where the area is defined and
	 * undefined, which is typically useful in conjunction with missing data;
	 * the generated path data will automatically be broken into multiple
	 * distinct subpaths, skipping undefined data.
	 * <p>
	 * 
	 * @param callback
	 * @return
	 */
	public Line defined(final DataFunction<Boolean> callback) {

		assertObjectIsNotAnonymous(callback);

		String memberName = createNewTemporaryInstanceName();
		JsObject d3jsObj = getD3();
		d3jsObj.setMember(memberName, callback);

		String command = "this.defined(function(d) { " //				
				+ "var result = d3." + memberName + ".apply(null,d, 0); "//				
				+ "return result; "//
				+ "});";

		JsObject result = evalForJsObject(command);
		if (result == null) {
			return null;
		}
		return new Line(engine, result);

	}

	/**
	 * Returns the current tension
	 * 
	 * @return the current tension
	 */
	public double tension() {
		Double result = callForDouble("tension");
		return result;
	}

	/**
	 * Sets the Cardinal spline interpolation tension to the specified number in
	 * the range [0, 1].
	 * <p>
	 * The tension only affects the Cardinal interpolation modes:
	 * {@link InterpolationMode#CARDINAL},
	 * {@link InterpolationMode#CARDINAL_OPEN} and
	 * {@link InterpolationMode#CARDINAL_CLOSED}.
	 * <p>
	 * The default tension is 0.7.
	 * <p>
	 * In some sense, this can be interpreted as the length of the tangent; 1
	 * will yield all zero tangents, and 0 yields a Catmull-Rom spline.
	 * 
	 * @see <a href="http://bl.ocks.org/1016220">live version</a>
	 * @param tension
	 *            the tension in the range [0, 1].
	 * @return the current area generator
	 */
	public Area tension(double tension) {
		JsObject result = call("tension", tension);
		if (result == null) {
			return null;
		}
		return new Area(engine, result);
	}

	/**
	 * Generate a piecewise linear area, as in an area chart.
	 * <p>
	 * Data must be an array-like structure. the type of the array elements
	 * depends on the x and y functions. the default x and y functions assumes
	 * that each input element is a two-element array of numbers.
	 * 
	 * @param data
	 * 
	 * @return
	 */
	public <T> String apply(T data) {
		String result = callThisForString(data);
		return result;
	}

	/**
	 * Generate a piecewise linear area, as in an area chart.
	 * <p>
	 * Data must be an array-like structure. the type of the array elements
	 * depends on the x and y functions. the default x and y functions assumes
	 * that each input element is a two-element array of numbers.
	 * <p>
	 * The index will be passed through to the line's accessor functions.
	 * <p>
	 * 
	 * @param data
	 * @param index
	 * @return
	 */
	public <T> String apply(T data, int index) {
		String result = callForString("this", data, index);
		return result;
	}

	//#end region

}
