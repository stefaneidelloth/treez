package org.treez.results.atom.graph;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.attribute.TextField;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.results.atom.graphicspage.GraphicsPropertiesPageModel;

/**
 * The main settings for a graph
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Data implements GraphicsPropertiesPageModel {

	//#region ATTRIBUTES

	/**
	 * Left margin
	 */
	public final Attribute<String> leftMargin = new Wrap<>();

	/**
	 * Top margin
	 */
	public final Attribute<String> topMargin = new Wrap<>();

	/**
	 * Width
	 */
	public final Attribute<String> width = new Wrap<>();

	/**
	 * Height
	 */
	public final Attribute<String> height = new Wrap<>();

	/**
	 * Hide
	 */
	public final Attribute<Boolean> hide = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page mainPage = root.createPage("data", "   Data   ");

		Section main = mainPage.createSection("data");

		TextField leftMarginField = main.createTextField(leftMargin, "leftMargin", "2.5 cm");
		leftMarginField.setLabel("Left margin");

		TextField topMarginField = main.createTextField(topMargin, "topMargin", "0.5 cm");
		topMarginField.setLabel("Top margin");

		main.createTextField(width, "width", "12 cm");

		main.createTextField(height, "height", "12 cm");

		main.createCheckBox(hide, "hide");
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection graphSelection, Selection rectSelection, GraphicsAtom parent) {

		GraphicsAtom.bindTranslationAttribute("graphTranslation", graphSelection, leftMargin, topMargin);
		GraphicsAtom.bindStringAttribute(rectSelection, "width", width);
		GraphicsAtom.bindStringAttribute(rectSelection, "height", height);
		GraphicsAtom.bindDisplayToBooleanAttribute("hideGraph", graphSelection, hide);

		return graphSelection;
	}

	//#end region

}
