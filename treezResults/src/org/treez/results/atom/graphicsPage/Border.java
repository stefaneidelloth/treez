package org.treez.results.atom.graphicsPage;

import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.attribute.attributeContainer.Page;
import org.treez.core.atom.attribute.attributeContainer.section.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.AbstractGraphicsAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Border implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	public final Attribute<String> color = new Wrap<>();

	public final Attribute<String> width = new Wrap<>();

	public final Attribute<String> style = new Wrap<>();

	public final Attribute<Double> transparency = new Wrap<>();

	public final Attribute<Boolean> hide = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom<?> parent) {

		Page borderPage = root.createPage("border");

		Section border = borderPage.createSection("border");

		border.createColorChooser(color, this, "black");

		border.createTextField(width, this, "2");

		border.createLineStyle(style, this, "solid");

		border.createDoubleVariableField(transparency, this, 0.0);

		border.createCheckBox(hide, this);
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection parentSelection, Selection rectSelection, AbstractGraphicsAtom parent) {

		AbstractGraphicsAtom.bindStringAttribute(rectSelection, "stroke", color);
		AbstractGraphicsAtom.bindStringAttribute(rectSelection, "stroke-width", width);
		AbstractGraphicsAtom.bindLineStyle(rectSelection, style);
		AbstractGraphicsAtom.bindLineTransparency(rectSelection, transparency);
		AbstractGraphicsAtom.bindLineTransparencyToBooleanAttribute(rectSelection, hide, transparency);

		return parentSelection;
	}

	//#end region

}
