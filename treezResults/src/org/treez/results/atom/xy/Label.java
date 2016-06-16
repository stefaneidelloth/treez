package org.treez.results.atom.xy;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.ComboBox;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.AbstractGraphicsAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Label implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	public final Attribute<String> horizontalPosition = new Wrap<>();

	public final Attribute<String> verticalPosition = new Wrap<>();

	public final Attribute<String> angle = new Wrap<>();

	public final Attribute<String> font = new Wrap<>();

	public final Attribute<String> fontSize = new Wrap<>();

	public final Attribute<String> color = new Wrap<>();

	public final Attribute<Boolean> italic = new Wrap<>();

	public final Attribute<Boolean> bold = new Wrap<>();

	public final Attribute<Boolean> underline = new Wrap<>();

	public final Attribute<Boolean> hide = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom<?> parent) {

		Page labelPage = root.createPage("label", "   Label   ");

		Section label = labelPage.createSection("label");

		ComboBox horzPosComboBox = label.createComboBox(horizontalPosition, this, "right, centre, left", "centre");
		horzPosComboBox.setLabel("Horz position");

		ComboBox vertPosComboBox = label.createComboBox(verticalPosition, this, "top, centre, bottom", "centre");
		vertPosComboBox.setLabel("Vert position");

		label.createTextField(angle, this, "0");

		label.createFont(font, "font");

		label.createSize(fontSize, this, "14pt").setLabel("Size");

		label.createColorChooser(color, this, "black");

		label.createCheckBox(italic, this);

		label.createCheckBox(bold, this);

		label.createCheckBox(underline, this);

		label.createCheckBox(hide, this);
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection graphSelection, Selection rectSelection, AbstractGraphicsAtom parent) {

		//parent.bindStringAttribute(selection, "x", leftMargin);

		return graphSelection;
	}

	//#end region

}
