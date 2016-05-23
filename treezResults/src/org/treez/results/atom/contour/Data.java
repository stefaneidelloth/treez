package org.treez.results.atom.contour;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.attribute.TextField;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Consumer;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.functions.AxisScaleFirstDatumFunction;
import org.treez.javafxd3.d3.functions.AxisScaleSecondDatumFunction;
import org.treez.javafxd3.d3.functions.ColorScaleLevelDatumFunction;
import org.treez.javafxd3.d3.geom.Polygon;
import org.treez.javafxd3.d3.scales.LinearScale;
import org.treez.javafxd3.d3.scales.QuantitativeScale;
import org.treez.javafxd3.d3.svg.Line;
import org.treez.results.atom.axis.Direction;
import org.treez.results.atom.contour.conrec.Conrec;
import org.treez.results.atom.contour.conrec.Point;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Data implements GraphicsPropertiesPageFactory {

	private static final Logger LOG = Logger.getLogger(Data.class);

	//#regionATTRIBUTES

	public final Attribute<String> barLengths = new Wrap<>();

	public final Attribute<String> barPositions = new Wrap<>();

	public final Attribute<String> barDirection = new Wrap<>();

	public final Attribute<Double> barFillRatio = new Wrap<>();

	public final Attribute<String> legendText = new Wrap<>();

	//public finalAttribute<String>labels=newWrap<>();

	public final Attribute<String> xAxis = new Wrap<>();

	public final Attribute<String> yAxis = new Wrap<>();

	//#endregion

	//#regionMETHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page dataPage = root.createPage("data", "Data");

		Section data = dataPage.createSection("data", "Data");

		Class<?> targetClass = org.treez.data.column.Column.class;
		String value = "root.data.table.columns.x";
		data.createModelPath(barLengths, this, value, targetClass, parent)//
				.setLabel("Xdata");

		targetClass = org.treez.data.column.Column.class;
		value = "root.data.table.columns.y";
		data.createModelPath(barPositions, this, value, targetClass, parent)//
				.setLabel("Ydata");
		data.createEnumComboBox(barDirection, "Direction", Direction.VERTICAL);

		final double defaultBarFillRatio = 0.75;
		data.createDoubleVariableField(barFillRatio, this, defaultBarFillRatio);

		TextField legendTextField = data.createTextField(legendText, "legendText", "");
		legendTextField.setLabel("Legendtext");

		targetClass = org.treez.results.atom.axis.Axis.class;
		value = "";

		data//
				.createModelPath(xAxis, this, value, targetClass, parent)//
				.setLabel("Xaxis");

		targetClass = org.treez.results.atom.axis.Axis.class;
		value = "";
		data//
				.createModelPath(yAxis, this, value, targetClass, parent)//
				.setLabel("Yaxis");

	}

	@Override
	@SuppressWarnings("checkstyle:magicnumber")
	public Selection plotWithD3(D3 d3, Selection contourSelection, Selection rectSelection, GraphicsAtom parent) {

		double[][] data = new double[][] {
				new double[] {
						0.4,
						0.4,
						0.7,
						-1.0,
						-0.1,
						0.6,
						-0.4,
						0.6,
						-0.4,
						1.3,
						0.7,
						-0.4,
						1.1,
						1.3,
						0.6,
						0.1,
						-0.0,
						-0.8,
						-0.8,
						-1.0 },
				new double[] {
						0.4,
						-0.4,
						0.4,
						-1.2,
						-0.7,
						0.4,
						-0.9,
						0.5,
						-0.9,
						1.2,
						0.5,
						-1.0,
						1.3,
						1.1,
						0.5,
						-0.0,
						-0.1,
						-1.2,
						-1.0,
						-0.9 },
				new double[] {
						0.7,
						0.4,
						0.1,
						-1.2,
						-0.2,
						0.5,
						-0.6,
						0.6,
						-0.2,
						0.9,
						0.6,
						-0.5,
						1.1,
						0.8,
						0.6,
						0.1,
						-0.4,
						-0.9,
						-0.7,
						-0.8 },
				new double[] {
						-1.0,
						-1.2,
						-1.2,
						-4.4,
						-1.9,
						-0.8,
						-2.2,
						-1.0,
						-2.2,
						0.0,
						-0.3,
						-2.0,
						-0.2,
						0.2,
						-0.8,
						-1.6,
						-1.9,
						-2.4,
						-2.3,
						-2.6 },
				new double[] {
						-0.1,
						-0.7,
						-0.2,
						-1.9,
						-2.0,
						-0.5,
						-1.9,
						-0.3,
						-1.7,
						0.4,
						-0.2,
						-1.9,
						0.3,
						0.4,
						-0.3,
						-0.8,
						-0.9,
						-2.1,
						-1.8,
						-2.0 },
				new double[] {
						0.6,
						0.4,
						0.5,
						-0.8,
						-0.5,
						-0.1,
						-0.8,
						0.6,
						-0.5,
						1.0,
						0.5,
						-0.7,
						0.8,
						1.0,
						0.5,
						0.1,
						-0.3,
						-0.9,
						-0.7,
						-1.1 },
				new double[] {
						-0.4,
						-0.9,
						-0.6,
						-2.2,
						-1.9,
						-0.8,
						-2.7,
						-0.6,
						-2.0,
						0.3,
						-0.3,
						-2.3,
						-0.0,
						-0.0,
						-0.6,
						-1.1,
						-1.3,
						-2.4,
						-2.0,
						-2.2 },
				new double[] {
						0.6,
						0.5,
						0.6,
						-1.0,
						-0.3,
						0.6,
						-0.6,
						0.1,
						-0.8,
						1.3,
						0.8,
						-0.8,
						1.1,
						1.3,
						0.4,
						0.1,
						0.1,
						-0.8,
						-1.0,
						-1.0 },
				new double[] {
						-0.4,
						-0.9,
						-0.2,
						-2.2,
						-1.7,
						-0.5,
						-2.0,
						-0.8,
						-2.9,
						0.3,
						-0.4,
						-2.2,
						-0.0,
						-0.0,
						-0.7,
						-0.7,
						-1.3,
						-2.4,
						-2.1,
						-2.6 },
				new double[] {
						1.3,
						1.2,
						0.9,
						0.0,
						0.4,
						1.0,
						0.3,
						1.3,
						0.3,
						1.1,
						1.0,
						0.2,
						0.7,
						1.9,
						0.9,
						-0.2,
						0.3,
						0.1,
						-0.4,
						-0.2 },
				new double[] {
						0.7,
						0.5,
						0.6,
						-0.3,
						-0.2,
						0.5,
						-0.3,
						0.8,
						-0.4,
						1.0,
						0.3,
						-0.3,
						1.0,
						1.1,
						0.6,
						0.1,
						0.3,
						-0.7,
						-0.5,
						-0.6 },
				new double[] {
						-0.4,
						-1.0,
						-0.5,
						-2.0,
						-1.9,
						-0.7,
						-2.3,
						-0.8,
						-2.2,
						0.2,
						-0.3,
						-2.7,
						0.0,
						-0.0,
						-0.6,
						-1.0,
						-1.1,
						-2.3,
						-2.1,
						-2.4 },
				new double[] {
						1.1,
						1.3,
						1.1,
						-0.2,
						0.3,
						0.8,
						-0.0,
						1.1,
						-0.0,
						0.7,
						1.0,
						0.0,
						1.6,
						0.8,
						1.0,
						0.8,
						0.7,
						-0.2,
						-0.2,
						-0.2 },
				new double[] {
						1.3,
						1.1,
						0.8,
						0.2,
						0.4,
						1.0,
						-0.0,
						1.3,
						-0.0,
						1.9,
						1.1,
						-0.0,
						0.8,
						1.2,
						1.1,
						0.0,
						0.2,
						-0.1,
						-0.4,
						0.0 },
				new double[] {
						0.6,
						0.5,
						0.6,
						-0.8,
						-0.3,
						0.5,
						-0.6,
						0.4,
						-0.7,
						0.9,
						0.6,
						-0.6,
						1.0,
						1.1,
						-0.2,
						0.1,
						-0.0,
						-0.9,
						-0.6,
						-1.2 },
				new double[] {
						0.1,
						-0.0,
						0.1,
						-1.6,
						-0.8,
						0.1,
						-1.1,
						0.1,
						-0.7,
						-0.2,
						0.1,
						-1.0,
						0.8,
						0.0,
						0.1,
						-0.6,
						-0.4,
						-1.2,
						-1.3,
						-1.4 },
				new double[] {
						-0.0,
						-0.1,
						-0.4,
						-1.9,
						-0.9,
						-0.3,
						-1.3,
						0.1,
						-1.3,
						0.3,
						0.3,
						-1.1,
						0.7,
						0.2,
						-0.0,
						-0.4,
						-1.3,
						-1.4,
						-1.6,
						-1.9 },
				new double[] {
						-0.8,
						-1.2,
						-0.9,
						-2.4,
						-2.1,
						-0.9,
						-2.4,
						-0.8,
						-2.4,
						0.1,
						-0.7,
						-2.3,
						-0.2,
						-0.1,
						-0.9,
						-1.2,
						-1.4,
						-3.0,
						-2.3,
						-2.5 },
				new double[] {
						-0.8,
						-1.0,
						-0.7,
						-2.3,
						-1.8,
						-0.7,
						-2.0,
						-1.0,
						-2.1,
						-0.4,
						-0.5,
						-2.1,
						-0.2,
						-0.4,
						-0.6,
						-1.3,
						-1.6,
						-2.3,
						-2.3,
						-2.4 },
				new double[] {
						-1.0,
						-0.9,
						-0.8,
						-2.6,
						-2.0,
						-1.1,
						-2.2,
						-1.0,
						-2.6,
						-0.2,
						-0.6,
						-2.4,
						-0.2,
						0.0,
						-1.2,
						-1.4,
						-1.9,
						-2.5,
						-2.4,
						-3.3 } };

		double cliff = -1000;
		double[][] cliffedData = new double[data.length + 2][data[0].length + 2];

		for (int rowIndex = 0; rowIndex < cliffedData.length; rowIndex++) {
			cliffedData[rowIndex][0] = cliff;
			cliffedData[rowIndex][cliffedData[0].length - 1] = cliff;
		}

		for (int colIndex = 0; colIndex < cliffedData[0].length; colIndex++) {
			cliffedData[0][colIndex] = cliff;
			cliffedData[cliffedData[0].length - 1][colIndex] = cliff;
		}

		for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
			for (int colIndex = 0; colIndex < data[0].length; colIndex++) {
				cliffedData[rowIndex + 1][colIndex + 1] = data[rowIndex][colIndex];
			}
		}

		Conrec conrec = new Conrec(null);

		double[] xs = range(0, cliffedData.length);
		double[] ys = range(0, cliffedData[0].length);
		double[] zs = range(-5, 3, 0.5);

		Contour contour = (Contour) parent;
		QuantitativeScale<?> xScale = contour.getXScale();
		QuantitativeScale<?> yScale = contour.getYScale();

		LinearScale colorScale = d3.scale() //
				.linear() //
				.domain(new double[] { -5, 3 }) //
				.range(new String[] { "#fff", "red" });

		Line lineGenerator = d3.svg() //
				.line() //
				.x(new AxisScaleFirstDatumFunction(xScale))
				.y(new AxisScaleSecondDatumFunction(yScale));

		LOG.info("starting contour");

		conrec.contour(cliffedData, 0, xs.length - 1, 0, ys.length - 1, xs, ys, zs.length, zs);

		LOG.info("getting contours");

		List<org.treez.results.atom.contour.conrec.Contour> contourData = conrec.getContours();

		LOG.info("converting to polygons");

		List<Polygon> polygons = convertToD3Polygons(d3, contourData);

		LOG.info("drawing");

		contourSelection.selectAll("path") //
				.data(polygons) //
				.enter() //
				.append("path") //
				.style("fill", new ColorScaleLevelDatumFunction(colorScale))
				.style("stroke", "black") //
				.attr("d", lineGenerator);

		LOG.info("finished");

		Consumer dataChangedConsumer = () -> {
			Contour bar = (Contour) parent;
			bar.updatePlotWithD3(d3);
		};
		barLengths.addModificationConsumer("replot", dataChangedConsumer);
		barPositions.addModificationConsumer("replot", dataChangedConsumer);

		barDirection.addModificationConsumer("replot", dataChangedConsumer);

		barFillRatio.addModificationConsumer("replot", dataChangedConsumer);

		//legendText.addModificationConsumer("replot",dataChangedConsumer);

		xAxis.addModificationConsumer("replot", dataChangedConsumer);
		yAxis.addModificationConsumer("replot", dataChangedConsumer);

		return contourSelection;
	}

	public double[] range(double start, double stop, double step) {

		List<Double> list = new ArrayList<>();
		double value = start;
		while (value <= stop) {
			list.add(value);
			value += step;
		}

		double[] result = new double[list.size()];
		for (int i = 0; i < list.size(); i++) {
			result[i] = list.get(i);
		}
		return result;
	}

	public double[] range(double start, double stop) {
		double[] result = new double[(int) (stop - start)];

		for (int i = 0; i < stop - start; i++) {
			result[i] = start + i;
		}

		return result;
	}

	private static
			List<Polygon>
			convertToD3Polygons(D3 d3, List<org.treez.results.atom.contour.conrec.Contour> contourData) {

		List<Polygon> polygons = new ArrayList<>();
		for (org.treez.results.atom.contour.conrec.Contour contour : contourData) {
			Polygon polygon = d3.geom().polygon(new Double[][] {});
			for (Point point : contour) {
				double[] pointCoordinates = new double[] { point.x, point.y };
				polygon.addPoint(pointCoordinates);
			}
			double level = contour.getLevel();
			polygon.setMember("level", level);
			polygons.add(polygon);
		}
		return polygons;

	}

	//#endregion

}
