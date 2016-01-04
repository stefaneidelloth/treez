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
import org.treez.results.atom.veuszpage.GraphicsPageModel;

/**
 * XY area settings
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Area implements GraphicsPageModel {

	//#region ATTRIBUTES

	/**
	 *
	 */
	public final Attribute<String> belowColor = new Wrap<>();

	/**
	 *
	 */
	//public final Attribute<String> belowFillStyle = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> belowTransparency = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<Boolean> belowHide = new Wrap<>();

	/**
	 *
	 */
	//public final Attribute<Boolean> belowHideErrorFill = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> aboveColor = new Wrap<>();

	/**
	 *
	 */
	//public final Attribute<String> aboveFillStyle = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> aboveTransparency = new Wrap<>();

	/**
	 *
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

		Section fillAbove = fillPage.createSection("fillAbove", "Fill above", false);

		fillAbove.createColorChooser(aboveColor, "color", "Color", "black");

		//fillAbove.createFillStyle(aboveFillStyle, "style", "Style");

		fillAbove.createTextField(aboveTransparency, "transparency", "0");

		fillAbove.createCheckBox(aboveHide, "hide", "Hide", true);

		//fillAbove.createCheckBox(aboveHideErrorFill, "hideErrorFill", "Hide error fill");

		//#region fill below section

		Section fillBelow = fillPage.createSection("fillBelow", "Fill below");

		fillBelow.createColorChooser(belowColor, "color", "Color", "black");

		//fillBelow.createFillStyle(belowFillStyle, "style", "Style");

		fillBelow.createTextField(belowTransparency, "transparency", "Transparency", "0");

		fillBelow.createCheckBox(belowHide, "hide", "Hide", true);

		//fillBelow.createCheckBox(belowHideErrorFill, "hideErrorFill", "Hide error fill");

		// #end region
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection graphSelection, Selection rectSelection, GraphicsAtom parent) {
		//see replotWithD3
		return graphSelection;
	}

	/**
	 * @param d3
	 * @param xySelection
	 * @param parent
	 * @param xyDataString
	 * @param xScale
	 * @param yScale
	 */
	public void replotWithD3(
			D3 d3,
			Selection xySelection,
			GraphicsAtom parent,
			String xyDataString,
			QuantitativeScale<?> xScale,
			QuantitativeScale<?> yScale) {

		Xy xy = (Xy) parent;
		String modeString = xy.line.interpolationMode.get();
		org.treez.javafxd3.d3.svg.InterpolationMode mode = org.treez.javafxd3.d3.svg.InterpolationMode
				.fromValue(modeString);

		plotAboveAreaWithD3(d3, xySelection, parent, xyDataString, xScale, yScale, mode);
		plotBelowAreaWithD3(d3, xySelection, parent, xyDataString, xScale, yScale, mode);

		xy.line.replotWithD3(d3, xySelection, parent, xyDataString, xScale, yScale);

	}

	private void plotAboveAreaWithD3(
			D3 d3,
			Selection xySelection,
			GraphicsAtom parent,
			String xyDataString,
			QuantitativeScale<?> xScale,
			QuantitativeScale<?> yScale,
			org.treez.javafxd3.d3.svg.InterpolationMode mode) {
		xySelection
				.selectAll("#area-above") //
				.remove();

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

		parent.bindStringAttribute(aboveArea, "fill", aboveColor);

		aboveTransparency.addModificationConsumer("updateTransparency", (data) -> {
			try {
				double transparency = Double.parseDouble(aboveTransparency.get());
				double opacity = 1 - transparency;
				aboveArea.attr("fill-opacity", "" + opacity);
			} catch (NumberFormatException exception) {

			}
		});

		parent.bindDisplayToBooleanAttribute("hideAboveArea", aboveArea, aboveHide);
	}

	private void plotBelowAreaWithD3(
			D3 d3,
			Selection xySelection,
			GraphicsAtom parent,
			String xyDataString,
			QuantitativeScale<?> xScale,
			QuantitativeScale<?> yScale,
			org.treez.javafxd3.d3.svg.InterpolationMode mode) {

		xySelection
				.selectAll("#area-below") //
				.remove();

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

		parent.bindStringAttribute(belowArea, "fill", belowColor);

		belowTransparency.addModificationConsumer("updateTransparency", (data) -> {
			try {
				double transparency = Double.parseDouble(belowTransparency.get());
				double opacity = 1 - transparency;
				belowArea.attr("fill-opacity", "" + opacity);
			} catch (NumberFormatException exception) {

			}
		});

		parent.bindDisplayToBooleanAttribute("hideBelowArea", belowArea, belowHide);
	}

	@Override
	public String createVeuszText(AbstractAtom parent) {

		String veuszString = "";

		return veuszString;
	}

	//#end region

}
