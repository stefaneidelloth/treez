package org.treez.results.atom.graph;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.results.atom.graphicspage.GraphicsPropertiesPageFactory;

/**
 * The border settings for a graph
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Border implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	/**
	 * Border color
	 */
	public final Attribute<String> color = new Wrap<>();

	/**
	 * Border width
	 */
	public final Attribute<String> width = new Wrap<>();

	/**
	 * Border style
	 */
	public final Attribute<String> style = new Wrap<>();

	/**
	 * Border transparency
	 */
	public final Attribute<String> transparency = new Wrap<>();

	/**
	 * Hide
	 */
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

		border.createTextField(transparency, "transparency", "0");

		border.createCheckBox(hide, "hide");
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection graphSelection, Selection rectSelection, GraphicsAtom parent) {

		GraphicsAtom.bindStringAttribute(rectSelection, "stroke", color);
		GraphicsAtom.bindStringAttribute(rectSelection, "stroke-width", width);
		GraphicsAtom.bindLineStyle(rectSelection, style);
		GraphicsAtom.bindLineTransparency(rectSelection, transparency);
		GraphicsAtom.bindLineTransparencyToBooleanAttribute(rectSelection, hide, transparency);

		return graphSelection;
	}

	//#end region

}
