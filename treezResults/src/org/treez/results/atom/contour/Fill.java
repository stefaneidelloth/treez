package org.treez.results.atom.contour;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.AbstractGraphicsAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.plotly.data.contour.ColorScale;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Fill implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	public final Attribute<String> colorScale = new Wrap<>();

	public final Attribute<Boolean> reverseScale = new Wrap<>();

	public final Attribute<Double> transparency = new Wrap<>();

	public final Attribute<Boolean> hide = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom<?> parent) {

		Page symbolPage = root.createPage("fill", "   Fill   ");

		Section fill = symbolPage.createSection("fill");

		fill.createEnumComboBox(colorScale, this, ColorScale.JET).setLabel("olor scale");

		fill.createCheckBox(reverseScale, this, false).setLabel("Reverse scale");

		fill.createDoubleVariableField(transparency, this, 0.0);

		fill.createCheckBox(hide, this);

	}

	@Override
	public Selection plotWithD3(D3 d3, Selection contourSelection, Selection rectSelection, AbstractGraphicsAtom parent) {

		return contourSelection;
	}

	//#end region

}
