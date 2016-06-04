package org.treez.results.atom.contour;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.CheckBox;
import org.treez.core.atom.attribute.CheckBoxEnableTarget;
import org.treez.core.atom.attribute.ColorChooser;
import org.treez.core.atom.attribute.EnumComboBox;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Consumer;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.plotly.Coloring;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Data implements GraphicsPropertiesPageFactory {

	//#regionATTRIBUTES

	public final Attribute<String> xData = new Wrap<>();

	public final Attribute<String> yData = new Wrap<>();

	public final Attribute<String> zData = new Wrap<>();

	public final Attribute<Boolean> automaticZLimits = new Wrap<>();

	public final Attribute<Double> zMin = new Wrap<>();

	public final Attribute<Double> zMax = new Wrap<>();

	public final Attribute<Boolean> automaticContours = new Wrap<>();

	public final Attribute<String> coloring = new Wrap<>();

	public final Attribute<Integer> numberOfContours = new Wrap<>();

	public final Attribute<Double> startLevel = new Wrap<>();

	public final Attribute<Double> endLevel = new Wrap<>();

	public final Attribute<Double> levelSize = new Wrap<>();

	public final Attribute<Boolean> connectGaps = new Wrap<>();

	//public finalAttribute<String>labels=newWrap<>();

	public final Attribute<String> xAxis = new Wrap<>();

	public final Attribute<String> yAxis = new Wrap<>();

	//#endregion

	//#regionMETHODS

	@Override
	@SuppressWarnings("checkstyle:magicnumber")
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page dataPage = root.createPage("data", "Data");

		//data

		Section dataSection = dataPage.createSection("data", "Data");

		Class<?> targetClass = org.treez.data.column.Column.class;
		String value = "root.data.table.columns.x";
		dataSection.createModelPath(xData, this, value, targetClass, parent)//
				.setLabel("x-Data");

		value = "root.data.table.columns.y";
		dataSection.createModelPath(yData, this, value, targetClass, parent)//
				.setLabel("y-Data");

		value = "root.data.table.columns.z";
		dataSection.createModelPath(zData, this, value, targetClass, parent)//
				.setLabel("z-Data");

		dataSection.createCheckBox(connectGaps, "Connect gaps", true);

		//axis
		Section axisSection = dataPage.createSection("axis", "Axis");

		targetClass = org.treez.results.atom.axis.Axis.class;
		value = "";
		axisSection.createModelPath(xAxis, this, value, targetClass, parent)//
				.setLabel("Xaxis");

		axisSection.createModelPath(yAxis, this, value, targetClass, parent)//
				.setLabel("Yaxis");

		//zLimits
		Section zLimits = dataPage.createSection("zLimits", "zLimits");
		zLimits.setLabel("z-Limits");

		CheckBox autoZLimitsCheckBox = zLimits.createCheckBox(automaticZLimits, "Automatic z limits", true);
		autoZLimitsCheckBox.addChild(new CheckBoxEnableTarget("zMin", false, "data.zLimits.zMin"));
		autoZLimitsCheckBox.addChild(new CheckBoxEnableTarget("zMax", false, "data.zLimits.zMax"));

		zLimits.createDoubleVariableField(zMin, this, 0.0).setEnabled(false);
		zLimits.createDoubleVariableField(zMax, this, 1.0).setEnabled(false);

		//contours
		Section contours = dataPage.createSection("contours", "z-Limits");

		CheckBox autoContourcheckBox = contours.createCheckBox(automaticContours, "Automatic contours", true);
		autoContourcheckBox.addChild(new CheckBoxEnableTarget("coloring", false, "data.contours.coloring"));
		autoContourcheckBox
				.addChild(new CheckBoxEnableTarget("numberOfContours", true, "data.contours.numberOfContours"));
		autoContourcheckBox.addChild(new CheckBoxEnableTarget("startLevel", false, "data.contours.startLevel"));
		autoContourcheckBox.addChild(new CheckBoxEnableTarget("endLevel", false, "data.contours.endLevel"));
		autoContourcheckBox.addChild(new CheckBoxEnableTarget("levelSize", false, "data.contours.levelSize"));

		contours.createIntegerVariableField(numberOfContours, this, 5);

		EnumComboBox<?> coloringComboBox = contours.createEnumComboBox(coloring, "coloring", Coloring.FILL);
		coloringComboBox.setLabel("Coloring mode");
		coloringComboBox.setEnabled(false);

		Contour contour = (Contour) parent;

		coloring.addModificationConsumer("enableOrDisableLineColor", () -> {
			Wrap<?> wrap = (Wrap<?>) contour.lines.color;
			ColorChooser colorChooser = (ColorChooser) wrap.getAttribute();
			boolean isLinesMode = coloring.get().equals(Coloring.LINES.toString());
			colorChooser.setEnabled(!isLinesMode);
		});

		contours.createDoubleVariableField(startLevel, this, 0.0).setEnabled(false);
		contours.createDoubleVariableField(endLevel, this, 10.0).setEnabled(false);
		contours.createDoubleVariableField(levelSize, this, 2.0).setEnabled(false);

	}

	@Override
	@SuppressWarnings("checkstyle:magicnumber")
	public Selection plotWithD3(D3 d3, Selection contourSelection, Selection rectSelection, GraphicsAtom parent) {

		Contour contour = (Contour) parent;

		Consumer dataChangedConsumer = () -> {
			contour.updatePlotWithD3(d3);
		};
		xData.addModificationConsumer("replot", dataChangedConsumer);

		//TODO: other listeners

		xAxis.addModificationConsumer("replot", dataChangedConsumer);
		yAxis.addModificationConsumer("replot", dataChangedConsumer);

		return contourSelection;
	}

	//#end region

}
