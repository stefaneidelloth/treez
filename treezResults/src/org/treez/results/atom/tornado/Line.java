package org.treez.results.atom.tornado;

import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.attribute.attributeContainer.Page;
import org.treez.core.atom.attribute.attributeContainer.section.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.AbstractGraphicsAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Consumer;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Line implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	public final Attribute<String> leftColor = new Wrap<>();

	public final Attribute<String> leftWidth = new Wrap<>();

	public final Attribute<String> leftStyle = new Wrap<>();

	public final Attribute<Double> leftTransparency = new Wrap<>();

	public final Attribute<Boolean> leftHide = new Wrap<>();

	public final Attribute<String> rightColor = new Wrap<>();

	public final Attribute<String> rightWidth = new Wrap<>();

	public final Attribute<String> rightStyle = new Wrap<>();

	public final Attribute<Double> rightTransparency = new Wrap<>();

	public final Attribute<Boolean> rightHide = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom<?> parent) {

		Page linePage = root.createPage("line", "   Line    ");

		Section left = linePage.createSection("left");

		left.createColorChooser(leftColor, this, "black").setLabel("Color");

		left.createTextField(leftWidth, this, "3").setLabel("width");

		left.createLineStyle(leftStyle, this, "solid").setLabel("Style");

		left.createDoubleVariableField(leftTransparency, this, 0.0);

		left.createCheckBox(leftHide, this, true).setLabel("Hide");

		Section right = linePage.createSection("right");

		right.createColorChooser(rightColor, this, "black").setLabel("Color");

		right.createTextField(rightWidth, this, "3").setLabel("width");

		right.createLineStyle(rightStyle, this, "solid").setLabel("Style");

		right.createDoubleVariableField(rightTransparency, this, 0.0);

		right.createCheckBox(rightHide, this, true).setLabel("Hide");
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection barSelection, Selection rectSelection, AbstractGraphicsAtom parent) {

		Selection rectsLeftSelection = barSelection //
				.select(".bar-rects-left") //
				.selectAll("rect");

		AbstractGraphicsAtom.bindStringAttribute(rectsLeftSelection, "stroke", leftColor);
		AbstractGraphicsAtom.bindStringAttribute(rectsLeftSelection, "stroke-width", leftWidth);
		AbstractGraphicsAtom.bindLineStyle(rectsLeftSelection, leftStyle);
		AbstractGraphicsAtom.bindLineTransparency(rectsLeftSelection, leftTransparency);
		AbstractGraphicsAtom.bindLineTransparencyToBooleanAttribute(rectsLeftSelection, leftHide, leftTransparency);

		Selection rectsRightSelection = barSelection //
				.select(".bar-rects-right") //
				.selectAll("rect");

		AbstractGraphicsAtom.bindStringAttribute(rectsRightSelection, "stroke", rightColor);
		AbstractGraphicsAtom.bindStringAttribute(rectsRightSelection, "stroke-width", rightWidth);
		AbstractGraphicsAtom.bindLineStyle(rectsRightSelection, rightStyle);
		AbstractGraphicsAtom.bindLineTransparency(rectsRightSelection, rightTransparency);
		AbstractGraphicsAtom.bindLineTransparencyToBooleanAttribute(rectsRightSelection, rightHide, rightTransparency);

		return barSelection;
	}

	public Selection formatLegendSymbolLine(Selection symbolSelection, Refreshable refreshable) {

		AbstractGraphicsAtom.bindStringAttribute(symbolSelection, "stroke", leftColor);
		AbstractGraphicsAtom.bindStringAttribute(symbolSelection, "stroke-width", leftWidth);
		AbstractGraphicsAtom.bindLineStyle(symbolSelection, leftStyle);
		AbstractGraphicsAtom.bindLineTransparency(symbolSelection, leftTransparency);
		AbstractGraphicsAtom.bindLineTransparencyToBooleanAttribute(symbolSelection, leftHide, leftTransparency);

		Consumer replotLegend = () -> refreshable.refresh();
		leftWidth.addModificationConsumer("lineWidthLegendSymbol", replotLegend);

		return symbolSelection;
	}

	//#end region

}
