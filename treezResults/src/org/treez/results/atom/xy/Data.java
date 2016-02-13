package org.treez.results.atom.xy;

import java.util.List;
import java.util.function.Consumer;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.attribute.TextField;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.scales.QuantitativeScale;
import org.treez.results.atom.axis.Axis;
import org.treez.results.atom.veuszpage.GraphicsPageModel;

/**
 * XY data settings
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Data implements GraphicsPageModel {

	//#region ATTRIBUTES

	/**
	 * x data
	 */
	public final Attribute<String> xData = new Wrap<>();

	/**
	 * y data
	 */
	public final Attribute<String> yData = new Wrap<>();

	/**
	 * legend text
	 */
	public final Attribute<String> legendText = new Wrap<>();

	/**
	 * labels
	 */
	//public final Attribute<String> labels = new Wrap<>();

	/**
	 * scale markers
	 */
	//public final Attribute<String> scaleMarkers = new Wrap<>();

	/**
	 * x axis
	 */
	public final Attribute<String> xAxis = new Wrap<>();

	/**
	 * y axis
	 */
	public final Attribute<String> yAxis = new Wrap<>();

	/**
	 * color markers
	 */
	//public final Attribute<String> colorMarkers = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page dataPage = root.createPage("data", "   Data   ");

		Section data = dataPage.createSection("data", "Data");

		Class<?> targetClass = org.treez.data.column.Column.class;
		String value = "root.data.table.columns.x";
		data
				.createModelPath(xData, "xdata", value, targetClass, parent) //
				.setLabel("X data");

		targetClass = org.treez.data.column.Column.class;
		value = "root.data.table.columns.y";
		data
				.createModelPath(yData, "ydata", value, targetClass, parent) //
				.setLabel("Y data");

		TextField legendTextField = data.createTextField(legendText, "legendText", "");
		legendTextField.setLabel("Legend text");
		//data.createTextField(labels, "labels", "Labels", "");

		//targetClass = org.treez.data.column.Column.class;
		//value = "";
		//data.createModelPath(scaleMarkers, "scaleMarkers", "Scale markers", value, targetClass, parent);

		targetClass = org.treez.results.atom.axis.Axis.class;
		value = "";
		data
				.createModelPath(xAxis, "xaxis", value, targetClass, parent) //
				.setLabel("X axis");

		targetClass = org.treez.results.atom.axis.Axis.class;
		value = "";
		data
				.createModelPath(yAxis, "yaxis", value, targetClass, parent) //
				.setLabel("Y axis");

		//data.createTextField(colorMarkers, "colorMarkers", "Color markers", "");

	}

	@Override
	public Selection plotWithD3(D3 d3, Selection xySelection, Selection rectSelection, GraphicsAtom parent) {

		Xy xy = (Xy) parent;

		//add change consumer for x and y data
		Consumer<String> dataChangedConsumer = (data) -> {
			String xyDataString = getXyDataString(parent);
			QuantitativeScale<?> xScale = getXScale(parent);
			QuantitativeScale<?> yScale = getYScale(parent);
			xy.area.replotWithD3(d3, xySelection, parent, xyDataString, xScale, yScale);
		};
		xData.addModificationConsumer("replot", dataChangedConsumer);
		yData.addModificationConsumer("replot", dataChangedConsumer);

		xAxis.addModificationConsumer("replot", dataChangedConsumer);
		yAxis.addModificationConsumer("replot", dataChangedConsumer);

		//execute consumer once
		dataChangedConsumer.accept(null);

		return xySelection;
	}

	private QuantitativeScale<?> getXScale(AbstractAtom parent) {
		Axis xAxisAtom = getXAxis(parent);
		QuantitativeScale<?> scale = (QuantitativeScale<?>) xAxisAtom.getScale();
		return scale;
	}

	private QuantitativeScale<?> getYScale(AbstractAtom parent) {
		Axis yAxisAtom = getYAxis(parent);
		QuantitativeScale<?> scale = (QuantitativeScale<?>) yAxisAtom.getScale();
		return scale;
	}

	private Axis getXAxis(AbstractAtom parent) {
		Axis xAxisAtom = (Axis) parent.getChildFromRoot(xAxis.toString());
		return xAxisAtom;
	}

	private Axis getYAxis(AbstractAtom parent) {
		Axis yAxisAtom = (Axis) parent.getChildFromRoot(yAxis.toString());
		return yAxisAtom;
	}

	private String getXyDataString(GraphicsAtom parent) {

		List<Object> xDataValues = getXData(parent);
		List<Object> yDataValues = getYData(parent);

		int xLength = xDataValues.size();
		int yLength = yDataValues.size();
		boolean lengthsAreOk = xLength == yLength;
		if (!lengthsAreOk) {
			String message = "The x and y data has to be of equal size but size of x data is " + xLength
					+ " and size of y data is " + yLength;
			throw new IllegalStateException(message);
		}

		List<String> rowList = new java.util.ArrayList<>();
		for (int rowIndex = 0; rowIndex < xLength; rowIndex++) {
			Object xDatum = xDataValues.get(rowIndex);
			Double x = Double.parseDouble(xDatum.toString());

			Object yDatum = yDataValues.get(rowIndex);
			Double y = Double.parseDouble(yDatum.toString());

			String rowString = "[" + x + "," + y + "]";
			rowList.add(rowString);
		}
		String xyDataString = "[" + String.join(",", rowList) + "]";
		return xyDataString;
	}

	@Override
	public String createVeuszText(AbstractAtom parent) {

		String veuszString = "";

		//add x data
		List<Object> xDataValues = getXData(parent);
		veuszString = veuszString + getVeuszTextNumericalData(xData, xDataValues);

		//add y data
		List<Object> yDataValues = getYData(parent);
		veuszString = veuszString + getVeuszTextNumericalData(yData, yDataValues);

		veuszString = veuszString + "Set('xData', u'" + xData + "')\n";
		veuszString = veuszString + "Set('yData', u'" + yData + "')\n";

		return veuszString;
	}

	private List<Object> getXData(AbstractAtom parent) {
		org.treez.data.column.Column xDataColumn = (org.treez.data.column.Column) parent
				.getChildFromRoot(xData.toString());
		List<Object> xDataValues = xDataColumn.getValues();
		return xDataValues;
	}

	private List<Object> getYData(AbstractAtom parent) {
		org.treez.data.column.Column yDataColumn = (org.treez.data.column.Column) parent
				.getChildFromRoot(yData.toString());
		List<Object> yDataValues = yDataColumn.getValues();
		return yDataValues;
	}

	/**
	 * Creates the veusz text for adding the numerical data of a single column
	 *
	 * @return
	 */
	private static String getVeuszTextNumericalData(Attribute<String> dataAtom, List<Object> dataValues) {
		String veuszString = "\n";

		veuszString = veuszString + "ImportString(u'" + dataAtom + "(numeric)','''\n";

		for (Object value : dataValues) {
			veuszString = veuszString + value.toString() + "\n";
		}
		veuszString = veuszString + "''')\n";
		return veuszString;
	}

	//#end region

}
