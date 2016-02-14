package org.treez.results.atom.xy;

import java.util.function.Consumer;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.attribute.SymbolStyleValue;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.core.atom.graphics.length.Length;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.functions.AxisTransformPointDatumFunction;
import org.treez.javafxd3.d3.scales.QuantitativeScale;
import org.treez.javafxd3.d3.svg.SymbolType;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.graphicspage.GraphicsPropertiesPageFactory;

/**
 * XY symbol settings
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Symbol implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	Selection symbolsSelection;

	/**
	 * Type
	 */
	public final Attribute<String> symbolType = new Wrap<>();

	/**
	 * Square size
	 */
	public final Attribute<String> size = new Wrap<>();

	/**
	 * If this is larger than 1, one the nth symbols will be shown
	 */
	//public final Attribute<String> thinMarkers = new Wrap<>();

	/**
	 * Hides the symbols
	 */
	public final Attribute<Boolean> hide = new Wrap<>();

	/**
	 *
	 */
	//public final Attribute<String> errorStyle = new Wrap<>();

	/**
	 * Fill color
	 */
	public final Attribute<String> fillColor = new Wrap<>();

	/**
	 *
	 */
	//public final Attribute<String> fillStyle = new Wrap<>();

	/**
	 * Fill transparency
	 */
	public final Attribute<String> fillTransparency = new Wrap<>();

	/**
	 * Hides the fill
	 */
	public final Attribute<Boolean> hideFill = new Wrap<>();

	/**
	 * Line color
	 */
	public final Attribute<String> lineColor = new Wrap<>();

	/**
	 * Line width
	 */
	public final Attribute<String> lineWidth = new Wrap<>();

	/**
	 * Line style
	 */
	public final Attribute<String> lineStyle = new Wrap<>();

	/**
	 * Line transparency
	 */
	public final Attribute<String> lineTransparency = new Wrap<>();

	/**
	 * Hides the line
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

		Section symbol = symbolPage.createSection("symbol");

		symbol.createSymbolType(symbolType, "symbol", "Symbol", "circle");

		symbol.createTextField(size, "size", "64");

		//symbol.createTextField(thinMarkers, "thinMarkers", "Thin markers", "1");

		symbol.createCheckBox(hide, "hide");

		//symbol.createErrorBarStyle(errorStyle, "errorStyle", "Error style");

		//#end region

		//#region symbol fill section

		Section fill = symbolPage.createSection("fill", true);

		fill.createColorChooser(fillColor, "color", "black");

		//fill.createFillStyle(fillStyle, "style", "Style");

		fill.createTextField(fillTransparency, "transparency", "0");

		fill.createCheckBox(hideFill, "hide");

		//markerFill.createColorMap(colorMap, "colorMap", "Color map");

		//markerFill.createCheckBox(invertMap, "invertMap", "Invert map");

		//#end region

		//#region symbol line section

		Section markerBorder = symbolPage.createSection("line", true);

		markerBorder.createColorChooser(lineColor, "color", "black");

		markerBorder.createTextField(lineWidth, "width", "0.5");

		markerBorder.createLineStyle(lineStyle, "style", "solid");

		markerBorder.createTextField(lineTransparency, "transparency", "0");

		markerBorder.createCheckBox(hideLine, "hide");

		// #end region
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection xySelection, Selection rectSelection, GraphicsAtom parent) {

		//remove old symbols group if it already exists
		xySelection //
				.select("#symbols") //
				.remove();

		//create new symbols group
		symbolsSelection = xySelection //
				.append("g") //
				.attr("id", "symbols") //
				.attr("class", "symbols") //
				.attr("clip-path", "url(#symbol-clip-path)");

		//create clipping path that ensures that the symbols are only
		//shown within the bounds of the graph
		Graph graph = (Graph) parent.getParentAtom();
		double width = Length.toPx(graph.main.width.get());
		double height = Length.toPx(graph.main.width.get());
		symbolsSelection
				.append("clipPath") //
				.attr("id", "symbol-clip-path") //
				.append("rect") //
				.attr("x", 0) //
				.attr("y", 0) //
				.attr("width", width) //
				.attr("height", height);

		//bind attributes
		GraphicsAtom.bindDisplayToBooleanAttribute("hideSymbols", symbolsSelection, hide);

		Consumer<String> replotSymbols = (data) -> {
			rePlotSymbols(d3, parent);
		};
		symbolType.addModificationConsumer("replotSymbols", replotSymbols);
		size.addModificationConsumer("replotSymbols", replotSymbols);

		//initially plot symbols
		replotSymbols.accept(null);

		//see method replotWithD3
		return xySelection;
	}

	private void rePlotSymbols(D3 d3, GraphicsAtom parent) {

		//remove old symbols
		symbolsSelection
				.selectAll("path") //
				.remove();

		//get symbol type and plot new symbols
		String symbolTypeString = symbolType.get();
		boolean isNoneSymbol = symbolTypeString.equals(SymbolStyleValue.NONE.toString());
		if (!isNoneSymbol) {
			//plot new symbols
			plotNewSymbols(d3, symbolTypeString, parent);
		}
	}

	private void plotNewSymbols(D3 d3, String symbolTypeString, GraphicsAtom parent) {

		SymbolType symbolTypeValue = SymbolType.fromString(symbolTypeString);
		int symbolSquareSize = Integer.parseInt(size.get());

		//symbol path generator
		org.treez.javafxd3.d3.svg.Symbol symbol = d3 //
				.svg() //
				.symbol() //
				.size(symbolSquareSize) //
				.type(symbolTypeValue);
		String symbolDString = symbol.generate();

		//create symbols
		Xy xy = (Xy) parent;
		String xyDataString = xy.getXyDataString();
		QuantitativeScale<?> xScale = xy.getXScale();
		QuantitativeScale<?> yScale = xy.getYScale();

		symbolsSelection
				.selectAll("path") //
				.data(xyDataString) //
				.enter() //
				.append("path") //
				.attr("transform", new AxisTransformPointDatumFunction(xScale, yScale)) //
				.attr("d", symbolDString);

		//bind attributes
		GraphicsAtom.bindStringAttribute(symbolsSelection, "fill", fillColor);
		GraphicsAtom.bindTransparency(symbolsSelection, fillTransparency);
		GraphicsAtom.bindTransparencyToBooleanAttribute(symbolsSelection, hideFill, fillTransparency);

		GraphicsAtom.bindStringAttribute(symbolsSelection, "stroke", lineColor);
		GraphicsAtom.bindLineTransparency(symbolsSelection, lineTransparency);
		GraphicsAtom.bindLineTransparencyToBooleanAttribute(symbolsSelection, hideLine, lineTransparency);

		GraphicsAtom.bindLineStyle(symbolsSelection, lineStyle);

		GraphicsAtom.bindStringAttribute(symbolsSelection, "stroke-width", lineWidth);
	}

	//#end region

}
