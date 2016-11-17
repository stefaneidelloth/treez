package org.treez.results.atom.xy;

import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.attribute.SymbolStyleValue;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.AbstractGraphicsAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.atom.graphics.length.Length;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Consumer;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.functions.AxisTransformPointDatumFunction;
import org.treez.javafxd3.d3.scales.QuantitativeScale;
import org.treez.javafxd3.d3.svg.SymbolType;
import org.treez.results.atom.graph.Graph;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Symbol implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	Selection symbolsSelection;

	public final Attribute<String> symbolType = new Wrap<>();

	public final Attribute<String> size = new Wrap<>();

	/**
	 * If this is larger than 1, one the nth symbols will be shown
	 */
	//public final Attribute<String> thinMarkers = new Wrap<>();

	public final Attribute<Boolean> hide = new Wrap<>();

	//public final Attribute<String> errorStyle = new Wrap<>();

	public final Attribute<String> fillColor = new Wrap<>();

	//public final Attribute<String> fillStyle = new Wrap<>();

	public final Attribute<Double> fillTransparency = new Wrap<>();

	public final Attribute<Boolean> hideFill = new Wrap<>();

	public final Attribute<String> lineColor = new Wrap<>();

	public final Attribute<String> lineWidth = new Wrap<>();

	public final Attribute<String> lineStyle = new Wrap<>();

	public final Attribute<Double> lineTransparency = new Wrap<>();

	public final Attribute<Boolean> hideLine = new Wrap<>();

	//public final Attribute<String> colorMap = new Wrap<>();

	//public final Attribute<Boolean> invertMap = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom<?> parent) {

		Page symbolPage = root.createPage("symbol", "   Symbol   ");

		//#region marker section

		Section symbol = symbolPage.createSection("symbol");

		symbol.createSymbolType(symbolType, this, "Symbol", "circle");

		symbol.createTextField(size, this, "64");

		//symbol.createTextField(thinMarkers, "thinMarkers", "Thin markers", "1");

		symbol.createCheckBox(hide, this).setLabel("Hide");

		//symbol.createErrorBarStyle(errorStyle, "errorStyle", "Error style");

		//#end region

		//#region symbol fill section

		Section fill = symbolPage.createSection("fill", true);

		fill.createColorChooser(fillColor, this, "black").setLabel("Color");

		//fill.createFillStyle(fillStyle, "style", "Style");

		fill.createDoubleVariableField(fillTransparency, this, 0.0).setLabel("Transparency");

		fill.createCheckBox(hideFill, this).setLabel("Hide");

		//markerFill.createColorMap(colorMap, this, "Color map");

		//markerFill.createCheckBox(invertMap, this, "Invert map");

		//#end region

		//#region symbol line section

		Section markerBorder = symbolPage.createSection("line", true);

		markerBorder.createColorChooser(lineColor, this, "black");

		markerBorder.createTextField(lineWidth, this, "0.5");

		markerBorder.createLineStyle(lineStyle, this, "solid");

		markerBorder.createDoubleVariableField(lineTransparency, this, 0.0).setLabel("Transparency");

		markerBorder.createCheckBox(hideLine, this).setLabel("Hide");

		//#end region
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection xySelection, Selection rectSelection, AbstractGraphicsAtom parent) {

		String parentName = parent.getName();
		String id = "symbols_" + parentName;
		String clipPathId = id + "_clip-path";

		//remove old symbols group if it already exists
		xySelection //
				.select("#" + id) //
				.remove();

		//create new symbols group
		symbolsSelection = xySelection //
				.append("g") //
				.attr("id", id) //
				.attr("class", "symbols") //
				.attr("clip-path", "url(#" + clipPathId);

		//create clipping path that ensures that the symbols are only
		//shown within the bounds of the graph
		Graph graph = getGraph(parent);

		double width = Length.toPx(graph.data.width.get());
		double height = Length.toPx(graph.data.width.get());
		symbolsSelection.append("clipPath") //
				.attr("id", clipPathId) //
				.append("rect") //
				.attr("x", 0) //
				.attr("y", 0) //
				.attr("width", width) //
				.attr("height", height);

		//bind attributes
		AbstractGraphicsAtom.bindDisplayToBooleanAttribute("hideSymbols", symbolsSelection, hide);

		Consumer replotSymbols = () -> {
			rePlotSymbols(d3, parent);
		};
		symbolType.addModificationConsumer("replotSymbols", replotSymbols);
		size.addModificationConsumer("replotSymbols", replotSymbols);

		//initially plot symbols
		replotSymbols.consume();

		//see method replotWithD3
		return xySelection;
	}

	private Graph getGraph(AbstractGraphicsAtom parent) {
		AbstractAtom<?> grandParent = parent.getParentAtom();
		Graph graph;
		boolean isGraph = Graph.class.isAssignableFrom(grandParent.getClass());
		if (isGraph) {
			graph = (Graph) grandParent;
		} else {
			AbstractAtom<?> greatGrandParent = grandParent.getParentAtom();
			graph = (Graph) greatGrandParent;
		}
		return graph;
	}

	private void rePlotSymbols(D3 d3, AbstractGraphicsAtom parent) {

		//remove old symbols
		symbolsSelection.selectAll("path") //
				.remove();

		//get symbol type and plot new symbols
		String symbolTypeString = symbolType.get();
		boolean isNoneSymbol = symbolTypeString.equals(SymbolStyleValue.NONE.toString());
		if (!isNoneSymbol) {
			//plot new symbols
			plotNewSymbols(d3, symbolTypeString, parent);
		}
	}

	private void plotNewSymbols(D3 d3, String symbolTypeString, AbstractGraphicsAtom parent) {

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

		symbolsSelection.selectAll("path") //
				.data(xyDataString) //
				.enter() //
				.append("path") //
				.attr("transform", new AxisTransformPointDatumFunction(xScale, yScale)) //
				.attr("d", symbolDString);

		//bind attributes
		AbstractGraphicsAtom.bindStringAttribute(symbolsSelection, "fill", fillColor);
		AbstractGraphicsAtom.bindTransparency(symbolsSelection, fillTransparency);
		AbstractGraphicsAtom.bindTransparencyToBooleanAttribute(symbolsSelection, hideFill, fillTransparency);

		AbstractGraphicsAtom.bindStringAttribute(symbolsSelection, "stroke", lineColor);
		AbstractGraphicsAtom.bindLineTransparency(symbolsSelection, lineTransparency);
		AbstractGraphicsAtom.bindLineTransparencyToBooleanAttribute(symbolsSelection, hideLine, lineTransparency);

		AbstractGraphicsAtom.bindLineStyle(symbolsSelection, lineStyle);

		AbstractGraphicsAtom.bindStringAttribute(symbolsSelection, "stroke-width", lineWidth);
	}

	public void plotLegendSymbolWithD3(D3 d3, Selection parentSelection, int xSymbol, Refreshable refreshable) {

		symbolType.addModificationConsumer("replotLegendSymbol", () -> refreshable.refresh());
		size.addModificationConsumer("replotLegendSymbol", () -> refreshable.refresh());
		plotLegendSymbols(d3, parentSelection, xSymbol);
	}

	private void plotLegendSymbols(D3 d3, Selection parentSelection, int xSymbol) {

		parentSelection //
				.select(".legend-symbol") //
				.remove();

		String symbolTypeString = symbolType.get();
		boolean isNoneSymbol = symbolTypeString.equals(SymbolStyleValue.NONE.toString());
		if (!isNoneSymbol) {

			SymbolType symbolTypeValue = SymbolType.fromString(symbolTypeString);
			int symbolSquareSize = Integer.parseInt(size.get());

			//symbol path generator
			org.treez.javafxd3.d3.svg.Symbol symbol = d3 //
					.svg() //
					.symbol() //
					.size(symbolSquareSize) //
					.type(symbolTypeValue);

			String symbolDString = symbol.generate();

			//create symbol
			Selection legendSymbol = parentSelection //
					.append("path") //
					.classed("legend-symbol", true) //
					.attr("transform", "translate(" + xSymbol + ",0)") //
					.attr("d", symbolDString);

			//bind attributes
			AbstractGraphicsAtom.bindStringAttribute(legendSymbol, "fill", fillColor);
			AbstractGraphicsAtom.bindTransparency(legendSymbol, fillTransparency);
			AbstractGraphicsAtom.bindTransparencyToBooleanAttribute(legendSymbol, hideFill, fillTransparency);

			AbstractGraphicsAtom.bindStringAttribute(legendSymbol, "stroke", lineColor);
			AbstractGraphicsAtom.bindLineTransparency(legendSymbol, lineTransparency);
			AbstractGraphicsAtom.bindLineTransparencyToBooleanAttribute(legendSymbol, hideLine, lineTransparency);

			AbstractGraphicsAtom.bindLineStyle(legendSymbol, lineStyle);

			AbstractGraphicsAtom.bindStringAttribute(legendSymbol, "stroke-width", lineWidth);

		}
	}

	//#end region

}
