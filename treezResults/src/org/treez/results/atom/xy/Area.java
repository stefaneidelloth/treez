package org.treez.results.atom.xy;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.functions.AxisScaleFirstDatumFunction;
import org.treez.javafxd3.d3.functions.AxisScaleSecondDatumFunction;
import org.treez.javafxd3.d3.scales.QuantitativeScale;
import org.treez.results.atom.graphicspage.GraphicsPropertiesPageFactory;

/**
 * XY area settings
 */
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

	/**
	 *
	 */
	//public final Attribute<Boolean> belowHideErrorFill = new Wrap<>();

	/**
	 * Color of the "above area" (=area above the plot line)
	 */
	public final Attribute<String> aboveColor = new Wrap<>();

	/**
	 *
	 */
	//public final Attribute<String> aboveFillStyle = new Wrap<>();

	/**
	 * Transparency of the "above area"
	 */
	public final Attribute<String> aboveTransparency = new Wrap<>();

	/**
	 * Hides the "above area"
	 */
	public final Attribute<Boolean> aboveHide = new Wrap<>();

	/**
	 *
	 */
	//public final Attribute<Boolean> aboveHideErrorFill = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page fillPage = root.createPage("fill", "   Fill     ");

		//#region fill above section

		Section fillAbove = fillPage.createSection("fillAbove", false);
		fillAbove.setTitle("Fill above");

		fillAbove.createColorChooser(aboveColor, "color", "black");

		//fillAbove.createFillStyle(aboveFillStyle, "style", "Style");

		fillAbove.createTextField(aboveTransparency, "transparency", "0");

		fillAbove.createCheckBox(aboveHide, "hide", true);

		//fillAbove.createCheckBox(aboveHideErrorFill, "hideErrorFill", "Hide error fill");

		//#region fill below section

		Section fillBelow = fillPage.createSection("fillBelow", "Fill below");

		fillBelow.createColorChooser(belowColor, "color", "black");

		//fillBelow.createFillStyle(belowFillStyle, "style", "Style");

		fillBelow.createTextField(belowTransparency, "transparency", "0");

		fillBelow.createCheckBox(belowHide, "hide", true);

		//fillBelow.createCheckBox(belowHideErrorFill, "hideErrorFill", "Hide error fill");

		// #end region
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection xySelection, Selection rectSelection, GraphicsAtom parent) {

		Xy xy = (Xy) parent;
		String xyDataString = xy.getXyDataString();
		QuantitativeScale<?> xScale = xy.getXScale();
		QuantitativeScale<?> yScale = xy.getYScale();

		String modeString = xy.line.interpolation.get();
		org.treez.javafxd3.d3.svg.InterpolationMode mode = org.treez.javafxd3.d3.svg.InterpolationMode
				.fromValue(modeString);

		plotAboveAreaWithD3(d3, xySelection, xyDataString, xScale, yScale, mode);
		plotBelowAreaWithD3(d3, xySelection, xyDataString, xScale, yScale, mode);

		return xySelection;
	}

	private void plotAboveAreaWithD3(
			D3 d3,
			Selection xySelection,
			String xyDataString,
			QuantitativeScale<?> xScale,
			QuantitativeScale<?> yScale,
			org.treez.javafxd3.d3.svg.InterpolationMode mode) {

		//remove old area group if it already exists
		xySelection
				.selectAll("#area-above") //
				.remove();

		//create new area group
		Selection areaAboveSelection = xySelection //
				.append("g") //
				.attr("id", "area-above") //
				.attr("class", "area-above");

		org.treez.javafxd3.d3.svg.Area areaAbovePathGenerator = d3 //
				.svg()//
				.area() //
				.x(new AxisScaleFirstDatumFunction(xScale)) //
				.y1(new AxisScaleSecondDatumFunction(yScale))//
				.interpolate(mode);

		Selection aboveArea = areaAboveSelection //
				.append("path") //
				.attr("d", areaAbovePathGenerator.generate(xyDataString));

		GraphicsAtom.bindStringAttribute(aboveArea, "fill", aboveColor);
		GraphicsAtom.bindTransparency(aboveArea, aboveTransparency);
		GraphicsAtom.bindDisplayToBooleanAttribute("hideAboveArea", aboveArea, aboveHide);
	}

	private void plotBelowAreaWithD3(
			D3 d3,
			Selection xySelection,
			String xyDataString,
			QuantitativeScale<?> xScale,
			QuantitativeScale<?> yScale,
			org.treez.javafxd3.d3.svg.InterpolationMode mode) {

		//remove old area group if it already exists
		xySelection
				.selectAll("#area-below") //
				.remove();

		//create new area group
		Selection areaBelowSelection = xySelection //
				.append("g") //
				.attr("id", "area-below") //
				.attr("class", "area-below");

		double yMin = yScale.apply(0.0).asDouble();

		org.treez.javafxd3.d3.svg.Area areaBelowPathGenerator = d3 //
				.svg()//
				.area() //
				.x(new AxisScaleFirstDatumFunction(xScale)) //
				.y0(yMin) //
				.y1(new AxisScaleSecondDatumFunction(yScale))//
				.interpolate(mode);

		Selection belowArea = areaBelowSelection //
				.append("path") //
				.attr("d", areaBelowPathGenerator.generate(xyDataString));

		GraphicsAtom.bindStringAttribute(belowArea, "fill", belowColor);
		GraphicsAtom.bindTransparency(belowArea, aboveTransparency);
		GraphicsAtom.bindDisplayToBooleanAttribute("hideBelowArea", belowArea, belowHide);
	}

	//#end region

}
