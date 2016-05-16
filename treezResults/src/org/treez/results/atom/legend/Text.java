package org.treez.results.atom.legend;

import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Consumer;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;

/**
 * Represents the label for an axis
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Text implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	public final Attribute<String> font = new Wrap<>();

	public final Attribute<Integer> size = new Wrap<>();

	public final Attribute<String> color = new Wrap<>();

	public final Attribute<Boolean> italic = new Wrap<>();

	public final Attribute<Boolean> bold = new Wrap<>();

	public final Attribute<Boolean> underline = new Wrap<>();

	public final Attribute<Boolean> hide = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page textPage = root.createPage("text");

		Section text = textPage.createSection("text");

		text.createFont(font, "font");

		final int defaultFontSize = 14;
		text.createIntegerVariableField(size, this, defaultFontSize) //
				.setLabel("Size");

		text.createColorChooser(color, "color", "black");

		text.createCheckBox(italic, "italic");

		text.createCheckBox(bold, "bold");

		text.createCheckBox(underline, "underline");

		text.createCheckBox(hide, "hide");

	}

	@Override
	public Selection plotWithD3(D3 d3, Selection legendSelection, Selection rectSelection, GraphicsAtom parent) {

		//not needed here since text formatting is called while creating the legend entries

		return legendSelection;
	}

	public Selection formatText(Selection textSelection, Refreshable main) {

		GraphicsAtom.bindStringAttribute(textSelection, "font-family", font);
		GraphicsAtom.bindIntegerAttribute(textSelection, "font-size", size);
		GraphicsAtom.bindStringAttribute(textSelection, "fill", color);
		GraphicsAtom.bindFontItalicStyle(textSelection, italic);
		GraphicsAtom.bindFontBoldStyle(textSelection, bold);
		GraphicsAtom.bindFontUnderline(textSelection, underline);
		GraphicsAtom.bindTransparencyToBooleanAttribute(textSelection, hide);

		Consumer refreshLegendLayout = () -> main.refresh();
		font.addModificationConsumer("font", refreshLegendLayout);
		size.addModificationConsumer("font", refreshLegendLayout);
		color.addModificationConsumer("font", refreshLegendLayout);
		italic.addModificationConsumer("font", refreshLegendLayout);
		bold.addModificationConsumer("font", refreshLegendLayout);
		underline.addModificationConsumer("font", refreshLegendLayout);

		return textSelection;
	}

	//#end region

}
