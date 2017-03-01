package org.treez.results.atom.xy;

import java.util.List;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.ColorChooser;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.AbstractGraphicsAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.JsEngine;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.functions.data.axis.AxisScaleFirstDataFunction;
import org.treez.javafxd3.d3.functions.data.axis.AxisScaleSecondDataFunction;
import org.treez.javafxd3.d3.scales.QuantitativeScale;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Area implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	/**
	 * Color of the "below area" (= area below the plot line)
	 */
	public final Attribute<String> belowColor = new Wrap<>();

	/**
	 *
	 */
	//public final Attribute<String> belowFillStyle = new Wrap<>();

	/**
	 * Transparency of the "below area"
	 */
	public final Attribute<String> belowTransparency = new Wrap<>();

	/**
	 * Hides the "below area"
	 */
	public final Attribute<Boolean> belowHide = new Wrap<>();

	//public final Attribute<Boolean> belowHideErrorFill = new Wrap<>();

	/**
	 * Color of the "above area" (=area above the plot line)
	 */
	public final Attribute<String> aboveColor = new Wrap<>();

	//public final Attribute<String> aboveFillStyle = new Wrap<>();

	/**
	 * Transparency of the "above area"
	 */
	public final Attribute<Double> aboveTransparency = new Wrap<>();

	/**
	 * Hides the "above area"
	 */
	public final Attribute<Boolean> aboveHide = new Wrap<>();

	//public final Attribute<Boolean> aboveHideErrorFill = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom<?> parent) {

		Page fillPage = root.createPage("fill", "   Fill     ");

		//#region fill above section

		Section fillAbove = fillPage.createSection("fillAbove", false);
		fillAbove.setLabel("Fill above");

		fillAbove.createColorChooser(aboveColor, this, "black").setLabel("Color");

		//fillAbove.createFillStyle(aboveFillStyle, "style", "Style");

		fillAbove.createDoubleVariableField(aboveTransparency, this, 0.0).setLabel("Transparency");

		fillAbove.createCheckBox(aboveHide, this, true).setLabel("Hide");

		//fillAbove.createCheckBox(aboveHideErrorFill, "hideErrorFill", "Hide error fill");

		//#region fill below section

		Section fillBelow = fillPage.createSection("fillBelow", "Fill below");

		ColorChooser belowColorChooser = fillBelow.createColorChooser(belowColor, this, "black").setLabel("Color");

		//fillBelow.createFillStyle(belowFillStyle, "style", "Style");

		fillBelow.createTextField(belowTransparency, this, "0").setLabel("Transparency");

		fillBelow.createCheckBox(belowHide, this, true).setLabel("Hide");

		//fillBelow.createCheckBox(belowHideErrorFill, "hideErrorFill", "Hide error fill");

		//#end region
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection xySelection, Selection rectSelection, AbstractGraphicsAtom parent) {

		Xy xy = (Xy) parent;
		String parentName = xy.getName();

		List<Double> xData = xy.getXDataAsDoubles();
		List<Double> yData = xy.getYDataAsDoubles();

		String xyDataString = xy.createXyDataString(xData, yData);
		QuantitativeScale<?> xScale = xy.getXScale();
		QuantitativeScale<?> yScale = xy.getYScale();

		String modeString = xy.line.interpolation.get();
		org.treez.javafxd3.d3.svg.InterpolationMode mode = org.treez.javafxd3.d3.svg.InterpolationMode
				.fromValue(modeString);

		plotAboveAreaWithD3(d3, parentName, xySelection, xyDataString, xScale, yScale, mode);
		plotBelowAreaWithD3(d3, parentName, xySelection, xyDataString, xScale, yScale, mode);

		return xySelection;
	}

	private void plotAboveAreaWithD3(
			D3 d3,
			String parentName,
			Selection xySelection,
			String xyDataString,
			QuantitativeScale<?> xScale,
			QuantitativeScale<?> yScale,
			org.treez.javafxd3.d3.svg.InterpolationMode mode) {

		String id = "area-above_" + parentName;

		//remove old area group if it already exists
		xySelection.selectAll("#" + id) //
				.remove();

		//create new area group
		Selection areaAboveSelection = xySelection //
				.append("g") //
				.attr("id", id) //
				.attr("class", "area-above");

		JsEngine engine = xySelection.getJsEngine();

		org.treez.javafxd3.d3.svg.Area areaAbovePathGenerator = d3 //
				.svg()//
				.area() //
				.x(new AxisScaleFirstDataFunction(engine, xScale)) //
				.y1(new AxisScaleSecondDataFunction(engine, yScale))//
				.interpolate(mode);

		Selection aboveArea = areaAboveSelection //
				.append("path") //
				.attr("d", areaAbovePathGenerator.generate(xyDataString));

		AbstractGraphicsAtom.bindStringAttribute(aboveArea, "fill", aboveColor);
		AbstractGraphicsAtom.bindTransparency(aboveArea, aboveTransparency);
		AbstractGraphicsAtom.bindDisplayToBooleanAttribute("hideAboveArea", aboveArea, aboveHide);
	}

	private void plotBelowAreaWithD3(
			D3 d3,
			String parentName,
			Selection xySelection,
			String xyDataString,
			QuantitativeScale<?> xScale,
			QuantitativeScale<?> yScale,
			org.treez.javafxd3.d3.svg.InterpolationMode mode) {

		String id = "area-below_" + parentName;

		//remove old area group if it already exists
		xySelection.selectAll("#" + id) //
				.remove();

		//create new area group
		Selection areaBelowSelection = xySelection //
				.append("g") //
				.attr("id", id) //
				.attr("class", "area-below");

		double yMin = yScale.apply(0.0).asDouble();

		JsEngine engine = xySelection.getJsEngine();

		org.treez.javafxd3.d3.svg.Area areaBelowPathGenerator = d3 //
				.svg()//
				.area() //
				.x(new AxisScaleFirstDataFunction(engine, xScale)) //
				.y0(yMin) //
				.y1(new AxisScaleSecondDataFunction(engine, yScale))//
				.interpolate(mode);

		Selection belowArea = areaBelowSelection //
				.append("path") //
				.attr("d", areaBelowPathGenerator.generate(xyDataString));

		AbstractGraphicsAtom.bindStringAttribute(belowArea, "fill", belowColor);
		AbstractGraphicsAtom.bindTransparency(belowArea, aboveTransparency);
		AbstractGraphicsAtom.bindDisplayToBooleanAttribute("hideBelowArea", belowArea, belowHide);
	}

	//#end region

}
