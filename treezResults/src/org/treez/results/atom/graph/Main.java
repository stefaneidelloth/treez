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
import org.treez.results.atom.veuszpage.GraphicsPageModel;

/**
 * The main settings for a graph
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Main implements GraphicsPageModel {

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

		Page mainPage = root.createPage("main", "   Main   ");

		Section main = mainPage.createSection("main");

		main.createTextField(leftMargin, "leftMargin", "Left margin", "1 cm");

		main.createTextField(topMargin, "topMargin", "Top margin", "1 cm");

		main.createTextField(width, "width", "Width", "8 cm");

		main.createTextField(height, "height", "Height", "8 cm");

		main.createCheckBox(hide, "hide");
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection graphSelection, Selection rectSelection, GraphicsAtom parent) {

		parent.bindTranslationAttribute(graphSelection, leftMargin, topMargin);

		parent.bindStringAttribute(rectSelection, "width", width);
		parent.bindStringAttribute(rectSelection, "height", height);
		parent.bindDisplayToBooleanAttribute(graphSelection, hide);

		return graphSelection;
	}

	@Override
	public String createVeuszText(AbstractAtom parent) {

		String veuszString = "";

		if (hide.get()) {
			veuszString = veuszString + "Set('hide', True)";
		}
		veuszString = veuszString + "Set('leftMargin', u'" + leftMargin + "')\n";
		veuszString = veuszString + "Set('topMargin', u'" + topMargin + "')\n";
		//veuszString = veuszString + "Set('rightMargin', u'" + width + "')\n";
		//veuszString = veuszString + "Set('bottomMargin', u'" + height + "')\n";
		//veuszString = veuszString + "Set('aspect', u'" + aspectRatio + "')\n";

		return veuszString;
	}

	//#end region

}
