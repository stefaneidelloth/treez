package org.treez.results.atom.tornado;

import org.treez.core.atom.attribute.AttributeRoot;
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
import org.treez.results.atom.axis.Direction;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Data implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	//#region DOMAIN

	public final Attribute<String> domainLabel = new Wrap<>();

	public final Attribute<String> domainBase = new Wrap<>();

	public final Attribute<String> domainLeft = new Wrap<>();

	public final Attribute<String> domainRight = new Wrap<>();

	public final Attribute<String> domainUnit = new Wrap<>();

	public final Attribute<String> domainAxis = new Wrap<>();

	public final Attribute<String> labelMode = new Wrap<>();

	//#end region

	//#region RANGE

	public final Attribute<String> rangeBase = new Wrap<>();

	public final Attribute<String> rangeLeft = new Wrap<>();

	public final Attribute<String> rangeRight = new Wrap<>();

	public final Attribute<String> rangeUnit = new Wrap<>();

	public final Attribute<String> rangeAxis = new Wrap<>();

	public final Attribute<String> sortingMode = new Wrap<>();

	//#end region

	//#region GENERAL

	public final Attribute<String> legendText = new Wrap<>();

	public final Attribute<String> leftLabel = new Wrap<>();

	public final Attribute<String> rightLabel = new Wrap<>();

	public final Attribute<String> barDirection = new Wrap<>();

	public final Attribute<Double> barFillRatio = new Wrap<>();

	//#end region

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {
		Page dataPage = root.createPage("data", "   Data   ");
		createDomainSection(parent, dataPage);
		createRangeSection(parent, dataPage);
		createGeneralSection(dataPage);
	}

	private void createDomainSection(AbstractAtom parent, Page dataPage) {
		Section domainSection = dataPage.createSection("domain", "Domain data");

		Class<?> targetClass = org.treez.data.column.Column.class;
		String defaultValue = "root.data.table.columns.domainLabel";
		domainSection.createModelPath(domainLabel, this, defaultValue, targetClass, parent) //
				.setLabel("Label");

		defaultValue = "root.data.table.columns.domainBase";
		domainSection.createModelPath(domainBase, this, defaultValue, targetClass, parent) //
				.setLabel("Base");

		defaultValue = "root.data.table.columns.domainLeft";
		domainSection.createModelPath(domainLeft, this, defaultValue, targetClass, parent) //
				.setLabel("Left");

		defaultValue = "root.data.table.columns.domainRight";
		domainSection.createModelPath(domainRight, this, defaultValue, targetClass, parent) //
				.setLabel("Right");

		defaultValue = "root.data.table.columns.domainUnit";
		domainSection.createModelPath(domainUnit, this, defaultValue, targetClass, parent) //
				.setLabel("Unit");

		targetClass = org.treez.results.atom.axis.Axis.class;
		defaultValue = "";
		domainSection //
				.createModelPath(domainAxis, this, defaultValue, targetClass, parent) //
				.setLabel("Axis");

		domainSection.createEnumComboBox(labelMode, this, LabelMode.ABSOLUTE).setLabel("Label mode");
	}

	private void createRangeSection(AbstractAtom parent, Page dataPage) {
		Class<?> targetClass;
		String defaultValue;
		Section rangeSection = dataPage.createSection("range", "Range data");

		targetClass = org.treez.data.column.Column.class;
		defaultValue = "root.data.table.columns.rangeBase";
		rangeSection.createModelPath(rangeBase, this, defaultValue, targetClass, parent) //
				.setLabel("Range base");

		defaultValue = "root.data.table.columns.rangeLeft";
		rangeSection.createModelPath(rangeLeft, this, defaultValue, targetClass, parent) //
				.setLabel("Range left");

		defaultValue = "root.data.table.columns.rangeRight";
		rangeSection.createModelPath(rangeRight, this, defaultValue, targetClass, parent) //
				.setLabel("Range right");

		rangeSection.createTextField(rangeUnit, this).setLabel("Unit");

		targetClass = org.treez.results.atom.axis.Axis.class;
		defaultValue = "";
		rangeSection //
				.createModelPath(rangeAxis, this, defaultValue, targetClass, parent) //
				.setLabel("Axis");

		rangeSection.createEnumComboBox(sortingMode, this, SortingMode.LARGEST_DIFFERENCE).setLabel("Sorting mode");

	}

	private void createGeneralSection(Page dataPage) {
		Section generalSection = dataPage.createSection("general");

		generalSection.createTextField(legendText, "legendText", "").setLabel("Legend text");
		generalSection.createTextField(leftLabel, this).setLabel("Left label");
		generalSection.createTextField(rightLabel, this).setLabel("Right label");

		generalSection.createEnumComboBox(barDirection, this, Direction.HORIZONTAL);

		final double defaultBarFillRatio = 0.75;
		generalSection.createDoubleVariableField(barFillRatio, this, defaultBarFillRatio);
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection xySelection, Selection rectSelection, GraphicsAtom parent) {

		Consumer dataChangedConsumer = () -> {
			Tornado bar = (Tornado) parent;
			bar.updatePlotWithD3(d3);
		};
		domainLabel.addModificationConsumer("replot", dataChangedConsumer);
		domainBase.addModificationConsumer("replot", dataChangedConsumer);
		domainLeft.addModificationConsumer("replot", dataChangedConsumer);
		domainRight.addModificationConsumer("replot", dataChangedConsumer);
		domainUnit.addModificationConsumer("replot", dataChangedConsumer);
		domainAxis.addModificationConsumer("replot", dataChangedConsumer);

		rangeBase.addModificationConsumer("replot", dataChangedConsumer);
		rangeLeft.addModificationConsumer("replot", dataChangedConsumer);
		rangeRight.addModificationConsumer("replot", dataChangedConsumer);
		rangeUnit.addModificationConsumer("replot", dataChangedConsumer);
		rangeAxis.addModificationConsumer("replot", dataChangedConsumer);

		// legendText
		leftLabel.addModificationConsumer("replot", dataChangedConsumer);
		rightLabel.addModificationConsumer("replot", dataChangedConsumer);
		labelMode.addModificationConsumer("replot", dataChangedConsumer);
		sortingMode.addModificationConsumer("replot", dataChangedConsumer);
		barDirection.addModificationConsumer("replot", dataChangedConsumer);
		barFillRatio.addModificationConsumer("replot", dataChangedConsumer);

		return xySelection;
	}

	//#end region

}
