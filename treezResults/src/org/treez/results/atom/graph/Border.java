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
 * The border settings for a graph
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Border implements GraphicsPageModel {

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

		Page borderPage = root.createPage("border");

		Section border = borderPage.createSection("border");

		border.createColorChooser(color, "color", "black");

		border.createSize(width, "width");

		border.createLineStyle(style, "style", "solid");

		border.createTextField(transparency, "transparency", "0");

		border.createCheckBox(hide, "hide");
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection graphSelection, Selection rectSelection, GraphicsAtom parent) {

		//parent.bindStringAttribute(selection, "x", leftMargin);

		return graphSelection;
	}

	@Override
	public String createVeuszText(AbstractAtom parent) {
		String veuszString = "\n";

		veuszString = veuszString + "Set('Border/color', u'" + color + "')\n";
		veuszString = veuszString + "Set('Border/width', u'" + width + "')\n";
		veuszString = veuszString + "Set('Border/style', u'" + style + "')\n";
		veuszString = veuszString + "Set('Border/transparency', " + transparency + ")\n";
		if (hide.get()) {
			veuszString = veuszString + "Set('Border/hide', True)";
		}
		veuszString = veuszString + "\n";

		return veuszString;
	}

	//#end region

}
