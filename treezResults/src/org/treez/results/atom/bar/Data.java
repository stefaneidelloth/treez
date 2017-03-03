package org.treez.results.atom.bar;

import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.attribute.attributeContainer.Page;
import org.treez.core.atom.attribute.attributeContainer.section.Section;
import org.treez.core.atom.attribute.text.TextField;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.AbstractGraphicsAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Consumer;
import org.treez.core.attribute.Wrap;
import org.treez.core.path.FilterDelegate;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.results.atom.axis.Axis;
import org.treez.results.atom.axis.Direction;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Data implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	public final Attribute<String> barLengths = new Wrap<>();

	public final Attribute<String> barPositions = new Wrap<>();

	public final Attribute<Direction> barDirection = new Wrap<>();

	public final Attribute<Double> barFillRatio = new Wrap<>();

	public final Attribute<String> legendText = new Wrap<>();

	//public final Attribute<String> labels = new Wrap<>();

	public final Attribute<String> xAxis = new Wrap<>();

	public final Attribute<String> yAxis = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom<?> parent) {

		Page dataPage = root.createPage("data", "   Data   ");

		Section data = dataPage.createSection("data", "Data");

		Class<?> columClass = org.treez.data.column.Column.class;

		String positionColumn = "root.data.table.columns.x";
		data.createModelPath(barPositions, this, positionColumn, columClass, parent) //
				.setLabel("Bar positions");

		String lengthColumn = "root.data.table.columns.y";
		data.createModelPath(barLengths, this, lengthColumn, columClass, parent) //
				.setLabel("Bar lengths");

		data.createEnumComboBox(barDirection, this, Direction.VERTICAL) //
				.setLabel("Bar direction");

		final double defaultBarFillRatio = 0.75;
		data.createDoubleVariableField(barFillRatio, this, defaultBarFillRatio);

		TextField legendTextField = data.createTextField(legendText, this, "");
		legendTextField.setLabel("Legend text");

		Class<?> axisClass = org.treez.results.atom.axis.Axis.class;

		String xAxisPath = "";
		FilterDelegate xAxisFilterDelegate = (atom) -> {
			Axis axis = (Axis) atom;
			return axis.data.direction.get().equals(Direction.HORIZONTAL);
		};
		data //
				.createModelPath(xAxis, this, xAxisPath, axisClass, parent, xAxisFilterDelegate) //
				.setLabel("X axis");

		String yAxisPath = "";
		FilterDelegate yAxisFilterDelegate = (atom) -> {
			Axis axis = (Axis) atom;
			return axis.data.direction.get().equals(Direction.VERTICAL);
		};
		data //
				.createModelPath(yAxis, this, yAxisPath, axisClass, parent, yAxisFilterDelegate) //
				.setLabel("Y axis");

	}

	@Override
	public Selection plotWithD3(D3 d3, Selection xySelection, Selection rectSelection, AbstractGraphicsAtom parent) {

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
