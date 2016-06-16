package org.treez.results.atom.graph;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.attribute.TextField;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.AbstractGraphicsAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Consumer;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;

/**
 * The main settings for a graph
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Data implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	public final Attribute<String> leftMargin = new Wrap<>();

	public final Attribute<String> topMargin = new Wrap<>();

	public final Attribute<String> width = new Wrap<>();

	public final Attribute<String> height = new Wrap<>();

	public final Attribute<Boolean> hide = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom<?> parent) {

		Page mainPage = root.createPage("data", "   Data   ");

		Section main = mainPage.createSection("data");

		TextField leftMarginField = main.createTextField(leftMargin, this, "2.5 cm");
		leftMarginField.setLabel("Left margin");

		TextField topMarginField = main.createTextField(topMargin, this, "0.5 cm");
		topMarginField.setLabel("Top margin");

		main.createTextField(width, this, "12 cm");

		main.createTextField(height, this, "12 cm");

		main.createCheckBox(hide, this);
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection graphSelection, Selection rectSelection, AbstractGraphicsAtom parent) {

		AbstractGraphicsAtom.bindTranslationAttribute("graphTranslation", graphSelection, leftMargin, topMargin);
		AbstractGraphicsAtom.bindStringAttribute(rectSelection, "width", width);
		AbstractGraphicsAtom.bindStringAttribute(rectSelection, "height", height);
		AbstractGraphicsAtom.bindDisplayToBooleanAttribute("hideGraph", graphSelection, hide);

		Consumer replotGraph = () -> {
			Graph graph = (Graph) parent;
			graph.updatePlotWithD3(d3);
		};
		width.addModificationConsumer("replotGraph", replotGraph);
		height.addModificationConsumer("replotGraph", replotGraph);

		return graphSelection;
	}

	//#end region

}
