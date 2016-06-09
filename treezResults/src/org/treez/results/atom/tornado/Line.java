package org.treez.results.atom.tornado;

import org.treez.core.adaptable.Refreshable;
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
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page linePage = root.createPage("line", "   Line    ");

		Section left = linePage.createSection("left");

		left.createColorChooser(leftColor, "color", "black");

		left.createTextField(leftWidth, "width", "3");

		left.createLineStyle(leftStyle, "style", "solid");

		left.createDoubleVariableField(leftTransparency, this, 0.0);

		left.createCheckBox(leftHide, "hide", true);

		Section right = linePage.createSection("right");

		right.createColorChooser(rightColor, "color", "black");

		right.createTextField(rightWidth, "width", "3");

		right.createLineStyle(rightStyle, "style", "solid");

		right.createDoubleVariableField(rightTransparency, this, 0.0);

		right.createCheckBox(rightHide, "hide", true);
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection barSelection, Selection rectSelection, GraphicsAtom parent) {

		Selection rectsLeftSelection = barSelection //
				.select(".bar-rects-left") //
				.selectAll("rect");

		GraphicsAtom.bindStringAttribute(rectsLeftSelection, "stroke", leftColor);
		GraphicsAtom.bindStringAttribute(rectsLeftSelection, "stroke-width", leftWidth);
		GraphicsAtom.bindLineStyle(rectsLeftSelection, leftStyle);
		GraphicsAtom.bindLineTransparency(rectsLeftSelection, leftTransparency);
		GraphicsAtom.bindLineTransparencyToBooleanAttribute(rectsLeftSelection, leftHide, leftTransparency);

		Selection rectsRightSelection = barSelection //
				.select(".bar-rects-right") //
				.selectAll("rect");

		GraphicsAtom.bindStringAttribute(rectsRightSelection, "stroke", rightColor);
		GraphicsAtom.bindStringAttribute(rectsRightSelection, "stroke-width", rightWidth);
		GraphicsAtom.bindLineStyle(rectsRightSelection, rightStyle);
		GraphicsAtom.bindLineTransparency(rectsRightSelection, rightTransparency);
		GraphicsAtom.bindLineTransparencyToBooleanAttribute(rectsRightSelection, rightHide, rightTransparency);

		return barSelection;
	}

	public Selection formatLegendSymbolLine(Selection symbolSelection, Refreshable refreshable) {

		GraphicsAtom.bindStringAttribute(symbolSelection, "stroke", leftColor);
		GraphicsAtom.bindStringAttribute(symbolSelection, "stroke-width", leftWidth);
		GraphicsAtom.bindLineStyle(symbolSelection, leftStyle);
		GraphicsAtom.bindLineTransparency(symbolSelection, leftTransparency);
		GraphicsAtom.bindLineTransparencyToBooleanAttribute(symbolSelection, leftHide, leftTransparency);

		Consumer replotLegend = () -> refreshable.refresh();
		leftWidth.addModificationConsumer("lineWidthLegendSymbol", replotLegend);

		return symbolSelection;
	}

	//#end region

}
