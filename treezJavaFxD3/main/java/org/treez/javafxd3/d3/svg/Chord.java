package org.treez.javafxd3.d3.svg;

import org.treez.javafxd3.d3.functions.DataFunction;

import org.treez.javafxd3.d3.core.JsEngine;
import org.treez.javafxd3.d3.core.JsObject;

/**
 * Constructs a new chord generator with the default accessor functions (that
 * assume the input data is an object with named attributes matching the
 * accessors; see below for details).
 * <p>
 * While the default accessors assume that the chord dimensions are all
 * specified dynamically, it is very common to set one or more of the dimensions
 * as a constant, such as the radius. The returned function generates path data
 * for a closed shape connecting two arcs with quadratic Bézier curves, as in a
 * chord diagram:
 * 
 * <p>
 * <img src="https://github.com/mbostock/d3/wiki/chord.png">
 * <p>
 * 
 * A chord generator is often used in conjunction with an {@link Arc} generator,
 * so as to draw annular segments at the start and end of the chords. In
 * addition, the {@link org.treez.javafxd3.d3.layout.Chord} layout is useful
 * for generating objects that describe a set of grouped chords from a matrix,
 * compatible with the default accessors.
 */
public class Chord extends PathDataGenerator {

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 * 
	 * @param engine
	 * @param wrappedJsObject
	 */
	public Chord(JsEngine engine, JsObject wrappedJsObject) {
		super(engine, wrappedJsObject);

	}

	//#end region

	//#region METHODS

	/**
	 * Set the source accessor.
	 * <p>
	 * The purpose of the source accessor is to return an object that describes
	 * the starting arc of the chord. The returned object is subsequently passed
	 * to the {@link #radius(DataFunction)}, {@link #startAngle(DataFunction)}
	 * and {@link #endAngle(DataFunction)} accessors.
	 * <p>
	 * This allows these other accessors to be reused for both the source and
	 * target arc descriptions.
	 * <p>
	 * The default accessor assumes that the input data is a JavaScriptObject
	 * with suitably-named attributes.
	 * <p>
	 * The source-accessor is invoked in the same manner as other value
	 * functions in D3.
	 * <p>
	 * 
	 * @param accessor
	 *            the function returning the source arc object
	 * @return the current chord generator
	 */
	public Chord source(final DataFunction<?> accessor) {

		assertObjectIsNotAnonymous(accessor);

		JsObject d3JsObject = getD3();
		String accessorName = createNewTemporaryInstanceName();
		d3JsObject.setMember(accessorName, accessor);

		String command = "this.source(function(d, i) { " //
				+ "return d3." + accessorName + ".apply(this,d,i);"//
				+ " });";
		
		JsObject result = evalForJsObject(command);
		if(result==null){
			return null;
		}
		return new Chord(engine, result);

	}

	/**
	 * Set the target accessor.
	 * <p>
	 * The purpose of the target accessor is to return an object that describes
	 * the ending arc of the chord. The returned object is subsequently passed
	 * to the {@link #radius(DataFunction)}, {@link #startAngle(DataFunction)}
	 * and {@link #endAngle(DataFunction)} accessors.
	 * <p>
	 * This allows these other accessors to be reused for both the source and
	 * target arc descriptions.
	 * <p>
	 * The default accessor assumes that the input data is a JavaScriptObject
	 * with suitably-named attributes.
	 * <p>
	 * The target-accessor is invoked in the same manner as other value
	 * functions in D3.
	 * <p>
	 * 
	 * @param accessor
	 *            the function returning the target arc object
	 * @return the current chord generator
	 */
	public Chord target(final DataFunction<?> accessor) {

		assertObjectIsNotAnonymous(accessor);

		JsObject d3JsObject = getD3();
		String accessorName = createNewTemporaryInstanceName();
		d3JsObject.setMember(accessorName, accessor);

		String command = "this.target(function(d, i) { " //
				+ "return d3." + accessorName + ".apply(this,d,i);"//
				+ " });";
		
		JsObject result = evalForJsObject(command);
		if(result==null){
			return null;
		}
		return new Chord(engine, result);
	}

	/**
	 * Set the radius accessor. The accessor will be invoked passing the source
	 * or target in the value parameter. The accessor must return the radius.
	 * <p>
	 * The default accessor assumes that the input source or target description
	 * is a JavaScriptObject with suitably-named attributes.
	 * <p>
	 * 
	 * @param accessor
	 *            the function returning the radius
	 * @return the current chord generator
	 */
	public Chord radius(final DataFunction<Double> accessor) {

		assertObjectIsNotAnonymous(accessor);

		JsObject d3JsObject = getD3();
		String accessorName = createNewTemporaryInstanceName();
		d3JsObject.setMember(accessorName, accessor);

		String command = "this.radius(function(d, i) { " //
				+ "return d3." + accessorName + ".apply(this,d,i);"//
				+ " });";
		
		JsObject result = evalForJsObject(command);
		if(result==null){
			return null;
		}
		return new Chord(engine, result);
	}

	/**
	 * Set the radius as a constant.
	 * <p>
	 * 
	 * @param radius
	 *            the radius
	 * @return the current chord generator
	 */
	public Chord radius(final double radius) {
		JsObject result = call("radius", radius);
		return new Chord(engine, result);
	}

	/**
	 * Set the start angle accessor. The accessor will be invoked passing the
	 * source or target in the value parameter. The accessor must return the
	 * start angle in radians.
	 * <p>
	 * Angles are specified in radians, even though SVG typically uses degrees.
	 * <p>
	 * The default accessor assumes that the input source or target description
	 * is a JavaScriptObject with suitably-named attributes.
	 * <p>
	 * 
	 * @param accessor
	 *            the function returning the start angle
	 * @return the current chord generator
	 */
	public Chord startAngle(final DataFunction<Double> accessor) {

		assertObjectIsNotAnonymous(accessor);

		JsObject d3JsObject = getD3();
		String accessorName = createNewTemporaryInstanceName();
		d3JsObject.setMember(accessorName, accessor);

		String command = "this.startAngle(function(d, i) { " //
				+ "return d3." + accessorName + ".apply(this,d,i);"//
				+ " });";
		
		JsObject result = evalForJsObject(command);
		if(result==null){
			return null;
		}
		return new Chord(engine, result);
		
	}

	/**
	 * Set the start angle as a constant in radians.
	 * <p>
	 * Angles are specified in radians, even though SVG typically uses degrees.
	 * <p>
	 * 
	 * @param startAngle
	 *            the angle in radians
	 * @return the current chord generator
	 */
	public Chord startAngle(final double startAngle) {
		JsObject result = call("startAngle", startAngle);
		return new Chord(engine, result);
	}

	/**
	 * Set the end angle accessor. The accessor will be invoked passing the
	 * source or target in the value parameter. The accessor must return the end
	 * angle in radians.
	 * <p>
	 * Angles are specified in radians, even though SVG typically uses degrees.
	 * <p>
	 * The default accessor assumes that the input source or target description
	 * is a JavaScriptObject with suitably-named attributes.
	 * <p>
	 * 
	 * @param accessor
	 *            the function returning the end angle
	 * @return the current chord generator
	 */
	public Chord endAngle(final DataFunction<Double> accessor) {

		assertObjectIsNotAnonymous(accessor);

		JsObject d3JsObject = getD3();
		String accessorName = createNewTemporaryInstanceName();
		d3JsObject.setMember(accessorName, accessor);

		String command = "this.endAngle(function(d, i) { " //
				+ "return d3." + accessorName + ".apply(this,d,i);"//
				+ " });";
		
		JsObject result = evalForJsObject(command);
		if(result==null){
			return null;
		}
		return new Chord(engine, result);
	}

	/**
	 * Set the end angle as a constant in radians.
	 * <p>
	 * Angles are specified in radians, even though SVG typically uses degrees.
	 * <p>
	 * 
	 * @param endAngle
	 *            the angle in radians
	 * @return the current chord generator
	 */
	public Chord endAngle(final double endAngle) {
		JsObject result = call("endAngle", endAngle);
		return new Chord(engine, result);
	}

	//#end region
}
