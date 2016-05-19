package org.treez.results.atom.bar;

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
import org.treez.results.atom.axis.Direction;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Data implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	public final Attribute<String> barLengths = new Wrap<>();

	public final Attribute<String> barPositions = new Wrap<>();

	public final Attribute<String> barDirection = new Wrap<>();

	public final Attribute<Double> barFillRatio = new Wrap<>();

	public final Attribute<String> legendText = new Wrap<>();

	//public final Attribute<String> labels = new Wrap<>();

	public final Attribute<String> xAxis = new Wrap<>();

	public final Attribute<String> yAxis = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page dataPage = root.createPage("data", "   Data   ");

		Section data = dataPage.createSection("data", "Data");

		Class<?> targetClass = org.treez.data.column.Column.class;
		String value = "root.data.table.columns.x";
		data.createModelPath(barLengths, this, value, targetClass, parent) //
				.setLabel("X data");

		targetClass = org.treez.data.column.Column.class;
		value = "root.data.table.columns.y";
		data.createModelPath(barPositions, this, value, targetClass, parent) //
				.setLabel("Y data");
		data.createEnumComboBox(barDirection, "Direction", Direction.VERTICAL);

		final double defaultBarFillRatio = 0.75;
		data.createDoubleVariableField(barFillRatio, this, defaultBarFillRatio);

		TextField legendTextField = data.createTextField(legendText, "legendText", "");
		legendTextField.setLabel("Legend text");

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

	}

	@Override
	public Selection plotWithD3(D3 d3, Selection xySelection, Selection rectSelection, GraphicsAtom parent) {

		Consumer dataChangedConsumer = () -> {
			Bar bar = (Bar) parent;
			bar.updatePlotWithD3(d3);
		};
		barLengths.addModificationConsumer("replot", dataChangedConsumer);
		barPositions.addModificationConsumer("replot", dataChangedConsumer);

		barDirection.addModificationConsumer("replot", dataChangedConsumer);

		barFillRatio.addModificationConsumer("replot", dataChangedConsumer);

		//legendText.addModificationConsumer("replot", dataChangedConsumer);

		xAxis.addModificationConsumer("replot", dataChangedConsumer);
		yAxis.addModificationConsumer("replot", dataChangedConsumer);

		return xySelection;
	}

	//#end region

}
