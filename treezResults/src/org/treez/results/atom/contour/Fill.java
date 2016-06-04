package org.treez.results.atom.contour;

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
import org.treez.javafxd3.plotly.ColorScale;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Fill implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	public final Attribute<String> colorScale = new Wrap<>();

	public final Attribute<Boolean> reverseScale = new Wrap<>();

	public final Attribute<String> transparency = new Wrap<>();

	public final Attribute<Boolean> hide = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page symbolPage = root.createPage("fill", "   Fill   ");

		Section fill = symbolPage.createSection("fill");

		fill.createEnumComboBox(colorScale, "Color scale", ColorScale.JET);

		fill.createCheckBox(reverseScale, "Reverse scale", false);

		fill.createTextField(transparency, "transparency", "0");

		fill.createCheckBox(hide, "hide");

	}

	@Override
	public Selection plotWithD3(D3 d3, Selection contourSelection, Selection rectSelection, GraphicsAtom parent) {

		return contourSelection;
	}

	//#end region

}
