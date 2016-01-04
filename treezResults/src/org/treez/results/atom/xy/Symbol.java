package org.treez.results.atom.xy;

import java.util.function.Consumer;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.LineStyleValue;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.attribute.SymbolStyleValue;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.functions.AxisTransformPointDatumFunction;
import org.treez.javafxd3.d3.scales.QuantitativeScale;
import org.treez.javafxd3.d3.svg.SymbolType;
import org.treez.results.atom.veuszpage.GraphicsPageModel;

/**
 * XY symbol settings
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Symbol implements GraphicsPageModel {

	//#region ATTRIBUTES

	Selection symbolsSelection;

	/**
	 *
	 */
	public final Attribute<String> symbolType = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> size = new Wrap<>();

	/**
	 * If this is larger than 1, one the nth symbols will be shown
	 */
	//public final Attribute<String> thinMarkers = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<Boolean> hide = new Wrap<>();

	/**
	 *
	 */
	//public final Attribute<String> errorStyle = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> fillColor = new Wrap<>();

	/**
	 *
	 */
	//public final Attribute<String> fillStyle = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> fillTransparency = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<Boolean> hideFill = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> lineColor = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> lineWidth = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> lineStyle = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> lineTransparency = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<Boolean> hideLine = new Wrap<>();

	/**
	 *
	 */
	//public final Attribute<String> colorMap = new Wrap<>();

	/**
	 *
	 */
	//public final Attribute<Boolean> invertMap = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page symbolPage = root.createPage("symbol", "   Symbol   ");

		// #region marker section

		Section symbol = symbolPage.createSection("symbol", "Symbol");

		symbol.createSymbolType(symbolType, "symbol", "Symbol", "circle");

		symbol.createTextField(size, "size", "Size", "64");

		//symbol.createTextField(thinMarkers, "thinMarkers", "Thin markers", "1");

		symbol.createCheckBox(hide, "hide", "Hide");

		//symbol.createErrorBarStyle(errorStyle, "errorStyle", "Error style");

		//#end region

		//#region symbol fill section

		Section fill = symbolPage.createSection("fill", "Fill", true);

		fill.createColorChooser(fillColor, "color", "Color", "black");

		//fill.createFillStyle(fillStyle, "style", "Style");

		fill.createTextField(fillTransparency, "transparency", "Transparency", "0");

		fill.createCheckBox(hideFill, "hide", "Hide");

		//markerFill.createColorMap(colorMap, "colorMap", "Color map");

		//markerFill.createCheckBox(invertMap, "invertMap", "Invert map");

		//#end region

		//#region symbol line section

		Section markerBorder = symbolPage.createSection("line", "Line", true);

		markerBorder.createColorChooser(lineColor, "color", "Color", "black");

		markerBorder.createTextField(lineWidth, "width", "Width", "0.5");

		markerBorder.createLineStyle(lineStyle, "style", "Style");

		markerBorder.createTextField(lineTransparency, "transparency", "Transparency", "0");

		markerBorder.createCheckBox(hideLine, "hide", "Hide");

		// #end region
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection xySelection, Selection rectSelection, GraphicsAtom parent) {
		//see method replotWithD3
		return xySelection;
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

		//remove old symbols group
		xySelection //
				.select("#symbols") //
				.remove();

		//create new symbols group
		symbolsSelection = xySelection //
				.append("g") //
				.attr("id", "symbols") //
				.attr("class", "symbols");

		Consumer<String> dataChangedConsumer = (data) -> {
			rePlotSymbols(d3, parent, xyDataString, xScale, yScale);
		};

		parent.bindDisplayToBooleanAttribute("hideSymbols", symbolsSelection, hide);
		symbolType.addModificationConsumer("replotSymbols", dataChangedConsumer);
		size.addModificationConsumer("replotSymbols", dataChangedConsumer);

		dataChangedConsumer.accept(null);

	}

	private void rePlotSymbols(
			D3 d3,
			GraphicsAtom parent,
			String xyDataString,
			QuantitativeScale<?> xScale,
			QuantitativeScale<?> yScale) {

		String symbolTypeString = symbolType.get();
		boolean isNone = symbolTypeString.equals(SymbolStyleValue.NONE.toString());

		//remove old symbols
		symbolsSelection
				.selectAll("path") //
				.remove();

		if (!isNone) {
			//plot new symbols
			SymbolType symbolTypeValue = SymbolType.fromString(symbolTypeString);
			int symbolSquareSize = Integer.parseInt(size.get());

			org.treez.javafxd3.d3.svg.Symbol symbol = d3 //
					.svg() //
					.symbol() //
					.size(symbolSquareSize) //
					.type(symbolTypeValue);
			String symbolDString = symbol.generate();

			symbolsSelection
					.selectAll("path") //
					.data(xyDataString) //
					.enter() //
					.append("path") //
					.attr("transform", new AxisTransformPointDatumFunction(xScale, yScale)) //
					.attr("d", symbolDString);

			parent.bindStringAttribute(symbolsSelection, "fill", fillColor);
			fillTransparency.addModificationConsumer("updateFillTransparency", (data) -> {
				try {
					double transparency = Double.parseDouble(fillTransparency.get());
					double opacity = 1 - transparency;
					symbolsSelection.attr("fill-opacity", "" + opacity);
				} catch (NumberFormatException exception) {

				}
			});
			hideFill.addModificationConsumer("hideFill", (data) -> {
				try {
					boolean doHide = hideFill.get();
					if (doHide) {
						symbolsSelection.attr("fill-opacity", "0");
					} else {
						double transparency = Double.parseDouble(fillTransparency.get());
						double opacity = 1 - transparency;
						symbolsSelection.attr("fill-opacity", "" + opacity);
					}
				} catch (NumberFormatException exception) {

				}
			});

			parent.bindStringAttribute(symbolsSelection, "stroke", lineColor);

			lineTransparency.addModificationConsumer("updateLineTransparency", (data) -> {
				try {
					double transparency = Double.parseDouble(lineTransparency.get());
					double opacity = 1 - transparency;
					symbolsSelection.attr("stroke-opacity", "" + opacity);
				} catch (NumberFormatException exception) {

				}
			});

			lineStyle.addModificationConsumer("updateLineStyle", (data) -> {
				String lineStyleString = lineStyle.get();
				LineStyleValue lineStyle = LineStyleValue.fromString(lineStyleString);
				String dashArray = lineStyle.getDashArray();
				symbolsSelection.attr("stroke-dasharray", dashArray);
			});

			hideLine.addModificationConsumer("hideLine", (data) -> {
				try {
					boolean doHide = hideLine.get();
					if (doHide) {
						symbolsSelection.attr("stroke-opacity", "0");
					} else {
						double transparency = Double.parseDouble(lineTransparency.get());
						double opacity = 1 - transparency;
						symbolsSelection.attr("stroke-opacity", "" + opacity);
					}
				} catch (NumberFormatException exception) {

				}
			});

			parent.bindStringAttribute(symbolsSelection, "stroke-width", lineWidth);
		}
	}

	@Override
	public String createVeuszText(AbstractAtom parent) {

		String veuszString = "";

		return veuszString;
	}

	//#end region

}
