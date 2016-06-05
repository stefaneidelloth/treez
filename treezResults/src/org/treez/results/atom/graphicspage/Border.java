package org.treez.results.atom.graphicspage;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsAtom;
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
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page borderPage = root.createPage("border", "Border");

		Section border = borderPage.createSection("border");

		border.createColorChooser(color, "color", "black");

		border.createTextField(width, "width", "2");

		border.createLineStyle(style, "style", "solid");

		border.createDoubleVariableField(transparency, this, 0.0);

		border.createCheckBox(hide, "hide");
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection parentSelection, Selection rectSelection, GraphicsAtom parent) {

		GraphicsAtom.bindStringAttribute(rectSelection, "stroke", color);
		GraphicsAtom.bindStringAttribute(rectSelection, "stroke-width", width);
		GraphicsAtom.bindLineStyle(rectSelection, style);
		GraphicsAtom.bindLineTransparency(rectSelection, transparency);
		GraphicsAtom.bindLineTransparencyToBooleanAttribute(rectSelection, hide, transparency);

		return parentSelection;
	}

	//#end region

}
