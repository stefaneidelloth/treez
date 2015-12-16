package org.treez.results.atom.xy;

import java.util.List;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.results.atom.graphics.GraphicsAtom;
import org.treez.results.atom.veuszpage.GraphicsPageModel;

/**
 * XY data settings
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Data implements GraphicsPageModel {

	//#region ATTRIBUTES

	/**
	 * Hide
	 */
	public final Attribute<Boolean> hide = new Wrap<>();

	/**
	 * x data
	 */
	public final Attribute<String> xData = new Wrap<>();

	/**
	 * y data
	 */
	public final Attribute<String> yData = new Wrap<>();

	/**
	 * key text
	 */
	public final Attribute<String> keyText = new Wrap<>();

	/**
	 * labels
	 */
	public final Attribute<String> labels = new Wrap<>();

	/**
	 * scale markers
	 */
	public final Attribute<String> scaleMarkers = new Wrap<>();

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
	public final Attribute<String> colorMarkers = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page dataPage = root.createPage("data", "   Data   ");

		Section data = dataPage.createSection("data");

		Class<?> targetClass = org.treez.data.column.Column.class;
		String value = "root.data.table.columns.x";
		data.createModelPath(xData, "xdata", "X data", value, targetClass, parent);

		targetClass = org.treez.data.column.Column.class;
		value = "root.data.table.columns.y";
		data.createModelPath(yData, "ydata", "Y data", value, targetClass, parent);

		data.createTextField(keyText, "keyText", "Key text", "");

		data.createTextField(labels, "labels", "Labels", "");

		targetClass = org.treez.data.column.Column.class;
		value = "";
		data.createModelPath(scaleMarkers, "scaleMarkers", "Scale markers", value, targetClass, parent);

		targetClass = org.treez.results.atom.axis.Axis.class;
		value = "";
		data.createModelPath(xAxis, "xaxis", "X axis", value, targetClass, parent);

		targetClass = org.treez.results.atom.axis.Axis.class;
		value = "";
		data.createModelPath(yAxis, "yaxis", "Y axis", value, targetClass, parent);

		data.createTextField(colorMarkers, "colorMarkers", "Color markers", "");

	}

	@Override
	public Selection plotWithD3(Selection graphSelection, Selection rectSelection, GraphicsAtom parent) {

		//parent.bindStringAttribute(selection, "x", leftMargin);

		return graphSelection;
	}

	@Override
	public String createVeuszText(AbstractAtom parent) {

		String veuszString = "";

		//add x data
		org.treez.data.column.Column xDataColumn = (org.treez.data.column.Column) parent
				.getChildFromRoot(xData.toString());
		List<Object> xDataValues = xDataColumn.getValues();
		veuszString = veuszString + getVeuszTextNumericalData(xData, xDataValues);

		//add y data
		org.treez.data.column.Column yDataColumn = (org.treez.data.column.Column) parent
				.getChildFromRoot(yData.toString());
		List<Object> yDataValues = yDataColumn.getValues();
		veuszString = veuszString + getVeuszTextNumericalData(yData, yDataValues);

		veuszString = veuszString + "Set('xData', u'" + xData + "')\n";
		veuszString = veuszString + "Set('yData', u'" + yData + "')\n";

		return veuszString;
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
