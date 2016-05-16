package org.treez.results.atom.xy;

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

/**
 * XY data settings
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Data implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	public final Attribute<String> xData = new Wrap<>();

	public final Attribute<String> yData = new Wrap<>();

	public final Attribute<String> legendText = new Wrap<>();

	//public final Attribute<String> labels = new Wrap<>();

	//public final Attribute<String> scaleMarkers = new Wrap<>();

	public final Attribute<String> xAxis = new Wrap<>();

	public final Attribute<String> yAxis = new Wrap<>();

	//public final Attribute<String> colorMarkers = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page dataPage = root.createPage("data", "   Data   ");

		Section data = dataPage.createSection("data", "Data");

		Class<?> targetClass = org.treez.data.column.Column.class;
		String value = "root.data.table.columns.x";
		data.createModelPath(xData, this, value, targetClass, parent) //
				.setLabel("X data");

		targetClass = org.treez.data.column.Column.class;
		value = "root.data.table.columns.y";
		data.createModelPath(yData, this, value, targetClass, parent) //
				.setLabel("Y data");

		TextField legendTextField = data.createTextField(legendText, "legendText", "");
		legendTextField.setLabel("Legend text");
		//data.createTextField(labels, "labels", "Labels", "");

		//targetClass = org.treez.data.column.Column.class;
		//value = "";
		//data.createModelPath(scaleMarkers, "scaleMarkers", "Scale markers", value, targetClass, parent);

		targetClass = org.treez.results.atom.axis.Axis.class;
		value = "";

		data //
				.createModelPath(xAxis, this, value, targetClass, parent) //
				.setLabel("X axis");

		targetClass = org.treez.results.atom.axis.Axis.class;
		value = "";
		data //
				.createModelPath(yAxis, this, value, targetClass, parent) //
				.setLabel("Y axis");

		//data.createTextField(colorMarkers, "colorMarkers", "Color markers", "");

	}

	@Override
	public Selection plotWithD3(D3 d3, Selection xySelection, Selection rectSelection, GraphicsAtom parent) {

		//this property page factory does create an own d3 group; the work will be
		//done by the other property page factories

		Consumer dataChangedConsumer = () -> {
			Xy xy = (Xy) parent;
			xy.updatePlotWithD3(d3);
		};
		xData.addModificationConsumer("replot", dataChangedConsumer);
		yData.addModificationConsumer("replot", dataChangedConsumer);

		xAxis.addModificationConsumer("replot", dataChangedConsumer);
		yAxis.addModificationConsumer("replot", dataChangedConsumer);

		return xySelection;
	}

	//#end region

}
