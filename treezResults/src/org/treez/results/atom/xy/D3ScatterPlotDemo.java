package org.treez.results.atom.xy;

import org.treez.core.atom.graphics.length.Length;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.functions.AxisScaleFirstDatumFunction;
import org.treez.javafxd3.d3.functions.AxisScaleSecondDatumFunction;
import org.treez.javafxd3.d3.functions.AxisTransformPointDatumFunction;
import org.treez.javafxd3.d3.scales.LinearScale;
import org.treez.javafxd3.d3.scales.QuantitativeScale;
import org.treez.javafxd3.d3.svg.Area;
import org.treez.javafxd3.d3.svg.Symbol;
import org.treez.javafxd3.d3.svg.SymbolType;
import org.treez.javafxd3.javafx.JavaFxD3Browser;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Demonstrates how d3.js can be used with a JavaFx WebView
 */
public class D3ScatterPlotDemo extends Application {

	//#region ATTRIBUTES

	/**
	 * The JavaFx scene
	 */
	private Scene scene;

	private JavaFxD3Browser browser;

	//#end region

	//#region METHODS

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {

		//set state title
		stage.setTitle("D3 scatter plot demo");

		//define d3 content as post loading hook
		Runnable postLoadingHook = () -> {
			System.out.println("Initial loading of browser is finished");

			//do some d3 stuff
			createD3Example();

		};

		//create browser
		browser = new JavaFxD3Browser(postLoadingHook, true);

		//create the scene
		scene = new Scene(browser, 800, 800, Color.web("#666970"));
		stage.setScene(scene);
		stage.show();

	}

	private void createD3Example() {

		D3 d3 = browser.getD3();

		Double[][] dataArray = { { 1.0, 0.0 }, { 100.0, 0.2 }, { 1000.0, 0.8 } };

		String pageWidth = "10cm";
		String pageHeight = "10cm";

		String leftMargin = "1cm";
		String topMargin = "1cm";

		String graphWidth = "8cm";
		String graphHeight = "8cm";

		boolean logXScale = true;

		double xmin = 1;
		double xmax = 700;

		String graphBackground = "lightblue";

		final double tickPadding = 0.0;

		final int symbolSquareSize = 64;
		String symbolStyle = "fill:red; stroke:blue; stroke-width:2";

		String lineStyle = "fill:none; stroke:red; stroke-width:2";

		String areaStyle = "fill:green;";

		//svg
		Selection svgSelection = d3 //
				.select("#svg")
				.attr("width", pageWidth) //
				.attr("height", pageHeight);

		//page
		Selection pageSelection = svgSelection //
				.append("g") //
				.attr("id", "page")
				.attr("width", pageWidth) //
				.attr("height", pageHeight);

		//graph
		Selection graphSelection = pageSelection //
				.append("g") //
				.attr("id", "graph") //
				.attr("transform", "translate(" + Length.toPx(leftMargin) + "," + Length.toPx(topMargin) + ")");

		@SuppressWarnings("unused")
		Selection graphRectSelection = graphSelection //
				.append("rect") //
				.attr("width", graphWidth) //
				.attr("height", graphHeight) //
				.attr("fill", graphBackground);

		//x axis
		Selection xAxisSelection = graphSelection //
				.append("g") //
				.attr("id", "" + "xAxis") //
				.attr("class", "axis") //
				.attr("transform", "translate(0," + Length.toPx(graphHeight) + ")");

		QuantitativeScale<?> xScale;

		if (logXScale) {
			xScale = d3 //
					.scale()//
					.log() //
					.clamp(true);

		} else {
			xScale = d3 //
					.scale()//
					.linear() //
					.clamp(true);
		}

		xScale
				.range(0, Length.toPx(graphWidth)) //
				.domain(xmin, xmax);

		org.treez.javafxd3.d3.svg.Axis xAxis = d3 //
				.svg() //
				.axis() //
				.scale(xScale)
				.tickPadding(tickPadding)
				.outerTickSize(0.0)
				.orient(org.treez.javafxd3.d3.svg.Axis.Orientation.BOTTOM);

		xAxis.apply(xAxisSelection);

		xAxisSelection //
				.selectAll("path, line") //
				.style("fill", "none") //
				.style("stroke", "#000")
				.style("stroke-width", "3px") //
				.style("shape-rendering", "geometricPrecision");

		if (logXScale) {
			//major ticks
			xAxisSelection //
					.selectAll(".tick:nth-child(1)") //
					.classed("major", true);

			xAxisSelection //
					.selectAll(".tick:nth-child(9n+1)") //
					.classed("major", true);

			Selection majorTickLines = xAxisSelection //
					.selectAll(".major") //
					.selectAll("line");

			Selection minorTickLines = xAxisSelection //
					.selectAll(".tick:not(.major)") //
					.selectAll("line");

			majorTickLines //
					.style("stroke", "blue") //
					.attr("y2", "+" + 20);

			minorTickLines //
					.style("stroke", "red")
					.attr("y2", "+" + 10);

		}

		//y axis
		Selection yAxisSelection = graphSelection //
				.append("g") //
				.attr("id", "" + "yAxis") //
				.attr("class", "axis");

		LinearScale yScale = d3 //
				.scale()//
				.linear()//
				.range(Length.toPx(graphHeight), 0.0);

		org.treez.javafxd3.d3.svg.Axis yAxis = d3 //
				.svg() //
				.axis() //
				.scale(yScale)
				.tickPadding(tickPadding)
				.orient(org.treez.javafxd3.d3.svg.Axis.Orientation.LEFT);

		yAxis.apply(yAxisSelection);

		yAxisSelection //
				.selectAll("path, line") //
				.style("fill", "none") //
				.style("stroke", "#000")
				.style("stroke-width", "3px") //
				.style("shape-rendering", "geometricPrecision");

		//xy plot
		Selection xySelection = graphSelection //
				.append("g") //
				.attr("id", "xy") //
				.attr("class", "xy");

		//plot line
		org.treez.javafxd3.d3.svg.Line linePathGenerator = d3 //
				.svg()//
				.line()
				.x(new AxisScaleFirstDatumFunction(xScale))
				.y(new AxisScaleSecondDatumFunction(yScale));
		//.interpolate(org.treez.javafxd3.d3.svg.InterpolationMode.STEP);

		@SuppressWarnings("unused")
		Selection line = xySelection //
				.append("path") //
				.attr("id", "line") //
				.attr("d", linePathGenerator.generate(dataArray))
				.attr("class", "line")
				.attr("style", lineStyle);

		//plot area beneath line
		double yMin = yScale.apply(0.0).asDouble();
		Area areaPathGenerator = d3 //
				.svg() //
				.area() //
				.x(new AxisScaleFirstDatumFunction(xScale))
				.y0(yMin)
				.y1(new AxisScaleSecondDatumFunction(yScale));
		String areaPath = areaPathGenerator.generate(dataArray);

		@SuppressWarnings("unused")
		Selection area = xySelection //
				.append("path") //
				.attr("id", "area") //
				.attr("d", areaPath)
				.attr("class", "area")
				.attr("style", areaStyle);

		//plot symbols
		Symbol symbol = d3 //
				.svg() //
				.symbol();
		symbol = symbol //
				.size(symbolSquareSize) //
				.type(SymbolType.DIAMOND);

		String symbolDString = symbol.generate();

		Selection symbolSelection = xySelection //
				.append("g") //
				.attr("id", "symbols") //
				.attr("class", "symbols");

		@SuppressWarnings("unused")
		Selection symbols = symbolSelection
				.selectAll("path") //
				.data(dataArray) //
				.enter() //
				.append("path") //
				.attr("transform", new AxisTransformPointDatumFunction(xScale, yScale)) //
				//.attrExpression("transform", "function(d, i) { return 'translate(' + d[0] + ',' + d[1] + ')'; }") //
				.attr("d", symbolDString) //
				.attr("style", symbolStyle);

	}

	//#end region

}
