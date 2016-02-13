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
 * The background settings for a graph
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Background implements GraphicsPageModel {

	//#region ATTRIBUTES

	/**
	 * Background color
	 */
	public final Attribute<String> color = new Wrap<>();

	/**
	 * Background fill style
	 */
	//public final Attribute<String> fillStyle = new Wrap<>();

	/**
	 * Background transparency
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

		Page backgroundPage = root.createPage("background", "Background");

		Section background = backgroundPage.createSection("background", "Background");

		background.createColorChooser(color, "color", "white");

		//background.createFillStyle(fillStyle, "style", "Style");

		background.createTextField(transparency, "transparency", "0");

		background.createCheckBox(hide, "hide");
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection graphSelection, Selection rectSelection, GraphicsAtom parent) {

		GraphicsAtom.bindStringAttribute(rectSelection, "fill", color);

		transparency.addModificationConsumerAndRun("updateTransparency", (data) -> {
			try {
				double fillTransparency = Double.parseDouble(transparency.get());
				double opacity = 1 - fillTransparency;
				rectSelection.attr("fill-opacity", "" + opacity);
			} catch (NumberFormatException exception) {}
		});

		hide.addModificationConsumerAndRun("hideFill", (data) -> {
			try {
				boolean doHide = hide.get();
				if (doHide) {
					rectSelection.attr("fill-opacity", "0");
				} else {
					double fillTransparency = Double.parseDouble(transparency.get());
					double opacity = 1 - fillTransparency;
					rectSelection.attr("fill-opacity", "" + opacity);
				}
			} catch (NumberFormatException exception) {

			}
		});

		return graphSelection;
	}

	@Override
	public String createVeuszText(AbstractAtom parent) {
		String veuszString = "\n";

		veuszString = veuszString + "Set('Background/color', u'" + color + "')\n";
		//veuszString = veuszString + "Set('Background/style', u'" + fillStyle + "')\n";
		if (hide.get()) {
			veuszString = veuszString + "Set('Background/hide', True)";
		}
		veuszString = veuszString + "Set('Background/transparency', " + transparency + ")\n";

		return veuszString;
	}

	//#end region

}
