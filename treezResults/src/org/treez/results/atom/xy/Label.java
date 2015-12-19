package org.treez.results.atom.xy;

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
 * XY label settings
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Label implements GraphicsPageModel {

	//#region ATTRIBUTES

	/**
	 *
	 */
	public final Attribute<String> horizontalPosition = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> verticalPosition = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> angle = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> font = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> fontSize = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> color = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<Boolean> italic = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<Boolean> bold = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<Boolean> underline = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<Boolean> hide = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page labelPage = root.createPage("label", "   Label   ");

		Section label = labelPage.createSection("label");

		label.createComboBox(horizontalPosition, "horizontalPosition", "Horz position", "right, centre, left",
				"centre");

		label.createComboBox(verticalPosition, "verticalPosition", "Vert position", "top, centre, bottom", "centre");

		label.createTextField(angle, "angle", "0");

		label.createFont(font, "font");

		label.createSize(fontSize, "size", "14pt");

		label.createColorChooser(color, "color", "black");

		label.createCheckBox(italic, "italic");

		label.createCheckBox(bold, "bold");

		label.createCheckBox(underline, "underline");

		label.createCheckBox(hide, "hide");
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection graphSelection, Selection rectSelection, GraphicsAtom parent) {

		//parent.bindStringAttribute(selection, "x", leftMargin);

		return graphSelection;
	}

	@Override
	public String createVeuszText(AbstractAtom parent) {

		String veuszString = "";

		return veuszString;
	}

	//#end region

}
